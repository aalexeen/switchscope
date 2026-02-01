package net.switchscope.model.component.catalog.device;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.validation.NoHtml;

import java.math.BigDecimal;
import java.util.*;

/**
 * Router Model Catalog Entry - represents a specific router model from a manufacturer
 */
@Entity
@DiscriminatorValue("ROUTER_MODEL")
@Getter
@Setter
@NoArgsConstructor
public class RouterModel extends DeviceModel {

    // Router-specific fields only (common fields moved to DeviceModel)

    // Router classification
    @Column(name = "router_class")
    @Size(max = 64)
    @NoHtml
    private String routerClass; // EDGE, BRANCH, ENTERPRISE, CORE

    @Column(name = "target_deployment")
    @Size(max = 128)
    @NoHtml
    private String targetDeployment; // Small Office, Branch Office, Data Center

    // Physical specifications (router-specific)
    @Column(name = "rack_units")
    @Min(1) @Max(10)
    private Integer rackUnits = 1;

    @Column(name = "form_factor")
    @Size(max = 64)
    @NoHtml
    private String formFactor; // RACK_MOUNT, DESKTOP, COMPACT

    // Interface specifications
    @Column(name = "ethernet_ports", nullable = false)
    @NotNull
    @Min(1) @Max(64)
    private Integer ethernetPorts;

    @Column(name = "gigabit_ethernet_ports")
    @Min(0)
    private Integer gigabitEthernetPorts = 0;

    @Column(name = "ten_gig_ports")
    @Min(0)
    private Integer tenGigPorts = 0;

    @Column(name = "sfp_ports")
    @Min(0)
    private Integer sfpPorts = 0;

    @Column(name = "serial_wan_ports")
    @Min(0)
    private Integer serialWanPorts = 0;

    // Performance specifications
    @Column(name = "routing_throughput_gbps")
    private BigDecimal routingThroughputGbps;

    @Column(name = "firewall_throughput_gbps")
    private BigDecimal firewallThroughputGbps;

    @Column(name = "vpn_throughput_mbps")
    private BigDecimal vpnThroughputMbps;

    // Routing capabilities
    @Column(name = "supports_bgp", nullable = false)
    private boolean supportsBgp = false;

    @Column(name = "supports_ospf", nullable = false)
    private boolean supportsOspf = false;

    @Column(name = "supports_nat", nullable = false)
    private boolean supportsNat = false;

    @Column(name = "supports_firewall", nullable = false)
    private boolean supportsFirewall = false;

    @Column(name = "supports_ipsec_vpn", nullable = false)
    private boolean supportsIpsecVpn = false;

    @Column(name = "max_vpn_tunnels")
    private Integer maxVpnTunnels;

    // WAN interface types supported
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "router_model_wan_interfaces",
                    joinColumns = @JoinColumn(name = "router_model_id"))
    @Column(name = "interface_type")
    private Set<String> supportedWanInterfaces = new HashSet<>();

    // Constructor
    public RouterModel(String name, String manufacturer, String modelNumber,
                      ComponentTypeEntity componentType, Integer ethernetPorts,
                      Integer powerConsumptionWatts) {
        super(name, manufacturer, modelNumber, componentType, powerConsumptionWatts);
        this.ethernetPorts = ethernetPorts;
    }

    // Implementation of abstract methods from DeviceModel
    @Override
    public BigDecimal getPowerEfficiency() {
        if (getPowerConsumptionWatts() == null || routingThroughputGbps == null ||
            routingThroughputGbps.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return routingThroughputGbps.divide(
            BigDecimal.valueOf(getPowerConsumptionWatts()), 3, java.math.RoundingMode.HALF_UP);
    }

    @Override
    public String getDeviceTypeSummary() {
        return getInterfaceSummary() +
               (hasVpnCapability() ? ", VPN" : "") +
               (supportsFirewall ? ", Firewall" : "");
    }

    // Router-specific business methods
    public boolean isEnterpriseRouter() {
        return "ENTERPRISE".equals(routerClass) || "CORE".equals(routerClass);
    }

    public boolean hasAdvancedRouting() {
        return supportsBgp || supportsOspf;
    }

    public boolean hasVpnCapability() {
        return supportsIpsecVpn && maxVpnTunnels != null && maxVpnTunnels > 0;
    }

    public String getInterfaceSummary() {
        List<String> interfaces = new ArrayList<>();

        if (gigabitEthernetPorts != null && gigabitEthernetPorts > 0) {
            interfaces.add(gigabitEthernetPorts + "x GE");
        }
        if (tenGigPorts != null && tenGigPorts > 0) {
            interfaces.add(tenGigPorts + "x 10GE");
        }
        if (sfpPorts != null && sfpPorts > 0) {
            interfaces.add(sfpPorts + "x SFP");
        }

        return String.join(", ", interfaces);
    }

    // Enhanced validation specific to routers
    @Override
    public boolean isValidModel() {
        if (!super.isValidModel()) {
            return false;
        }

        if (ethernetPorts == null || ethernetPorts <= 0) {
            return false;
        }

        // VPN validation
        if (supportsIpsecVpn && (maxVpnTunnels == null || maxVpnTunnels <= 0)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getManufacturer() + " " + getModelNumber() +
               " (" + ethernetPorts + " ports, " + getPowerConsumptionWatts() + "W" +
               (hasVpnCapability() ? ", VPN" : "") + ")";
    }
}
