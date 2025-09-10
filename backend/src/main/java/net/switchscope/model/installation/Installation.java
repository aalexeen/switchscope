package net.switchscope.model.installation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.BaseEntity;
import net.switchscope.model.component.Component;

import java.time.LocalDateTime;

@Entity
@Table(name = "installations")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Installation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_component_id", nullable = false)
    @NotNull
    private Component сomponent;

    @Enumerated(EnumType.STRING)
    @Column(name = "installed_item_type", nullable = false)
    @NotNull
    private InstallableType installedItemType;

    @Column(name = "installed_item_id", nullable = false)
    @NotNull
    private Integer installedItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private InstallationStatus status = InstallationStatus.INSTALLED;

    // Physical positioning
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

    // Who did the installation
    @Column(name = "installed_by")
    private String installedBy;

    @Column(name = "removed_by")
    private String removedBy;

    // Additional metadata
    @Column(name = "installation_notes")
    private String installationNotes;

    @Column(name = "cable_management")
    private String cableManagement;

    // Constructors
    public Installation(Component component, InstallableType installedItemType,
                       Integer installedItemId) {
        this.сomponent = component;
        this.installedItemType = installedItemType;
        this.installedItemId = installedItemId;
    }

    public Installation(Component component, InstallableType installedItemType,
                       Integer installedItemId, Integer rackPosition) {
        this(component, installedItemType, installedItemId);
        this.rackPosition = rackPosition;
    }

    // Helper methods
    public boolean isCurrentlyInstalled() {
        return status == InstallationStatus.INSTALLED;
    }

    public boolean isRackMounted() {
        return rackPosition != null;
    }

    public void markAsRemoved(String removedBy) {
        this.status = InstallationStatus.REMOVED;
        this.removedAt = LocalDateTime.now();
        this.removedBy = removedBy;
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
        return "Installation:" + id + "[" + installedItemType + ":" + installedItemId +
               " in " + сomponent.getName() + "]";
    }
}
