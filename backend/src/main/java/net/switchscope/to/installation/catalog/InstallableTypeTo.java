package net.switchscope.to.installation.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
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
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresRackPosition = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasStandardRackUnits = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsPowerManagement = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresEnvironmentalControl = false;

    // Installation characteristics
    @Min(1) @Max(1440)
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer typicalInstallationTimeMinutes;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresShutdown = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hotSwappable = false;

    // Physical characteristics
    @Min(1) @Max(10)
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer defaultRackUnits;

    @DecimalMin("0.1") @DecimalMax("500.0")
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Double maxWeightKg;

    // Installation priority
    @Min(1) @Max(10)
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer installationPriority;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean deviceType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean connectivityType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String category;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean housingType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean powerType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean implemented;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean requiresSpecialHandling;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean highPriority;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean lowPriority;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String priorityLevel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String estimatedInstallationTime;

    public InstallableTypeTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public InstallableTypeTo(UUID id, String name) {
        super(id, name);
    }
}
