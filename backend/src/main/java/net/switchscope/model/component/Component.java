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

/**
 * Abstract base class for all network infrastructure components
 * Extends NamedEntity to provide common component functionality
 */
@Entity
@Table(name = "components")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "component_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Component extends NamedEntity {

    @Column(name = "manufacturer")
    @Size(max = 128)
    @NoHtml
    private String manufacturer;

    @Column(name = "model")
    @Size(max = 128)
    @NoHtml
    private String model;

    @Column(name = "serial_number", unique = true)
    @Size(max = 128)
    @NoHtml
    private String serialNumber;

    @Column(name = "part_number")
    @Size(max = 128)
    @NoHtml
    private String partNumber;

    @Column(name = "description")
    @Size(max = 512)
    @NoHtml
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private ComponentStatus status = ComponentStatus.PLANNED;

    @Column(name = "installation_date")
    private LocalDateTime installationDate;

    @Column(name = "last_maintenance_date")
    private LocalDateTime lastMaintenanceDate;

    @Column(name = "next_maintenance_date")
    private LocalDateTime nextMaintenanceDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installation_id")
    private Installation installation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_component_id")
    private Component parentComponent;

    @OneToMany(mappedBy = "parentComponent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Component> childComponents = new ArrayList<>();

    // Constructors
    protected Component(Integer id, String name) {
        super(id, name);
    }

    protected Component(Integer id, String name, String manufacturer, String model, String serialNumber) {
        super(id, name);
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
    }

    protected Component(Integer id, String name, String manufacturer, String model,
                       String serialNumber, ComponentStatus status, Location location) {
        super(id, name);
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.status = status;
        this.location = location;
    }

    // Component relationships
    public void addChildComponent(Component child) {
        if (child != null) {
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

    // Abstract methods that must be implemented by concrete components
    public abstract ComponentType getComponentType();
    public abstract boolean canHoldOtherComponents();
    public abstract boolean isInstallable();
    public abstract boolean requiresPhysicalSpace();
    public abstract Map<String, String> getSpecifications();

    // Common helper methods
    public boolean isOperational() {
        return status != null && status.isOperational();
    }

    public boolean requiresMaintenance() {
        return status == ComponentStatus.MAINTENANCE ||
               (nextMaintenanceDate != null && nextMaintenanceDate.isBefore(LocalDateTime.now()));
    }

    public boolean isAvailable() {
        return status != null && status.isAvailable();
    }

    public boolean requiresAttention() {
        return status != null && status.requiresAttention();
    }

    public boolean hasChildren() {
        return !childComponents.isEmpty();
    }

    public boolean isRootComponent() {
        return parentComponent == null;
    }

    /**
     * Get full hierarchical path of this component
     * @return string representation like "Rack1/Chassis2/Module3"
     */
    public String getComponentPath() {
        if (parentComponent == null) {
            return name;
        }
        return parentComponent.getComponentPath() + "/" + name;
    }

    /**
     * Get component hierarchy level (0 for root components)
     */
    public int getLevel() {
        if (parentComponent == null) {
            return 0;
        }
        return parentComponent.getLevel() + 1;
    }

    /**
     * Validate component configuration
     * Default implementation checks basic requirements
     */
    public boolean isValidConfiguration() {
        return name != null && !name.trim().isEmpty() &&
               status != null &&
               getComponentType() != null;
    }

    /**
     * Get component type based on discriminator
     */
    @Transient
    public String getDiscriminatorValue() {
        DiscriminatorValue discriminator = this.getClass().getAnnotation(DiscriminatorValue.class);
        return discriminator != null ? discriminator.value() : this.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + id + "[" + name + ", " +
               (model != null ? model : "no-model") + "]";
    }
}