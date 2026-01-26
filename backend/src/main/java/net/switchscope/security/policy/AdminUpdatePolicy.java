package net.switchscope.security.policy;

/**
 * Update policy for users with ADMIN role.
 * Admins can nullify all fields except {@link FieldAccessLevel#REQUIRED}
 * and {@link FieldAccessLevel#READ_ONLY}.
 */
public class AdminUpdatePolicy implements UpdatePolicy {

    @Override
    public boolean canNullify(String fieldName, FieldAccessLevel level) {
        return switch (level) {
            case REQUIRED, READ_ONLY -> false;
            case ADMIN_NULLABLE, USER_WRITABLE -> true;
        };
    }

    @Override
    public String getPolicyName() {
        return "ADMIN";
    }
}
