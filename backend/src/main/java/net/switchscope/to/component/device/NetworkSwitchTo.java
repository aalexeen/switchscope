package net.switchscope.to.component.device;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for NetworkSwitch entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class NetworkSwitchTo extends DeviceTo {

    @Schema(description = "Switch model catalog ID")
    private UUID switchModelId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Switch model name")
    private String switchModelName;

    // Switch-specific capabilities
    private Integer maxPorts;

    private Boolean supportsPoe;

    private Integer poeBudgetWatts;

    private Boolean supportsStacking;

    private Integer stackId;

    private Integer stackPriority;

    private Integer maxStackMembers;

    // Performance characteristics
    private Double switchingCapacityGbps;

    private Double forwardingRateMpps;

    private Integer macAddressTableSize;

    // VLAN support
    private Boolean vlanSupport;

    private Integer maxVlans;

    private Boolean spanningTreeSupport;

    private Boolean qosSupport;

    private Boolean portMirroring;

    private Boolean linkAggregation;

    // VLAN configuration
    @Min(1) @Max(4094)
    private Integer nativeVlan;

    @Min(1) @Max(4094)
    private Integer voiceVlan;

    private Double packetBufferSizeMb;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean poeCapability;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Double availablePoeBudget;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean stackable;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean inStack;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean stackMaster;

    public NetworkSwitchTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public NetworkSwitchTo(UUID id, String name) {
        super(id, name);
    }
}

