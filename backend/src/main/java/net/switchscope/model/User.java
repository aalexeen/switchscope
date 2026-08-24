package net.switchscope.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import net.switchscope.HasIdAndEmail;
import net.switchscope.mapper.Default;
import net.switchscope.model.security.RoleEntity;
import net.switchscope.validation.NoHtml;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends NamedEntity implements HasIdAndEmail {

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @NotBlank
    @Size(max = 128)
    @NoHtml   // https://stackoverflow.com/questions/17480809
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank
    @Size(max = 128)
    // https://stackoverflow.com/a/12505165/548473
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "enabled", nullable = false, columnDefinition = "bool default true")
    private boolean enabled = true;

    @Column(name = "registered", nullable = false, columnDefinition = "timestamp default now()", updatable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date registered = new Date();

    /**
     * The roles held, as rows rather than as enum constants.
     * <p>
     * Eager because authentication is stateless: every request loads the user, and every request
     * needs to know what they may do. The join column is {@code role_id}; the {@code role} string
     * this used to map was dropped with the enum.
     * <p>
     * It still <em>serialises</em> as {@code ["ADMIN"]}, because this entity is what
     * {@code /api/profile} answers with and the frontend does {@code roles.includes('ADMIN')} on
     * that shape. The rows carry a display name, a sort order and their permissions; none of that
     * is the client's business, and turning the wire shape into objects would have broken the
     * role gate on {@code /users/**} silently.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"},
                    name = "uk_user_role_role_id"))
    @JsonSerialize(contentConverter = RoleCode.class)
    private Set<RoleEntity> roles = new HashSet<>();

    public User(User u) {
        this(u.id, u.name, u.email, u.password, u.enabled, u.registered, u.roles);
    }

    @Default
    public User(UUID id, String name, String email, String password) {
        this(id, name, email, password, true, new Date(), Set.of());
    }

    public User(UUID id, String name, String email, String password, boolean enabled, Date registered, @NonNull Collection<RoleEntity> roles) {
        super(id, name);
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.registered = registered;
        setRoles(roles);
    }

    public void setRoles(Collection<RoleEntity> roles) {
        this.roles = new HashSet<>(roles);
    }

    /** The codes, which is what everything outside the model asks for: authorities, JSON, gates. */
    public Set<String> roleCodes() {
        return roles.stream().map(RoleEntity::getCode).collect(Collectors.toCollection(TreeSet::new));
    }

    public boolean hasRole(String code) {
        return roles.stream().anyMatch(role -> code.equals(role.getCode()));
    }

    /** Keeps the JSON a list of codes without a second property to keep in step with the first. */
    static final class RoleCode extends StdConverter<RoleEntity, String> {
        @Override
        public String convert(RoleEntity role) {
            return role.getCode();
        }
    }

    @Override
    public String toString() {
        return "User:" + id + '[' + email + ']';
    }
}
