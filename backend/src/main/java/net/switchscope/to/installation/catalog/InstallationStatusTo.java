package net.switchscope.to.installation.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.to.component.catalog.BaseCodedTo;
import net.switchscope.validation.NoHtml;

import java.util.HashSet;
import java.util.UUID;
import java.util.Set;

/**
 * DTO for InstallationStatusEntity - catalog of installation statuses
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class InstallationStatusTo extends BaseCodedTo {

    // Status characteristics
    private Boolean physicallyPresent = false;

    private Boolean operational = false;

    private Boolean requiresAttention = false;

    private Boolean finalStatus = false;

    private Boolean errorStatus = false;

    // UI representation
    @Size(max = 16)
    @NoHtml
    private String colorCategory;

    @Size(max = 64)
    @NoHtml
    private String iconClass;

    // Status workflow
    @Min(1) @Max(100)
    private Integer statusOrder;

    @Min(1)
    private Integer autoTransitionMinutes;

    // Business rules
    private Boolean allowsEquipmentOperation = false;

    private Boolean allowsMaintenance = false;

    private Boolean requiresDocumentation = false;

    private Boolean notifiesStakeholders = false;

    // Possible next statuses (codes)
    @Schema(description = "Codes of possible next statuses")
    private Set<String> nextPossibleStatusCodes = new HashSet<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String statusCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean progressStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean successStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean warningStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean terminalStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean allowsStatusChange;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean hasAutoTransition;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer urgencyLevel;

    public InstallationStatusTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public InstallationStatusTo(UUID id, String name) {
        super(id, name);
    }
}
