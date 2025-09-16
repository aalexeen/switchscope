package net.switchscope.model.component.device;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import net.switchscope.model.component.Component;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.catalog.SwitchModel;
import net.switchscope.model.location.Location;
import net.switchscope.model.port.Port;
import net.switchscope.validation.NoHtml;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Abstract base class for all network devices
 * Updated to work with strict ComponentTypeEntity references
 */
@Entity
@DiscriminatorValue("DEVICE")
public abstract class Device extends Component implements HasPorts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "switch_model_id")
    private SwitchModel switchModel;

    // Device-specific networking fields
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

    // Firmware and software
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

    // Device credentials and access
    @Column(name = "admin_username")
    @Size(max = 64)
    @NoHtml
    private String adminUsername;

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

    // Device capabilities
    @Column(name = "max_ports")
    private Integer maxPorts;

    @Column(name = "supports_poe", nullable = false)
    private boolean supportsPoe = false;

    @Column(name = "poe_budget_watts")
    private Integer poeBudgetWatts;

    @Column(name = "supports_stacking", nullable = false)
    private boolean supportsStacking = false;

    @Column(name = "stack_id")
    private Integer stackId;

    @Column(name = "stack_priority")
    private Integer stackPriority;

    @Column(name = "max_stack_members")
    private Integer maxStackMembers;

    // Performance characteristics
    @Column(name = "switching_capacity_gbps")
    private Double switchingCapacityGbps;

    @Column(name = "forwarding_rate_mpps")
    private Double forwardingRateMpps;

    @Column(name = "mac_address_table_size")
    private Integer macAddressTableSize;

    // Monitoring and management
    @Column(name = "is_monitored", nullable = false)
    private boolean monitored = false;

    @Column(name = "monitoring_interval_seconds")
    private Integer monitoringIntervalSeconds = 300; // 5 minutes default

    @Column(name = "last_ping_time")
    private java.time.LocalDateTime lastPingTime;

    @Column(name = "last_ping_success", nullable = false)
    private boolean lastPingSuccess = false;

    @Column(name = "uptime_seconds")
    private Long uptimeSeconds;

    // Physical characteristics specific to devices
    @Column(name = "console_port_type")
    @Size(max = 32)
    private String consolePortType; // RJ45, USB, Serial

    @Column(name = "has_redundant_power", nullable = false)
    private boolean hasRedundantPower = false;

    @Column(name = "operating_temperature_min")
    private Integer operatingTemperatureMin;

    @Column(name = "operating_temperature_max")
    private Integer operatingTemperatureMax;

    @Column(name = "vlan_support", nullable = false)
    private boolean vlanSupport = false;

    @Column(name = "max_vlans")
    private Integer maxVlans;

    @Column(name = "spanning_tree_support", nullable = false)
    private boolean spanningTreeSupport = false;

    @Column(name = "qos_support", nullable = false)
    private boolean qosSupport = false;

    @Column(name = "port_mirroring", nullable = false)
    private boolean portMirroring = false;

    @Column(name = "link_aggregation", nullable = false)
    private boolean linkAggregation = false;

    // VLAN configuration
    @Column(name = "native_vlan")
    @Min(1) @Max(4094)
    private Integer nativeVlan = 1;

    @Column(name = "voice_vlan")
    @Min(1) @Max(4094)
    private Integer voiceVlan;

    @Column(name = "packet_buffer_size_mb")
    private Double packetBufferSizeMb;

    // Switch-specific monitoring
    @Column(name = "cpu_utilization_percent")
    private Integer cpuUtilizationPercent;

    @Column(name = "memory_utilization_percent")
    private Integer memoryUtilizationPercent;

    @Column(name = "temperature_celsius")
    private Integer temperatureCelsius;

    // Relationships
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Port> ports = new ArrayList<>();

    // Constructors
    protected Device() {
        super();
    }

    protected Device(UUID id, String name, ComponentTypeEntity componentType) {
        super(id, name, componentType);
    }

    protected Device(UUID id, String name, String manufacturer, String model,
                    String serialNumber, ComponentTypeEntity componentType) {
        super(id, name, manufacturer, model, serialNumber, componentType);
    }

    protected Device(UUID id, String name, String manufacturer, String model,
                    String serialNumber, ComponentStatusEntity status, Location location,
                    ComponentTypeEntity componentType) {
        super(id, name, manufacturer, model, serialNumber, status, location, componentType);
    }

    // Implementation of Component abstract methods
    @Override
    public boolean canHoldOtherComponents() {
        return true; // Devices typically don't contain other components (except modules)
    }

    @Override
    public boolean isInstallable() {
        return true; // Devices are installable
    }

    // HasPorts interface implementation
    @Override
    public List<Port> getPorts() {
        return new ArrayList<>(ports);
    }

    @Override
    public void addPort(Port port) {
        if (port != null && !ports.contains(port)) {
            ports.add(port);
            port.setDevice(this);
        }
    }

    @Override
    public void removePort(Port port) {
        if (port != null && ports.contains(port)) {
            ports.remove(port);
            port.setDevice(null);
        }
    }

    @Override
    public Port getPortByNumber(int portNumber) {
        return ports.stream()
                .filter(port -> port.getPortNumber() == portNumber)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Port> getAvailablePorts() {
        return ports.stream()
                .filter(Port::isAvailable)
                .toList();
    }

    @Override
    public int getPortCount() {
        return ports.size();
    }

    @Override
    public int getAvailablePortCount() {
        return (int) ports.stream().filter(Port::isAvailable).count();
    }

    // Device-specific abstract methods
    public abstract boolean isManaged();
    public abstract String getDefaultProtocol();
    public abstract String getDeviceType(); // SWITCH, ROUTER, etc.

    // Management and monitoring methods
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
               lastPingTime.isAfter(java.time.LocalDateTime.now().minusMinutes(10));
    }

    // PoE methods
    public boolean hasPoeCapability() {
        return supportsPoe && poeBudgetWatts != null && poeBudgetWatts > 0;
    }

    public int getAvailablePoeBudget() {
        if (!hasPoeCapability()) {
            return 0;
        }

        int usedPower = ports.stream()
                .filter(Port::isPoeEnabled)
                .mapToInt(Port::getPoeConsumptionWatts)
                .sum();

        return Math.max(0, poeBudgetWatts - usedPower);
    }

    // Stacking methods
    public boolean isStackable() {
        return supportsStacking;
    }

    public boolean isInStack() {
        return stackId != null;
    }

    public boolean isStackMaster() {
        return isInStack() && stackPriority != null && stackPriority == 1;
    }

    // Performance validation
    public boolean hasPerformanceSpecs() {
        return switchingCapacityGbps != null || forwardingRateMpps != null;
    }

    public boolean isSwitch() {
        return componentType.getComponentTypeName() != null && "SWITCH".equals(componentType.getComponentTypeName());
    }

    public boolean isRouter() {
        return componentType != null && "ROUTER".equals(componentType.getCode());
    }

    public boolean supportsVlans() {
        return isSwitch() && Boolean.TRUE.equals(vlanSupport);
    }

    public String getEffectiveManufacturer() {
        return switchModel != null ? switchModel.getManufacturer() : manufacturer;
    }

    public String getEffectiveModel() {
        return switchModel != null ? switchModel.getModelNumber() : model;
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

        // PoE validation
        if (supportsPoe && (poeBudgetWatts == null || poeBudgetWatts <= 0)) {
            return false;
        }

        // Stack validation
        if (isInStack() && (stackPriority == null || stackPriority < 1)) {
            return false;
        }

        // Port count validation
        if (maxPorts != null && ports.size() > maxPorts) {
            return false;
        }

        return true;
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

    // Enhanced toString for devices
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getId() + "[" + getName() +
               (getModel() != null ? ", " + getModel() : "") +
               (managementIp != null ? ", " + managementIp : "") +
               ", " + ports.size() + " ports]";
    }
}