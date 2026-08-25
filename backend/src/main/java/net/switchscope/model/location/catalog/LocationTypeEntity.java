package net.switchscope.model.location.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.BaseCodedEntity;
import net.switchscope.validation.NoHtml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dynamic catalog entity for location types in equipment placement hierarchy
 */
@Entity
@Table(name = "location_types_catalog",
       uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
@Getter
@Setter
@NoArgsConstructor
public class LocationTypeEntity extends BaseCodedEntity {

    /**
     * Where a type sits when nothing says otherwise - the middle, so a new type is neither a
     * campus nor a rack until someone decides. Named because the field initializer and
     * {@link #supplyHierarchyLevel()} have to agree on it.
     */
    private static final int DEFAULT_HIERARCHY_LEVEL = 50;

    // Hierarchy characteristics
    @Column(name = "hierarchy_level", nullable = false)
    @Min(1) @Max(100)
    private Integer hierarchyLevel = DEFAULT_HIERARCHY_LEVEL; // Lower numbers = higher in hierarchy

    @Column(name = "can_have_children", nullable = false)
    private boolean canHaveChildren = true;

    @Column(name = "can_hold_equipment", nullable = false)
    private boolean canHoldEquipment = true;

    @Column(name = "requires_address", nullable = false)
    private boolean requiresAddress = false;

    // Physical characteristics
    @Column(name = "is_physical_location", nullable = false)
    private boolean physicalLocation = true;

    @Column(name = "is_rack_like", nullable = false)
    private boolean rackLike = false; // For racks, cabinets, etc.

    @Column(name = "is_room_like", nullable = false)
    private boolean roomLike = false; // For rooms, offices, etc.

    @Column(name = "is_building_like", nullable = false)
    private boolean buildingLike = false; // For buildings, datacenters, etc.

    // Capacity constraints
    @Column(name = "max_children_count")
    @Min(0)
    private Integer maxChildrenCount; // Maximum number of child locations

    @Column(name = "max_equipment_count")
    @Min(0)
    private Integer maxEquipmentCount; // Maximum number of equipment items

    @Column(name = "default_rack_units")
    @Min(1) @Max(100)
    private Integer defaultRackUnits; // For rack-like locations

    // UI and presentation
    @Column(name = "color_category")
    @Size(max = 16)
    @NoHtml
    private String colorCategory = "secondary"; // success, info, warning, danger, secondary

    @Column(name = "icon_class")
    @Size(max = 64)
    @NoHtml
    private String iconClass;

    @Column(name = "map_symbol")
    @Size(max = 16)
    @NoHtml
    private String mapSymbol; // Symbol for floor plans/maps

    // Business rules
    @Column(name = "requires_access_control", nullable = false)
    private boolean requiresAccessControl = false;

    @Column(name = "requires_climate_control", nullable = false)
    private boolean requiresClimateControl = false;

    @Column(name = "requires_power_management", nullable = false)
    private boolean requiresPowerManagement = false;

    @Column(name = "requires_monitoring", nullable = false)
    private boolean requiresMonitoring = false;

    // Location type relationships - which types can be parent/child
    @ManyToMany
    @JoinTable(
        name = "location_type_hierarchy",
        joinColumns = @JoinColumn(name = "parent_type_id"),
        inverseJoinColumns = @JoinColumn(name = "child_type_id")
    )
    private Set<LocationTypeEntity> allowedChildTypes = new HashSet<>();

    @ManyToMany(mappedBy = "allowedChildTypes")
    private Set<LocationTypeEntity> allowedParentTypes = new HashSet<>();

    /**
     * Gives a location type the level its request did not carry.
     * <p>
     * The same shape as the rack's capacity and the port's six flags: the generated create mapping
     * assigns every property unconditionally, so a payload without {@code hierarchyLevel} writes
     * null over the default and the NOT NULL column refuses the insert - {@code POST
     * /api/catalogs/location-types} answered 409 for a field its own schema calls optional.
     * <p>
     * Only the boxed properties are exposed to this. A primitive cannot hold null, so MapStruct
     * guards those assignments with a null check of its own - which is why the twelve {@code
     * boolean} flags on this class have never needed one.
     */
    @PrePersist
    void supplyHierarchyLevel() {
        if (hierarchyLevel == null) {
            hierarchyLevel = DEFAULT_HIERARCHY_LEVEL;
        }
    }

    // Constructor
    public LocationTypeEntity(String code, String name, String displayName) {
        super(code, name, displayName);
    }

    public LocationTypeEntity(String code, String name, String displayName,
                             String description, Integer hierarchyLevel) {
        super(code, name, displayName, description);
        this.hierarchyLevel = hierarchyLevel;
    }

    // Hierarchy management
    public void addAllowedChildType(LocationTypeEntity childType) {
        if (childType != null && !childType.equals(this)) {
            allowedChildTypes.add(childType);
            childType.getAllowedParentTypes().add(this);
        }
    }

    public void removeAllowedChildType(LocationTypeEntity childType) {
        if (childType != null) {
            allowedChildTypes.remove(childType);
            childType.getAllowedParentTypes().remove(this);
        }
    }

    public boolean canHaveChildOfType(LocationTypeEntity childType) {
        return canHaveChildren &&
               (allowedChildTypes.isEmpty() || allowedChildTypes.contains(childType));
    }

    public boolean canBeChildOf(LocationTypeEntity parentType) {
        return parentType != null && parentType.canHaveChildOfType(this);
    }

    // Business logic methods
    public boolean isTopLevel() {
        return hierarchyLevel != null && hierarchyLevel <= 10;
    }

    public boolean isMiddleLevel() {
        return hierarchyLevel != null && hierarchyLevel > 10 && hierarchyLevel <= 50;
    }

    public boolean isBottomLevel() {
        return hierarchyLevel != null && hierarchyLevel > 50;
    }

    public boolean isVirtual() {
        return !physicalLocation;
    }

    public boolean isSpecialType() {
        return hierarchyLevel != null && hierarchyLevel >= 90; // For VIRTUAL, EXTERNAL, UNKNOWN
    }

    public boolean hasCapacityLimits() {
        return maxChildrenCount != null || maxEquipmentCount != null;
    }

    public boolean isSecureLocation() {
        return requiresAccessControl;
    }

    public boolean isDatacenterLike() {
        return requiresClimateControl && requiresPowerManagement && requiresMonitoring;
    }

    // Capacity validation
    public boolean canAcceptMoreChildren(int currentChildrenCount) {
        return !canHaveChildren || maxChildrenCount == null ||
               currentChildrenCount < maxChildrenCount;
    }

    public boolean canAcceptMoreEquipment(int currentEquipmentCount) {
        return !canHoldEquipment || maxEquipmentCount == null ||
               currentEquipmentCount < maxEquipmentCount;
    }

    // UI helpers
    public String getLocationCategory() {
        if (buildingLike) return "BUILDING";
        if (roomLike) return "ROOM";
        if (rackLike) return "RACK";
        if (!physicalLocation) return "VIRTUAL";
        return "GENERAL";
    }

    public List<LocationTypeEntity> getOrderedAllowedChildTypes() {
        return allowedChildTypes.stream()
                .sorted((t1, t2) -> {
                    int level1 = t1.getHierarchyLevel() != null ? t1.getHierarchyLevel() : 50;
                    int level2 = t2.getHierarchyLevel() != null ? t2.getHierarchyLevel() : 50;
                    return Integer.compare(level1, level2);
                })
                .toList();
    }

    // Equipment compatibility
    public boolean isCompatibleWithEquipmentType(String equipmentType) {
        // This would be implemented based on business rules
        // For now, basic logic
        if (!canHoldEquipment) return false;

        if (rackLike) {
            return "RACK_MOUNTED".equals(equipmentType) ||
                   "NETWORK_DEVICE".equals(equipmentType);
        }

        if (roomLike) {
            return !"VIRTUAL".equals(equipmentType);
        }

        return true;
    }

    @Override
    public String toString() {
        return "LocationTypeEntity:" + getId() + "[" + getCode() +
               ", " + getDisplayName() +
               ", level=" + hierarchyLevel +
               ", category=" + getLocationCategory() + "]";
    }
}