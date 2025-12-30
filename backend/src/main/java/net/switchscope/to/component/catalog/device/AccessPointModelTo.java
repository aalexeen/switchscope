package net.switchscope.to.component.catalog.device;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.validation.NoHtml;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for Access Point Model catalog entry
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AccessPointModelTo extends DeviceModelTo {

    // WiFi specifications
    @NotBlank
    @Size(max = 32)
    @NoHtml
    private String wifiStandard;

    @Size(max = 32)
    @NoHtml
    private String wifiGeneration;

    @NotNull
    @Min(1) @Max(1000)
    private Integer maxClients;

    // Band support
    private Boolean dualBand = false;

    private Boolean supports5Ghz = false;

    private Boolean supports6Ghz = false;

    // Performance specifications
    @NotNull
    @Min(1) @Max(10000)
    private Integer maxThroughputMbps;

    // Coverage specifications
    private Integer indoorCoverageSqm;

    private Integer outdoorCoverageSqm;

    // Physical specifications
    @Size(max = 64)
    @NoHtml
    private String formFactor;

    private Integer weightG;

    @Size(max = 8)
    @NoHtml
    private String ipRating;

    // PoE specifications
    private Boolean poeRequired = true;

    @Size(max = 32)
    @NoHtml
    private String poeStandard;

    // Security features
    private Boolean supportsWpa3 = false;

    private Boolean supportsEnterpriseAuth = false;

    // Advanced features
    private Boolean supportsMesh = false;

    private Boolean supportsMuMimo = false;

    private Boolean supportsBeamforming = false;

    // Management capabilities
    private Boolean controllerManaged = false;

    private Boolean cloudManaged = false;

    // Environment
    private Boolean indoorUse = true;

    private Boolean outdoorUse = false;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String bandSummary;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean wifi6;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean enterprise;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean hasAdvancedFeatures;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal powerEfficiency;

    public AccessPointModelTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public AccessPointModelTo(UUID id, String name) {
        super(id, name);
    }
}

