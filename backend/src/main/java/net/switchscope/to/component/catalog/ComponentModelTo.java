package net.switchscope.to.component.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.to.NamedTo;
import net.switchscope.validation.NoHtml;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Base DTO for all component model catalog entries
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class ComponentModelTo extends NamedTo {

    // Basic model identification
    @NotBlank
    @Size(max = 128)
    @NoHtml
    private String manufacturer;

    @NotBlank
    @Size(max = 128)
    @NoHtml
    private String modelNumber;

    @Size(max = 128)
    @NoHtml
    private String partNumber;

    @Size(max = 64)
    @NoHtml
    private String sku;

    // Component type relationship
    @NotNull
    @Schema(description = "Component type ID")
    private UUID componentTypeId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Component type code")
    private String componentTypeCode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Component type display name")
    private String componentTypeDisplayName;

    // Model status and lifecycle
    private Boolean active = true;

    private Boolean discontinued = false;

    private Boolean endOfLife = false;

    private LocalDateTime releaseDate;

    private LocalDateTime discontinueDate;

    // Documentation and support
    @Size(max = 512)
    private String datasheetUrl;

    @Size(max = 512)
    private String manualUrl;

    @Size(max = 512)
    private String supportUrl;

    // Warranty and lifecycle
    @Min(0) @Max(20)
    private Integer warrantyYears;

    @Min(1) @Max(50)
    private Integer expectedLifespanYears;

    @Min(1) @Max(60)
    private Integer maintenanceIntervalMonths;

    // Common physical characteristics
    @DecimalMin("0.001") @DecimalMax("1000.0")
    private Double weightKg;

    @Size(max = 64)
    @NoHtml
    private String dimensionsMm;

    // Environmental specifications
    private Integer operatingTemperatureMin;

    private Integer operatingTemperatureMax;

    @Min(0) @Max(100)
    private Integer operatingHumidityMin;

    @Min(0) @Max(100)
    private Integer operatingHumidityMax;

    private Integer storageTemperatureMin;

    private Integer storageTemperatureMax;

    // Certifications and compliance
    @Size(max = 512)
    @NoHtml
    private String certifications;

    @Size(max = 512)
    @NoHtml
    private String complianceStandards;

    // Pricing and availability
    @DecimalMin("0.01")
    private Double msrp;

    @DecimalMin("0.01")
    private Double listPrice;

    @Size(max = 3)
    @Pattern(regexp = "[A-Z]{3}")
    private String currencyCode;

    // Catalog management
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdDate;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedDate;

    private Boolean verified = false;

    private LocalDateTime verificationDate;

    @Size(max = 2048)
    @NoHtml
    private String notes;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String modelDesignation;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String lifecycleStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean currentlySupported;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean availableForPurchase;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String operatingTemperatureRange;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String humidityRange;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String categoryName;

    protected ComponentModelTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, name);
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
    }

    protected ComponentModelTo(UUID id, String name) {
        super(id, name);
    }
}

