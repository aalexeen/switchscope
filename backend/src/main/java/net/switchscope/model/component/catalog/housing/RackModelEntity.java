
package net.switchscope.model.component.catalog.housing;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.BaseCodedEntity;
import net.switchscope.validation.NoHtml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dynamic catalog entity for rack types
 */
@Entity
@Table(name = "rack_types_catalog",
       uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RackModelEntity extends BaseCodedEntity {

    // Physical characteristics
    @Column(name = "standard_width_inches")
    @DecimalMin("10.0") @DecimalMax("30.0")
    private Double standardWidthInches = 19.0; // Default 19"

    @Column(name = "standard_depth_inches")
    @DecimalMin("20.0") @DecimalMax("50.0")
    private Double standardDepthInches = 24.0; // Default depth

    @Column(name = "typical_capacity")
    @Min(1) @Max(100)
    private Integer typicalCapacity = 42; // Default 42U

    @Column(name = "max_capacity")
    @Min(1) @Max(100)
    private Integer maxCapacity = 50; // Maximum possible capacity

    // Type characteristics
    @Column(name = "is_standard_rack", nullable = false)
    private boolean standardRack = true; // Supports standard rack units

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

    // Equipment suitability
    @Column(name = "suitable_for_servers", nullable = false)
    private boolean suitableForServers = true;

    @Column(name = "suitable_for_networking", nullable = false)
    private boolean suitableForNetworking = true;

    @Column(name = "suitable_for_storage", nullable = false)
    private boolean suitableForStorage = true;

    @Column(name = "suitable_for_telecom", nullable = false)
    private boolean suitableForTelecom = false;

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
    @Column(name = "has_front_door", nullable = false)
    private boolean hasFrontDoor = true;

    @Column(name = "has_rear_door", nullable = false)
    private boolean hasRearDoor = true;

    @Column(name = "has_side_panels", nullable = false)
    private boolean hasSidePanels = true;

    @Column(name = "has_locks", nullable = false)
    private boolean hasLocks = true;

    // UI representation
    @Column(name = "color_category")
    @Size(max = 16)
    @NoHtml
    private String colorCategory = "secondary";

    @Column(name = "icon_class")
    @Size(max = 64)
    @NoHtml
    private String iconClass;

    // Installation characteristics
    @Column(name = "requires_floor_anchoring", nullable = false)
    private boolean requiresFloorAnchoring = true;

    @Column(name = "requires_electrical_connection", nullable = false)
    private boolean requiresElectricalConnection = false;

    @Column(name = "requires_network_connection", nullable = false)
    private boolean requiresNetworkConnection = false;

    @Column(name = "assembly_time_hours")
    @Min(1) @Max(24)
    private Integer assemblyTimeHours = 2;

    // Door type options (dynamic)
    @ElementCollection
    @CollectionTable(
        name = "rack_type_door_options",
        joinColumns = @JoinColumn(name = "rack_type_id")
    )
    @Column(name = "door_type")
    @Size(max = 32)
    private Set<String> availableDoorTypes = new HashSet<>();

    @Column(name = "default_front_door_type")
    @Size(max = 32)
    @NoHtml
    private String defaultFrontDoorType = "PERFORATED";

    @Column(name = "default_rear_door_type")
    @Size(max = 32)
    @NoHtml
    private String defaultRearDoorType = "PERFORATED";

    // Cooling type options (dynamic)
    @ElementCollection
    @CollectionTable(
        name = "rack_type_cooling_options",
        joinColumns = @JoinColumn(name = "rack_type_id")
    )
    @Column(name = "cooling_type")
    @Size(max = 32)
    private Set<String> availableCoolingTypes = new HashSet<>();

    @Column(name = "default_cooling_type")
    @Size(max = 32)
    @NoHtml
    private String defaultCoolingType = "PASSIVE";

    // PDU type options (dynamic)
    @ElementCollection
    @CollectionTable(
        name = "rack_type_pdu_options",
        joinColumns = @JoinColumn(name = "rack_type_id")
    )
    @Column(name = "pdu_type")
    @Size(max = 64)
    private Set<String> availablePduTypes = new HashSet<>();

    // Lock type options (dynamic)
    @ElementCollection
    @CollectionTable(
        name = "rack_type_lock_options",
        joinColumns = @JoinColumn(name = "rack_type_id")
    )
    @Column(name = "lock_type")
    @Size(max = 32)
    private Set<String> availableLockTypes = new HashSet<>();

    // Cable management options (dynamic)
    @ElementCollection
    @CollectionTable(
        name = "rack_type_cable_management_options",
        joinColumns = @JoinColumn(name = "rack_type_id")
    )
    @Column(name = "cable_management_type")
    @Size(max = 64)
    private Set<String> availableCableManagementTypes = new HashSet<>();

    // Compatible equipment types (many-to-many with component types that can be installed)
    @ElementCollection
    @CollectionTable(
        name = "rack_type_compatible_equipment",
        joinColumns = @JoinColumn(name = "rack_type_id")
    )
    @Column(name = "equipment_type_code")
    @Size(max = 64)
    private Set<String> compatibleEquipmentTypes = new HashSet<>();

    // Rack accessories and options
    @ElementCollection
    @CollectionTable(
        name = "rack_type_standard_accessories",
        joinColumns = @JoinColumn(name = "rack_type_id")
    )
    @Column(name = "accessory_name")
    @Size(max = 128)
    private Set<String> standardAccessories = new HashSet<>();

    @ElementCollection
    @CollectionTable(
        name = "rack_type_optional_accessories",
        joinColumns = @JoinColumn(name = "rack_type_id")
    )
    @Column(name = "accessory_name")
    @Size(max = 128)
    private Set<String> optionalAccessories = new HashSet<>();

    // Constructor
    public RackModelEntity(String code, String name, String displayName) {
        super(code, name, displayName);
        initializeDefaultOptions();
    }

    public RackModelEntity(String code, String name, String displayName,
                         String description, Double standardWidthInches) {
        super(code, name, displayName, description);
        this.standardWidthInches = standardWidthInches;
        initializeDefaultOptions();
    }

    // Initialize default configuration options
    private void initializeDefaultOptions() {
        // Default door types
        availableDoorTypes.add("PERFORATED");
        availableDoorTypes.add("GLASS");
        availableDoorTypes.add("SOLID");
        availableDoorTypes.add("SPLIT");
        availableDoorTypes.add("NONE");

        // Default cooling types
        availableCoolingTypes.add("PASSIVE");
        availableCoolingTypes.add("ACTIVE");
        availableCoolingTypes.add("LIQUID");
        availableCoolingTypes.add("HYBRID");

        // Default PDU types
        availablePduTypes.add("BASIC");
        availablePduTypes.add("SWITCHED");
        availablePduTypes.add("METERED");
        availablePduTypes.add("SMART");

        // Default lock types
        availableLockTypes.add("KEY");
        availableLockTypes.add("COMBINATION");
        availableLockTypes.add("ELECTRONIC");
        availableLockTypes.add("HANDLE");

        // Default cable management types
        availableCableManagementTypes.add("VERTICAL_MANAGERS");
        availableCableManagementTypes.add("HORIZONTAL_MANAGERS");
        availableCableManagementTypes.add("CABLE_TRAYS");
        availableCableManagementTypes.add("FINGER_DUCTS");
    }

    // Door type management
    public void addAvailableDoorType(String doorType) {
        if (doorType != null && !doorType.trim().isEmpty()) {
            availableDoorTypes.add(doorType.toUpperCase());
        }
    }

    public void removeAvailableDoorType(String doorType) {
        availableDoorTypes.remove(doorType.toUpperCase());
    }

    public boolean isDoorTypeAvailable(String doorType) {
        return doorType != null && availableDoorTypes.contains(doorType.toUpperCase());
    }

    // Cooling type management
    public void addAvailableCoolingType(String coolingType) {
        if (coolingType != null && !coolingType.trim().isEmpty()) {
            availableCoolingTypes.add(coolingType.toUpperCase());
        }
    }

    public void removeAvailableCoolingType(String coolingType) {
        availableCoolingTypes.remove(coolingType.toUpperCase());
    }

    public boolean isCoolingTypeAvailable(String coolingType) {
        return coolingType != null && availableCoolingTypes.contains(coolingType.toUpperCase());
    }

    // PDU type management
    public void addAvailablePduType(String pduType) {
        if (pduType != null && !pduType.trim().isEmpty()) {
            availablePduTypes.add(pduType.toUpperCase());
        }
    }

    public boolean isPduTypeAvailable(String pduType) {
        return pduType != null && availablePduTypes.contains(pduType.toUpperCase());
    }

    // Lock type management
    public void addAvailableLockType(String lockType) {
        if (lockType != null && !lockType.trim().isEmpty()) {
            availableLockTypes.add(lockType.toUpperCase());
        }
    }

    public boolean isLockTypeAvailable(String lockType) {
        return lockType != null && availableLockTypes.contains(lockType.toUpperCase());
    }

    // Cable management type management
    public void addAvailableCableManagementType(String cableManagementType) {
        if (cableManagementType != null && !cableManagementType.trim().isEmpty()) {
            availableCableManagementTypes.add(cableManagementType.toUpperCase());
        }
    }

    public boolean isCableManagementTypeAvailable(String cableManagementType) {
        return cableManagementType != null &&
               availableCableManagementTypes.contains(cableManagementType.toUpperCase());
    }

    // Business logic methods
    public boolean supportsStandardRackUnits() {
        return standardRack;
    }

    public boolean isSuitableForNetworkEquipment() {
        return suitableForNetworking;
    }

    public boolean isSuitableForServerEquipment() {
        return suitableForServers;
    }

    public boolean isSuitableForStorageEquipment() {
        return suitableForStorage;
    }

    public boolean isSuitableForTelecomEquipment() {
        return suitableForTelecom;
    }

    public boolean isOutdoorSuitable() {
        return outdoorRated;
    }

    public boolean isCompact() {
        return typicalCapacity != null && typicalCapacity <= 12;
    }

    public boolean isFullHeight() {
        return typicalCapacity != null && typicalCapacity >= 42;
    }

    public boolean isHalfHeight() {
        return typicalCapacity != null && typicalCapacity > 12 && typicalCapacity < 42;
    }

    // Equipment compatibility
    public boolean isCompatibleWithEquipmentType(String equipmentTypeCode) {
        return compatibleEquipmentTypes.isEmpty() || 
               compatibleEquipmentTypes.contains(equipmentTypeCode);
    }

    public void addCompatibleEquipmentType(String equipmentTypeCode) {
        if (equipmentTypeCode != null) {
            compatibleEquipmentTypes.add(equipmentTypeCode);
        }
    }

    public void removeCompatibleEquipmentType(String equipmentTypeCode) {
        compatibleEquipmentTypes.remove(equipmentTypeCode);
    }

    // Capacity validation
    public boolean canAccommodateCapacity(int requestedCapacity) {
        return maxCapacity == null || requestedCapacity <= maxCapacity;
    }

    public boolean canSupportWeight(double weightKg) {
        return maxWeightCapacityKg == null || weightKg <= maxWeightCapacityKg;
    }

    public boolean canSupportPower(int powerWatts) {
        return maxPowerCapacityWatts == null || powerWatts <= maxPowerCapacityWatts;
    }

    // Installation requirements
    public boolean requiresSpecialInstallation() {
        return requiresFloorAnchoring || requiresElectricalConnection || 
               requiresNetworkConnection;
    }

    public List<String> getInstallationRequirements() {
        List<String> requirements = new ArrayList<>();
        
        if (requiresFloorAnchoring) {
            requirements.add("Floor anchoring required");
        }
        if (requiresElectricalConnection) {
            requirements.add("Electrical connection required");
        }
        if (requiresNetworkConnection) {
            requirements.add("Network connection required");
        }
        if (assemblyTimeHours != null && assemblyTimeHours > 4) {
            requirements.add("Extended assembly time (" + assemblyTimeHours + " hours)");
        }
        
        return requirements;
    }

    // Accessory management
    public void addStandardAccessory(String accessory) {
        if (accessory != null) {
            standardAccessories.add(accessory);
        }
    }

    public void addOptionalAccessory(String accessory) {
        if (accessory != null) {
            optionalAccessories.add(accessory);
        }
    }

    public boolean hasStandardAccessory(String accessory) {
        return standardAccessories.contains(accessory);
    }

    public boolean hasOptionalAccessory(String accessory) {
        return optionalAccessories.contains(accessory);
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
        
        if (typicalCapacity != null) {
            if (desc.length() > 0) desc.append(", ");
            desc.append(typicalCapacity).append("U capacity");
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
    public String toString() {
        return "RackTypeEntity:" + getId() + "[" + getCode() + 
               ", " + getDisplayName() + 
               ", " + getDimensionsDescription() + "]";
    }
}