package net.switchscope.to.component.housing;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
import net.switchscope.to.component.ComponentTo;
import net.switchscope.validation.NoHtml;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for Rack entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RackTo extends ComponentTo {

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer rackUnitsTotal;

    @Schema(description = "Rack type/model catalog ID")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID rackTypeId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Rack type name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String rackTypeName;

    // Physical configuration
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double actualWidthInches;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double actualDepthInches;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double actualHeightInches;

    // Door and panel configuration
    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String frontDoorType;

    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String rearDoorType;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasSidePanels;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasFrontDoor;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasRearDoor;

    // Power and infrastructure
    @Min(0) @Max(50)
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer powerOutlets;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer powerCapacityWatts;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasPdu;

    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String pduType;

    // Cooling configuration
    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String coolingType;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasFans;

    @Min(0) @Max(20)
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer fanCount;

    // Security features
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasLocks;

    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String lockType;

    // Environmental monitoring
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasTemperatureMonitoring;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasHumidityMonitoring;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasPowerMonitoring;

    // Cable management
    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String cableManagementType;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasCableTrays;

    // Installation details
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean floorAnchored;

    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String floorAnchorType;

    // Weight and capacity
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double emptyWeightKg;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double maxLoadWeightKg;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer occupiedRackUnits;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer availableRackUnits;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Double utilizationPercentage;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean hasAvailableSpace;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String dimensionsDescription;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean wallMountable;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean outdoorRated;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean environmentalMonitoring;

    public RackTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public RackTo(UUID id, String name) {
        super(id, name);
    }
}

