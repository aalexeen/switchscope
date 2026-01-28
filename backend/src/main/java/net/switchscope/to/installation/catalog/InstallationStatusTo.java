package net.switchscope.to.installation.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.switchscope.security.policy.FieldAccess;
import net.switchscope.security.policy.FieldAccessLevel;
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
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean physicallyPresent = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean operational = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresAttention = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean finalStatus = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean errorStatus = false;

    // UI representation
    @Size(max = 16)
    @NoHtml
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String colorCategory;

    @Size(max = 64)
    @NoHtml
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private String iconClass;

    // Status workflow
    @Min(1) @Max(100)
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer statusOrder;

    @Min(1)
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Integer autoTransitionMinutes;

    // Business rules
    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean allowsEquipmentOperation = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean allowsMaintenance = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean requiresDocumentation = false;

    @FieldAccess(FieldAccessLevel.USER_WRITABLE)
    private Boolean notifiesStakeholders = false;

    // Possible next statuses (codes)
    @Schema(description = "Codes of possible next statuses")
    @FieldAccess(FieldAccessLevel.ADMIN_NULLABLE)
    private Set<String> nextPossibleStatusCodes = new HashSet<>();

    // Computed read-only fields
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private String statusCategory;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean progressStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean successStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean warningStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean terminalStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean allowsStatusChange;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Boolean hasAutoTransition;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @FieldAccess(FieldAccessLevel.READ_ONLY)
    private Integer urgencyLevel;

    public InstallationStatusTo(UUID id, String name, String code, String displayName) {
        super(id, name, code, displayName);
    }

    public InstallationStatusTo(UUID id, String name) {
        super(id, name);
    }
}
