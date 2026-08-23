package net.switchscope.web;

import net.switchscope.model.Role;
import net.switchscope.model.User;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

@Getter
public class AuthUser extends org.springframework.security.core.userdetails.User {

    private final User user;

    public AuthUser(@NonNull User user) {
        this(user, user.getRoles());
    }

    /**
     * @param authorities the roles as {@code ROLE_*} plus, since stage 2, the permission codes the
     *                    roles grant. Both kinds live in one collection because they are checked
     *                    two different ways - {@code hasRole} for the coarse {@code /api/admin/**}
     *                    boundary, {@code hasAuthority} for everything else - and mixing the two
     *                    styles on one route is how role hierarchies produce surprises
     */
    public AuthUser(@NonNull User user, @NonNull Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), authorities);
        this.user = user;
    }

    public UUID id() {
        return user.getId();
    }

    public boolean hasRole(Role role) {
        return user.hasRole(role);
    }

    @Override
    public String toString() {
        return "AuthUser:" + id() + '[' + user.getEmail() + ']';
    }
}
