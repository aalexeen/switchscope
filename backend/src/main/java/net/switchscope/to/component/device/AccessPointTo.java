package net.switchscope.to.component.device;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.validation.NoHtml;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for AccessPoint (WiFi) entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AccessPointTo extends DeviceTo {

    // WiFi capabilities
    @Size(max = 64)
    @NoHtml
    private String wifiStandard;

    private Integer maxClients;

    private Boolean supportsDualBand;

    private Boolean supports5Ghz;

    private Boolean supports6Ghz;

    // Power over Ethernet
    private Boolean poeRequired;

    private Integer poeConsumptionWatts;

    // Coverage and performance
    private Integer coverageRadiusMeters;

    private Integer maxThroughputMbps;

    // Security features
    private Boolean supportsWpa3;

    private Boolean supportsEnterpriseAuth;

    // Management
    private Boolean controllerManaged;

    @Size(max = 64)
    private String controllerIp;

    // SSIDs
    private Set<String> ssids = new HashSet<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean wifi6;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean modernSecurity;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer ssidCount;

    public AccessPointTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public AccessPointTo(UUID id, String name) {
        super(id, name);
    }
}

