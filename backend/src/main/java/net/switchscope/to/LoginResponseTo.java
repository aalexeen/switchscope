package net.switchscope.to;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.switchscope.HasIdAndEmail;
import net.switchscope.model.Role;

import java.util.Set;
import java.util.UUID;

/**
 * What {@code /api/auth/login} and {@code /api/auth/check} answer: who the caller is, and what the
 * server will let them do.
 * <p>
 * The {@code permissions} half is new in stage 3 and is what the frontend gates routes and buttons
 * on. It is additive on purpose - {@code roles} keeps serialising as {@code ["ADMIN"]}, because
 * {@code services/auth.js} and {@code UserAccount.vue} read it that way and the client-side gate is
 * a convenience, not the boundary: the server refuses regardless of what the client chose to show.
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class LoginResponseTo extends NamedTo implements HasIdAndEmail {
    String email;
    Set<Role> roles;
    Set<String> permissions;

    public LoginResponseTo(UUID id, String name, String email, Set<Role> roles, Set<String> permissions) {
        super(id, name);
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "LoginResponseTo:" + id + '[' + email + ']' + " roles:" + roles
                + " permissions:" + permissions.size();
    }
}
