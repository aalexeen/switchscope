package net.switchscope.model.component;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

/**
 * Component type entity - specific types within categories
 * Examples: SWITCH, ROUTER (in connectivity), RACK, CHASSIS (in housing)
 */
@Entity
@Table(name = "component_types_catalog",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"code", "category_id"}, name = "uk_code_category")})
@Getter
@Setter
@NoArgsConstructor
public class ComponentTypeEntity extends UIStyledEntity {

    // FK to ComponentCategoryEntity
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull
    private ComponentCategoryEntity category;

    @Column(name = "is_system_type", nullable = false)
    private boolean systemType = false; // Cannot delete system types

    // Physical properties
    @Column(name = "requires_rack_space", nullable = false)
    private boolean requiresRackSpace = false;

    @Column(name = "typical_rack_units")
    private Integer typicalRackUnits;

    /*@Column(name = "requires_physical_space", nullable = false)
    private boolean requiresPhysicalSpace = true;*/

    @Column(name = "can_contain_components", nullable = false)
    private boolean canContainComponents = false;

    @Column(name = "is_installable", nullable = false)
    private boolean installable = true;

    // Technical properties
    @Column(name = "requires_management", nullable = false)
    private boolean requiresManagement = false;

    @Column(name = "supports_snmp", nullable = false)
    private boolean supportsSnmp = false;

    @Column(name = "has_firmware", nullable = false)
    private boolean hasFirmware = false;

    @Column(name = "can_have_ip_address", nullable = false)
    private boolean canHaveIpAddress = false;

    @Column(name = "processes_network_traffic", nullable = false)
    private boolean processesNetworkTraffic = false;

    // Power and cooling
    @Column(name = "requires_power", nullable = false)
    private boolean requiresPower = false;

    @Column(name = "typical_power_consumption_watts")
    private Integer typicalPowerConsumptionWatts;

    @Column(name = "generates_heat", nullable = false)
    private boolean generatesHeat = false;

    @Column(name = "needs_cooling", nullable = false)
    private boolean needsCooling = false;

    // Maintenance
    @Column(name = "recommended_maintenance_interval_months")
    private Integer recommendedMaintenanceIntervalMonths;

    @Column(name = "typical_lifespan_years")
    private Integer typicalLifespanYears;

