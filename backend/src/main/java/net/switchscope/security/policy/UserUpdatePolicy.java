package net.switchscope.security.policy;

/**
 * Update policy for users with USER role.
 * Users can only nullify fields marked as {@link FieldAccessLevel#USER_WRITABLE}.
 */
public class UserUpdatePolicy implements UpdatePolicy {

    @Override
    public boolean canNullify(String fieldName, FieldAccessLevel level) {
        return level == FieldAccessLevel.USER_WRITABLE;
    }

    @Override
    public String getPolicyName() {
        return "USER";
    }
}
