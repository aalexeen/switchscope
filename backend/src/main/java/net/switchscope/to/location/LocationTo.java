package net.switchscope.to.location;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.to.NamedTo;
import net.switchscope.validation.NoHtml;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;

/**
 * DTO for Location entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LocationTo extends NamedTo {

    // Location type relationship
    @NotNull
    @Schema(description = "Location type ID")
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private UUID typeId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Location type code")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String typeCode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Location type display name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String typeDisplayName;

    @Size(max = 512)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String address;

    // Parent-child hierarchy
    @Schema(description = "Parent location ID (optional)")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID parentLocationId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Parent location name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String parentLocationName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Child location IDs")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private List<UUID> childLocationIds = new ArrayList<>();

    // Additional location-specific fields
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer capacityChildren;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer capacityEquipment;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer floorNumber;

    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String roomNumber;

    @Size(max = 128)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String coordinates;

    @Size(max = 256)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String accessRequirements;

    // Climate and environment
    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String temperatureRange;

    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String humidityRange;

    // Power and infrastructure
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer powerCapacityWatts;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer availableRackUnits;

    @FieldAccess(FieldAccessLevel.REQUIRED)
    private Boolean hasUps = false;

    @FieldAccess(FieldAccessLevel.REQUIRED)
    private Boolean hasGenerator = false;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String fullPath;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String fullPathWithTypes;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer level;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer hierarchyLevel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String locationCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean rootLocation;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean hasChildren;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean physical;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean virtual;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean rackLike;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean roomLike;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean buildingLike;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer childrenCapacity;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer equipmentCapacity;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer availableChildrenSlots;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer totalRackUnits;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean hasBackupPower;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer childLocationCount;

    public LocationTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, name);
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
    }

    public LocationTo(UUID id, String name) {
        super(id, name);
    }
}

