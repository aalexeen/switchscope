package net.switchscope.to.component.device;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
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
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String wifiStandard;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer maxClients;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsDualBand;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supports5Ghz;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supports6Ghz;

    // Power over Ethernet
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean poeRequired;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer poeConsumptionWatts;

    // Coverage and performance
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer coverageRadiusMeters;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer maxThroughputMbps;

    // Security features
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsWpa3;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean supportsEnterpriseAuth;

    // Management
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean controllerManaged;

    @Size(max = 64)
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String controllerIp;

    // SSIDs
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Set<String> ssids = new HashSet<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean wifi6;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean modernSecurity;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer ssidCount;

    public AccessPointTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public AccessPointTo(UUID id, String name) {
        super(id, name);
    }
}

