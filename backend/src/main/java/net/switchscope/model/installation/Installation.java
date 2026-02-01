package net.switchscope.model.installation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.BaseEntity;
import net.switchscope.model.component.Component;
import net.switchscope.model.installation.catalog.InstallableTypeEntity;
import net.switchscope.model.installation.catalog.InstallationStatusEntity;
import net.switchscope.model.location.Location;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "installations")
@Getter
@Setter
@NoArgsConstructor
public class Installation extends BaseEntity {

    // Location where the component is installed
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    @NotNull
    private Location location;

    // Housing component (component that contains/houses the installed item)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_component_id", nullable = true)
    private Component component;

    // What type of item is being installed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installable_type_id", nullable = false)
    @NotNull
    private InstallableTypeEntity installedItemType;

    // ID of the actual installed item (Component.id, Device.id, etc.)
    @Column(name = "installed_item_id", nullable = false)
    @NotNull
    private UUID installedItemId;

    // Current status of the installation
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = false)
    @NotNull
    private InstallationStatusEntity status;

    // Physical positioning within the location
    @Column(name = "rack_position")
    private Integer rackPosition;

    @Column(name = "rack_unit_height")
    private Integer rackUnitHeight;

    @Column(name = "position_description")
    private String positionDescription;

    // Timestamps
    @Column(name = "installed_at", nullable = false)
    @NotNull
    private LocalDateTime installedAt = LocalDateTime.now();

    @Column(name = "removed_at")
    private LocalDateTime removedAt;

    @Column(name = "last_status_change", nullable = false)
    @NotNull
    private LocalDateTime lastStatusChange = LocalDateTime.now();

    // Who did the installation
    @Column(name = "installed_by")
    private String installedBy;

    @Column(name = "removed_by")
    private String removedBy;

    @Column(name = "status_changed_by")
    private String statusChangedBy;

    // Additional metadata
    @Column(name = "installation_notes")
    private String installationNotes;

    @Column(name = "cable_management")
    private String cableManagement;

    // Constructors for direct location installation (e.g., standalone equipment)
    public Installation(Location location, InstallableTypeEntity installedItemType,
            UUID installedItemId) {
        this.location = location;
        this.installedItemType = installedItemType;
        this.installedItemId = installedItemId;
    }

    public Installation(Location location, InstallableTypeEntity installedItemType,
            UUID installedItemId, Integer rackPosition) {
        this(location, installedItemType, installedItemId);
        this.rackPosition = rackPosition;
    }

    public Installation(Location location, InstallableTypeEntity installedItemType,
            UUID installedItemId, InstallationStatusEntity status) {
        this(location, installedItemType, installedItemId);
        this.status = status;
    }

    // Constructors for component-housed installation (e.g., modules in chassis)
    public Installation(Location location, Component housingComponent,
            InstallableTypeEntity installedItemType, UUID installedItemId) {
        this.location = location;
        this.component = housingComponent;
        this.installedItemType = installedItemType;
        this.installedItemId = installedItemId;
    }

    public Installation(Location location, Component housingComponent,
            InstallableTypeEntity installedItemType, UUID installedItemId,
            InstallationStatusEntity status) {
        this(location, housingComponent, installedItemType, installedItemId);
        this.status = status;
    }

    // Location-related methods
    public boolean isDirectLocationInstallation() {
        return component == null;
    }

    public boolean isComponentHousedInstallation() {
        return component != null;
    }

    public String getInstallationLocationPath() {
        StringBuilder path = new StringBuilder(location.getFullPath());

        if (component != null) {
            path.append("/").append(component.getName());
        }

        if (rackPosition != null) {
            path.append(" [U").append(rackPosition).append("]");
        } else if (positionDescription != null) {
            path.append(" [").append(positionDescription).append("]");
        }

        return path.toString();
    }

    public boolean isValidLocationInstallation() {
        if (location == null || !location.canAcceptEquipment()) {
            return false;
        }

        // Check if location can accept rack installations
        if (isRackRequired() && !location.isRackLike()) {
            return false;
        }

        // Check housing component compatibility
        if (component != null && !component.canContainComponent(null)) { // TODO: resolve actual component
            return false;
        }

        return true;
    }

    // Helper methods
    public boolean isCurrentlyInstalled() {
        return status != null && status.isPhysicallyPresent() && !status.isFinalStatus();
    }

    public boolean isOperational() {
        return status != null && status.isOperational();
    }

    public boolean requiresAttention() {
        return status != null && status.isRequiresAttention();
    }

    public boolean isRackMounted() {
        return rackPosition != null;
    }

    public boolean isRackRequired() {
        return installedItemType != null && installedItemType.isRequiresRackPosition();
    }

    public boolean supportsHotSwap() {
        return installedItemType != null && installedItemType.isHotSwappable();
    }

    public boolean requiresShutdown() {
        return installedItemType != null && installedItemType.isRequiresShutdown();
    }

    // Location validation helpers
    public boolean isValidRackPosition() {
        if (!isRackMounted()) return true;
        if (!location.isRackLike()) return false;

        int totalRackUnits = location.getTotalRackUnits();
        return rackPosition > 0 && rackPosition <= totalRackUnits;
    }

    public boolean fitsInLocation() {
        if (rackUnitHeight != null && location.isRackLike()) {
            // Check if the equipment fits in the rack from the specified position
            return (rackPosition + rackUnitHeight - 1) <= location.getTotalRackUnits();
        }
        return true;
    }

    // Status management
    public void changeStatus(InstallationStatusEntity newStatus, String changedBy) {
        if (newStatus != null &&
                (status == null || status.canTransitionTo(newStatus.getCode()))) {
            this.status = newStatus;
            this.lastStatusChange = LocalDateTime.now();
            this.statusChangedBy = changedBy;

            // Auto-set removed timestamp for final/error statuses
            if (newStatus.isFinalStatus() || newStatus.isErrorStatus()) {
                if (this.removedAt == null) {
                    this.removedAt = LocalDateTime.now();
                    this.removedBy = changedBy;
                }
            }
        }
    }

    public boolean canChangeStatusTo(InstallationStatusEntity targetStatus) {
        return status == null || status.canTransitionTo(targetStatus.getCode());
    }

    public void markAsRemoved(String removedBy) {
        this.removedAt = LocalDateTime.now();
        this.removedBy = removedBy;
    }

    // Auto-transition support
    public boolean shouldAutoTransition() {
        if (status == null || !status.hasAutoTransition()) {
            return false;
        }

        long minutesSinceLastChange = java.time.Duration.between(
                lastStatusChange, LocalDateTime.now()).toMinutes();

        return status.shouldAutoTransition(minutesSinceLastChange);
    }

    // Enhanced installation validation
    public boolean isValidInstallation() {
        if (installedItemType == null || location == null) {
            return false;
        }

        // Check rack position requirement
        if (installedItemType.isRequiresRackPosition() && rackPosition == null) {
            return false;
        }

        // Validate location compatibility
        if (!isValidLocationInstallation()) {
            return false;
        }

        // Validate rack positioning
        if (!isValidRackPosition() || !fitsInLocation()) {
            return false;
        }

        return true;
    }

    public String getInstallationPriority() {
        return installedItemType != null ? installedItemType.getPriorityLevel() : "MEDIUM";
    }

    public String getEstimatedInstallationTime() {
        return installedItemType != null ?
                installedItemType.getEstimatedInstallationTime() : "Unknown";
    }

    // Business logic helpers
    public boolean allowsEquipmentOperation() {
        return status != null && status.isAllowsEquipmentOperation();
    }

    public boolean allowsMaintenance() {
        return status != null && status.isAllowsMaintenance();
    }

    public boolean requiresDocumentation() {
        return status != null && status.isRequiresDocumentation();
    }

    public boolean notifiesStakeholders() {
        return status != null && status.isNotifiesStakeholders();
    }

    public int getStatusUrgencyLevel() {
        return status != null ? status.getUrgencyLevel() : 5;
    }

    // Location-specific business logic
    public boolean requiresLocationAccessControl() {
        return location != null && location.requiresAccessControl();
    }

    public boolean requiresLocationClimateControl() {
        return location != null && location.requiresClimateControl();
    }

    public boolean hasLocationBackupPower() {
        return location != null && location.hasBackupPower();
    }

    // Physical space calculations
    public int getOccupiedRackUnits() {
        if (!isRackMounted() || rackUnitHeight == null) {
            return isRackRequired() ? (installedItemType.getDefaultRackUnits() != null ?
                    installedItemType.getDefaultRackUnits() : 1) : 0;
        }
        return rackUnitHeight;
    }

    public List<Integer> getOccupiedRackPositions() {
        if (!isRackMounted()) {
            return List.of();
        }

        List<Integer> positions = new ArrayList<>();
        int height = getOccupiedRackUnits();
        for (int i = 0; i < height; i++) {
            positions.add(rackPosition + i);
        }
        return positions;
    }

    // Enhanced location and component information
    public String getFullLocationDescription() {
        StringBuilder desc = new StringBuilder();

        desc.append("Location: ").append(location.getFullPath());

        if (component != null) {
            desc.append(", Housing: ").append(component.getName());
        }

        if (isRackMounted()) {
            desc.append(", Rack Position: U").append(rackPosition);
            if (rackUnitHeight != null && rackUnitHeight > 1) {
                desc.append("-U").append(rackPosition + rackUnitHeight - 1);
            }
        } else if (positionDescription != null) {
            desc.append(", Position: ").append(positionDescription);
        }

        return desc.toString();
    }

    /**
     * Get the actual installed object
     * This method should be implemented in service layer to resolve the object
     */
    @Transient
    public Object getInstalledItem() {
        // Implementation will be in InstallationService
        // to resolve object by type and ID
        throw new UnsupportedOperationException("Use InstallationService.getInstalledItem()");
    }

    @Override
    public String toString() {
        return "Installation:" + id + "[" +
                (installedItemType != null ? installedItemType.getCode() : "null") +
                ":" + installedItemId +
                " at " + (location != null ? location.getName() : "null") +
                (component != null ? " in " + component.getName() : "") +
                ", status=" + (status != null ? status.getCode() : "null") + "]";
    }
}
