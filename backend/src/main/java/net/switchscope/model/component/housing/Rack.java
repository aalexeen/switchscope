package net.switchscope.model.component.housing;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.Component;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.catalog.housing.RackModelEntity;
import net.switchscope.model.installation.Installation;
import net.switchscope.validation.NoHtml;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("RACK")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rack extends Component {

    @Column(name = "rack_units_total")
    private Integer rackUnitsTotal = 42; // Standard 42U rack

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rack_type_id", nullable = false)
    private RackModelEntity rackType;

    // Physical configuration
    @Column(name = "actual_width_inches")
    private Double actualWidthInches;

    @Column(name = "actual_depth_inches")
    private Double actualDepthInches;

    @Column(name = "actual_height_inches")
    private Double actualHeightInches;

    // Door and panel configuration
    @Column(name = "front_door_type")
    @Size(max = 32)
    @NoHtml
    private String frontDoorType = "PERFORATED";

    @Column(name = "rear_door_type")
    @Size(max = 32)
    @NoHtml
    private String rearDoorType = "PERFORATED";

    @Column(name = "has_side_panels", nullable = false)
    private boolean hasSidePanels = true;

    @Column(name = "has_front_door", nullable = false)
    private boolean hasFrontDoor = true;

    @Column(name = "has_rear_door", nullable = false)
    private boolean hasRearDoor = true;

    // Power and infrastructure
    @Column(name = "power_outlets")
    @Min(0) @Max(50)
    private Integer powerOutlets;

    @Column(name = "power_capacity_watts")
    private Integer powerCapacityWatts;

    @Column(name = "has_pdu", nullable = false)
    private boolean hasPdu = false; // Power Distribution Unit

    @Column(name = "pdu_type")
    @Size(max = 64)
    @NoHtml
    private String pduType;

    // Cooling configuration
    @Column(name = "cooling_type")
    @Size(max = 32)
    @NoHtml
    private String coolingType = "PASSIVE";

    @Column(name = "has_fans", nullable = false)
    private boolean hasFans = false;

    @Column(name = "fan_count")
    @Min(0) @Max(20)
    private Integer fanCount;

    // Security features
    @Column(name = "has_locks", nullable = false)
    private boolean hasLocks = true;

    @Column(name = "lock_type")
    @Size(max = 32)
    @NoHtml
    private String lockType;

    // Environmental monitoring
    @Column(name = "has_temperature_monitoring", nullable = false)
    private boolean hasTemperatureMonitoring = false;

    @Column(name = "has_humidity_monitoring", nullable = false)
    private boolean hasHumidityMonitoring = false;

    @Column(name = "has_power_monitoring", nullable = false)
    private boolean hasPowerMonitoring = false;

    // Cable management
    @Column(name = "cable_management_type")
    @Size(max = 64)
    @NoHtml
    private String cableManagementType;

    @Column(name = "has_cable_trays", nullable = false)
    private boolean hasCableTrays = true;

    // Installation details
    @Column(name = "is_floor_anchored", nullable = false)
    private boolean floorAnchored = false;

    @Column(name = "floor_anchor_type")
    @Size(max = 32)
    @NoHtml
    private String floorAnchorType;

    // Weight and capacity
    @Column(name = "empty_weight_kg")
    private Double emptyWeightKg;

    @Column(name = "max_load_weight_kg")
    private Double maxLoadWeightKg;

    // Constructors
    public Rack(UUID id, String name, ComponentTypeEntity componentType, RackModelEntity rackType) {
        super(id, name, componentType);
        this.rackType = rackType;
        initializeFromRackType();
    }

    public Rack(UUID id, String name, ComponentTypeEntity componentType,
                RackModelEntity rackType, Integer rackUnitsTotal) {
        super(id, name, componentType);
        this.rackType = rackType;
        this.rackUnitsTotal = rackUnitsTotal;
        initializeFromRackType();
    }

    public Rack(UUID id, String name, String manufacturer, String model,
                ComponentTypeEntity componentType, RackModelEntity rackType) {
        super(id, name, manufacturer, model, null, componentType);
        this.rackType = rackType;
        initializeFromRackType();
    }

    // Initialize rack properties from rack type
    private void initializeFromRackType() {
        if (rackType != null) {
            if (rackUnitsTotal == null) {
                rackUnitsTotal = rackType.getTypicalCapacityU();
            }
            if (actualWidthInches == null) {
                actualWidthInches = rackType.getStandardWidthInches();
            }
            if (actualDepthInches == null) {
                actualDepthInches = rackType.getStandardDepthInches();
            }
            if (powerCapacityWatts == null) {
                powerCapacityWatts = rackType.getMaxPowerCapacityWatts();
            }
            if (maxLoadWeightKg == null) {
                maxLoadWeightKg = rackType.getMaxWeightCapacityKg();
            }

            // Set features based on rack type capabilities
            hasLocks = rackType.isHasLocks();
            hasCableTrays = rackType.isHasCableManagement();

            // Set default types from rack type
            /*if (frontDoorType == null) {
                frontDoorType = rackType.getDefaultFrontDoorType();
            }
            if (rearDoorType == null) {
                rearDoorType = rackType.getDefaultRearDoorType();
            }*/
            if (coolingType == null) {
                coolingType = rackType.getCoolingType();
            }
        }
    }

    // Implementing abstract methods from Component
    @Override
    public boolean isInstallable() {
        return true;
    }

    @Override
    public Map<String, String> getSpecifications() {
        Map<String, String> specs = new HashMap<>();

        specs.put("Total Rack Units", String.valueOf(rackUnitsTotal));
        specs.put("Dimensions", getDimensionsDescription());
        specs.put("Front Door", frontDoorType != null ? frontDoorType : "N/A");
        specs.put("Rear Door", rearDoorType != null ? rearDoorType : "N/A");
        specs.put("Side Panels", String.valueOf(hasSidePanels));
        specs.put("Cooling", coolingType != null ? coolingType : "N/A");

        if (powerOutlets != null) {
            specs.put("Power Outlets", String.valueOf(powerOutlets));
        }
        if (powerCapacityWatts != null) {
            specs.put("Power Capacity", powerCapacityWatts + "W");
        }
        if (maxLoadWeightKg != null) {
            specs.put("Max Load", maxLoadWeightKg + " kg");
        }
        if (pduType != null) {
            specs.put("PDU Type", pduType);
        }
        if (lockType != null) {
            specs.put("Lock Type", lockType);
        }
        if (cableManagementType != null) {
            specs.put("Cable Management", cableManagementType);
        }

        return specs;
    }

    // Validation methods for configuration
    public boolean isValidFrontDoorType() {
        return frontDoorType == null || isValidDoorType(frontDoorType);
    }

    public boolean isValidRearDoorType() {
        return rearDoorType == null || isValidDoorType(rearDoorType);
    }

    private boolean isValidDoorType(String doorType) {
        return doorType != null && (
            "PERFORATED".equals(doorType) ||
            "GLASS".equals(doorType) ||
            "SOLID".equals(doorType) ||
            "NONE".equals(doorType)
        );
    }

    public boolean isValidCoolingType(String coolingType) {
        return coolingType == null || rackType == null ||
               rackType.isCoolingTypeAvailable(coolingType);
    }

    private boolean isValidPduType() {
        return pduType == null || (
            "BASIC".equals(pduType) ||
            "SWITCHED".equals(pduType) ||
            "METERED".equals(pduType) ||
            "SMART".equals(pduType)
        );
    }

    private boolean isValidLockType() {
        return lockType == null || (
            "KEY".equals(lockType) ||
            "COMBINATION".equals(lockType) ||
            "ELECTRONIC".equals(lockType) ||
            "HANDLE".equals(lockType)
        );
    }

    private boolean isValidCableManagementType() {
        return cableManagementType == null || (
            "VERTICAL_MANAGERS".equals(cableManagementType) ||
            "HORIZONTAL_MANAGERS".equals(cableManagementType) ||
            "CABLE_TRAYS".equals(cableManagementType) ||
            "FINGER_DUCTS".equals(cableManagementType)
        );
    }

    // Helper method to get active installations housed in this rack
    private List<Installation> getActiveInstallations() {
        // This would be implemented through InstallationService
        // For now, return empty list - actual implementation would query installations
        // where housing_component_id = this.id and status is active
        return List.of();
    }

    // Rack capacity and utilization methods
    public boolean hasAvailableSpace() {
        return getAvailableSpace() > 0;
    }

    public int getOccupiedSpace() {
        return getActiveInstallations().stream()
                .filter(Installation::isRackMounted)
                .mapToInt(Installation::getOccupiedRackUnits)
                .sum();
    }

    public int getAvailableSpace() {
        return rackUnitsTotal - getOccupiedSpace();
    }

    /**
     * Get all occupied rack positions
     */
    public Set<Integer> getOccupiedPositions() {
        return getActiveInstallations().stream()
                .filter(Installation::isRackMounted)
                .flatMap(inst -> inst.getOccupiedRackPositions().stream())
                .collect(Collectors.toSet());
    }

    /**
     * Check if specific rack position is available
     */
    public boolean isPositionAvailable(int position) {
        if (position < 1 || position > rackUnitsTotal) {
            return false;
        }
        return !getOccupiedPositions().contains(position);
    }

    /**
     * Check if range of positions is available for equipment of given height
     */
    public boolean isPositionRangeAvailable(int startPosition, int height) {
        if (startPosition < 1 || startPosition + height - 1 > rackUnitsTotal) {
            return false;
        }

        Set<Integer> occupied = getOccupiedPositions();
        for (int pos = startPosition; pos < startPosition + height; pos++) {
            if (occupied.contains(pos)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Find first available position for equipment of given height
     */
    public Integer findFirstAvailablePosition(int height) {
        for (int pos = 1; pos <= rackUnitsTotal - height + 1; pos++) {
            if (isPositionRangeAvailable(pos, height)) {
                return pos;
            }
        }
        return null;
    }

    /**
     * Get rack utilization percentage
     */
    public double getUtilizationPercentage() {
        if (rackUnitsTotal == 0) return 0.0;
        return (double) getOccupiedSpace() / rackUnitsTotal * 100.0;
    }

    // Enhanced rack type methods (using RackModelEntity)
    /*public boolean supportsStandardRackUnits() {
        return rackType != null && rackType.supportsStandardRackUnits();
    }*/

    /*public boolean isSuitableForNetworkEquipment() {
        return rackType != null && rackType.isSuitableForNetworkEquipment();
    }*/

    /*public boolean isSuitableForServerEquipment() {
        return rackType != null && rackType.isSuitableForServerEquipment();
    }*/

    /*public boolean isSuitableForStorageEquipment() {
        return rackType != null && rackType.isSuitableForStorageEquipment();
    }*/

    public boolean isWallMountable() {
        return rackType != null && rackType.isWallMountable();
    }

    public boolean isOutdoorRated() {
        return rackType != null && rackType.isOutdoorRated();
    }

    public boolean isEnclosed() {
        return rackType != null && rackType.isEnclosed();
    }

    public boolean isOpenFrame() {
        return rackType != null && rackType.isOpenFrame();
    }

    // Equipment compatibility validation
    /*public boolean canAccommodateEquipmentType(String equipmentTypeCode) {
        return rackType != null && rackType.isCompatibleWithEquipmentType(equipmentTypeCode);
    }*/

    public boolean canSupportWeight(double additionalWeightKg) {
        if (maxLoadWeightKg == null) return true;

        double currentWeight = getCurrentLoadWeight();
        return currentWeight + additionalWeightKg <= maxLoadWeightKg;
    }

    public boolean canSupportPowerLoad(int additionalWatts) {
        if (powerCapacityWatts == null) return true;

        int currentPowerLoad = getCurrentPowerLoad();
        return currentPowerLoad + additionalWatts <= powerCapacityWatts;
    }

    // Current load calculations (would be implemented through service layer)
    public double getCurrentLoadWeight() {
        // TODO: Calculate from installed equipment
        return 0.0;
    }

    public int getCurrentPowerLoad() {
        // TODO: Calculate from installed equipment power consumption
        return 0;
    }

    // Environmental and monitoring capabilities
    public boolean hasEnvironmentalMonitoring() {
        return hasTemperatureMonitoring || hasHumidityMonitoring;
    }

    public boolean hasAdvancedMonitoring() {
        return hasEnvironmentalMonitoring() || hasPowerMonitoring;
    }

    public boolean requiresActiveClimateControl() {
        return "ACTIVE".equalsIgnoreCase(coolingType) || "LIQUID".equalsIgnoreCase(coolingType);
    }

    // Installation requirements
    /*public boolean requiresFloorAnchoring() {
        return rackType != null && rackType.isRequiresFloorAnchoring() && !isWallMountable();
    }*/

    public boolean requiresElectricalConnection() {
        return hasPdu || hasFans || hasEnvironmentalMonitoring();
    }

    /*public List<String> getInstallationRequirements() {
        List<String> requirements = new ArrayList<>();

        if (requiresFloorAnchoring()) {
            requirements.add("Floor anchoring required");
        }
        if (requiresElectricalConnection()) {
            requirements.add("Electrical connection required");
        }
        if (rackType != null && rackType.getAssemblyTimeHours() != null && rackType.getAssemblyTimeHours() > 4) {
            requirements.add("Extended assembly time (" + rackType.getAssemblyTimeHours() + " hours)");
        }

        return requirements;
    }*/

    // Dimensions and space calculations
    public String getDimensionsDescription() {
        StringBuilder desc = new StringBuilder();

        if (actualWidthInches != null) {
            desc.append(actualWidthInches).append("\" W");
        }
        if (actualDepthInches != null) {
            if (desc.length() > 0) desc.append(" × ");
            desc.append(actualDepthInches).append("\" D");
        }
        if (actualHeightInches != null) {
            if (desc.length() > 0) desc.append(" × ");
            desc.append(actualHeightInches).append("\" H");
        } else if (rackUnitsTotal != null) {
            if (desc.length() > 0) desc.append(", ");
            desc.append(rackUnitsTotal).append("U");
        }

        return desc.toString();
    }

    // Alternative methods for compatibility
    public int getOccupiedRackUnits() {
        return getOccupiedSpace();
    }

    public int getAvailableRackUnits() {
        return getAvailableSpace();
    }

    public boolean hasAvailableRackSpace() {
        return hasAvailableSpace();
    }

    @Override
    public boolean isValidConfiguration() {
        return super.isValidConfiguration() &&
                rackUnitsTotal != null && rackUnitsTotal > 0 &&
                rackType != null &&
                (maxLoadWeightKg == null || maxLoadWeightKg > 0) &&
                (powerCapacityWatts == null || powerCapacityWatts > 0) &&
                isValidFrontDoorType() &&
                isValidRearDoorType() &&
                isValidCoolingType(coolingType) &&
                isValidPduType() &&
                isValidLockType() &&
                isValidCableManagementType();
    }

    @Override
    public String toString() {
        return "Rack:" + getId() + "[" + getName() +
               ", " + (rackType != null ? rackType.getModelDesignation() : "no-type") +
               ", " + getOccupiedSpace() + "/" + rackUnitsTotal + "U]";
    }
}