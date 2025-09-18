
package net.switchscope.model.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.NamedEntity;
import net.switchscope.model.location.catalog.LocationTypeEntity;
import net.switchscope.validation.NoHtml;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location extends NamedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_type_id", nullable = false)
    @NotNull
    private LocationTypeEntity type;

    @Column(name = "address")
    @Size(max = 512)
    @NoHtml
    private String address;

    // Self-reference for parent-child hierarchy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_location_id")
    @JsonIgnore // Avoid circular references in JSON
    private Location parentLocation;

    @OneToMany(mappedBy = "parentLocation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> childLocations = new ArrayList<>();

    // Additional location-specific fields
    @Column(name = "capacity_children")
    private Integer capacityChildren; // Override type default if needed

    @Column(name = "capacity_equipment")
    private Integer capacityEquipment; // Override type default if needed

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "room_number")
    @Size(max = 32)
    @NoHtml
    private String roomNumber;

    @Column(name = "coordinates")
    @Size(max = 128)
    @NoHtml
    private String coordinates; // GPS coordinates or building coordinates

    @Column(name = "access_requirements")
    @Size(max = 256)
    @NoHtml
    private String accessRequirements; // Access card, key, etc.

    // Climate and environment
    @Column(name = "temperature_range")
    @Size(max = 64)
    @NoHtml
    private String temperatureRange; // e.g., "18-24°C"

    @Column(name = "humidity_range")
    @Size(max = 64)
    @NoHtml
    private String humidityRange; // e.g., "45-55%"

    // Power and infrastructure
    @Column(name = "power_capacity_watts")
    private Integer powerCapacityWatts;

    @Column(name = "available_rack_units")
    private Integer availableRackUnits; // For rack-like locations

    @Column(name = "has_ups", nullable = false)
    private boolean hasUps = false;

    @Column(name = "has_generator", nullable = false)
    private boolean hasGenerator = false;

    // Constructors
    public Location(UUID id, String name, LocationTypeEntity type) {
        this(id, name, type, null, null);
    }

    public Location(UUID id, String name, LocationTypeEntity type, String address) {
        this(id, name, type, address, null);
    }

    public Location(UUID id, String name, LocationTypeEntity type, String address, Location parentLocation) {
        super(id, name, null);
        this.type = type;
        this.address = address;
        this.parentLocation = parentLocation;
    }

    // Child location management
    public void addChildLocation(Location child) {
        if (child != null && canAcceptChild(child)) {
            childLocations.add(child);
            child.setParentLocation(this);
        }
    }

    public void removeChildLocation(Location child) {
        if (child != null) {
            childLocations.remove(child);
            child.setParentLocation(null);
        }
    }

    // Validation methods
    public boolean canAcceptChild(Location child) {
        if (child == null || !type.isCanHaveChildren()) {
            return false;
        }

        // Check if this type can have children of the child's type
        if (!type.canHaveChildOfType(child.getType())) {
            return false;
        }

        // Check capacity limits
        int effectiveCapacity = capacityChildren != null ?
                               capacityChildren :
                               (type.getMaxChildrenCount() != null ? type.getMaxChildrenCount() : Integer.MAX_VALUE);

        return childLocations.size() < effectiveCapacity;
    }

    public boolean canAcceptEquipment() {
        return type.isCanHoldEquipment();
    }

    public boolean isValidHierarchy() {
        if (parentLocation == null) {
            return true; // Root location
        }

        return type.canBeChildOf(parentLocation.getType());
    }

    // Helper methods
    public boolean isRootLocation() {
        return parentLocation == null;
    }

    public boolean hasChildren() {
        return !childLocations.isEmpty();
    }

    public boolean isPhysical() {
        return type.isPhysicalLocation();
    }

    public boolean isVirtual() {
        return type.isVirtual();
    }

    public boolean isRackLike() {
        return type.isRackLike();
    }

    public boolean isRoomLike() {
        return type.isRoomLike();
    }

    public boolean isBuildingLike() {
        return type.isBuildingLike();
    }

    public boolean requiresAddress() {
        return type.isRequiresAddress();
    }

    public boolean requiresAccessControl() {
        return type.isRequiresAccessControl();
    }

    public boolean requiresClimateControl() {
        return type.isRequiresClimateControl();
    }

    public boolean requiresPowerManagement() {
        return type.isRequiresPowerManagement();
    }

    public boolean requiresMonitoring() {
        return type.isRequiresMonitoring();
    }

    // Enhanced path and hierarchy methods
    /**
     * Get full path from root to current location
     * @return string like "Building/Floor1/Room101"
     */
    public String getFullPath() {
        if (parentLocation == null) {
            return name;
        }
        return parentLocation.getFullPath() + "/" + name;
    }

    /**
     * Get full path with types
     * @return string like "Building(Main Building)/Floor(Floor1)/Room(Room101)"
     */
    public String getFullPathWithTypes() {
        String pathElement = type.getDisplayName() + "(" + name + ")";
        if (parentLocation == null) {
            return pathElement;
        }
        return parentLocation.getFullPathWithTypes() + "/" + pathElement;
    }

    /**
     * Get nesting level (0 - root location)
     */
    public int getLevel() {
        if (parentLocation == null) {
            return 0;
        }
        return parentLocation.getLevel() + 1;
    }

    public int getHierarchyLevel() {
        return type.getHierarchyLevel() != null ? type.getHierarchyLevel() : 50;
    }

    public String getLocationCategory() {
        return type.getLocationCategory();
    }

    // Capacity management
    public int getChildrenCapacity() {
        return capacityChildren != null ? capacityChildren :
               (type.getMaxChildrenCount() != null ? type.getMaxChildrenCount() : Integer.MAX_VALUE);
    }

    public int getEquipmentCapacity() {
        return capacityEquipment != null ? capacityEquipment :
               (type.getMaxEquipmentCount() != null ? type.getMaxEquipmentCount() : Integer.MAX_VALUE);
    }

    public int getAvailableChildrenSlots() {
        return Math.max(0, getChildrenCapacity() - childLocations.size());
    }

    /**
     * Get available equipment slots based on capacity settings.
     * Actual equipment count should be retrieved through InstallationService.
     */
    public int getAvailableEquipmentSlots(int currentEquipmentCount) {
        return Math.max(0, getEquipmentCapacity() - currentEquipmentCount);
    }

    // Rack-specific methods
    public int getTotalRackUnits() {
        if (!isRackLike()) return 0;
        return availableRackUnits != null ? availableRackUnits :
               (type.getDefaultRackUnits() != null ? type.getDefaultRackUnits() : 42);
    }

    /**
     * Calculate used rack units based on installations.
     * This should be calculated through InstallationService.
     */
    public int getAvailableRackUnits(int usedRackUnits) {
        return Math.max(0, getTotalRackUnits() - usedRackUnits);
    }

    // Environment status
    public boolean hasAdequatePower(Integer usedPowerWatts) {
        if (powerCapacityWatts == null) return true;
        if (usedPowerWatts == null) return true;
        return usedPowerWatts <= powerCapacityWatts;
    }

    public boolean hasBackupPower() {
        return hasUps || hasGenerator;
    }

    // Service layer integration points
    /**
     * Check if location can accept equipment installation.
     * Actual equipment count should be provided by InstallationService.
     */
    public boolean canAcceptEquipmentInstallation(int currentEquipmentCount) {
        if (!canAcceptEquipment()) {
            return false;
        }

        return getAvailableEquipmentSlots(currentEquipmentCount) > 0;
    }

    /**
     * Check if location can accept rack-mounted equipment.
     * Used rack units should be provided by InstallationService.
     */
    public boolean canAcceptRackInstallation(int usedRackUnits, int requiredRackUnits) {
        if (!isRackLike()) {
            return false;
        }

        return getAvailableRackUnits(usedRackUnits) >= requiredRackUnits;
    }

    @Override
    public String toString() {
        return "Location:" + id + "[" + name + ", " +
               (type != null ? type.getDisplayName() : "null") +
               ", level=" + getLevel() + "]";
    }
}