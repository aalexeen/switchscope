package net.switchscope.to.component.connectivity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.to.component.ComponentTo;
import net.switchscope.validation.NoHtml;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for PatchPanel entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PatchPanelTo extends ComponentTo {

    @Schema(description = "Patch panel model catalog ID")
    private UUID patchPanelModelId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Patch panel model name")
    private String patchPanelModelName;

    // Panel specifications
    private Integer portCount;

    @Size(max = 64)
    @NoHtml
    private String panelType;

    @Size(max = 64)
    @NoHtml
    private String connectorType;

    // Rack positioning
    @Min(1) @Max(10)
    private Integer rackUnits;

    private Integer rackPosition;

    // Panel identification and labeling
    @Size(max = 64)
    @NoHtml
    private String panelLabel;

    @Size(max = 64)
    @NoHtml
    private String portLabelingScheme;

    // Installation and configuration
    @Size(max = 32)
    @NoHtml
    private String mountingType;

    @Size(max = 32)
    @NoHtml
    private String terminationType;

    // Performance characteristics
    @Size(max = 16)
    @NoHtml
    private String categoryRating;

    private Boolean shielded;

    // Cable management
    private Boolean cableManagement;

    @Size(max = 64)
    @NoHtml
    private String cableManagementType;

    // Documentation
    @Size(max = 1024)
    @NoHtml
    private String installationNotes;

    // Related cable run IDs
    private Set<UUID> cableRunIds = new HashSet<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean fiberPanel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean copperPanel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean highDensity;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String portDensityInfo;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer cableRunCount;

    public PatchPanelTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public PatchPanelTo(UUID id, String name) {
        super(id, name);
    }
}

