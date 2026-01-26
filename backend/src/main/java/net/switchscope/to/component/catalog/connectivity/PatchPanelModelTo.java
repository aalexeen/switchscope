package net.switchscope.to.component.catalog.connectivity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
import net.switchscope.to.component.catalog.ComponentModelTo;
import net.switchscope.validation.NoHtml;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for Patch Panel Model catalog entry
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PatchPanelModelTo extends ComponentModelTo {

    // Panel specifications
    @NotNull
    @Min(1) @Max(144)
    private Integer portCount;

    @NotNull
    @Min(1) @Max(10)
    private Integer rackUnits;

    @NotBlank
    @Size(max = 64)
    @NoHtml
    private String panelType;

    @Size(max = 64)
    @NoHtml
    private String connectorType;

    // Physical characteristics
    @Size(max = 32)
    @NoHtml
    private String mountingType;

    @Size(max = 32)
    @NoHtml
    private String terminationType;

    @Size(max = 64)
    @NoHtml
    private String portLayout;

    private Double portDensity;

    // Performance specifications
    @Size(max = 16)
    @NoHtml
    private String categoryRating;

    private Integer maxFrequencyMhz;

    private Boolean shielded = false;

    // Construction
    @Size(max = 32)
    @NoHtml
    private String housingMaterial;

    @Size(max = 32)
    @NoHtml
    private String finishColor;

    private Boolean cableManagement = false;

    @Size(max = 64)
    @NoHtml
    private String cableManagementType;

    // Labeling and identification
    private Boolean portLabeling = false;

    @Size(max = 32)
    @NoHtml
    private String labelType;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String portSummary;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String specificationSummary;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean fiberPanel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean copperPanel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean highDensity;

    public PatchPanelModelTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public PatchPanelModelTo(UUID id, String name) {
        super(id, name);
    }
}

