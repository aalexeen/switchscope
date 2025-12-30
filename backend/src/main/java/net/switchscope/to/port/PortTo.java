package net.switchscope.to.port;

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

/**
 * Base DTO for Port entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class PortTo extends NamedTo {

    // Device relationship
    @NotNull
    @Schema(description = "Device ID this port belongs to")
    private UUID deviceId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Device name")
    private String deviceName;

    // Connector relationship (optional)
    @Schema(description = "Connected connector ID")
    private UUID connectorId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Connector name")
    private String connectorName;

    // Basic port characteristics
    @NotNull
    @Min(1) @Max(9999)
    private Integer portNumber;

    @Size(max = 64)
    @NoHtml
    private String portLabel;

    // Port status and state
    @Size(max = 32)
    @NoHtml
    private String status;

    @Size(max = 32)
    @NoHtml
    private String adminStatus;

    @Size(max = 32)
    @NoHtml
    private String operationalStatus;

    // Speed and duplex
    @Min(0)
    private Long speedMbps;

    @Min(0)
    private Long maxSpeedMbps;

    @Size(max = 16)
    @NoHtml
    private String duplexMode;

    private Boolean autoNegotiation;

    // Physical characteristics
    @Size(max = 32)
    @NoHtml
    private String connectorType;

    @Size(max = 32)
    @NoHtml
    private String mediumType;

    // VLAN and network configuration
    @Min(1) @Max(4094)
    private Integer accessVlan;

    @Min(1) @Max(4094)
    private Integer nativeVlan;

    @Size(max = 16)
    @NoHtml
    private String portMode;

    // Power over Ethernet
    private Boolean poeEnabled;

    @Min(0) @Max(8)
    private Integer poeClass;

    @DecimalMin("0.0")
    private Double poePowerWatts;

    @DecimalMin("0.0")
    private Double poeMaxPowerWatts;

    // Traffic statistics
    private Long bytesIn;

    private Long bytesOut;

    private Long packetsIn;

    private Long packetsOut;

    private Long errorsIn;

    private Long errorsOut;

    private Long discardsIn;

    private Long discardsOut;

    // Timestamps
    private OffsetDateTime lastChange;

    private OffsetDateTime lastActivity;

    private OffsetDateTime statsLastReset;

    // Configuration and notes
    @Size(max = 1024)
    @NoHtml
    private String configurationNotes;

    private Boolean monitoringEnabled;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String portType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Map<String, String> specifications;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean up;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean down;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean adminEnabled;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean available;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean connected;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean poeCapable;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean poePowered;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean trunkPort;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean accessPort;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Double speedGbps;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Double maxSpeedGbps;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Double utilizationPercent;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long totalPackets;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long totalBytes;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long totalErrors;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
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

