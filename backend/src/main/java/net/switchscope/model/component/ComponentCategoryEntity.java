package net.switchscope.model.component;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

/**
 * Component category entity - high-level grouping of component types
 * Examples: housing, connectivity, support, module
 */
@Entity
@Table(name = "component_categories_catalog")
@Getter
@Setter
@NoArgsConstructor
public class ComponentCategoryEntity extends UIStyledEntity {

    @Column(name = "is_system_category", nullable = false)
    private boolean systemCategory = false; // Cannot delete system categories

    // Business logic properties
    @Column(name = "is_infrastructure", nullable = false)
    private boolean infrastructure = false; // Housing/support vs active equipment

    // One-to-many relationship with component types
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComponentTypeEntity> componentTypes = new ArrayList<>();

    // Custom properties for extensibility
    @ElementCollection
    @CollectionTable(name = "component_category_properties",
                    joinColumns = @JoinColumn(name = "component_category_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> properties = new HashMap<>();

    // Constructors
    public ComponentCategoryEntity(String code, String displayName) {
        super(code, displayName);
    }

    public ComponentCategoryEntity(UUID id, String code, String displayName, String description) {
        super(id, code, displayName, description);
    }


    // Component type management
    public void addComponentType(ComponentTypeEntity componentType) {
        if (componentType != null && !componentTypes.contains(componentType)) {
            componentTypes.add(componentType);
            componentType.setCategory(this);
        }
    }

    public void removeComponentType(ComponentTypeEntity componentType) {
        if (componentType != null && componentTypes.contains(componentType)) {
            componentTypes.remove(componentType);
            componentType.setCategory(null);
        }
    }

    public List<ComponentTypeEntity> getActiveComponentTypes() {
        return componentTypes.stream()
                .filter(ComponentTypeEntity::isActive)
                .sorted(Comparator.comparing(ComponentTypeEntity::getSortOrder)
                        .thenComparing(ComponentTypeEntity::getDisplayName))
                .toList();
    }

    public boolean hasComponentTypes() {
        return !componentTypes.isEmpty();
    }

    public boolean hasActiveComponentTypes() {
        return componentTypes.stream().anyMatch(ComponentTypeEntity::isActive);
    }

    // Validation methods
    public boolean canBeDeleted() {
        return !systemCategory && !hasActiveComponentTypes();
    }

    public boolean canBeDeactivated() {
        return !systemCategory && !hasActiveComponentTypes();
    }

    // Business logic methods
    public boolean isHousingCategory() {
        return "housing".equalsIgnoreCase(getCode());
    }

    public boolean isConnectivityCategory() {
        return "connectivity".equalsIgnoreCase(getCode());
    }

    public boolean isSupportCategory() {
        return "support".equalsIgnoreCase(getCode());
    }

    public boolean isModuleCategory() {
        return "module".equalsIgnoreCase(getCode());
    }

    /**
     * Check if this category can contain components of another category
     */
    public boolean canContainCategory(ComponentCategoryEntity otherCategory) {

        // Housing can contain connectivity and support equipment
        if (isHousingCategory()) {
            return otherCategory.isConnectivityCategory() ||
                   otherCategory.isSupportCategory() ||
                   otherCategory.isModuleCategory();
        }

        // Connectivity equipment can contain modules
        if (isConnectivityCategory()) {
            return otherCategory.isModuleCategory();
        }

        return false;
    }

    /**
     * Get typical power consumption category for this category
     */
    public String getTypicalPowerConsumption() {
        return switch (getCode().toLowerCase()) {
            case "housing" -> "none";
            case "connectivity" -> "high";
            case "support" -> "variable";
            case "module" -> "medium";
            default -> "unknown";
        };
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

    /**
     * Builder pattern for easier construction
     */
    public static class Builder {
        private ComponentCategoryEntity category;

        public Builder(String code, String displayName) {
            category = new ComponentCategoryEntity(code, displayName);
        }

        public Builder description(String description) {
            category.setDescription(description);
            return this;
        }

        public Builder colorClass(String colorClass) {
            category.setColorClass(colorClass);
            return this;
        }

        public Builder iconClass(String iconClass) {
            category.setIconClass(iconClass);
            return this;
        }

        public Builder systemCategory(boolean systemCategory) {
            category.setSystemCategory(systemCategory);
            return this;
        }

        public Builder sortOrder(Integer sortOrder) {
            category.setSortOrder(sortOrder);
            return this;
        }

        public Builder infrastructure(boolean infrastructure) {
            category.setInfrastructure(infrastructure);
            return this;
        }

        public ComponentCategoryEntity build() {
            return category;
        }
    }

    @Override
    public String toString() {
        return "ComponentCategoryEntity[" + getCode() + ":" + getDisplayName() +
               " (" + componentTypes.size() + " types)]";
    }
}