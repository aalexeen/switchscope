package net.switchscope.security.permission;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares which configurable permission an endpoint needs.
 * <p>
 * Usually just the action - {@code @RequiresPermission("update")} - which the
 * {@link PermissionRegistry} joins to the {@link PermissionResource} of the controller that serves
 * the request, giving {@code catalog.component-type:update}. A value containing {@code ':'} is
 * taken as a complete code and used verbatim; that is for the odd endpoint that does not belong to
 * its controller's resource.
 * <p>
 * In stage 1 this is a plain marker: the registry reports on it, nothing enforces it. Enforcement
 * arrives in stage 2, when the recorded code becomes a {@code hasAuthority(...)} check. Keeping it
 * free of SpEL until then means the audit pass cannot lock anyone out of a working system.
 *
 * @see PermissionRegistry
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RequiresPermission {

    /**
     * An action ({@code read}, {@code create}, {@code update}, {@code delete}, ...) to be qualified
     * by the controller's {@link PermissionResource}, or a full {@code <domain>.<resource>:<action>}
     * code if it contains {@code ':'}.
     */
    String value();
}
