
package net.switchscope.model.component.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.NamedEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.validation.NoHtml;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract base class for all component catalog models
 * Contains common attributes shared by all equipment models
 */
@Entity
@Table(name = "component_models_catalog",
       uniqueConstraints = @UniqueConstraint(columnNames = {"manufacturer", "model_number"}, name = "uk_component_model_manufacturer_model"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "model_class", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ComponentModel extends NamedEntity {

    // Basic model identification (name and description inherited from NamedEntity)
    @Column(name = "manufacturer", nullable = false)
    @NotBlank
    @Size(max = 128)
    @NoHtml
    private String manufacturer;

    @Column(name = "model_number", nullable = false)
    @NotBlank
    @Size(max = 128)
    @NoHtml
    private String modelNumber;

    @Column(name = "part_number")
    @Size(max = 128)
    @NoHtml
    private String partNumber;

    @Column(name = "sku")
    @Size(max = 64)
    @NoHtml
    private String sku; // Stock Keeping Unit

    // Component type relationship
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "component_type_id", nullable = false)
    @NotNull
    private ComponentTypeEntity componentType;

    // Model status and lifecycle
    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "discontinued", nullable = false)
    private boolean discontinued = false;

    @Column(name = "end_of_life", nullable = false)
    private boolean endOfLife = false;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "discontinue_date")
    private LocalDateTime discontinueDate;

    // Documentation and support (description is inherited from NamedEntity)
    @Column(name = "datasheet_url")
    @Size(max = 512)
    private String datasheetUrl;

    @Column(name = "manual_url")
    @Size(max = 512)
    private String manualUrl;

    @Column(name = "support_url")
    @Size(max = 512)
    private String supportUrl;

    // Warranty and lifecycle
    @Column(name = "warranty_years")
    @Min(0) @Max(20)
    private Integer warrantyYears;

    @Column(name = "expected_lifespan_years")
    @Min(1) @Max(50)
    private Integer expectedLifespanYears;

    @Column(name = "maintenance_interval_months")
    @Min(1) @Max(60)
    private Integer maintenanceIntervalMonths;

    // Common physical characteristics
    @Column(name = "weight_kg")
    @DecimalMin("0.001") @DecimalMax("1000.0")
    private Double weightKg;

    @Column(name = "dimensions_mm")
    @Size(max = 64)
    @NoHtml
    private String dimensionsMm; // Format: "WxDxH"

    // Environmental specifications
    @Column(name = "operating_temperature_min")
    private Integer operatingTemperatureMin;

    @Column(name = "operating_temperature_max")
    private Integer operatingTemperatureMax;

    @Column(name = "operating_humidity_min")
    @Min(0) @Max(100)
    private Integer operatingHumidityMin;

    @Column(name = "operating_humidity_max")
    @Min(0) @Max(100)
    private Integer operatingHumidityMax;

    @Column(name = "storage_temperature_min")
    private Integer storageTemperatureMin;

    @Column(name = "storage_temperature_max")
    private Integer storageTemperatureMax;

    // Certifications and compliance
    @Column(name = "certifications")
    @Size(max = 512)
    @NoHtml
    private String certifications; // CE, FCC, UL, etc. (comma-separated)

    @Column(name = "compliance_standards")
    @Size(max = 512)
    @NoHtml
    private String complianceStandards; // RoHS, WEEE, etc. (comma-separated)

    // Pricing and availability (optional)
    @Column(name = "msrp")
    @DecimalMin("0.01")
    private Double msrp; // Manufacturer Suggested Retail Price

    @Column(name = "list_price")
    @DecimalMin("0.01")
    private Double listPrice;

    @Column(name = "currency_code")
    @Size(max = 3)
    @Pattern(regexp = "[A-Z]{3}")
    private String currencyCode = "USD";

    // Catalog management
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "verified", nullable = false)
    private boolean verified = false; // Model data has been verified

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    @Column(name = "notes")
    @Size(max = 2048)
    @NoHtml
    private String notes; // Internal notes about the model

    // Constructor
    protected ComponentModel(String name, String manufacturer, String modelNumber,
                            ComponentTypeEntity componentType) {
        super(null, name, null); // NamedEntity constructor
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.componentType = componentType;
        this.createdDate = LocalDateTime.now();
    }

    // Constructor with description
    protected ComponentModel(String name, String description, String manufacturer, 
                            String modelNumber, ComponentTypeEntity componentType) {
        super(null, name, description); // NamedEntity constructor with description
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.componentType = componentType;
        this.createdDate = LocalDateTime.now();
    }

    // Lifecycle management
    @PreUpdate
    private void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    /**
     * Basic model validation - can be overridden by subclasses
     * Subclasses should call super.isValidModel() for base validation
     */
    public boolean isValidModel() {
        // Base validation logic
        return hasMinimumRequiredFields() &&
               isValidTemperatureRange() &&
               isValidHumidityRange() &&
               isValidLifecycleDates();
    }

    // Helper methods for validation
    private boolean isValidTemperatureRange() {
        return !hasOperatingTemperatureRange() ||
               operatingTemperatureMin < operatingTemperatureMax;
    }

    private boolean isValidHumidityRange() {
        return !hasHumidityRange() ||
               operatingHumidityMin < operatingHumidityMax;
    }

    private boolean isValidLifecycleDates() {
        return discontinueDate == null || releaseDate == null ||
               !discontinueDate.isBefore(releaseDate);
    }


    // Common business methods
    public boolean isCurrentlySupported() {
        return active && !discontinued && !endOfLife;
    }

    public boolean isAvailableForPurchase() {
        return active && !discontinued;
    }

    public boolean needsReplacement() {
        return discontinued || endOfLife ||
               (discontinueDate != null && discontinueDate.isBefore(LocalDateTime.now()));
    }

    public String getModelDesignation() {
        return manufacturer + " " + modelNumber;
    }

    public String getFullName() {
        return getName() != null ? getName() : getModelDesignation();
    }

    // Environmental methods
    public boolean hasOperatingTemperatureRange() {
        return operatingTemperatureMin != null && operatingTemperatureMax != null;
    }

    public boolean isOperatingTemperatureValid(int temperature) {
        return hasOperatingTemperatureRange() &&
               temperature >= operatingTemperatureMin &&
               temperature <= operatingTemperatureMax;
    }

    public String getOperatingTemperatureRange() {
        if (hasOperatingTemperatureRange()) {
            return operatingTemperatureMin + "°C to " + operatingTemperatureMax + "°C";
        }
        return "Not specified";
    }

    public boolean hasHumidityRange() {
        return operatingHumidityMin != null && operatingHumidityMax != null;
    }

    public String getHumidityRange() {
        if (hasHumidityRange()) {
            return operatingHumidityMin + "% to " + operatingHumidityMax + "% RH";
        }
        return "Not specified";
    }

    // Certification methods
    public boolean hasCertification(String certification) {
        return certifications != null &&
               certifications.toUpperCase().contains(certification.toUpperCase());
    }

    public boolean isCompliantWith(String standard) {
        return complianceStandards != null &&
               complianceStandards.toUpperCase().contains(standard.toUpperCase());
    }

    // Documentation methods
    public boolean hasDatasheet() {
        return datasheetUrl != null && !datasheetUrl.trim().isEmpty();
    }

    public boolean hasManual() {
        return manualUrl != null && !manualUrl.trim().isEmpty();
    }

    public boolean hasDocumentation() {
        return hasDatasheet() || hasManual() || 
               (getDescription() != null && !getDescription().trim().isEmpty());
    }

    // Component type helper methods
    public String getComponentTypeCode() {
        return componentType != null ? componentType.getCode() : null;
    }

    public String getComponentTypeName() {
        return componentType != null ? componentType.getDisplayName() : null;
    }

    public String getCategoryName() {
        return componentType != null && componentType.getCategory() != null ?
               componentType.getCategory().getDisplayName() : null;
    }

    // Validation methods
    public boolean hasMinimumRequiredFields() {
        return getName() != null && !getName().trim().isEmpty() &&
               manufacturer != null && !manufacturer.trim().isEmpty() &&
               modelNumber != null && !modelNumber.trim().isEmpty() &&
               componentType != null;
    }

    // Lifecycle status
    public String getLifecycleStatus() {
        if (endOfLife) return "End of Life";
        if (discontinued) return "Discontinued";
        if (!active) return "Inactive";
        return "Active";
    }

    // Enhanced validation
    public boolean isValidConfiguration() {
        return isValidModel(); // Убрать дублирование логики
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getId() + "[" +
               getModelDesignation() +
               (componentType != null ? ", " + componentType.getCode() : "") +
               ", " + getLifecycleStatus() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComponentModel that)) return false;
        if (!super.equals(o)) return false;

        return manufacturer.equals(that.manufacturer) &&
               modelNumber.equals(that.modelNumber);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + manufacturer.hashCode();
        result = 31 * result + modelNumber.hashCode();
        return result;
    }
}