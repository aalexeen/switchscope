package net.switchscope.model.component.catalog.connectiviy;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.validation.NoHtml;

/**
 * Connector Model Catalog Entry - represents a specific connector type
 */
@Entity
@Table(name = "connector_models_catalog",
       uniqueConstraints = @UniqueConstraint(columnNames = {"manufacturer", "model_number"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConnectorModel extends ComponentModel {

    // Connector type and specifications
    @Column(name = "connector_type", nullable = false)
    @NotBlank
    @Size(max = 64)
    @NoHtml
    private String connectorType; // RJ45, RJ11, LC, SC, ST, FC, MTP, SFP+, etc.

    @Column(name = "connector_standard")
    @Size(max = 64)
    @NoHtml
    private String connectorStandard; // 8P8C, IEC 61754-20, TIA-568, etc.

    @Column(name = "gender")
    @Size(max = 16)
    @NoHtml
    private String gender; // MALE, FEMALE, HERMAPHRODITE

    // Physical characteristics
    @Column(name = "mounting_type")
    @Size(max = 32)
    @NoHtml
    private String mountingType; // PANEL_MOUNT, CABLE_END, BULKHEAD, SURFACE_MOUNT

    @Column(name = "plating_material")
    @Size(max = 32)
    @NoHtml
    private String platingMaterial; // GOLD, NICKEL, SILVER

    @Column(name = "housing_material")
    @Size(max = 32)
    @NoHtml
    private String housingMaterial; // PLASTIC, METAL, CERAMIC

    // Performance specifications
    @Column(name = "max_frequency_mhz")
    private Integer maxFrequencyMhz;

    @Column(name = "insertion_loss_db")
    private Double insertionLossDb;

    @Column(name = "return_loss_db")
    private Double returnLossDb;

    @Column(name = "mating_cycles")
    @Min(1)
    private Integer matingCycles; // Number of connect/disconnect cycles

    // Environmental specifications
    @Column(name = "operating_temperature_min")
    private Integer operatingTemperatureMin;

    @Column(name = "operating_temperature_max")
    private Integer operatingTemperatureMax;

    @Column(name = "ip_rating")
    @Size(max = 8)
    @NoHtml
    private String ipRating; // IP65, IP67, etc.

    // Compatibility
    @Column(name = "compatible_cable_types")
    @Size(max = 256)
    @NoHtml
    private String compatibleCableTypes; // CAT5E,CAT6,CAT6A

    @Column(name = "shielded", nullable = false)
    private boolean shielded = false;

    @Column(name = "keyed", nullable = false)
    private boolean keyed = false; // Prevents incorrect insertion

    // Constructor
    public ConnectorModel(String name, String manufacturer, String modelNumber,
                         ComponentTypeEntity componentType, String connectorType) {
        super(name, manufacturer, modelNumber, componentType);
        this.connectorType = connectorType;
    }

    // Business methods
    public boolean isFiberOptic() {
        return connectorType != null &&
               (connectorType.equals("LC") || connectorType.equals("SC") ||
                connectorType.equals("ST") || connectorType.equals("FC") ||
                connectorType.equals("MTP") || connectorType.equals("MPO"));
    }

    public boolean isCopper() {
        return connectorType != null &&
               (connectorType.startsWith("RJ") || connectorType.equals("8P8C"));
    }

    public boolean isHighDensity() {
        return connectorType != null &&
               (connectorType.equals("MTP") || connectorType.equals("MPO"));
    }

    public String getCompatibilitySummary() {
        StringBuilder summary = new StringBuilder();
        if (compatibleCableTypes != null && !compatibleCableTypes.trim().isEmpty()) {
            summary.append("Cables: ").append(compatibleCableTypes);
        }
        if (maxFrequencyMhz != null) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("Max ").append(maxFrequencyMhz).append(" MHz");
        }
        return summary.toString();
    }

    @Override
    public boolean isValidModel() {
        if (!super.isValidModel()) {
            return false;
        }

        if (connectorType == null || connectorType.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getManufacturer() + " " + getModelNumber() +
               " (" + connectorType +
               (gender != null ? " " + gender : "") + ")";
    }
}