package net.switchscope.to.component.catalog.housing;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
import net.switchscope.to.component.catalog.ComponentModelTo;
import net.switchscope.validation.NoHtml;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for Rack Model catalog entry
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RackModelTo extends ComponentModelTo {

    // Physical characteristics
    @DecimalMin("10.0") @DecimalMax("30.0")
    private Double standardWidthInches;

    @DecimalMin("20.0") @DecimalMax("50.0")
    private Double standardDepthInches;

    @Min(1) @Max(100)
    private Integer typicalCapacityU;

    @Min(1) @Max(100)
    private Integer maxCapacityU;

    // Type characteristics
    private Boolean wallMountable = false;

    private Boolean floorStanding = true;

    private Boolean enclosed = true;

    private Boolean openFrame = false;

    private Boolean outdoorRated = false;

    // Physical constraints
    @DecimalMin("50.0") @DecimalMax("2000.0")
    private Double maxWeightCapacityKg;

    @Min(100) @Max(50000)
    private Integer maxPowerCapacityWatts;

    // Environmental features
    private Boolean hasVentilation = true;

    private Boolean hasCableManagement = true;

    private Boolean hasPowerDistribution = false;

    private Boolean hasCoolingSystem = false;

    // Security features
    private Boolean hasLocks = true;

    // Installation characteristics
    @Min(1) @Max(24)
    private Integer assemblyTimeHours;

    @Size(max = 32)
    @NoHtml
    private String doorType;

    @Size(max = 32)
    @NoHtml
    private String coolingType;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String rackCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String sizeCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String dimensionsDescription;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String featuresSummary;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean compact;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean fullHeight;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean halfHeight;

    public RackModelTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public RackModelTo(UUID id, String name) {
        super(id, name);
    }
}

