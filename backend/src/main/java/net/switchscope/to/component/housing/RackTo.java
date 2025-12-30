package net.switchscope.to.component.housing;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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

    private Integer rackUnitsTotal;

    @Schema(description = "Rack type/model catalog ID")
    private UUID rackTypeId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Rack type name")
    private String rackTypeName;

    // Physical configuration
    private Double actualWidthInches;

    private Double actualDepthInches;

    private Double actualHeightInches;

    // Door and panel configuration
    @Size(max = 32)
    @NoHtml
    private String frontDoorType;

    @Size(max = 32)
    @NoHtml
    private String rearDoorType;

    private Boolean hasSidePanels;

    private Boolean hasFrontDoor;

    private Boolean hasRearDoor;

    // Power and infrastructure
    @Min(0) @Max(50)
    private Integer powerOutlets;

    private Integer powerCapacityWatts;

    private Boolean hasPdu;

    @Size(max = 64)
    @NoHtml
    private String pduType;

    // Cooling configuration
    @Size(max = 32)
    @NoHtml
    private String coolingType;

    private Boolean hasFans;

    @Min(0) @Max(20)
    private Integer fanCount;

    // Security features
    private Boolean hasLocks;

    @Size(max = 32)
    @NoHtml
    private String lockType;

    // Environmental monitoring
    private Boolean hasTemperatureMonitoring;

    private Boolean hasHumidityMonitoring;

    private Boolean hasPowerMonitoring;

    // Cable management
    @Size(max = 64)
    @NoHtml
    private String cableManagementType;

    private Boolean hasCableTrays;

    // Installation details
    private Boolean floorAnchored;

    @Size(max = 32)
    @NoHtml
    private String floorAnchorType;

    // Weight and capacity
    private Double emptyWeightKg;

    private Double maxLoadWeightKg;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer occupiedRackUnits;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer availableRackUnits;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Double utilizationPercentage;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean hasAvailableSpace;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String dimensionsDescription;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean wallMountable;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean outdoorRated;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean environmentalMonitoring;

    public RackTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public RackTo(UUID id, String name) {
        super(id, name);
    }
}

