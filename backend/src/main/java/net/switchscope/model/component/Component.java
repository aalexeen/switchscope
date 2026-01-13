package net.switchscope.model.component;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.NamedEntity;
import net.switchscope.model.installation.Installation;
import net.switchscope.model.location.Location;
import net.switchscope.validation.NoHtml;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract base class for all network infrastructure components
 * Updated to work with strict ComponentTypeEntity and ComponentStatusEntity references
 */
@Entity
@Table(name = "components",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"manufacturer", "serial_number"}, name = "uk_manufacturer_serial")})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "component_class", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class Component extends NamedEntity {

    @Column(name = "manufacturer", nullable = false)
    @Size(max = 128)
    @NoHtml
    private String manufacturer;

    @Column(name = "model")
    @Size(max = 128)
    @NoHtml
    private String model;

    @Column(name = "serial_number", nullable = false)
    @Size(max = 128)
    @NoHtml
    private String serialNumber;

    @Column(name = "part_number")
    @Size(max = 128)
    @NoHtml
    private String partNumber;

    // FK to ComponentStatusEntity (replacing enum)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "component_status_id", nullable = false)
    @NotNull
    private ComponentStatusEntity componentStatus;

    // FK to ComponentTypeEntity
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "component_type_id", nullable = false)
    @NotNull
    private ComponentTypeEntity componentType;

    // FK to ComponentNatureEntity (optional)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "component_nature_id")
    private ComponentNatureEntity componentNature;

    @Column(name = "last_maintenance_date")
    private LocalDateTime lastMaintenanceDate;

    @Column(name = "next_maintenance_date")
    private LocalDateTime nextMaintenanceDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installation_id")
    private Installation installation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_component_id")
    private Component parentComponent;

    @OneToMany(mappedBy = "parentComponent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Component> childComponents = new ArrayList<>();

    // Constructors
    public Component(UUID id, String name, ComponentTypeEntity componentType) {
        super(id, name, null);
        this.componentType = componentType;
    }

    public Component(UUID id, String name, String manufacturer, String model,
            String serialNumber, ComponentTypeEntity componentType) {
        super(id, name, null);
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.componentType = componentType;
    }

    public Component(UUID id, String name, String manufacturer, String model,
            String serialNumber, ComponentStatusEntity status,
            ComponentTypeEntity componentType) {
        super(id, name, null);
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.componentStatus = status;
        this.componentType = componentType;
    }


    // Component relationships
    public void addChildComponent(Component child) {
        if (child != null && canContainComponent(child)) {
            childComponents.add(child);
            child.setParentComponent(this);
        }
    }

    public void removeChildComponent(Component child) {
        if (child != null) {
            childComponents.remove(child);
            child.setParentComponent(null);
        }
    }

    // Methods delegating to ComponentTypeEntity
    public boolean canHoldOtherComponents() {
        return componentType == null || !componentType.isCanContainComponents();
    }

/*    public boolean requiresPhysicalSpace() {
        return componentType != null && componentType.isRequiresPhysicalSpace();
    }*/

    public boolean requiresRackSpace() {
        return componentType != null && componentType.isRequiresRackSpace();
    }

    public boolean canBeMountedInRack() {
        return requiresRackSpace();
    }

    public int getTypicalRackUnits() {
        return componentType != null ? componentType.getTypicalRackUnits() : 0;
    }

    public boolean requiresManagement() {
        return componentType != null && componentType.isRequiresManagement();
    }

    public boolean supportsSnmpMonitoring() {
        return componentType != null && componentType.isSupportsSnmp();
    }

    public boolean hasFirmware() {
        return componentType != null && componentType.isHasFirmware();
    }

    public boolean canHaveIpAddress() {
        return componentType != null && componentType.isCanHaveIpAddress();
    }

    public boolean processesNetworkTraffic() {
        return componentType != null && componentType.isProcessesNetworkTraffic();
    }

    public boolean requiresPower() {
        return componentType != null && componentType.isRequiresPower();
    }

    public boolean generatesHeat() {
        return componentType != null && componentType.isGeneratesHeat();
    }

    public boolean needsCooling() {
        return componentType != null && componentType.isNeedsCooling();
    }

    // Category helper methods
    public boolean isHousingComponent() {
        return componentType != null && componentType.isHousingComponent();
    }

    public boolean isConnectivityComponent() {
        return componentType != null && componentType.isConnectivityComponent();
    }

    public boolean isSupportComponent() {
        return componentType != null && componentType.isSupportComponent();
    }

    public boolean isModuleComponent() {
        return componentType != null && componentType.isModuleComponent();
    }

    public boolean isNetworkingEquipment() {
        return componentType != null && componentType.isNetworkingEquipment();
    }

    // Abstract methods that must be implemented by concrete components
    public abstract boolean isInstallable();

    public abstract Map<String, String> getSpecifications();

    // Status-related methods
    public boolean isOperational() {
        return componentStatus != null && componentStatus.isOperational();
    }

    public boolean requiresMaintenance() {
        return (componentStatus != null && "MAINTENANCE".equals(componentStatus.getCode())) ||
                (nextMaintenanceDate != null && nextMaintenanceDate.isBefore(LocalDateTime.now()));
    }

    public boolean isAvailable() {
        return componentStatus != null && componentStatus.isAvailable();
    }

    public boolean requiresAttention() {
        return componentStatus != null && componentStatus.requiresAttention();
    }

    // Hierarchy methods
    public boolean hasChildren() {
        return !childComponents.isEmpty();
    }

    public boolean isRootComponent() {
        return parentComponent == null;
    }

    public String getComponentPath() {
        if (parentComponent == null) {
            return name;
        }
        return parentComponent.getComponentPath() + "/" + name;
    }

    public int getLevel() {
        if (parentComponent == null) {
            return 0;
        }
        return parentComponent.getLevel() + 1;
    }

    // Enhanced validation that considers component type
    public boolean isValidConfiguration() {
        boolean basicValid = name != null && !name.trim().isEmpty() &&
                componentStatus != null &&
                componentType != null;

        if (!basicValid) {
            return false;
        }

        // Additional validation based on component requirements
        if (requiresManagement()) {
            // Managed components should have more detailed information
            return manufacturer != null && !manufacturer.trim().isEmpty() &&
                    model != null && !model.trim().isEmpty();
        }

        return true;
    }

    // Enhanced containment logic
    public boolean canContainComponent(Component child) {
        if (canHoldOtherComponents() || child == null || child.getComponentType() == null) {
            return false;
        }

        return componentType.canContainType(child.getComponentType());
    }

    public boolean canContainComponentType(ComponentTypeEntity childType) {
        if (canHoldOtherComponents() || childType == null) {
            return false;
        }

        return componentType.canContainType(childType);
    }

    // Maintenance methods
    public int getRecommendedMaintenanceIntervalMonths() {
        return componentType != null ?
                componentType.getRecommendedMaintenanceIntervalMonths() : 12;
    }

    public int getTypicalLifespanYears() {
        return componentType != null ?
                componentType.getTypicalLifespanYears() : 10;
    }

    // Power consumption
    public String getPowerConsumptionCategory() {
        return componentType != null ?
                componentType.getPowerConsumptionCategory() : "unknown";
    }

    public Integer getTypicalPowerConsumptionWatts() {
        return componentType != null ?
                componentType.getTypicalPowerConsumptionWatts() : null;
    }

    // Status transition
    public boolean canTransitionToStatus(ComponentStatusEntity newStatus) {
        return newStatus != null && componentStatus != null &&
               componentStatus.canTransitionTo(newStatus.getCode());
    }

    public List<String> getNextPossibleStatusCodes() {
        if (componentStatus != null) {
            return new ArrayList<>(componentStatus.getNextPossibleStatusCodes());
        }
        return new ArrayList<>();
    }

    // UI helpers
    public String getStatusColorClass() {
        return componentStatus != null ? componentStatus.getColorClass() : "secondary";
    }

    public String getTypeIconClass() {
        return componentType != null ? componentType.getIconClass() : null;
    }

    public String getTypeColorClass() {
        return componentType != null ? componentType.getColorClass() : null;
    }

    // Enhanced component description
    public String getDetailedDescription() {
        StringBuilder desc = new StringBuilder();

        if (componentType != null) {
            desc.append(componentType.getDisplayName());
            if (componentType.getCategory() != null) {
                desc.append(" (").append(componentType.getCategory().getDisplayName()).append(")");
            }
        }

        if (manufacturer != null) {
            desc.append(" - ").append(manufacturer);
        }

        if (model != null) {
            desc.append(" ").append(model);
        }

        if (componentStatus != null) {
            desc.append(" [").append(componentStatus.getDisplayName()).append("]");
        }

        return desc.toString();
    }

    // Backward compatibility methods
    @Transient
    public String getComponentTypeCode() {
        return componentType != null ? componentType.getCode() : null;
    }

    @Transient
    public String getComponentCategoryCode() {
        return componentType != null && componentType.getCategory() != null ?
                componentType.getCategory().getCode() : null;
    }

    @Transient
    public String getStatusCode() {
        return componentStatus != null ? componentStatus.getCode() : null;
    }

    @Transient
    public String getComponentNatureCode() {
        return componentNature != null ? componentNature.getCode() : null;
    }

    // Get component type based on discriminator
    @Transient
    public String getDiscriminatorValue() {
        DiscriminatorValue discriminator = this.getClass().getAnnotation(DiscriminatorValue.class);
        return discriminator != null ? discriminator.value() : this.getClass().getSimpleName();
    }

    // Location access through Installation
    @Transient
    public Location getLocation() {
        return installation != null ? installation.getLocation() : null;
    }

    @Transient
    public boolean isInstalled() {
        return installation != null && installation.isCurrentlyInstalled();
    }

    @Transient
    public LocalDateTime getInstallationDate() {
        return installation != null ? installation.getInstalledAt() : null;
    }

    @Transient
    public String getLocationAddress() {
        return installation != null ? installation.getInstallationLocationPath() : "Not installed";
    }

    @Transient
    public boolean hasLocationBackupPower() {
        return installation != null && installation.hasLocationBackupPower();
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + id + "[" + name + ", " +
                (model != null ? model : "no-model") + ", " +
                (componentType != null ? componentType.getCode() : "no-type") + "]";
    }
}