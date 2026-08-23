package net.switchscope.config;

import net.switchscope.security.permission.PermissionEnforcementProperties;
import net.switchscope.security.permission.PermissionRegistry;
import net.switchscope.security.permission.RequiresPermissionAuthorizationManager;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.Pointcuts;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.web.bind.annotation.RequestMapping;

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
 * <h2>The pointcut is deliberately wider than the annotation</h2>
 * It matches every {@code @RequestMapping} handler, not only annotated ones, so that an endpoint
 * with no {@link net.switchscope.security.permission.RequiresPermission} still reaches the manager
 * and is refused. A forgotten annotation has to close - matching only annotated methods would make
 * forgetting one the way to stay unprotected. The union of class- and method-level matchers with
 * {@code checkInherited} covers {@code @GetMapping} and friends as meta-annotations and the
 * mappings inherited from {@code AbstractCrudController}.
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
        Pointcut pointcut = Pointcuts.union(
                new AnnotationMatchingPointcut(null, RequestMapping.class, true),
                new AnnotationMatchingPointcut(RequestMapping.class, true));
        var interceptor = new AuthorizationManagerBeforeMethodInterceptor(
                pointcut, new RequiresPermissionAuthorizationManager(registry, properties));
        interceptor.setOrder(AuthorizationInterceptorsOrder.PRE_AUTHORIZE.getOrder() - 1);
        return interceptor;
    }
}
