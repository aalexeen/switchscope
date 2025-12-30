package net.switchscope.to.location.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.to.component.catalog.BaseCodedTo;
import net.switchscope.validation.NoHtml;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for LocationTypeEntity - catalog of location types
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LocationTypeTo extends BaseCodedTo {

    // Hierarchy characteristics
    @Min(1) @Max(100)
    private Integer hierarchyLevel;

    private Boolean canHaveChildren = true;

    private Boolean canHoldEquipment = true;

    private Boolean requiresAddress = false;

    // Physical characteristics
    private Boolean physicalLocation = true;

    private Boolean rackLike = false;

    private Boolean roomLike = false;

    private Boolean buildingLike = false;

    // Capacity constraints
    @Min(0)
    private Integer maxChildrenCount;

    @Min(0)
    private Integer maxEquipmentCount;

    @Min(1) @Max(100)
    private Integer defaultRackUnits;

    // UI and presentation
    @Size(max = 16)
    @NoHtml
    private String colorCategory;

    @Size(max = 64)
    @NoHtml
    private String iconClass;

    @Size(max = 16)
    @NoHtml
    private String mapSymbol;

    // Business rules
    private Boolean requiresAccessControl = false;

    private Boolean requiresClimateControl = false;

    private Boolean requiresPowerManagement = false;

    private Boolean requiresMonitoring = false;

    // Allowed child types (IDs)
    @Schema(description = "IDs of allowed child location types")
    private Set<UUID> allowedChildTypeIds = new HashSet<>();

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "IDs of allowed parent location types")
    private Set<UUID> allowedParentTypeIds = new HashSet<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String locationCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean topLevel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean middleLevel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean bottomLevel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean virtual;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean specialType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean hasCapacityLimits;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean secureLocation;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean datacenterLike;

    public LocationTypeTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public LocationTypeTo(UUID id, String name) {
        super(id, name);
    }
}

