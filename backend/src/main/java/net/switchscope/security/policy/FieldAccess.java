package net.switchscope.security.policy;

import java.lang.annotation.*;

/**
 * Annotation to mark field access level for update operations.
 * Determines who can set the field to null during updates.
 *
 * <p>Default level is {@link FieldAccessLevel#ADMIN_NULLABLE},
 * meaning only admins can nullify the field.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * public class MyDto {
 *     &#64;FieldAccess(FieldAccessLevel.REQUIRED)
 *     private String name;  // Cannot be nullified by anyone
 *
 *     &#64;FieldAccess(FieldAccessLevel.USER_WRITABLE)
 *     private Boolean active;  // Any user can nullify
 *
 *     &#64;FieldAccess  // Default: ADMIN_NULLABLE
 *     private String description;  // Only admin can nullify
 * }
 * </pre>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldAccess {
    /**
     * The access level for this field.
     * @return the field access level
     */
    FieldAccessLevel value() default FieldAccessLevel.ADMIN_NULLABLE;
}
