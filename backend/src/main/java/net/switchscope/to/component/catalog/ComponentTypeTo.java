package net.switchscope.to.component.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
    private UUID categoryId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Category code")
    private String categoryCode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Category display name")
    private String categoryDisplayName;

    private Boolean systemType = false;

    // Physical properties
    private Boolean requiresRackSpace = false;

    private Integer typicalRackUnits;

    private Boolean canContainComponents = false;

    private Boolean installable = true;

    // Technical properties
    private Boolean requiresManagement = false;

    private Boolean supportsSnmp = false;

    private Boolean hasFirmware = false;

    private Boolean canHaveIpAddress = false;

    private Boolean processesNetworkTraffic = false;

    // Power and cooling
    private Boolean requiresPower = false;

    private Integer typicalPowerConsumptionWatts;

    private Boolean generatesHeat = false;

    private Boolean needsCooling = false;

    // Maintenance
    private Integer recommendedMaintenanceIntervalMonths;

    private Integer typicalLifespanYears;

    // Containment rules
    private Set<String> allowedChildTypeCodes = new HashSet<>();

    private Set<String> allowedChildCategoryCodes = new HashSet<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean housingComponent;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean connectivityComponent;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean supportComponent;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean moduleComponent;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean networkingEquipment;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String powerConsumptionCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean canBeDeleted;

    // Custom properties for extensibility
    private Map<String, String> properties = new HashMap<>();

    public ComponentTypeTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public ComponentTypeTo(UUID id, String name) {
        super(id, name);
    }
}

