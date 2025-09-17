package net.switchscope.model.component.device;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.HasPortsImpl;
import net.switchscope.model.component.catalog.device.SwitchModel;
import net.switchscope.model.location.Location;
import net.switchscope.model.port.Port;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Network switch device implementation
 * Contains switch-specific functionality and attributes
 */
@Entity
@DiscriminatorValue("NETWORK_SWITCH")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NetworkSwitch extends HasPortsImpl {

    // Link to switch model catalog
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "switch_model_id")
    private SwitchModel switchModel;

    // Switch-specific capabilities
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

    // Switch performance characteristics
    @Column(name = "switching_capacity_gbps")
    private Double switchingCapacityGbps;

    @Column(name = "forwarding_rate_mpps")
    private Double forwardingRateMpps;

    @Column(name = "mac_address_table_size")
    private Integer macAddressTableSize;

    // VLAN support (switch-specific)
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
    @Min(1)
    @Max(4094)
    private Integer nativeVlan = 1;

    @Column(name = "voice_vlan")
    @Min(1)
    @Max(4094)
    private Integer voiceVlan;

    @Column(name = "packet_buffer_size_mb")
    private Double packetBufferSizeMb;

    // Constructors
    public NetworkSwitch(UUID id, String name, ComponentTypeEntity componentType) {
        super(id, name, componentType);
    }

    public NetworkSwitch(UUID id, String name, String manufacturer, String model,
            String serialNumber, ComponentTypeEntity componentType) {
        super(id, name, manufacturer, model, serialNumber, componentType);
    }

    public NetworkSwitch(UUID id, String name, String manufacturer, String model,
            String serialNumber, ComponentStatusEntity status, Location location,
            ComponentTypeEntity componentType) {
        super(id, name, manufacturer, model, serialNumber, status, location, componentType);
    }

    // Device abstract method implementations
    @Override
    public boolean isManaged() {
        return hasManagementIp() && isSnmpEnabled();
    }

    @Override
    public String getDefaultProtocol() {
        return "SNMP";
    }

    @Override
    public String getDeviceType() {
        return "SWITCH";
    }

    @Override
    public Map<String, String> getSpecifications() {
        Map<String, String> specs = new HashMap<>();

        if (maxPorts != null) specs.put("Max Ports", maxPorts.toString());
        if (supportsPoe) {
            specs.put("PoE Support", "Yes");
            if (poeBudgetWatts != null) specs.put("PoE Budget", poeBudgetWatts + "W");
        }
        if (supportsStacking) specs.put("Stacking", "Supported");
        if (switchingCapacityGbps != null) specs.put("Switching Capacity", switchingCapacityGbps + " Gbps");
        if (forwardingRateMpps != null) specs.put("Forwarding Rate", forwardingRateMpps + " Mpps");
        if (vlanSupport) specs.put("VLAN Support", "Yes");

        return specs;
    }

    // Switch-specific methods
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

    public boolean isStackable() {
        return supportsStacking;
    }

    public boolean isInStack() {
        return stackId != null;
    }

    public boolean isStackMaster() {
        return isInStack() && stackPriority != null && stackPriority == 1;
    }

    public boolean supportsVlans() {
        return vlanSupport;
    }

    public String getEffectiveManufacturer() {
        return switchModel != null ? switchModel.getManufacturer() : getManufacturer();
    }

    public String getEffectiveModel() {
        return switchModel != null ? switchModel.getModelNumber() : getModel();
    }

    // Enhanced validation
    @Override
    public boolean isValidConfiguration() {
        if (!super.isValidConfiguration()) {
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

    @Override
    public String toString() {
        return "NetworkSwitch:" + getId() + "[" + getName() +
                (getModel() != null ? ", " + getModel() : "") +
                (getManagementIp() != null ? ", " + getManagementIp() : "") +
                ", " + ports.size() + " ports]";
    }
}