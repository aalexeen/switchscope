package net.switchscope.to.component.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;

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
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer maxRoutes;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsBgp;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsOspf;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsEigrp;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsRip;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsStaticRouting;

    // NAT capabilities
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsNat;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer maxNatSessions;

    // VPN capabilities
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsIpsecVpn;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer maxVpnTunnels;

    // Firewall capabilities
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsAcl;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsFirewall;

    // Routing performance
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double routingThroughputGbps;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Long packetsPerSecond;

    // WAN interfaces
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Set<String> wanInterfaceTypes = new HashSet<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean enterpriseRouter;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean vpnCapability;

    public RouterTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public RouterTo(UUID id, String name) {
        super(id, name);
    }
}

