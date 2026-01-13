package net.switchscope.to.component.catalog;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
    private Boolean requiresManagement = false;

    private Boolean canHaveIpAddress = false;

    private Boolean hasFirmware = false;

    private Boolean processesNetworkTraffic = false;

    private Boolean supportsSnmpMonitoring = false;

    @Size(max = 64)
    @NoHtml
    private String powerConsumptionCategory = "none";

    // Custom properties for extensibility
    private Map<String, String> properties = new HashMap<>();

    public ComponentNatureTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public ComponentNatureTo(UUID id, String name) {
        super(id, name);
    }
}

