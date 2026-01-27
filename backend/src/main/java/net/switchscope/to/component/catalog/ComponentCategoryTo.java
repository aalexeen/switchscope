package net.switchscope.to.component.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for ComponentCategoryEntity - high-level grouping of component types
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ComponentCategoryTo extends UIStyledTo {

    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Boolean systemCategory = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean infrastructure = false;

    // Custom properties for extensibility
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Map<String, String> properties = new HashMap<>();

    // Read-only: list of component type IDs in this category
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private List<UUID> componentTypeIds = new ArrayList<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer componentTypeCount;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean hasActiveComponentTypes;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean canBeDeleted;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean housingCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean connectivityCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean supportCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean moduleCategory;

    public ComponentCategoryTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public ComponentCategoryTo(UUID id, String name) {
        super(id, name);
    }
}

