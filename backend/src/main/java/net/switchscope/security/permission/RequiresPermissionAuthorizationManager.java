package net.switchscope.security.permission;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * Decides whether the caller may invoke a controller method, from the same scan that produces the
 * coverage report.
 *
 * <h2>Why this and not {@code @PreAuthorize}</h2>
 * {@link net.switchscope.web.AbstractCrudController} declares five mappings <em>once</em> for nine
 * subclasses. An expression written on a shared method is one expression, and nine different
 * permissions are needed - which is why stage 1 split the annotation into a resource on the class
 * and an action on the method in the first place. Spring Security has the same problem with
 * {@code @Secured} and solves it the same way: {@code SecuredAuthorizationManager} keys on
 * {@code MethodClassKey(method, targetClass)}. This class is that pattern applied to
 * {@link RequiresPermission}, reading the registry so that what is enforced and what is reported
 * are one lookup, not two implementations.
 *
 * <h2>The bridge-method trap</h2>
 * {@code RackController} overrides {@code get(UUID)} with a narrowed return type, so the compiler
 * emits a synthetic bridge {@code BaseTo get(UUID)} alongside {@code RackTo get(UUID)}; other
 * operations are inherited unchanged and have no bridge at all. The reflective method arriving here
 * is therefore not reliably the one the scan recorded. Both sides normalise through
 * {@link AopUtils#getMostSpecificMethod}, which resolves bridges - skip it and every catalog route
 * refuses users who hold the permission.
 *
 * <h2>Fail closed</h2>
 * An endpoint the scan knows nothing about is refused, never allowed. A forgotten annotation has to
 * close: the previous gap in this project was exactly an endpoint nobody had marked, and treating
 * "no requirement found" as "no requirement needed" is how it stayed open.
 */
public class RequiresPermissionAuthorizationManager implements AuthorizationManager<MethodInvocation> {

    /** Stable marker so shadow-mode refusals can be grepped out of the log. */
    public static final String SHADOW_MARKER = "PERMISSION-SHADOW-DENY";

    private static final Logger log = LoggerFactory.getLogger(RequiresPermissionAuthorizationManager.class);

    private final PermissionRegistry registry;
    private final PermissionEnforcementProperties properties;

    public RequiresPermissionAuthorizationManager(PermissionRegistry registry,
                                                  PermissionEnforcementProperties properties) {
        this.registry = registry;
        this.properties = properties;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation invocation) {
        if (properties.getMode() == PermissionMode.AUDIT && properties.getEnforceDomains().isEmpty()) {
            return null; // stage 1 behaviour: observe at startup, say nothing about requests
        }
        Object target = invocation.getThis();
        Class<?> targetClass = target != null
                ? AopProxyUtils.ultimateTargetClass(target)
                : invocation.getMethod().getDeclaringClass();
        Method method = AopUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);

        return switch (registry.requirementOf(targetClass, method)) {
            case EndpointRequirement.Exempt ignored -> null;
            case EndpointRequirement.Unknown unknown -> decideUnknown(unknown, targetClass, method);
            case EndpointRequirement.Permission(String code) ->
                    decide(code, isGranted(authentication, code), targetClass, method);
        };
    }

    /**
     * An endpoint with no known requirement. It has no domain, so it cannot be promoted by
     * {@code enforce-domains} and follows the global mode alone.
     */
    private AuthorizationDecision decideUnknown(EndpointRequirement.Unknown unknown,
                                                Class<?> targetClass, Method method) {
        if (properties.getMode() == PermissionMode.ENFORCE) {
            log.error("Refusing {}#{}: {}. An endpoint that declares nothing must close, not open",
                    targetClass.getSimpleName(), method.getName(), unknown);
            return new AuthorizationDecision(false);
        }
        log.warn("{} {}#{} would be refused: {}", SHADOW_MARKER,
                targetClass.getSimpleName(), method.getName(), unknown);
        return null;
    }

    private AuthorizationDecision decide(String code, boolean granted, Class<?> targetClass, Method method) {
        if (granted) {
            return new AuthorizationDecision(true);
        }
        if (properties.modeFor(code) == PermissionMode.ENFORCE) {
            return new AuthorizationDecision(false);
        }
        log.warn("{} {}#{} would be refused: caller lacks '{}'", SHADOW_MARKER,
                targetClass.getSimpleName(), method.getName(), code);
        return null;
    }

    private static boolean isGranted(Supplier<Authentication> authentication, String code) {
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (code.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
