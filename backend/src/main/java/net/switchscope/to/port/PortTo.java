package net.switchscope.to.port;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.to.NamedTo;
import net.switchscope.validation.NoHtml;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;

/**
 * Base DTO for Port entity.
 * <p>
 * Polymorphic over the existing {@code portType} property, whose values mirror the JPA
 * {@code @DiscriminatorValue} of {@link net.switchscope.model.port.EthernetPort} and
 * {@link net.switchscope.model.port.FiberPort}.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "portType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EthernetPortTo.class, name = "ETHERNET"),
        @JsonSubTypes.Type(value = FiberPortTo.class, name = "FIBER")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class PortTo extends NamedTo {

    // Device relationship
    @NotNull
    @Schema(description = "Device ID this port belongs to")
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private UUID deviceId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Device name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String deviceName;

    // Connector relationship (optional)
    @Schema(description = "Connected connector ID")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID connectorId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Connector name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String connectorName;

    // Basic port characteristics
    @NotNull
    @Min(1) @Max(9999)
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private Integer portNumber;

    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String portLabel;

    // Port status and state
    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String status;

    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String adminStatus;

    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String operationalStatus;

    // Speed and duplex
    @Min(0)
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Long speedMbps;

    @Min(0)
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Long maxSpeedMbps;

    @Size(max = 16)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String duplexMode;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean autoNegotiation;

    // Physical characteristics
    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String connectorType;

    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String mediumType;

    // VLAN and network configuration
    @Min(1) @Max(4094)
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer accessVlan;

    @Min(1) @Max(4094)
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer nativeVlan;

    @Size(max = 16)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String portMode;

    // Power over Ethernet
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean poeEnabled;

    @Min(0) @Max(8)
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer poeClass;

    @DecimalMin("0.0")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double poePowerWatts;

    @DecimalMin("0.0")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double poeMaxPowerWatts;

    // Traffic statistics
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Long bytesIn;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Long bytesOut;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Long packetsIn;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Long packetsOut;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Long errorsIn;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Long errorsOut;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Long discardsIn;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Long discardsOut;

    // Timestamps
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private OffsetDateTime lastChange;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private OffsetDateTime lastActivity;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private OffsetDateTime statsLastReset;

    // Configuration and notes
    @Size(max = 1024)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String configurationNotes;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean monitoringEnabled;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String portType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Map<String, String> specifications;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean up;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean down;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean adminEnabled;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean available;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean connected;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean poeCapable;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean poePowered;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean trunkPort;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean accessPort;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Double speedGbps;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Double maxSpeedGbps;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Double utilizationPercent;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Long totalPackets;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Long totalBytes;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Long totalErrors;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Double errorRate;

    protected PortTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, name);
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
    }

    protected PortTo(UUID id, String name) {
        super(id, name);
    }
}

