package net.switchscope.security.policy;

import net.switchscope.model.Role;
import net.switchscope.web.AuthUtil;
import org.springframework.stereotype.Component;

/**
 * Resolves the appropriate {@link UpdatePolicy} based on the current user's role.
 */
@Component
public class UpdatePolicyResolver {

    private static final UpdatePolicy USER_POLICY = new UserUpdatePolicy();
    private static final UpdatePolicy ADMIN_POLICY = new AdminUpdatePolicy();

    /**
     * Resolves the update policy for the currently authenticated user.
     *
     * @return {@link AdminUpdatePolicy} if user has ADMIN role, {@link UserUpdatePolicy} otherwise
     */
    public UpdatePolicy resolve() {
        var authUser = AuthUtil.safeGet();
        if (authUser != null && authUser.hasRole(Role.ADMIN)) {
            return ADMIN_POLICY;
        }
        return USER_POLICY;
    }

    /**
     * Resolves a policy for a specific role.
     * Useful for testing.
     *
     * @param isAdmin true to get admin policy, false for user policy
     * @return the appropriate policy
     */
    public UpdatePolicy resolve(boolean isAdmin) {
        return isAdmin ? ADMIN_POLICY : USER_POLICY;
    }
}
