package net.switchscope.model.component.catalog.connectiviy;

import jakarta.annotation.PostConstruct;
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
 * Patch Panel Model Catalog Entry - represents a specific patch panel model
 */
@Entity
@DiscriminatorValue("PATCH_PANEL_MODEL")
@Getter
@Setter
@NoArgsConstructor
public class PatchPanelModel extends ComponentModel {

    // Panel specifications
    @Column(name = "port_count", nullable = false)
    @NotNull
    @Min(1) @Max(144)
    private Integer portCount;

    @Column(name = "rack_units", nullable = false)
    @NotNull
    @Min(1) @Max(10)
    private Integer rackUnits;

    @Column(name = "panel_type", nullable = false)
    @NotBlank
    @Size(max = 64)
    @NoHtml
    private String panelType; // COPPER, FIBER, HYBRID, COAX

    @Column(name = "connector_type")
    @Size(max = 64)
    @NoHtml
    private String connectorType; // RJ45, LC, SC, ST, etc.

    // Physical characteristics
    @Column(name = "mounting_type")
    @Size(max = 32)
    @NoHtml
    private String mountingType; // FRONT_MOUNT, REAR_MOUNT, SWING_OUT

    @Column(name = "termination_type")
    @Size(max = 32)
    @NoHtml
    private String terminationType; // 110_BLOCK, KRONE, LSA, FIBER_ADAPTER

    @Column(name = "port_layout")
    @Size(max = 64)
    @NoHtml
    private String portLayout; // 1x24, 2x12, 1x48, etc.

    @Column(name = "port_density")
    private Double portDensity; // ports per rack unit

    // Performance specifications
    @Column(name = "category_rating")
    @Size(max = 16)
    @NoHtml
    private String categoryRating; // CAT5E, CAT6, CAT6A

    @Column(name = "max_frequency_mhz")
    private Integer maxFrequencyMhz;

    @Column(name = "shielded", nullable = false)
    private boolean shielded = false;

    // Construction
    @Column(name = "housing_material")
    @Size(max = 32)
    @NoHtml
    private String housingMaterial; // STEEL, ALUMINUM, PLASTIC

    @Column(name = "finish_color")
    @Size(max = 32)
    @NoHtml
    private String finishColor; // BLACK, WHITE, GRAY

    @Column(name = "cable_management", nullable = false)
    private boolean cableManagement = false;

    @Column(name = "cable_management_type")
    @Size(max = 64)
    @NoHtml
    private String cableManagementType; // D_RING, HORIZONTAL_BAR, VERTICAL_BAR

    // Labeling and identification
    @Column(name = "port_labeling", nullable = false)
    private boolean portLabeling = false;

    @Column(name = "label_type")
    @Size(max = 32)
    @NoHtml
    private String labelType; // PRINTED, REMOVABLE, WRITE_ON

    // Constructor
    public PatchPanelModel(String name, String manufacturer, String modelNumber,
                          ComponentTypeEntity componentType, Integer portCount,
                          Integer rackUnits, String panelType) {
        super(name, manufacturer, modelNumber, componentType);
        this.portCount = portCount;
        this.rackUnits = rackUnits;
        this.panelType = panelType;
    }

    @PostConstruct
    private void calculatePortDensity() {
        if (portCount != null && rackUnits != null && rackUnits > 0) {
            this.portDensity = (double) portCount / rackUnits;
        }
    }

    // Business methods
    public boolean isFiberPanel() {
        return "FIBER".equals(panelType) || "HYBRID".equals(panelType);
    }

    public boolean isCopperPanel() {
        return "COPPER".equals(panelType) || "HYBRID".equals(panelType);
    }

    public boolean isHighDensity() {
        return portDensity != null && portDensity >= 24.0; // 24+ ports per U
    }

    public String getPortSummary() {
        return portCount + " ports in " + rackUnits + "U (" +
               String.format("%.1f", portDensity) + " ports/U)";
    }

    public String getSpecificationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(getPortSummary());

        if (categoryRating != null) {
            summary.append(", ").append(categoryRating);
        }
        if (connectorType != null) {
            summary.append(", ").append(connectorType);
        }
        if (shielded) {
            summary.append(", Shielded");
        }

        return summary.toString();
    }

    @Override
    public boolean isValidModel() {
        if (!super.isValidModel()) {
            return false;
        }

        if (portCount == null || portCount <= 0 ||
            rackUnits == null || rackUnits <= 0 ||
            panelType == null || panelType.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getManufacturer() + " " + getModelNumber() +
               " (" + getPortSummary() + ", " + panelType + ")";
    }
}