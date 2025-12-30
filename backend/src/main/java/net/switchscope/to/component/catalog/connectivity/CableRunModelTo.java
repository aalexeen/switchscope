package net.switchscope.to.component.catalog.connectivity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.to.component.catalog.ComponentModelTo;
import net.switchscope.validation.NoHtml;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for Cable Run Model catalog entry
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CableRunModelTo extends ComponentModelTo {

    // Cable type and category
    @NotBlank
    @Size(max = 64)
    @NoHtml
    private String cableType;

    @Size(max = 32)
    @NoHtml
    private String cableCategory;

    // Physical specifications
    @Size(max = 16)
    @NoHtml
    private String conductorGauge;

    @Min(1) @Max(1000)
    private Integer conductorCount;

    @Size(max = 64)
    @NoHtml
    private String jacketType;

    @Size(max = 32)
    @NoHtml
    private String shieldingType;

    // Performance specifications
    private Integer maxFrequencyMhz;

    private BigDecimal maxBandwidthGbps;

    @Min(1)
    private Integer maxDistanceMeters;

    private BigDecimal attenuationDbPer100m;

    // Environmental specifications (specific to cable)
    private Integer cableOperatingTemperatureMin;

    private Integer cableOperatingTemperatureMax;

    private Boolean indoorUse = true;

    private Boolean outdoorUse = false;

    private Boolean plenumRated = false;

    private Boolean fireRetardant = false;

    // Installation specifications
    @Min(1)
    private Integer bendRadiusMm;

    @Min(1)
    private Integer pullingTensionLbs;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String performanceSummary;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean fiberOptic;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean copper;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean plenumSafe;

    public CableRunModelTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public CableRunModelTo(UUID id, String name) {
        super(id, name);
    }
}

