package net.switchscope.model.component.device;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.switchscope.model.component.Component;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.location.Location;
import net.switchscope.validation.NoHtml;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract base class for all network devices
 * Contains only common device attributes shared by all device types
 */
@Entity
@DiscriminatorValue("DEVICE")
@Getter
@Setter
public abstract class Device extends Component {

    // Basic network management (common for all devices)
    @Column(name = "management_ip")
    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^$",
            message = "Invalid IP address format")
    private String managementIp;

    @Column(name = "subnet_mask")
    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^$",
            message = "Invalid subnet mask format")
    private String subnetMask;

    @Column(name = "default_gateway")
    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^$",
            message = "Invalid gateway IP address format")
    private String defaultGateway;

    @Column(name = "management_vlan")
    private Integer managementVlan;

    // Device firmware and software (common)
    @Column(name = "firmware_version")
    @Size(max = 128)
    @NoHtml
    private String firmwareVersion;

    @Column(name = "software_version")
    @Size(max = 128)
    @NoHtml
    private String softwareVersion;

    @Column(name = "boot_loader_version")
    @Size(max = 128)
    @NoHtml
    private String bootLoaderVersion;

    // Device credentials and access (common)
    @Column(name = "admin_username")
    @Size(max = 64)
    @NoHtml
    private String adminUsername;

    @Column(name = "admin_password")
    @Convert(converter = net.switchscope.config.EncryptedStringConverter.class)
    private String adminPassword;

    @Column(name = "snmp_community_read")
    @Size(max = 128)
    @NoHtml
    private String snmpCommunityRead;

    @Column(name = "snmp_community_write")
    @Size(max = 128)
    @NoHtml
    private String snmpCommunityWrite;

    @Column(name = "snmp_version")
    @Size(max = 16)
    private String snmpVersion = "v2c"; // v1, v2c, v3

    // Monitoring and management (common)
    @Column(name = "is_monitored", nullable = false)
    private boolean monitored = false;

    @Column(name = "monitoring_interval_seconds")
    private Integer monitoringIntervalSeconds = 300; // 5 minutes default

    @Column(name = "last_ping_time")
    private LocalDateTime lastPingTime;

    @Column(name = "last_ping_success", nullable = false)
    private boolean lastPingSuccess = false;

    @Column(name = "uptime_seconds")
    private Long uptimeSeconds;

    // Physical characteristics (common)
    @Column(name = "console_port_type")
    @Size(max = 32)
    private String consolePortType; // RJ45, USB, Serial

    @Column(name = "has_redundant_power", nullable = false)
    private boolean hasRedundantPower = false;

    @Column(name = "operating_temperature_min")
    private Integer operatingTemperatureMin;

    @Column(name = "operating_temperature_max")
    private Integer operatingTemperatureMax;

    // Device monitoring (common)
    @Column(name = "cpu_utilization_percent")
    private Integer cpuUtilizationPercent;

    @Column(name = "memory_utilization_percent")
    private Integer memoryUtilizationPercent;

    @Column(name = "temperature_celsius")
    private Integer temperatureCelsius;

    // Constructors
    public Device() {
        super();
    }

    public Device(UUID id, String name, ComponentTypeEntity componentType) {
        super(id, name, componentType);
    }

    public Device(UUID id, String name, String manufacturer, String model,
            String serialNumber, ComponentTypeEntity componentType) {
        super(id, name, manufacturer, model, serialNumber, componentType);
    }

    public Device(UUID id, String name, String manufacturer, String model,
            String serialNumber, ComponentStatusEntity status,
            ComponentTypeEntity componentType) {
        super(id, name, manufacturer, model, serialNumber, status, componentType);
    }

    // Implementation of Component abstract methods
    @Override
    public boolean isInstallable() {
        return true; // All devices are installable
    }

    // Device-specific abstract methods
    public abstract boolean isManaged();

    public abstract String getDefaultProtocol();

    public abstract String getDeviceType(); // SWITCH, ROUTER, ACCESS_POINT, etc.

    // Optional: method to check if device has ports (not all devices have ports)
    public abstract boolean hasPorts();

    // Common device methods
    public boolean hasManagementIp() {
        return managementIp != null && !managementIp.trim().isEmpty();
    }

    public boolean isSnmpEnabled() {
        return snmpCommunityRead != null && !snmpCommunityRead.trim().isEmpty();
    }

    public boolean canBePinged() {
        return hasManagementIp();
    }

    public boolean isReachable() {
        return lastPingSuccess && lastPingTime != null &&
                lastPingTime.isAfter(LocalDateTime.now().minusMinutes(10));
    }

    // Network configuration helper
    public String getNetworkConfiguration() {
        StringBuilder config = new StringBuilder();

        if (hasManagementIp()) {
            config.append("IP: ").append(managementIp);
            if (subnetMask != null) {
                config.append("/").append(subnetMask);
            }
            if (defaultGateway != null) {
                config.append(", GW: ").append(defaultGateway);
            }
            if (managementVlan != null) {
                config.append(", VLAN: ").append(managementVlan);
            }
        } else {
            config.append("No management IP configured");
        }

        return config.toString();
    }

    // Monitoring status
    public String getMonitoringStatus() {
        if (!monitored) {
            return "Not monitored";
        }

        if (!canBePinged()) {
            return "Cannot ping - no management IP";
        }

        if (isReachable()) {
            return "Online";
        }

        if (lastPingTime != null) {
            return "Last seen: " + lastPingTime.toString();
        }

        return "Unknown";
    }

    // Enhanced validation
    @Override
    public boolean isValidConfiguration() {
        if (!super.isValidConfiguration()) {
            return false;
        }

        // Managed devices should have management IP
        if (isManaged() && !hasManagementIp()) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getId() + "[" + getName() +
                (getModel() != null ? ", " + getModel() : "") +
                (managementIp != null ? ", " + managementIp : "") + "]";
    }
}