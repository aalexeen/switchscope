package net.switchscope.web;

import net.switchscope.model.Role;
import net.switchscope.model.User;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class AuthUser extends org.springframework.security.core.userdetails.User {

    private static final String ROLE_PREFIX = "ROLE_";

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

    /**
     * The permission half of {@link #getAuthorities()} - every authority that is not a
     * {@code ROLE_*} one, which is exactly what {@link Role#getAuthority()} produces and nothing
     * else does.
     * <p>
     * This is the collection the authorization advisor matches against, so a client reading it
     * back learns what the server will actually allow. Recomputing the same set from
     * {@code RolePermissionCatalog} in a controller would be a second derivation of one fact, and
     * the two would drift the first time one of them learned a rule the other did not.
     */
    public Set<String> permissions() {
        return getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.startsWith(ROLE_PREFIX))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String toString() {
        return "AuthUser:" + id() + '[' + user.getEmail() + ']';
    }
}
