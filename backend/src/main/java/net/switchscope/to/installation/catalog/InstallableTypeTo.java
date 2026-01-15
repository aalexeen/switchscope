package net.switchscope.to.installation.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.to.component.catalog.BaseCodedTo;

import java.util.UUID;

/**
 * DTO for InstallableTypeEntity - catalog of installable component types
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class InstallableTypeTo extends BaseCodedTo {

    // Configuration flags
    private Boolean requiresRackPosition = false;

    private Boolean hasStandardRackUnits = false;

    private Boolean supportsPowerManagement = false;

    private Boolean requiresEnvironmentalControl = false;

    // Installation characteristics
    @Min(1) @Max(1440)
    private Integer typicalInstallationTimeMinutes;

    private Boolean requiresShutdown = false;

    private Boolean hotSwappable = false;

    // Physical characteristics
    @Min(1) @Max(10)
    private Integer defaultRackUnits;

    @DecimalMin("0.1") @DecimalMax("500.0")
    private Double maxWeightKg;

    // Installation priority
    @Min(1) @Max(10)
    private Integer installationPriority;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean deviceType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean connectivityType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String category;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean housingType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean powerType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean implemented;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean requiresSpecialHandling;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean highPriority;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean lowPriority;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String priorityLevel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String estimatedInstallationTime;

    public InstallableTypeTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public InstallableTypeTo(UUID id, String name) {
        super(id, name);
    }
}
