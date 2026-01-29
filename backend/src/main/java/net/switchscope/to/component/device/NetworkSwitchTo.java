package net.switchscope.to.component.device;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;

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
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID switchModelId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Switch model name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String switchModelName;

    // Switch-specific capabilities
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer maxPorts;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsPoe;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer poeBudgetWatts;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsStacking;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer stackId;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer stackPriority;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer maxStackMembers;

    // Performance characteristics
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double switchingCapacityGbps;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double forwardingRateMpps;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer macAddressTableSize;

    // VLAN support
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean vlanSupport;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer maxVlans;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean spanningTreeSupport;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean qosSupport;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean portMirroring;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean linkAggregation;

    // VLAN configuration
    @Min(1) @Max(4094)
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer nativeVlan;

    @Min(1) @Max(4094)
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer voiceVlan;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double packetBufferSizeMb;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean poeCapability;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Double availablePoeBudget;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean stackable;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean inStack;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean stackMaster;

    public NetworkSwitchTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public NetworkSwitchTo(UUID id, String name) {
        super(id, name);
    }
}

