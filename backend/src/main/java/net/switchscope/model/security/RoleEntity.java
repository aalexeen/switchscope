package net.switchscope.model.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.BaseCodedEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * A job function, as data rather than as an enum.
 * <p>
 * Replaces the two-valued {@code Role} enum, which offered exactly one setting - grant ADMIN, i.e.
 * grant everything. New roles ({@code CATALOG_EDITOR}, {@code AUDITOR}) are rows, not a release.
 * <p>
 * {@code permissions} is the configuration itself: a row in {@code role_permissions} means allowed,
 * its absence means denied. Nothing else decides. Deliberately no role hierarchy - inheritance is
 * expressed by copying grants, so there is one place to read an answer out of.
 */
@Entity
@Table(name = "roles",
        uniqueConstraints = @UniqueConstraint(columnNames = "code", name = "uk_roles_code"),
        indexes = @Index(name = "idx_roles_active_sort", columnList = "is_active, sort_order"))
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity extends BaseCodedEntity {

    /**
     * The two roles the application names in code. Everything else about a role is data; these two
     * are referenced because {@code /api/admin/**} is gated by role and because a registration has
     * to become something. They are codes, not an enum: the difference is that a third role needs
     * no constant here.
     */
    public static final String ADMIN_CODE = "ADMIN";
    public static final String USER_CODE = "USER";

    /**
     * A role the application itself relies on ({@code ADMIN}, {@code USER}); it may be re-granted
     * but not deleted.
     */
    @Column(name = "system_role", nullable = false)
    private boolean systemRole = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"role_id", "permission_id"},
                    name = "uk_role_permissions"))
    private Set<PermissionEntity> permissions = new HashSet<>();

    public RoleEntity(String code, String displayName, boolean systemRole) {
        super(code, displayName);
        this.systemRole = systemRole;
    }
}
