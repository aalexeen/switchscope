package net.switchscope.to.component.connectivity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
import net.switchscope.to.component.ComponentTo;
import net.switchscope.validation.NoHtml;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO for CableRun entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CableRunTo extends ComponentTo {

    @Schema(description = "Cable model catalog ID")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID cableModelId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Cable model name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String cableModelName;

    // Cable specifications
    @NotNull
    @DecimalMin(value = "0.1")
    @DecimalMax(value = "10000.0")
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private Double cableLengthMeters;

    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String cableType;

    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String cableCategory;

    // Installation details
    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String installationMethod;

    @Size(max = 128)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String cablePathway;

    // Testing and certification
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean tested;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private LocalDate testDate;

    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String testResult;

    @Size(max = 512)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String testNotes;

    // Documentation
    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String cableId;

    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String circuitId;

    @Size(max = 1024)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String installationNotes;

    // Location references
    @Schema(description = "Start location ID")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID startLocationId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Start location name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String startLocationName;

    @Schema(description = "End location ID")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID endLocationId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "End location name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String endLocationName;

    // Ordered list of location IDs for multi-point cables
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private List<UUID> locationIds = new ArrayList<>();

    // Related connector IDs
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private List<UUID> connectorIds = new ArrayList<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean passed;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean requiresTesting;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean pointToPoint;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean multiPoint;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String orderedLocationPath;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer connectorCount;

    public CableRunTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public CableRunTo(UUID id, String name) {
        super(id, name);
    }
}

