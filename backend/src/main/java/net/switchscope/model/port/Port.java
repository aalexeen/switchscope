package net.switchscope.model.port;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.NamedEntity;
import net.switchscope.model.component.device.Device;
import net.switchscope.model.component.connectivity.Connector;
import net.switchscope.validation.NoHtml;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Port entity - represents a network port on a device
 * Full implementation according to network infrastructure schema
 */
@Entity
@Table(name = "ports")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "port_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class Port extends NamedEntity {

    // Device relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Device device;

    // One-to-one relationship with Connector (nullable)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connector_id")
    private Connector connector;

    // Basic port characteristics
    @Column(name = "port_number", nullable = false)
    @Min(1) @Max(9999)
    private Integer portNumber;

    @Column(name = "port_label")
    @Size(max = 64)
    @NoHtml
    private String portLabel; // Physical label on the port

    // Port status and state
    @Column(name = "status", nullable = false)
    @Size(max = 32)
    @NoHtml
    private String status = "DOWN"; // UP, DOWN, TESTING, DORMANT

    @Column(name = "admin_status", nullable = false)
    @Size(max = 32)
    @NoHtml
    private String adminStatus = "UP"; // UP, DOWN, TESTING

    @Column(name = "operational_status", nullable = false)
    @Size(max = 32)
    @NoHtml
    private String operationalStatus = "DOWN"; // UP, DOWN, TESTING, UNKNOWN, DORMANT, NOT_PRESENT, LOWER_LAYER_DOWN

    // Speed and duplex
    @Column(name = "speed_mbps")
    @Min(0)
    private Long speedMbps; // Current speed in Mbps

    @Column(name = "max_speed_mbps")
    @Min(0)
    private Long maxSpeedMbps; // Maximum supported speed in Mbps

    @Column(name = "duplex_mode")
    @Size(max = 16)
    @NoHtml
    private String duplexMode; // FULL, HALF, AUTO

    @Column(name = "auto_negotiation", nullable = false)
    private Boolean autoNegotiation = true;

    // Physical characteristics
    @Column(name = "connector_type")
    @Size(max = 32)
    @NoHtml
    private String connectorType; // RJ45, SFP, SFP+, QSFP, etc.

    @Column(name = "medium_type")
    @Size(max = 32)
    @NoHtml
    private String mediumType; // COPPER, FIBER, WIRELESS

    // VLAN and network configuration
    @Column(name = "access_vlan")
    @Min(1) @Max(4094)
    private Integer accessVlan;

    @Column(name = "native_vlan")
    @Min(1) @Max(4094)
    private Integer nativeVlan;

    @Column(name = "port_mode")
    @Size(max = 16)
    @NoHtml
    private String portMode = "ACCESS"; // ACCESS, TRUNK, HYBRID

    // Power over Ethernet
    @Column(name = "poe_enabled", nullable = false)
    private Boolean poeEnabled = false;

    @Column(name = "poe_class")
    @Min(0) @Max(8)
    private Integer poeClass; // PoE class (0-8)

    @Column(name = "poe_power_watts")
    @DecimalMin("0.0")
    private Double poePowerWatts; // Current PoE power consumption

    @Column(name = "poe_max_power_watts")
    @DecimalMin("0.0")
    private Double poeMaxPowerWatts; // Maximum PoE power available

    // Traffic statistics
    @Column(name = "bytes_in")
    private Long bytesIn = 0L;

    @Column(name = "bytes_out")
    private Long bytesOut = 0L;

    @Column(name = "packets_in")
    private Long packetsIn = 0L;

    @Column(name = "packets_out")
    private Long packetsOut = 0L;

    @Column(name = "errors_in")
    private Long errorsIn = 0L;

    @Column(name = "errors_out")
    private Long errorsOut = 0L;

    @Column(name = "discards_in")
    private Long discardsIn = 0L;

    @Column(name = "discards_out")
    private Long discardsOut = 0L;

    // Timestamps
    @Column(name = "last_change")
    private OffsetDateTime lastChange;

    @Column(name = "last_activity")
    private OffsetDateTime lastActivity;

    @Column(name = "stats_last_reset")
    private OffsetDateTime statsLastReset;

    // Configuration and notes
    @Column(name = "configuration_notes")
    @Size(max = 1024)
    @NoHtml
    private String configurationNotes;

    @Column(name = "monitoring_enabled", nullable = false)
    private Boolean monitoringEnabled = true;

    // Constructors
    protected Port(UUID id, String name, Device device, Integer portNumber) {
        super(id, name, null);
        this.device = device;
        this.portNumber = portNumber;
    }

    protected Port(UUID id, String name, String description, Device device, Integer portNumber) {
        super(id, name, description);
        this.device = device;
        this.portNumber = portNumber;
    }

    // Abstract methods to be implemented by concrete port types
    public abstract String getPortType();
    public abstract Map<String, String> getSpecifications();
    public abstract boolean supportsSpeed(Long speedMbps);
    public abstract boolean isCompatibleWith(String connectorType);

    // Business methods
    public boolean isUp() {
        return "UP".equals(operationalStatus);
    }

    public boolean isDown() {
        return "DOWN".equals(operationalStatus);
    }

    public boolean isAdminEnabled() {
        return "UP".equals(adminStatus);
    }

    public boolean isAvailable() {
        return connector == null && isAdminEnabled();
    }

    public boolean isConnected() {
        return connector != null;
    }

    public boolean isPoeCapable() {
        return poeEnabled != null && poeEnabled;
    }

    public boolean isPoePowered() {
        return isPoeCapable() && poePowerWatts != null && poePowerWatts > 0;
    }

    public boolean isTrunkPort() {
        return "TRUNK".equals(portMode);
    }

    public boolean isAccessPort() {
        return "ACCESS".equals(portMode);
    }

    // Speed conversion methods
    public Double getSpeedGbps() {
        return speedMbps != null ? speedMbps / 1000.0 : null;
    }

    public Double getMaxSpeedGbps() {
        return maxSpeedMbps != null ? maxSpeedMbps / 1000.0 : null;
    }

    // Utilization calculations
    public double getUtilizationPercent() {
        if (speedMbps == null || speedMbps == 0) {
            return 0.0;
        }

        long totalTraffic = (bytesIn != null ? bytesIn : 0) + (bytesOut != null ? bytesOut : 0);
        if (totalTraffic == 0) {
            return 0.0;
        }

        // This is a simplified calculation - in reality you'd need time-based measurements
        return Math.min(100.0, (totalTraffic * 8.0) / (speedMbps * 1000000.0) * 100.0);
    }

    public long getTotalPackets() {
        return (packetsIn != null ? packetsIn : 0) + (packetsOut != null ? packetsOut : 0);
    }

    public long getTotalBytes() {
        return (bytesIn != null ? bytesIn : 0) + (bytesOut != null ? bytesOut : 0);
    }

    public long getTotalErrors() {
        return (errorsIn != null ? errorsIn : 0) + (errorsOut != null ? errorsOut : 0);
    }

    public double getErrorRate() {
        long totalPackets = getTotalPackets();
        long totalErrors = getTotalErrors();

        if (totalPackets == 0) {
            return 0.0;
        }

        return (totalErrors * 100.0) / totalPackets;
    }

    // Configuration methods
    public void setSpeed(Long speedMbps, String duplexMode) {
        this.speedMbps = speedMbps;
        this.duplexMode = duplexMode;
        this.lastChange = OffsetDateTime.now();
    }

    public void setVlanConfiguration(Integer accessVlan, String portMode) {
        this.accessVlan = accessVlan;
        this.portMode = portMode;
        this.lastChange = OffsetDateTime.now();
    }

    public void enablePoe(Integer poeClass, Double maxPowerWatts) {
        this.poeEnabled = true;
        this.poeClass = poeClass;
        this.poeMaxPowerWatts = maxPowerWatts;
        this.lastChange = OffsetDateTime.now();
    }

    public void disablePoe() {
        this.poeEnabled = false;
        this.poeClass = null;
        this.poePowerWatts = null;
        this.poeMaxPowerWatts = null;
        this.lastChange = OffsetDateTime.now();
    }

    public void resetStatistics() {
        this.bytesIn = 0L;
        this.bytesOut = 0L;
        this.packetsIn = 0L;
        this.packetsOut = 0L;
        this.errorsIn = 0L;
        this.errorsOut = 0L;
        this.discardsIn = 0L;
        this.discardsOut = 0L;
        this.statsLastReset = OffsetDateTime.now();
    }

    // Connector management
    public void connectTo(Connector connector) {
        if (this.connector != null) {
            disconnectConnector();
        }

        this.connector = connector;
        if (connector != null) {
            connector.setPort(this);
            this.lastChange = OffsetDateTime.now();
        }
    }

    public void disconnectConnector() {
        if (this.connector != null) {
            Connector oldConnector = this.connector;
            this.connector = null;
            oldConnector.setPort(null);
            this.lastChange = OffsetDateTime.now();
        }
    }

    // Validation
    public boolean isValidConfiguration() {
        if (device == null || portNumber == null || portNumber <= 0) {
            return false;
        }

        if (speedMbps != null && maxSpeedMbps != null && speedMbps > maxSpeedMbps) {
            return false;
        }

        if (accessVlan != null && (accessVlan < 1 || accessVlan > 4094)) {
            return false;
        }

        if (poeClass != null && (poeClass < 0 || poeClass > 8)) {
            return false;
        }

        return true;
    }

    // Common specifications (to be extended by subclasses)
    protected Map<String, String> getCommonSpecifications() {
        Map<String, String> specs = new HashMap<>();

        if (speedMbps != null) specs.put("Speed", speedMbps + " Mbps");
        if (maxSpeedMbps != null) specs.put("Max Speed", maxSpeedMbps + " Mbps");
        if (duplexMode != null) specs.put("Duplex", duplexMode);
        if (connectorType != null) specs.put("Connector", connectorType);
        if (mediumType != null) specs.put("Medium", mediumType);
        if (portMode != null) specs.put("Mode", portMode);
        if (isPoeCapable()) specs.put("PoE", "Enabled");

        return specs;
    }

    @Override
    public String toString() {
        return getPortType() + ":" + getId() + "[" + getName() +
               ", Port#" + portNumber +
               (device != null ? ", Device: " + device.getName() : "") +
               ", Status: " + operationalStatus +
               (isConnected() ? ", Connected" : ", Available") + "]";
    }
}