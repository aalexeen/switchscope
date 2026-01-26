package net.switchscope.security.policy;

/**
 * Defines access levels for field modifications during updates.
 * Controls who can set a field to null.
 */
public enum FieldAccessLevel {
    /**
     * Field is required and cannot be nullified by anyone.
     * Used for mandatory fields like name, manufacturer, modelNumber.
     */
    REQUIRED,

    /**
     * Only ADMIN role can nullify this field.
     * Default level for most technical fields.
     */
    ADMIN_NULLABLE,

    /**
     * Any authenticated user can nullify this field.
     * Used for user-modifiable flags like active, discontinued.
     */
    USER_WRITABLE,

    /**
     * Field is read-only and ignored during updates.
     * Used for computed or system-managed fields.
     */
    READ_ONLY
}