    // Containment rules (for housing components)
    @ElementCollection
    @CollectionTable(name = "component_type_allowed_child_types",
                    joinColumns = @JoinColumn(name = "parent_type_id"))
    @Column(name = "child_type_code")
    private Set<String> allowedChildTypeCodes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "component_type_allowed_child_categories",
                    joinColumns = @JoinColumn(name = "parent_type_id"))
    @Column(name = "child_category_code")
    private Set<String> allowedChildCategoryCodes = new HashSet<>();

    // Custom properties for extensibility
    @ElementCollection
    @CollectionTable(name = "component_type_properties",
                    joinColumns = @JoinColumn(name = "component_type_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> properties = new HashMap<>();

    // Constructors
    public ComponentTypeEntity(String code, String displayName, ComponentCategoryEntity category) {
        super(code, displayName);
        this.category = category;
    }

    public ComponentTypeEntity(UUID id, String code, String displayName, ComponentCategoryEntity category, String description) {
        super(id, code, displayName, description);
        this.category = category;
    }


    // Category delegation methods
    public boolean isHousingComponent() {
        return category != null && category.isHousingCategory();
    }

    public boolean isConnectivityComponent() {
        return category != null && category.isConnectivityCategory();
    }

    public boolean isSupportComponent() {
        return category != null && category.isSupportCategory();
    }

    public boolean isModuleComponent() {
        return category != null && category.isModuleCategory();
    }

    // Physical space methods
    public boolean canBeMountedInRack() {
        return requiresRackSpace;
    }

    public int getTypicalRackUnits() {
        return typicalRackUnits != null ? typicalRackUnits : 0;
    }

    public String getComponentTypeName() {
        return getCode() != null ? getCode() : getDisplayName();
    }

    // Networking equipment check
    public boolean isNetworkingEquipment() {
        return isConnectivityComponent() &&
               ("SWITCH".equals(getCode()) || "ROUTER".equals(getCode()) || "PATCH_PANEL".equals(getCode()));
    }

    // Containment logic
    public void addAllowedChildTypeCode(String childTypeCode) {
        if (childTypeCode != null && !childTypeCode.trim().isEmpty()) {
            allowedChildTypeCodes.add(childTypeCode);
        }
    }

    public void addAllowedChildCategoryCode(String childCategoryCode) {
        if (childCategoryCode != null && !childCategoryCode.trim().isEmpty()) {
            allowedChildCategoryCodes.add(childCategoryCode);
        }
    }

    public boolean canContainType(ComponentTypeEntity childType) {
        if (!canContainComponents || childType == null) {
            return false;
        }

        // Check specific type codes
        if (allowedChildTypeCodes.contains(childType.getCode())) {
            return true;
        }

        // Check category codes
        if (childType.getCategory() != null &&
            allowedChildCategoryCodes.contains(childType.getCategory().getCode())) {
            return true;
        }

        // Default rules based on categories
        return category != null && category.canContainCategory(childType.getCategory());
    }

    public boolean canContainTypeCode(String childTypeCode) {
        return canContainComponents && allowedChildTypeCodes.contains(childTypeCode);
    }

    public boolean canContainCategoryCode(String childCategoryCode) {
        return canContainComponents && allowedChildCategoryCodes.contains(childCategoryCode);
    }

    // Validation
    public boolean canBeDeleted() {
        return !systemType && isActive();
    }

    public boolean isValidConfiguration() {
        boolean basicValid = getCode() != null && !getCode().trim().isEmpty() &&
                           getDisplayName() != null && !getDisplayName().trim().isEmpty() &&
                           category != null;

        if (!basicValid) {
            return false;
        }

        // Rack space validation
        if (requiresRackSpace && (typicalRackUnits == null || typicalRackUnits <= 0)) {
            return false;
        }

        // Management validation
        if (requiresManagement && !canHaveIpAddress) {
            return false; // Managed components typically need IP addresses
        }

        return true;
    }

    // Property management
    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    // Maintenance and lifecycle
    public int getRecommendedMaintenanceIntervalMonths() {
        if (recommendedMaintenanceIntervalMonths != null) {
            return recommendedMaintenanceIntervalMonths;
        }

        // Default based on component characteristics
        if (requiresManagement || processesNetworkTraffic) {
            return 6; // Active equipment needs more frequent maintenance
        }

        if (isHousingComponent()) {
            return 24; // Housing needs less frequent maintenance
        }

        return 12; // Default
    }

    public int getTypicalLifespanYears() {
        if (typicalLifespanYears != null) {
            return typicalLifespanYears;
        }

        // Default based on component type
        if (isNetworkingEquipment()) {
            return 7; // Network equipment typical lifespan
        }

        if (isHousingComponent()) {
            return 15; // Housing infrastructure lasts longer
        }

        return 10; // Default
    }

    // Power consumption
    public String getPowerConsumptionCategory() {
        if (typicalPowerConsumptionWatts != null) {
            if (typicalPowerConsumptionWatts == 0) return "none";
            if (typicalPowerConsumptionWatts < 50) return "low";
            if (typicalPowerConsumptionWatts < 200) return "medium";
            if (typicalPowerConsumptionWatts < 500) return "high";
            return "very_high";
        }

        // Default based on category
        return category != null ? category.getTypicalPowerConsumption() : "unknown";
    }

    /**
     * Builder pattern for easier construction
     */
    public static class Builder {
        private ComponentTypeEntity type;

        public Builder(String code, String displayName, ComponentCategoryEntity category) {
            type = new ComponentTypeEntity(code, displayName, category);
        }

        public Builder description(String description) {
            type.setDescription(description);
            return this;
        }

        public Builder iconClass(String iconClass) {
            type.setIconClass(iconClass);
            return this;
        }

        public Builder colorClass(String colorClass) {
            type.setColorClass(colorClass);
            return this;
        }

        public Builder systemType(boolean systemType) {
            type.setSystemType(systemType);
            return this;
        }

        public Builder requiresRackSpace(boolean requires, Integer rackUnits) {
            type.setRequiresRackSpace(requires);
            type.setTypicalRackUnits(rackUnits);
            return this;
        }

        public Builder canContainComponents(boolean canContain) {
            type.setCanContainComponents(canContain);
            return this;
        }

        public Builder requiresManagement(boolean requires) {
            type.setRequiresManagement(requires);
            return this;
        }

        public Builder networkingCapabilities(boolean supportsSnmp, boolean hasFirmware,
                                            boolean canHaveIp, boolean processesTraffic) {
            type.setSupportsSnmp(supportsSnmp);
            type.setHasFirmware(hasFirmware);
            type.setCanHaveIpAddress(canHaveIp);
            type.setProcessesNetworkTraffic(processesTraffic);
            return this;
        }

        public Builder powerAndCooling(boolean requiresPower, Integer powerConsumption,
                                     boolean generatesHeat, boolean needsCooling) {
            type.setRequiresPower(requiresPower);
            type.setTypicalPowerConsumptionWatts(powerConsumption);
            type.setGeneratesHeat(generatesHeat);
            type.setNeedsCooling(needsCooling);
            return this;
        }

        public Builder maintenance(Integer maintenanceInterval, Integer lifespan) {
            type.setRecommendedMaintenanceIntervalMonths(maintenanceInterval);
            type.setTypicalLifespanYears(lifespan);
            return this;
        }

        public Builder allowedChildTypes(Set<String> childTypeCodes) {
            type.setAllowedChildTypeCodes(new HashSet<>(childTypeCodes));
            return this;
        }

        public Builder allowedChildCategories(Set<String> childCategoryCodes) {
            type.setAllowedChildCategoryCodes(new HashSet<>(childCategoryCodes));
            return this;
        }

        public ComponentTypeEntity build() {
            return type;
        }
    }

    @Override
    public String toString() {
        return "ComponentTypeEntity[" + getCode() + ":" + getDisplayName() +
               " (" + (category != null ? category.getCode() : "no-category") + ")]";
    }
}