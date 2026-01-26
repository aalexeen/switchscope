package net.switchscope.to.component.catalog.device;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
import net.switchscope.validation.NoHtml;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for Switch Model catalog entry
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SwitchModelTo extends DeviceModelTo {

    // Switch classification
    @Size(max = 32)
    @NoHtml
    private String switchClass;

    @Size(max = 32)
    @NoHtml
    private String switchTier;

    private Boolean stackable = false;

    private Integer maxStackUnits;

    // Physical specifications
    @NotNull
    @Min(1) @Max(10)
    private Integer rackUnits;

    @Size(max = 32)
    @NoHtml
    private String formFactor;

    // Port specifications
    @NotNull
    @Min(1) @Max(128)
    private Integer totalPorts;

    @Min(0)
    private Integer gigabitPorts;

    @Min(0)
    private Integer fastEthernetPorts;

    @Min(0)
    private Integer tenGigPorts;

    @Min(0)
    private Integer sfpPorts;

    @Min(0)
    private Integer sfpPlusPorts;

    @Min(0)
    private Integer consolePorts;

    // PoE specifications
    private Boolean supportsPoe = false;

    private Integer poeBudgetWatts;

    @Size(max = 32)
    @NoHtml
    private String poeStandard;

    // Performance specifications
    private BigDecimal switchingCapacityGbps;

    private BigDecimal forwardingRateMpps;

    private Integer macAddressTableSize;

    private BigDecimal bufferSizeMb;

    private BigDecimal latencyMicroseconds;

    // Network features
    private Integer maxVlans;

    private Boolean supportsSpanningTree = true;

    private Boolean supportsLinkAggregation = false;

    private Boolean supportsQos = false;

    private Boolean supportsIgmpSnooping = false;

    private Boolean supportsPortMirroring = false;

    private Boolean supportsVoiceVlan = false;

    private Boolean supportsAcl = false;

    private Boolean layer3Routing = false;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String portSummary;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean managed;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean enterprise;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean hasPoePlus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean supportsHighSpeed;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private BigDecimal powerEfficiency;

    public SwitchModelTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public SwitchModelTo(UUID id, String name) {
        super(id, name);
    }
}

