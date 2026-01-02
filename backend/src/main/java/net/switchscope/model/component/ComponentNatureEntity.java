package net.switchscope.model.component;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.validation.NoHtml;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Dynamic component nature entity replacing the hardcoded ComponentNature enum
 */
@Entity
@Table(name = "component_natures_catalog")
@Getter
@Setter
@NoArgsConstructor
public class ComponentNatureEntity extends UIStyledEntity {

    // Business logic flags
    @Column(name = "requires_management", nullable = false)
    private boolean requiresManagement = false;

    @Column(name = "can_have_ip_address", nullable = false)
    private boolean canHaveIpAddress = false;

    @Column(name = "has_firmware", nullable = false)
    private boolean hasFirmware = false;

    @Column(name = "processes_network_traffic", nullable = false)
    private boolean processesNetworkTraffic = false;

    @Column(name = "supports_snmp_monitoring", nullable = false)
    private boolean supportsSnmpMonitoring = false;

    @Column(name = "power_consumption_category")
    @Size(max = 64)
    @NoHtml
    private String powerConsumptionCategory = "none"; // none, low, medium, high, variable

    @ElementCollection
    @CollectionTable(name = "component_nature_properties",
                    joinColumns = @JoinColumn(name = "component_nature_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> properties = new HashMap<>();

    // Constructors
    public ComponentNatureEntity(String code, String displayName) {
        super(code, displayName, "primary"); // default color class
    }

    public ComponentNatureEntity(UUID id, String code, String displayName, String description) {
        super(id, code, displayName, description);
        this.setColorClass("primary"); // default color class
    }

    // Business logic methods
    public boolean requiresManagement() {
        return requiresManagement;
    }

    public boolean canHaveIpAddress() {
        return canHaveIpAddress;
    }

    public boolean hasFirmware() {
        return hasFirmware;
    }

    public boolean processesNetworkTraffic() {
        return processesNetworkTraffic;
    }

    public boolean supportsSnmpMonitoring() {
        return supportsSnmpMonitoring;
    }

    /**
     * Add custom property
     */
    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    /**
     * Get custom property
     */
    public String getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Get custom property with default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    /**
     * Check if has custom property
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * Builder pattern for easier construction
     */
    public static class Builder {
        private ComponentNatureEntity nature;

        public Builder(String code, String displayName) {
            nature = new ComponentNatureEntity(code, displayName);
        }

        public Builder description(String description) {
            nature.setDescription(description);
            return this;
        }

        public Builder colorClass(String colorClass) {
            nature.setColorClass(colorClass);
            return this;
        }

        public Builder iconClass(String iconClass) {
            nature.setIconClass(iconClass);
            return this;
        }

        public Builder requiresManagement(boolean requires) {
            nature.setRequiresManagement(requires);
            return this;
        }

        public Builder canHaveIpAddress(boolean canHave) {
            nature.setCanHaveIpAddress(canHave);
            return this;
        }

        public Builder hasFirmware(boolean hasFirmware) {
            nature.setHasFirmware(hasFirmware);
            return this;
        }

        public Builder processesNetworkTraffic(boolean processes) {
            nature.setProcessesNetworkTraffic(processes);
            return this;
        }

        public Builder supportsSnmpMonitoring(boolean supports) {
            nature.setSupportsSnmpMonitoring(supports);
            return this;
        }

        public Builder powerConsumptionCategory(String category) {
            nature.setPowerConsumptionCategory(category);
            return this;
        }

        public ComponentNatureEntity build() {
            return nature;
        }
    }
}