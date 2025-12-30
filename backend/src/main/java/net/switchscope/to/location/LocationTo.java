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
    private UUID typeId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Location type code")
    private String typeCode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Location type display name")
    private String typeDisplayName;

    @Size(max = 512)
    @NoHtml
    private String address;

    // Parent-child hierarchy
    @Schema(description = "Parent location ID (optional)")
    private UUID parentLocationId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Parent location name")
    private String parentLocationName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Child location IDs")
    private List<UUID> childLocationIds = new ArrayList<>();

    // Additional location-specific fields
    private Integer capacityChildren;

    private Integer capacityEquipment;

    private Integer floorNumber;

    @Size(max = 32)
    @NoHtml
    private String roomNumber;

    @Size(max = 128)
    @NoHtml
    private String coordinates;

    @Size(max = 256)
    @NoHtml
    private String accessRequirements;

    // Climate and environment
    @Size(max = 64)
    @NoHtml
    private String temperatureRange;

    @Size(max = 64)
    @NoHtml
    private String humidityRange;

    // Power and infrastructure
    private Integer powerCapacityWatts;

    private Integer availableRackUnits;

    private Boolean hasUps = false;

    private Boolean hasGenerator = false;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String fullPath;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String fullPathWithTypes;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer level;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer hierarchyLevel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String locationCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean rootLocation;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean hasChildren;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean physical;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean virtual;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean rackLike;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean roomLike;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean buildingLike;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer childrenCapacity;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer equipmentCapacity;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer availableChildrenSlots;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer totalRackUnits;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean hasBackupPower;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
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

