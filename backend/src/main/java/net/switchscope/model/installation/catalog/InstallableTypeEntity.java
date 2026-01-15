
package net.switchscope.model.installation.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.BaseCodedEntity;
import net.switchscope.model.component.InstallableCategory;
import net.switchscope.service.component.InstallableComponentRegistry;

/**
 * Dynamic catalog entity for installable component types
 */
@Entity
@Table(name = "installable_types_catalog",
       uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
@Getter
@Setter
@NoArgsConstructor
public class InstallableTypeEntity extends BaseCodedEntity {

    // Configuration flags
    @Column(name = "requires_rack_position", nullable = false)
    private boolean requiresRackPosition = false;

    @Column(name = "has_standard_rack_units", nullable = false) 
    private boolean hasStandardRackUnits = false;

    @Column(name = "supports_power_management", nullable = false)
    private boolean supportsPowerManagement = false;

    @Column(name = "requires_environmental_control", nullable = false)
    private boolean requiresEnvironmentalControl = false;

    // Installation characteristics
    @Column(name = "typical_installation_time_minutes")
    @Min(1) @Max(1440) // 1 minute to 24 hours
    private Integer typicalInstallationTimeMinutes;

    @Column(name = "requires_shutdown", nullable = false)
    private boolean requiresShutdown = false;

    @Column(name = "hot_swappable", nullable = false)
    private boolean hotSwappable = false;

    // Physical characteristics
    @Column(name = "default_rack_units")
    @Min(1) @Max(10)
    private Integer defaultRackUnits = 1;

    @Column(name = "max_weight_kg")
    @DecimalMin("0.1") @DecimalMax("500.0")
    private Double maxWeightKg;

    @Transient
    private transient InstallableComponentRegistry registry;

    // Installation priority (for ordering installations)
    @Column(name = "installation_priority")
    @Min(1) @Max(10)
    private Integer installationPriority = 5; // Default medium priority

    // Constructor
    public InstallableTypeEntity(String code, String name, String displayName) {
        super(code, name, displayName);
    }

    public InstallableTypeEntity(String code, String name, String displayName, 
                                String description) {
        super(code, name, displayName, description);
    }

    // Business methods
    public boolean isDeviceType() {
        return requireRegistry().isDeviceType(getCode());
    }

    public boolean isConnectivityType() {
        return requireRegistry().isConnectivityType(getCode());
    }

    public boolean isHousingType() {
        return requireRegistry().isHousingType(getCode());
    }

    public boolean isPowerType() {
        return requireRegistry().isPowerType(getCode());
    }

    public boolean isImplemented() {
        return requireRegistry().isImplemented(getCode());
    }

    public InstallableCategory getCategory() {
        return requireRegistry().getCategoryByCode(getCode()).orElse(null);
    }

    public boolean requiresSpecialHandling() {
        return requiresShutdown || !hotSwappable || requiresEnvironmentalControl;
    }

    public boolean isHighPriority() {
        return installationPriority != null && installationPriority <= 3;
    }

    public boolean isLowPriority() {
        return installationPriority != null && installationPriority >= 7;
    }

    public String getPriorityLevel() {
        if (installationPriority == null) return "MEDIUM";
        return switch (installationPriority) {
            case 1, 2, 3 -> "HIGH";
            case 4, 5, 6 -> "MEDIUM"; 
            case 7, 8, 9, 10 -> "LOW";
            default -> "MEDIUM";
        };
    }

    // Installation time estimation
    public String getEstimatedInstallationTime() {
        if (typicalInstallationTimeMinutes == null) return "Unknown";
        
        int minutes = typicalInstallationTimeMinutes;
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            return remainingMinutes > 0 ? 
                   hours + "h " + remainingMinutes + "m" : 
                   hours + " hours";
        }
    }

    public void setRegistry(InstallableComponentRegistry registry) {
        this.registry = registry;
    }

    private InstallableComponentRegistry requireRegistry() {
        if (registry == null) {
            throw new IllegalStateException("InstallableComponentRegistry is not set for code: " + getCode());
        }
        return registry;
    }

    @Override
    public String toString() {
        return "InstallableTypeEntity:" + getId() + "[" + getCode() + 
               ", " + getDisplayName() + "]";
    }
}
