package net.switchscope.model.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.BaseCodedEntity;
import net.switchscope.validation.NoHtml;

/**
 * One configurable API operation.
 * <p>
 * The code is {@code <domain>.<resource>:<action>}, e.g. {@code catalog.component-type:update}, and
 * matches what {@link net.switchscope.security.permission.RequiresPermission} declares on the
 * endpoint. {@code domain}, {@code resource} and {@code action} repeat the parts of the code as
 * columns: the code is the identity, the parts are what the admin UI groups and filters by, and
 * what the coverage matrix pivots on.
 * <p>
 * Being a {@link BaseCodedEntity} it is an ordinary catalog, so it inherits the project's catalog
 * plumbing - and, on the frontend, a table view - without new code.
 */
@Entity
@Table(name = "permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = "code", name = "uk_permissions_code"),
        indexes = {
                @Index(name = "idx_permissions_domain_resource", columnList = "domain, resource"),
                @Index(name = "idx_permissions_active_sort", columnList = "is_active, sort_order")
        })
@Getter
@Setter
@NoArgsConstructor
public class PermissionEntity extends BaseCodedEntity {

    @Column(name = "domain", nullable = false)
    @Size(max = 64)
    @NotNull
    @NoHtml
    private String domain;

    @Column(name = "resource", nullable = false)
    @Size(max = 128)
    @NotNull
    @NoHtml
    private String resource;

    @Column(name = "action", nullable = false)
    @Size(max = 64)
    @NotNull
    @NoHtml
    private String action;

    public PermissionEntity(String code, String displayName, String domain, String resource, String action) {
        super(code, displayName);
        this.domain = domain;
        this.resource = resource;
        this.action = action;
    }
}
