package net.switchscope.to.component.catalog.device;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.to.component.catalog.ComponentModelTo;
import net.switchscope.validation.NoHtml;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Base DTO for all device model catalog entries
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class DeviceModelTo extends ComponentModelTo {

    // Device-specific identification
    @Size(max = 128)
    @NoHtml
    private String productFamily;

    @Size(max = 128)
    @NoHtml
    private String series;

    // Power specifications
    @Min(1)
    private Integer powerConsumptionWatts;

    private Integer maxPowerConsumptionWatts;

    @Size(max = 64)
    private String powerInputType;

    private Boolean redundantPowerSupply = false;

    // Management capabilities
    @Size(max = 256)
    private String managementInterfaces;

    @Size(max = 64)
    private String snmpVersions;

    private Boolean supportsSsh = true;

    private Boolean supportsTelnet = true;

    private Boolean webManagement = true;

    // Device-specific environmental specifications
    private Boolean fanless = false;

    private Integer noiseLevelDb;

    // Additional device specifications
    private Map<String, String> deviceSpecs = new HashMap<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String managementSummary;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String powerSummary;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String deviceTypeSummary;

    protected DeviceModelTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    protected DeviceModelTo(UUID id, String name) {
        super(id, name);
    }
}

