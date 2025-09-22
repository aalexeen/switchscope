package net.switchscope.model.component.catalog.housing;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.validation.NoHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * Rack Model Catalog Entry - represents a specific rack type and specifications
 */
@Entity
@DiscriminatorValue("RACK_MODEL")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RackModelEntity extends ComponentModel {

    // Physical characteristics
    @Column(name = "standard_width_inches")
    @DecimalMin("10.0") @DecimalMax("30.0")
    private Double standardWidthInches = 19.0; // Default 19"

    @Column(name = "standard_depth_inches")
    @DecimalMin("20.0") @DecimalMax("50.0")
    private Double standardDepthInches = 24.0; // Default depth

    @Column(name = "typical_capacity_u")
    @Min(1) @Max(100)
    private Integer typicalCapacityU = 42; // Default 42U

    @Column(name = "max_capacity_u")
    @Min(1) @Max(100)
    private Integer maxCapacityU = 50; // Maximum possible capacity

    // Type characteristics
    @Column(name = "is_wall_mountable", nullable = false)
    private boolean wallMountable = false;

    @Column(name = "is_floor_standing", nullable = false)
    private boolean floorStanding = true;

    @Column(name = "is_enclosed", nullable = false)
    private boolean enclosed = true;

    @Column(name = "is_open_frame", nullable = false)
    private boolean openFrame = false;

    @Column(name = "is_outdoor_rated", nullable = false)
    private boolean outdoorRated = false;

    // Physical constraints
    @Column(name = "max_weight_capacity_kg")
    @DecimalMin("50.0") @DecimalMax("2000.0")
    private Double maxWeightCapacityKg = 500.0;

    @Column(name = "max_power_capacity_watts")
    @Min(100) @Max(50000)
    private Integer maxPowerCapacityWatts = 3000;

    // Environmental features
    @Column(name = "has_ventilation", nullable = false)
    private boolean hasVentilation = true;

    @Column(name = "has_cable_management", nullable = false)
    private boolean hasCableManagement = true;

    @Column(name = "has_power_distribution", nullable = false)
    private boolean hasPowerDistribution = false;

    @Column(name = "has_cooling_system", nullable = false)
    private boolean hasCoolingSystem = false;

    // Security features
    @Column(name = "has_locks", nullable = false)
    private boolean hasLocks = true;

    // Installation characteristics
    @Column(name = "assembly_time_hours")
    @Min(1) @Max(24)
    private Integer assemblyTimeHours = 2;

    @Column(name = "door_type")
    @Size(max = 32)
    @NoHtml
    private String doorType = "PERFORATED"; // PERFORATED, GLASS, SOLID, NONE

    @Column(name = "cooling_type")
    @Size(max = 32)
    @NoHtml
    private String coolingType = "PASSIVE"; // PASSIVE, ACTIVE, LIQUID

    // Constructor
    public RackModelEntity(String name, String manufacturer, String modelNumber,
                          ComponentTypeEntity componentType) {
        super(name, manufacturer, modelNumber, componentType);
    }

    public RackModelEntity(String name, String description, String manufacturer,
                          String modelNumber, ComponentTypeEntity componentType,
                          Double standardWidthInches, Integer typicalCapacityU) {
        super(name, description, manufacturer, modelNumber, componentType);
        this.standardWidthInches = standardWidthInches;
        this.typicalCapacityU = typicalCapacityU;
    }

    // Business logic methods
    public boolean isCompact() {
        return typicalCapacityU != null && typicalCapacityU <= 12;
    }

    public boolean isFullHeight() {
        return typicalCapacityU != null && typicalCapacityU >= 42;
    }

    public boolean isHalfHeight() {
        return typicalCapacityU != null && typicalCapacityU > 12 && typicalCapacityU < 42;
    }

    public boolean isOutdoorSuitable() {
        return outdoorRated;
    }

    // Capacity validation
    public boolean canAccommodateCapacity(int requestedCapacityU) {
        return maxCapacityU == null || requestedCapacityU <= maxCapacityU;
    }

    public boolean canSupportWeight(double weightKg) {
        return maxWeightCapacityKg == null || weightKg <= maxWeightCapacityKg;
    }

    public boolean canSupportPower(int powerWatts) {
        return maxPowerCapacityWatts == null || powerWatts <= maxPowerCapacityWatts;
    }

    // Category helpers
    public String getRackCategory() {
        if (wallMountable && !floorStanding) return "WALL_MOUNT";
        if (openFrame) return "OPEN_FRAME";
        if (enclosed) return "ENCLOSED";
        if (outdoorRated) return "OUTDOOR";
        return "STANDARD";
    }

    public String getSizeCategory() {
        if (isCompact()) return "COMPACT";
        if (isHalfHeight()) return "HALF_HEIGHT";
        if (isFullHeight()) return "FULL_HEIGHT";
        return "CUSTOM";
    }

    // Specifications summary
    public String getDimensionsDescription() {
        StringBuilder desc = new StringBuilder();

        if (standardWidthInches != null) {
            desc.append(standardWidthInches).append("\" width");
        }

        if (standardDepthInches != null) {
            if (desc.length() > 0) desc.append(" × ");
            desc.append(standardDepthInches).append("\" depth");
        }

        if (typicalCapacityU != null) {
            if (desc.length() > 0) desc.append(", ");
            desc.append(typicalCapacityU).append("U capacity");
        }

        return desc.toString();
    }

    public String getFeaturesSummary() {
        List<String> features = new ArrayList<>();

        if (enclosed) features.add("Enclosed");
        if (openFrame) features.add("Open Frame");
        if (wallMountable) features.add("Wall Mountable");
        if (outdoorRated) features.add("Outdoor Rated");
        if (hasCoolingSystem) features.add("Active Cooling");
        if (hasPowerDistribution) features.add("Power Distribution");

        return String.join(", ", features);
    }

    @Override
    public boolean isValidModel() {
        if (!super.isValidModel()) {
            return false;
        }

        // Rack-specific validation
        if (standardWidthInches != null && standardWidthInches <= 0) {
            return false;
        }

        if (typicalCapacityU != null && maxCapacityU != null &&
            typicalCapacityU > maxCapacityU) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getManufacturer() + " " + getModelNumber() +
               " (" + getDimensionsDescription() +
               ", " + getLifecycleStatus() + ")";
    }
}