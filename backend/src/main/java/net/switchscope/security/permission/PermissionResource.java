package net.switchscope.security.permission;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Names the resource a controller operates on, so that {@link RequiresPermission} on its handler
 * methods only has to name the action.
 * <p>
 * This split is what makes {@link net.switchscope.web.AbstractCrudController} workable: it declares
 * getAll/get/create/update/delete once for nine subclasses, so an action written there would
 * otherwise give all nine the same permission code. The action lives on the shared method, the
 * resource on each concrete controller, and the registry composes the two using the
 * <em>concrete</em> handler bean type.
 * <p>
 * The value is the {@code <domain>.<resource>} part of a permission code - {@code catalog.component-type}
 * in {@code catalog.component-type:update}. A resource with no sub-part is allowed
 * ({@code location}, {@code port}); it then forms codes like {@code location:delete}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PermissionResource {

    /**
     * The {@code <domain>.<resource>} prefix of every permission code in this controller.
     */
    String value();
}
