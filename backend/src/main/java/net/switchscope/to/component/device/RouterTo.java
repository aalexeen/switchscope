package net.switchscope.to.component.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for Router entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RouterTo extends DeviceTo {

    // Router-specific capabilities
    private Integer maxRoutes;

    private Boolean supportsBgp;

    private Boolean supportsOspf;

    private Boolean supportsEigrp;

    private Boolean supportsRip;

    private Boolean supportsStaticRouting;

    // NAT capabilities
    private Boolean supportsNat;

    private Integer maxNatSessions;

    // VPN capabilities
    private Boolean supportsIpsecVpn;

    private Integer maxVpnTunnels;

    // Firewall capabilities
    private Boolean supportsAcl;

    private Boolean supportsFirewall;

    // Routing performance
    private Double routingThroughputGbps;

    private Long packetsPerSecond;

    // WAN interfaces
    private Set<String> wanInterfaceTypes = new HashSet<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean enterpriseRouter;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean vpnCapability;

    public RouterTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public RouterTo(UUID id, String name) {
        super(id, name);
    }
}

