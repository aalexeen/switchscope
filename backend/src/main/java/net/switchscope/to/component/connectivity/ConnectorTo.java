package net.switchscope.to.component.connectivity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
import net.switchscope.to.component.ComponentTo;
import net.switchscope.validation.NoHtml;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for Connector entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ConnectorTo extends ComponentTo {

    @Schema(description = "Connector model catalog ID")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID connectorModelId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Connector model name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String connectorModelName;

    // Connector specifications
    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String connectorType;

    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String connectorPosition;

    @Size(max = 16)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String gender;

    // Physical identification
    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String connectorLabel;

    @Size(max = 16)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String colorCode;

    // Installation and condition
    @Size(max = 32)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String terminationQuality;

    @Size(max = 512)
    @NoHtml
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private String installationNotes;

    // Relationship to cable run
    @NotNull
    @Schema(description = "Cable run ID this connector belongs to")
    @FieldAccess(FieldAccessLevel.REQUIRED)
    private UUID cableRunId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Cable run name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String cableRunName;

    // Relationship to port (optional)
    @Schema(description = "Port ID this connector is connected to (if any)")
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private UUID portId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Port name")
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String portName;

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean startConnector;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean endConnector;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean intermediateConnector;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean needsRework;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean goodQuality;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean connectedToPort;

    public ConnectorTo(UUID id, OffsetDateTime createdAt, OffsetDateTime updatedAt, String name) {
        super(id, createdAt, updatedAt, name);
    }

    public ConnectorTo(UUID id, String name) {
        super(id, name);
    }
}

