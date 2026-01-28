package net.switchscope.to.component.catalog;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
import net.switchscope.validation.NoHtml;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for ComponentNatureEntity - dynamic component nature catalog
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ComponentNatureTo extends UIStyledTo {

    // Business logic flags
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresManagement = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean canHaveIpAddress = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean hasFirmware = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean processesNetworkTraffic = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsSnmpMonitoring = false;

    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String powerConsumptionCategory = "none";

    // Custom properties for extensibility
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Map<String, String> properties = new HashMap<>();

    public ComponentNatureTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public ComponentNatureTo(UUID id, String name) {
        super(id, name);
    }
}

