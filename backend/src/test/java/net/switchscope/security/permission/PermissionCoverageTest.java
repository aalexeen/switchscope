package net.switchscope.security.permission;

import net.switchscope.AbstractContextTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Asserts the property the permission model exists to guarantee: every operation the application
 * serves is either configurable or explicitly declared to be outside the model.
 * <p>
 * This is the check that was missing before. The {@code @PreAuthorize} annotations were there all
 * along; what was absent was anything that would notice when an endpoint had none - so a gap sat in
 * the project unremarked. A green build now means the gap is closed, not merely unobserved.
 * <p>
 * Runs against the same startup scan the application performs, so a failure here is a failure in
 * production too, not a test artifact.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
class PermissionCoverageTest extends AbstractContextTest {

    @Autowired
    private PermissionRegistry registry;

    /** By name: Spring Security publishes advisors of its own, and actuator a second handler mapping. */
    @Autowired
    @Qualifier("requiresPermissionAdvisor")
    private Advisor advisor;

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    @DisplayName("every served endpoint declares a permission or an explicit exemption")
    void noUnannotatedEndpoints() {
        assertThat(registry.getReport().unannotatedEndpoints())
                .as("endpoints with neither @RequiresPermission nor @AuthenticatedOnly")
                .isEmpty();
    }

    @Test
    @DisplayName("every permission the code requires can actually be granted")
    void noPermissionMissingFromDatabase() {
        assertThat(registry.getReport().missingInDatabase())
                .as("permission codes required by code but absent from the permissions table,"
                        + " leaving the operation grantable to nobody")
                .isEmpty();
    }

    @Test
    @DisplayName("no permission row is dead configuration")
    void noOrphanPermissions() {
        assertThat(registry.getReport().orphanPermissions())
                .as("permission rows no endpoint requires, which mislead whoever reads the admin UI;"
                        + " regenerate with tools/generate_permission_seed.py")
                .isEmpty();
    }

    @Test
    @DisplayName("each guarded endpoint resolves to its own permission")
    void everyGuardedEndpointIsResolvable() {
        long guarded = registry.getReport().endpoints().stream()
                .filter(e -> e.status() == EndpointPermission.Status.GUARDED)
                .count();
        assertThat(registry.getResolvableEndpointCount())
                .as("a smaller number means endpoints share a lookup key - the nine AbstractCrudController"
                        + " subclasses inherit create/update/delete as the same Method object, so keying"
                        + " on the method alone would silently collapse them onto one permission")
                .isEqualTo((int) guarded);
    }

    @Test
    @DisplayName("every declared permission can actually be enforced")
    void noUnproxyableEndpoints() {
        assertThat(registry.getReport().unproxyableEndpoints())
                .as("guarded endpoints Spring AOP cannot advise - a final class, or a final or"
                        + " non-public method. The annotation reads as protection and there is none,"
                        + " which is the same silence the registry exists to break")
                .isEmpty();
    }

    @Test
    @DisplayName("the advisor reaches every endpoint the application serves")
    void everyEndpointIsReachedByTheAdvisor() {
        Pointcut pointcut = ((PointcutAdvisor) advisor).getPointcut();
        List<HandlerMethod> served = handlerMapping.getHandlerMethods().values().stream()
                .filter(handler -> PermissionRegistry.isOwnEndpoint(handler.getBeanType()))
                .toList();
        assertThat(served)
                .as("the same endpoints the scan reports; nothing to check would make the assertion"
                        + " below pass by having nothing to say")
                .hasSize(registry.getReport().endpoints().size());

        List<String> unreachable = served.stream()
                .filter(handler -> !matches(pointcut, handler))
                .map(handler -> handler.getBeanType().getSimpleName() + '#' + handler.getMethod().getName())
                .toList();

        assertThat(unreachable)
                .as("an endpoint the pointcut does not match is never checked at all - the same"
                        + " silence as an unproxyable one, from the other direction, and it answers"
                        + " 200 where the permission said 403. The pointcut is narrow on purpose"
                        + " (only this application's classes, only methods that map a request), and"
                        + " this is what keeps 'only' from quietly becoming 'not all of them'")
                .isEmpty();
    }

    private static boolean matches(Pointcut pointcut, HandlerMethod handler) {
        Class<?> beanType = handler.getBeanType();
        return pointcut.getClassFilter().matches(beanType)
                && pointcut.getMethodMatcher().matches(handler.getMethod(), beanType);
    }

    @Test
    @DisplayName("the scan found the application's endpoints at all")
    void reportIsPopulated() {
        assertThat(registry.getReport().endpoints())
                .as("a report this small means the scan ran before the handler mapping was built")
                .hasSizeGreaterThan(50);
    }
}
