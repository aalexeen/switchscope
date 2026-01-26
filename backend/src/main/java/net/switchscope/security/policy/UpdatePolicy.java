package net.switchscope.security.policy;

/**
 * Interface for update policies that control field nullification permissions.
 * Different implementations provide different rules based on user role.
 */
public interface UpdatePolicy {

    /**
     * Checks if a field can be set to null under this policy.
     *
     * @param fieldName the name of the field being nullified
     * @param level the access level configured for this field
     * @return true if nullification is allowed, false otherwise
     */
    boolean canNullify(String fieldName, FieldAccessLevel level);

    /**
     * Returns a human-readable name for this policy.
     *
     * @return the policy name (e.g., "USER", "ADMIN")
     */
    String getPolicyName();
}
