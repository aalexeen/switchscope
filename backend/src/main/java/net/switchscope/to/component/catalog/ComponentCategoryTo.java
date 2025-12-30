package net.switchscope.to.component.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO for ComponentCategoryEntity - high-level grouping of component types
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ComponentCategoryTo extends UIStyledTo {

    private Boolean systemCategory = false;

    private Boolean infrastructure = false;

    // Read-only: list of component type IDs in this category
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<UUID> componentTypeIds = new ArrayList<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer componentTypeCount;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean hasActiveComponentTypes;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean canBeDeleted;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean housingCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean connectivityCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean supportCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean moduleCategory;

    public ComponentCategoryTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public ComponentCategoryTo(UUID id, String name) {
        super(id, name);
    }
}

