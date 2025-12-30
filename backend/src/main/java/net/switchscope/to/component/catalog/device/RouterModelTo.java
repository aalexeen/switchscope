package net.switchscope.to.component.catalog.device;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.validation.NoHtml;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for Router Model catalog entry
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RouterModelTo extends DeviceModelTo {

    // Router classification
    @Size(max = 64)
    @NoHtml
    private String routerClass;

    @Size(max = 128)
    @NoHtml
    private String targetDeployment;

    // Physical specifications
    @Min(1) @Max(10)
    private Integer rackUnits;

    @Size(max = 64)
    @NoHtml
    private String formFactor;

    // Interface specifications
    @NotNull
    @Min(1) @Max(64)
    private Integer ethernetPorts;

    @Min(0)
    private Integer gigabitEthernetPorts;

    @Min(0)
    private Integer tenGigPorts;

    @Min(0)
    private Integer sfpPorts;

    @Min(0)
    private Integer serialWanPorts;

    // Performance specifications
    private BigDecimal routingThroughputGbps;

    private BigDecimal firewallThroughputGbps;

    private BigDecimal vpnThroughputMbps;

    // Routing capabilities
    private Boolean supportsBgp = false;

    private Boolean supportsOspf = false;

    private Boolean supportsNat = false;

    private Boolean supportsFirewall = false;

    private Boolean supportsIpsecVpn = false;

    private Integer maxVpnTunnels;

    // WAN interface types supported
    private Set<String> supportedWanInterfaces = new HashSet<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String interfaceSummary;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean enterpriseRouter;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean hasAdvancedRouting;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean hasVpnCapability;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal powerEfficiency;

    public RouterModelTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public RouterModelTo(UUID id, String name) {
        super(id, name);
    }
}

