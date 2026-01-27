package net.switchscope.to.component.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for ComponentTypeEntity - specific types within categories
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ComponentTypeTo extends UIStyledTo {

    @NotNull
    @Schema(description = "Category ID this type belongs to")
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private UUID categoryId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Category code")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String categoryCode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Category display name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String categoryDisplayName;

    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Boolean systemType = false;

    // Physical properties
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresRackSpace = false;

    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer typicalRackUnits;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean canContainComponents = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean installable = true;

    // Technical properties
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresManagement = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsSnmp = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasFirmware = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean canHaveIpAddress = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean processesNetworkTraffic = false;

    // Power and cooling
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresPower = false;

    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer typicalPowerConsumptionWatts;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean generatesHeat = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean needsCooling = false;

    // Maintenance
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer recommendedMaintenanceIntervalMonths;

    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer typicalLifespanYears;

    // Containment rules
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Set<String> allowedChildTypeCodes = new HashSet<>();

    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Set<String> allowedChildCategoryCodes = new HashSet<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean housingComponent;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean connectivityComponent;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean supportComponent;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean moduleComponent;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean networkingEquipment;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String powerConsumptionCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean canBeDeleted;

    // Custom properties for extensibility
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Map<String, String> properties = new HashMap<>();

    public ComponentTypeTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public ComponentTypeTo(UUID id, String name) {
        super(id, name);
    }
}

