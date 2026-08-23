package net.switchscope.to.port;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.validation.NoHtml;

import java.time.OffsetDateTime;
import java.util.UUID;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;

/**
 * DTO for FiberPort entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class FiberPortTo extends PortTo {

    // Fiber-specific fields
    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String fiberType;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Integer wavelengthNm;

    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String fiberStandard;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double transmissionDistanceKm;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double txPowerDbm;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double rxPowerDbm;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Double opticalLossDb;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean singleMode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean multiMode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean hasOpticalSignal;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean opticalLinkHealthy;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String opticalLinkQuality;

    public FiberPortTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public FiberPortTo(UUID id, String name) {
        super(id, name);
    }
}

