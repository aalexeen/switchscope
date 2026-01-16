package net.switchscope.model.installation.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.BaseCodedEntity;
import net.switchscope.validation.NoHtml;

import java.util.*;

/**
 * Dynamic catalog entity for installation statuses
 */
@Entity
@Table(name = "installation_statuses_catalog",
       uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
@Getter
@Setter
@NoArgsConstructor
public class InstallationStatusEntity extends BaseCodedEntity {

    // Status characteristics
    @Column(name = "is_physically_present", nullable = false)
    private boolean physicallyPresent = false;

    @Column(name = "is_operational", nullable = false)
    private boolean operational = false;

    @Column(name = "requires_attention", nullable = false)
    private boolean requiresAttention = false;

    @Column(name = "is_final_status", nullable = false)
    private boolean finalStatus = false;

    @Column(name = "is_error_status", nullable = false)
    private boolean errorStatus = false;

    // UI representation
    @Column(name = "color_category")
    @Size(max = 16)
    @NoHtml
    private String colorCategory = "secondary"; // success, info, warning, danger, secondary

    @Column(name = "icon_class")
    @Size(max = 64)
    @NoHtml
    private String iconClass;

    // Status workflow
    @Column(name = "status_order")
    @Min(1) @Max(100)
    private Integer statusOrder = 50; // For ordering in workflows

    @Column(name = "auto_transition_minutes")
    @Min(1)
    private Integer autoTransitionMinutes; // Auto-transition after X minutes

    // Business rules
    @Column(name = "allows_equipment_operation", nullable = false)
    private boolean allowsEquipmentOperation = false;

    @Column(name = "allows_maintenance", nullable = false)
    private boolean allowsMaintenance = false;

    @Column(name = "requires_documentation", nullable = false)
    private boolean requiresDocumentation = false;

    @Column(name = "notifies_stakeholders", nullable = false)
    private boolean notifiesStakeholders = false;

    // Next possible status codes (stored as codes, not entity references)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "installation_status_allowed_transitions",
        joinColumns = @JoinColumn(name = "installation_status_id")
    )
    @Column(name = "to_status_code", length = 64)
    private Set<String> nextPossibleStatusCodes = new HashSet<>();

    // Constructor
    public InstallationStatusEntity(String code, String name, String displayName) {
        super(code, name, displayName);
    }

    public InstallationStatusEntity(String code, String name, String displayName,
                                   String description, String colorCategory) {
        super(code, name, displayName, description);
        this.colorCategory = colorCategory;
    }

    // Workflow management methods
    public void addNextPossibleStatusCode(String statusCode) {
        if (statusCode != null && !statusCode.trim().isEmpty()) {
            nextPossibleStatusCodes.add(statusCode);
        }
    }

    public void removeNextPossibleStatusCode(String statusCode) {
        nextPossibleStatusCodes.remove(statusCode);
    }

    public boolean canTransitionTo(String statusCode) {
        return statusCode != null && nextPossibleStatusCodes.contains(statusCode);
    }

    public Set<String> getNextPossibleStatusCodes() {
        return new HashSet<>(nextPossibleStatusCodes);
    }

    // Business logic methods
    public boolean isProgressStatus() {
        return !finalStatus && !errorStatus;
    }

    public boolean isSuccessStatus() {
        return operational && !requiresAttention;
    }

    public boolean isWarningStatus() {
        return physicallyPresent && requiresAttention && !errorStatus;
    }

    public boolean isTerminalStatus() {
        return finalStatus || (nextPossibleStatusCodes.isEmpty() && !errorStatus);
    }

    public boolean allowsStatusChange() {
        return !finalStatus;
    }

    public String getStatusCategory() {
        if (errorStatus) return "ERROR";
        if (operational) return "OPERATIONAL";
        if (physicallyPresent) return "PHYSICAL";
        if (finalStatus) return "FINAL";
        return "TRANSITIONAL";
    }

    // Auto-transition support
    public boolean hasAutoTransition() {
        return autoTransitionMinutes != null && autoTransitionMinutes > 0;
    }

    public boolean shouldAutoTransition(long minutesSinceUpdate) {
        return hasAutoTransition() && minutesSinceUpdate >= autoTransitionMinutes;
    }

    // Priority for urgent statuses
    public int getUrgencyLevel() {
        if (errorStatus) return 10; // Highest
        if (requiresAttention) return 7;
        if (!physicallyPresent && operational) return 8; // Inconsistent state
        if (operational) return 3; // Normal operation
        return 5; // Default
    }

    @Override
    public String toString() {
        return "InstallationStatusEntity:" + getId() + "[" + getCode() +
               ", " + getDisplayName() + ", " + getStatusCategory() + "]";
    }
}
