package net.switchscope.config;

import net.switchscope.security.permission.PermissionEnforcementProperties;
import net.switchscope.security.permission.PermissionRegistry;
import net.switchscope.security.permission.RequiresPermissionAuthorizationManager;
import org.springframework.aop.Advisor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

/**
 * Publishes the permission check as a method-security advisor.
 *
 * <h2>Why an advisor and not an annotation or a filter</h2>
 * Three candidates were weighed (the reasoning is written up in
 * {@code docs/backend-stage2-enforcement.md}):
 * <ul>
 *   <li>{@code @PreAuthorize} on the method cannot give nine subclasses of
 *       {@code AbstractCrudController} nine different permissions - one shared method carries one
 *       expression;</li>
 *   <li>a {@code HandlerInterceptor} works, but moves authorization outside Spring Security: no
 *       authorization events, no observability, and a second parallel place where access is
 *       decided - while covering no more entry points than method security does;</li>
 *   <li>an {@code AuthorizationManager} in {@code authorizeHttpRequests} would have to resolve the
 *       handler itself, which {@code HandlerMappingIntrospector} explicitly warns against
 *       ("should not be repeated multiple times per request").</li>
 * </ul>
 * What is left is the pattern Spring Security applies to its own {@code @Secured}.
 *
 * <h2>The pointcut is wider than the annotation, and exactly as wide as the endpoints</h2>
 * It matches every request handler, not only annotated ones, so that a handler with no
 * {@link net.switchscope.security.permission.RequiresPermission} still reaches the manager and is
 * refused. A forgotten annotation has to close - matching only annotated methods would make
 * forgetting one the way to stay unprotected.
 * <p>
 * "Every handler" has to be said carefully, and the first two attempts said it wrong. Both errors
 * were invisible until {@code mode} reached {@code ENFORCE}, because until then the manager only
 * logged what it would have refused:
 * <ul>
 *   <li><b>Too wide by class.</b> A class-level {@code @RequestMapping} matcher advises every
 *       method of a controller, and a controller has methods that serve no request:
 *       {@code AbstractUserController#initBinder} registers a validator and is invoked while the
 *       body is being bound - before this interceptor would have run for the handler itself. The
 *       registry knows nothing about it, quite rightly, so it arrived as {@code Unknown} and
 *       closed, and every {@code POST}/{@code PUT} on {@code /api/profile} answered 403 while
 *       {@code GET}, which binds nothing, went through. Fail-closed is a rule about endpoints; a
 *       method that is not an endpoint is not an endpoint someone forgot to annotate.</li>
 *   <li><b>Too wide by package.</b> springdoc's resources are handlers like any other, and the
 *       scan skips them on purpose ("not ours to gate") - so they too arrived as {@code Unknown}
 *       and closed, turning {@code /v3/api-docs}, {@code /v3/api-docs/swagger-config} and the
 *       Swagger UI at {@code /} into 403s, all of them {@code permitAll} at the filter chain and
 *       refused after it.</li>
 * </ul>
 * Hence the two halves below. {@link PermissionRegistry#isOwnEndpoint} is the same test the scan
 * applies, so what is scanned and what is advised cannot drift apart; and the method matcher asks
 * for the mapping annotation the way {@code RequestMappingHandlerMapping} does - through the method
 * hierarchy, so that {@code @GetMapping} as a meta-annotation, an override that does not repeat it
 * and the mappings {@code AbstractCrudController} declares once for nine subclasses all match,
 * while {@code @InitBinder} and friends do not.
 */
@Configuration
@EnableConfigurationProperties(PermissionEnforcementProperties.class)
public class PermissionEnforcementConfig {

    /**
     * {@code static} and infrastructure-role because an advisor is consulted while other beans are
     * still being created; a non-static factory method would drag this configuration class - and
     * everything it injects - into premature initialisation.
     * <p>
     * Ordered just before {@code @PreAuthorize} so that during the transition both run and the
     * narrower of the two wins. Deliberately an AND, never {@code legacyAllow || newAllow}: an OR
     * would preserve whichever check is broader and quietly undo the migration.
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static Advisor requiresPermissionAdvisor(PermissionRegistry registry,
                                             PermissionEnforcementProperties properties) {
        Pointcut pointcut = new ComposablePointcut(
                (ClassFilter) PermissionRegistry::isOwnEndpoint, HANDLER_METHODS);
        var interceptor = new AuthorizationManagerBeforeMethodInterceptor(
                pointcut, new RequiresPermissionAuthorizationManager(registry, properties));
        interceptor.setOrder(AuthorizationInterceptorsOrder.PRE_AUTHORIZE.getOrder() - 1);
        return interceptor;
    }

    /**
     * A method that serves a request, which is not the same set as "a method on a controller".
     * <p>
     * {@link AnnotationUtils#findAnnotation(Method, Class)} rather than a plain annotation matcher:
     * it follows meta-annotations, so {@code @GetMapping} counts, and it follows the method
     * hierarchy, so an override that does not repeat the mapping - and the five mappings
     * {@code AbstractCrudController} declares once for nine subclasses - count as well. Resolving
     * against the target class first is what makes bridge methods answer the same as the methods
     * they bridge.
     */
    private static final MethodMatcher HANDLER_METHODS = new StaticMethodMatcher() {
        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            Method specific = AopUtils.getMostSpecificMethod(method, targetClass);
            return AnnotationUtils.findAnnotation(specific, RequestMapping.class) != null;
        }
    };
}
