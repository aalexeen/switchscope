package net.switchscope.model.component;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.NamedEntity;
import net.switchscope.validation.NoHtml;

import java.util.*;

/**
 * Dynamic component status entity replacing the hardcoded ComponentStatus enum
 */
@Entity
@Table(name = "component_statuses")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ComponentStatusEntity extends NamedEntity {

    @Column(name = "code", unique = true, nullable = false)
    @Size(max = 64)
    @NotNull
    @NoHtml
    private String code; // Unique identifier (e.g., "ACTIVE", "MAINTENANCE")

    @Column(name = "display_name", nullable = false)
    @Size(max = 128)
    @NotNull
    @NoHtml
    private String displayName;

    @Column(name = "description")
    @Size(max = 500)
    @NoHtml
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "color_class")
    @Size(max = 128)
    private String colorClass = "secondary"; // CSS class for UI colors

    @Column(name = "icon_class")
    @Size(max = 128)
    private String iconClass; // CSS class for UI icons

    @Column(name = "lifecycle_phase")
    @Size(max = 64)
    @NoHtml
    private String lifecyclePhase; // procurement, deployment, operational, maintenance, end-of-life

    // Boolean flags for business logic
    @Column(name = "is_available", nullable = false)
    private boolean available = false;

    @Column(name = "is_operational", nullable = false)
    private boolean operational = false;

    @Column(name = "can_accept_installations", nullable = false)
    private boolean canAcceptInstallations = false;

    @Column(name = "requires_attention", nullable = false)
    private boolean requiresAttention = false;

    @Column(name = "is_physically_present", nullable = false)
    private boolean physicallyPresent = false;

    @Column(name = "is_in_inventory", nullable = false)
    private boolean inInventory = true;

    @Column(name = "is_in_transition", nullable = false)
    private boolean inTransition = false;

    @Column(name = "can_be_reserved", nullable = false)
    private boolean canBeReserved = false;

    // Next possible statuses (many-to-many relationship)
    @ManyToMany
    @JoinTable(name = "status_transitions",
               joinColumns = @JoinColumn(name = "from_status_id"),
               inverseJoinColumns = @JoinColumn(name = "to_status_id"))
    private Set<ComponentStatusEntity> nextPossibleStatuses = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "component_status_properties",
                    joinColumns = @JoinColumn(name = "component_status_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> properties = new HashMap<>();

    // Constructors
    public ComponentStatusEntity(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
        this.name = displayName; // NamedEntity requirement
    }

    public ComponentStatusEntity(UUID id, String code, String displayName, String description) {
        super(id, displayName);
        this.code = code;
        this.displayName = displayName;
        this.description = description;
    }

    // Business logic methods
    public boolean isAvailable() {
        return available;
    }

    public boolean isOperational() {
        return operational;
    }

    public boolean canAcceptInstallations() {
        return canAcceptInstallations;
    }

    public boolean requiresAttention() {
        return requiresAttention;
    }

    public boolean isPhysicallyPresent() {
        return physicallyPresent;
    }

    public boolean isInInventory() {
        return inInventory;
    }

    public boolean isInTransition() {
        return inTransition;
    }

    public boolean canBeReserved() {
        return canBeReserved;
    }

    /**
     * Add next possible status
     */
    public void addNextPossibleStatus(ComponentStatusEntity status) {
        if (status != null) {
            nextPossibleStatuses.add(status);
        }
    }

    /**
     * Remove next possible status
     */
    public void removeNextPossibleStatus(ComponentStatusEntity status) {
        nextPossibleStatuses.remove(status);
    }

    /**
     * Check if transition to another status is allowed
     */
    public boolean canTransitionTo(ComponentStatusEntity status) {
        return nextPossibleStatuses.contains(status);
    }

    /**
     * Get next possible status codes
     */
    public Set<String> getNextPossibleStatusCodes() {
        return nextPossibleStatuses.stream()
                .map(ComponentStatusEntity::getCode)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Add custom property
     */
    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    /**
     * Get custom property
     */
    public String getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Get custom property with default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    /**
     * Builder pattern for easier construction
     */
    public static class Builder {
        private ComponentStatusEntity status;

        public Builder(String code, String displayName) {
            status = new ComponentStatusEntity(code, displayName);
        }

        public Builder description(String description) {
            status.setDescription(description);
            return this;
        }

        public Builder colorClass(String colorClass) {
            status.setColorClass(colorClass);
            return this;
        }

        public Builder lifecyclePhase(String phase) {
            status.setLifecyclePhase(phase);
            return this;
        }

        public Builder available(boolean available) {
            status.setAvailable(available);
            return this;
        }

        public Builder operational(boolean operational) {
            status.setOperational(operational);
            return this;
        }

        public Builder canAcceptInstallations(boolean canAccept) {
            status.setCanAcceptInstallations(canAccept);
            return this;
        }

        public Builder requiresAttention(boolean requires) {
            status.setRequiresAttention(requires);
            return this;
        }

        public Builder physicallyPresent(boolean present) {
            status.setPhysicallyPresent(present);
            return this;
        }

        public Builder inTransition(boolean transition) {
            status.setInTransition(transition);
            return this;
        }

        public Builder canBeReserved(boolean reserved) {
            status.setCanBeReserved(reserved);
            return this;
        }

        public ComponentStatusEntity build() {
            return status;
        }
    }

    @Override
    public String toString() {
        return "ComponentStatusEntity[" + code + ":" + displayName + "]";
    }
}