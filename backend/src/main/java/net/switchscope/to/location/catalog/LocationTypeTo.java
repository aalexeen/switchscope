package net.switchscope.to.location.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
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
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer hierarchyLevel;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean canHaveChildren = true;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean canHoldEquipment = true;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresAddress = false;

    // Physical characteristics
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean physicalLocation = true;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean rackLike = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean roomLike = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean buildingLike = false;

    // Capacity constraints
    @Min(0)
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer maxChildrenCount;

    @Min(0)
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer maxEquipmentCount;

    @Min(1) @Max(100)
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer defaultRackUnits;

    // UI and presentation
    @Size(max = 16)
    @NoHtml
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String colorCategory;

    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String iconClass;

    @Size(max = 16)
    @NoHtml
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String mapSymbol;

    // Business rules
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresAccessControl = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresClimateControl = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresPowerManagement = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresMonitoring = false;

    // Allowed child types (IDs)
    @Schema(description = "IDs of allowed child location types")
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Set<UUID> allowedChildTypeIds = new HashSet<>();

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "IDs of allowed parent location types")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Set<UUID> allowedParentTypeIds = new HashSet<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String locationCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean topLevel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean middleLevel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean bottomLevel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean virtual;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean specialType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean hasCapacityLimits;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean secureLocation;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean datacenterLike;

    public LocationTypeTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public LocationTypeTo(UUID id, String name) {
        super(id, name);
    }
}

