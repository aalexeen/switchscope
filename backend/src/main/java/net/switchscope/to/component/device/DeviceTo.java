package net.switchscope.to.component.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.to.component.ComponentTo;
import net.switchscope.validation.NoHtml;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Base DTO for all network device types (switches, routers, access points)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class DeviceTo extends ComponentTo {

    // Network management
    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^$",
            message = "Invalid IP address format")
    private String managementIp;

    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^$",
            message = "Invalid subnet mask format")
    private String subnetMask;

    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^$",
            message = "Invalid gateway IP address format")
    private String defaultGateway;

    private Integer managementVlan;

    // Firmware and software
    @Size(max = 128)
    @NoHtml
    private String firmwareVersion;

    @Size(max = 128)
    @NoHtml
    private String softwareVersion;

    @Size(max = 128)
    @NoHtml
    private String bootLoaderVersion;

    // Credentials (write-only for security)
    @Size(max = 64)
    @NoHtml
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String adminUsername;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String adminPassword;

    // SNMP configuration
    @Size(max = 128)
    @NoHtml
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String snmpCommunityRead;

    @Size(max = 128)
    @NoHtml
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String snmpCommunityWrite;

    @Size(max = 16)
    private String snmpVersion;

    // Monitoring
    private Boolean monitored;

    private Integer monitoringIntervalSeconds;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime lastPingTime;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean lastPingSuccess;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long uptimeSeconds;

    // Physical characteristics
    @Size(max = 32)
    private String consolePortType;

    private Boolean hasRedundantPower;

    private Integer operatingTemperatureMin;

    private Integer operatingTemperatureMax;

    // Monitoring data (read-only)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer cpuUtilizationPercent;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer memoryUtilizationPercent;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer temperatureCelsius;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean managed;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean reachable;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String monitoringStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String networkConfiguration;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer portCount;

    protected DeviceTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    protected DeviceTo(UUID id, String name) {
        super(id, name);
    }
}

