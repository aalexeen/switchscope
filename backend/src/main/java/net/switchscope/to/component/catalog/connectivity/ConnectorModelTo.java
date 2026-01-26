package net.switchscope.to.component.catalog.connectivity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
 * DTO for Connector Model catalog entry
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ConnectorModelTo extends ComponentModelTo {

    // Connector type and specifications
    @NotBlank
    @Size(max = 64)
    @NoHtml
    private String connectorType;

    @Size(max = 64)
    @NoHtml
    private String connectorStandard;

    @Size(max = 16)
    @NoHtml
    private String gender;

    // Physical characteristics
    @Size(max = 32)
    @NoHtml
    private String mountingType;

    @Size(max = 32)
    @NoHtml
    private String platingMaterial;

    @Size(max = 32)
    @NoHtml
    private String housingMaterial;

    // Performance specifications
    private Integer maxFrequencyMhz;

    private Double insertionLossDb;

    private Double returnLossDb;

    @Min(1)
    private Integer matingCycles;

    // Environmental specifications (specific to connector)
    private Integer connectorOperatingTemperatureMin;

    private Integer connectorOperatingTemperatureMax;

    @Size(max = 8)
    @NoHtml
    private String ipRating;

    // Compatibility
    @Size(max = 256)
    @NoHtml
    private String compatibleCableTypes;

    private Boolean shielded = false;

    private Boolean keyed = false;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String compatibilitySummary;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean fiberOptic;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean copper;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean highDensity;

    public ConnectorModelTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public ConnectorModelTo(UUID id, String name) {
        super(id, name);
    }
}

