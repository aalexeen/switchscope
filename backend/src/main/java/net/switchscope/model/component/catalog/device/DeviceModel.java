
package net.switchscope.model.component.catalog.device;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.validation.NoHtml;

import java.math.BigDecimal;
import java.util.*;

/**
 * Base abstract class for all device model catalog entries
 * Contains device-specific fields and methods shared by all device models
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DeviceModel extends ComponentModel {

    // Device-specific identification
    @Column(name = "product_family")
    @Size(max = 128)
    @NoHtml
    private String productFamily;

    @Column(name = "series")
    @Size(max = 128)
    @NoHtml
    private String series;

    // Power specifications (device-specific)
    @Column(name = "power_consumption_watts", nullable = false)
    @NotNull
    @Min(1)
    private Integer powerConsumptionWatts;

    @Column(name = "max_power_consumption_watts")
    private Integer maxPowerConsumptionWatts;

    @Column(name = "power_input_type")
    @Size(max = 64)
    private String powerInputType;

    @Column(name = "redundant_power_supply", nullable = false)
    private boolean redundantPowerSupply = false;

    // Management capabilities (device-specific)
    @Column(name = "management_interfaces")
    @Size(max = 256)
    private String managementInterfaces;

    @Column(name = "snmp_versions")
    @Size(max = 64)
    private String snmpVersions = "v1,v2c";

    @Column(name = "supports_ssh", nullable = false)
    private boolean supportsSsh = true;

    @Column(name = "supports_telnet", nullable = false)
    private boolean supportsTelnet = true;

    @Column(name = "web_management", nullable = false)
    private boolean webManagement = true;

    // Device-specific environmental specifications
    @Column(name = "fanless", nullable = false)
    private boolean fanless = false;

    @Column(name = "noise_level_db")
    private Integer noiseLevelDb;

    // Additional device specifications as key-value pairs
    @ElementCollection
    @CollectionTable(name = "device_model_specifications",
                    joinColumns = @JoinColumn(name = "device_model_id"))
    @MapKeyColumn(name = "spec_name")
    @Column(name = "spec_value")
    private Map<String, String> deviceSpecs = new HashMap<>();

    // Constructor
    protected DeviceModel(String name, String manufacturer, String modelNumber,
                         ComponentTypeEntity componentType, Integer powerConsumptionWatts) {
        super(name, manufacturer, modelNumber, componentType);
        this.powerConsumptionWatts = powerConsumptionWatts;
    }

    // Abstract methods specific to devices
    public abstract BigDecimal getPowerEfficiency();
    public abstract String getDeviceTypeSummary();

    // Device-specific business methods
    public String getManagementSummary() {
        List<String> methods = new ArrayList<>();
        if (webManagement) methods.add("Web");
        if (supportsSsh) methods.add("SSH");
        if (supportsTelnet) methods.add("Telnet");
        if (snmpVersions != null && !snmpVersions.trim().isEmpty()) {
            methods.add("SNMP(" + snmpVersions + ")");
        }
        return String.join(", ", methods);
    }

    public String getPowerSummary() {
        StringBuilder power = new StringBuilder();
        power.append(powerConsumptionWatts).append("W");

        if (maxPowerConsumptionWatts != null) {
            power.append(" (max ").append(maxPowerConsumptionWatts).append("W)");
        }

        if (redundantPowerSupply) {
            power.append(", Redundant PSU");
        }

        return power.toString();
    }

    // Device specification management
    public void addDeviceSpec(String name, String value) {
        if (name != null && !name.trim().isEmpty() && value != null) {
            deviceSpecs.put(name, value);
        }
    }

    public String getDeviceSpec(String name) {
        return deviceSpecs.get(name);
    }

    // Enhanced validation for devices
    @Override
    public boolean isValidModel() {
        if (!super.isValidModel()) {
            return false;
        }

        if (powerConsumptionWatts == null || powerConsumptionWatts <= 0) {
            return false;
        }

        // Validate power consumption
        if (maxPowerConsumptionWatts != null &&
            maxPowerConsumptionWatts < powerConsumptionWatts) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getModelDesignation() +
               ", " + powerConsumptionWatts + "W, " + getLifecycleStatus() + "]";
    }
}