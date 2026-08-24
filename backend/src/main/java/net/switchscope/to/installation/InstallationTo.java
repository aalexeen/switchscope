package net.switchscope.to.installation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.to.BaseTo;
import net.switchscope.validation.NoHtml;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;

/**
 * DTO for Installation entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class InstallationTo extends BaseTo {

    // Location relationship
    @NotNull
    @Schema(description = "Location ID where component is installed")
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private UUID locationId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Location name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String locationName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Location full path")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String locationFullPath;

    // Housing component (optional)
    @Schema(description = "Housing component ID (for component-housed installations)")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID componentId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Housing component name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String componentName;

    // Installable type relationship
    @NotNull
    @Schema(description = "Installable type ID")
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private UUID installedItemTypeId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Installable type code")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String installedItemTypeCode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Installable type display name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String installedItemTypeDisplayName;

    // Installed item reference
    @NotNull
    @Schema(description = "ID of the installed item (Component UUID)")
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private UUID installedItemId;

    // Installation status
    @NotNull
    @Schema(description = "Installation status ID")
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private UUID statusId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Installation status code")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String statusCode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Installation status display name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String statusDisplayName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Status color category")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String statusColorCategory;

    // Physical positioning
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer rackPosition;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer rackUnitHeight;

    @Size(max = 256)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String positionDescription;

    // Timestamps
    @NotNull
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private LocalDateTime installedAt;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private LocalDateTime removedAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private LocalDateTime lastStatusChange;

    // Who did the installation
    @Size(max = 128)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String installedBy;

    @Size(max = 128)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String removedBy;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String statusChangedBy;

    // Additional metadata
    @Size(max = 2048)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String installationNotes;

    @Size(max = 256)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String cableManagement;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String installationLocationPath;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String fullLocationDescription;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean directLocationInstallation;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean componentHousedInstallation;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean currentlyInstalled;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean operational;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean requiresAttention;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean rackMounted;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean rackRequired;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean supportsHotSwap;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean requiresShutdown;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer occupiedRackUnits;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private List<Integer> occupiedRackPositions;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String installationPriority;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String estimatedInstallationTime;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean allowsEquipmentOperation;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean allowsMaintenance;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer statusUrgencyLevel;

    public InstallationTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        super(id, createdAt, updatedAt);
    }

    public InstallationTo(UUID id) {
        super(id);
    }
}

