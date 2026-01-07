package net.switchscope.to;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.switchscope.HasIdAndEmail;
import net.switchscope.model.Role;

import java.util.Set;
import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
public class LoginResponseTo extends NamedTo implements HasIdAndEmail {
    String email;
    Set<Role> roles;

    public LoginResponseTo(UUID id, String name, String email, Set<Role> roles) {
        super(id, name);
        this.email = email;
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "LoginResponseTo:" + id + '[' + email + ']' + " roles:" + roles;
    }
}
