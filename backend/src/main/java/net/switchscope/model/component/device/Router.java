package net.switchscope.model.component.device;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.HasPortsImpl;

import java.util.*;

/**
 * Router device implementation
 * Contains router-specific functionality and attributes
 */
@Entity
@DiscriminatorValue("ROUTER")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Router extends HasPortsImpl {

    // Router-specific capabilities
    @Column(name = "max_routes")
    private Integer maxRoutes;

    @Column(name = "supports_bgp", nullable = false)
    private boolean supportsBgp = false;

    @Column(name = "supports_ospf", nullable = false)
    private boolean supportsOspf = false;

    @Column(name = "supports_eigrp", nullable = false)
    private boolean supportsEigrp = false;

    @Column(name = "supports_rip", nullable = false)
    private boolean supportsRip = false;

    @Column(name = "supports_static_routing", nullable = false)
    private boolean supportsStaticRouting = true;

    // NAT capabilities
    @Column(name = "supports_nat", nullable = false)
    private boolean supportsNat = false;

    @Column(name = "max_nat_sessions")
    private Integer maxNatSessions;

    // VPN capabilities
    @Column(name = "supports_ipsec_vpn", nullable = false)
    private boolean supportsIpsecVpn = false;

    @Column(name = "max_vpn_tunnels")
    private Integer maxVpnTunnels;

    // Firewall capabilities
    @Column(name = "supports_acl", nullable = false)
    private boolean supportsAcl = false;

    @Column(name = "supports_firewall", nullable = false)
    private boolean supportsFirewall = false;

    // Routing performance
    @Column(name = "routing_throughput_gbps")
    private Double routingThroughputGbps;

    @Column(name = "packets_per_second")
    private Long packetsPerSecond;

    // WAN interfaces
    @ElementCollection
    @CollectionTable(name = "router_wan_interfaces",
            joinColumns = @JoinColumn(name = "router_id"))
    @Column(name = "interface_type")
    private Set<String> wanInterfaceTypes = new HashSet<>(); // Ethernet, Serial, T1, etc.

    // Constructors
    public Router(UUID id, String name, ComponentTypeEntity componentType) {
        super(id, name, componentType);
    }

    public Router(UUID id, String name, String manufacturer, String model,
            String serialNumber, ComponentTypeEntity componentType) {
        super(id, name, manufacturer, model, serialNumber, componentType);
    }

    // Device abstract method implementations
    @Override
    public boolean isManaged() {
        return hasManagementIp();
    }

    @Override
    public String getDefaultProtocol() {
        return "SSH";
    }

    @Override
    public String getDeviceType() {
        return "ROUTER";
    }

    @Override
    public Map<String, String> getSpecifications() {
        Map<String, String> specs = new HashMap<>();

        if (maxRoutes != null) specs.put("Max Routes", maxRoutes.toString());
        if (supportsBgp) specs.put("BGP", "Supported");
        if (supportsOspf) specs.put("OSPF", "Supported");
        if (supportsNat) {
            specs.put("NAT", "Supported");
            if (maxNatSessions != null) specs.put("Max NAT Sessions", maxNatSessions.toString());
        }
        if (supportsIpsecVpn) {
            specs.put("IPSec VPN", "Supported");
            if (maxVpnTunnels != null) specs.put("Max VPN Tunnels", maxVpnTunnels.toString());
        }
        if (routingThroughputGbps != null) specs.put("Routing Throughput", routingThroughputGbps + " Gbps");

        return specs;
    }

    // Router-specific methods
    public boolean isEnterpriseRouter() {
        return supportsBgp || supportsOspf;
    }

    public boolean hasVpnCapability() {
        return supportsIpsecVpn && maxVpnTunnels != null && maxVpnTunnels > 0;
    }

    public void addWanInterfaceType(String interfaceType) {
        if (interfaceType != null && !interfaceType.trim().isEmpty()) {
            wanInterfaceTypes.add(interfaceType);
        }
    }

    @Override
    public String toString() {
        return "Router:" + getId() + "[" + getName() +
                (getModel() != null ? ", " + getModel() : "") +
                (getManagementIp() != null ? ", " + getManagementIp() : "") +
                ", " + ports.size() + " ports]";
    }
}