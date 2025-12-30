package net.switchscope.to.component;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.to.NamedTo;
import net.switchscope.validation.NoHtml;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Base DTO for all component types
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class ComponentTo extends NamedTo {

    @Size(max = 128)
    @NoHtml
    private String manufacturer;

    @Size(max = 128)
    @NoHtml
    private String model;

    @Size(max = 128)
    @NoHtml
    private String serialNumber;

    @Size(max = 128)
    @NoHtml
    private String partNumber;

    @NotNull
    @Schema(description = "Component status ID")
    private UUID componentStatusId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Component status code")
    private String componentStatusCode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Component status display name")
    private String componentStatusDisplayName;

    @NotNull
    @Schema(description = "Component type ID")
    private UUID componentTypeId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Component type code")
    private String componentTypeCode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Component type display name")
    private String componentTypeDisplayName;

    @Schema(description = "Component nature ID (optional)")
    private UUID componentNatureId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Component nature code")
    private String componentNatureCode;

    private LocalDateTime lastMaintenanceDate;

    private LocalDateTime nextMaintenanceDate;

    @Schema(description = "Installation ID (optional)")
    private UUID installationId;

    @Schema(description = "Parent component ID (optional)")
    private UUID parentComponentId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Parent component name")
    private String parentComponentName;

    // Read-only computed fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String componentPath;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean operational;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean installed;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String locationAddress;

    protected ComponentTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, name);
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
    }

    protected ComponentTo(UUID id, String name) {
        super(id, name);
    }
}

