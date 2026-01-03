package net.switchscope.model.component.connectivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.catalog.connectiviy.PatchPanelModel;
import net.switchscope.model.component.device.HasPortsImpl;
import net.switchscope.validation.NoHtml;

/**
 * Patch Panel entity - represents a passive connectivity panel with ports
 */
@Entity
@DiscriminatorValue("PATCH_PANEL")
@Getter
@Setter
@NoArgsConstructor
public class PatchPanel extends HasPortsImpl {

    // Link to patch panel model catalog
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patch_panel_model_id")
    private PatchPanelModel patchPanelModel;

    // Panel specifications
    /*
     * @Column(name = "port_count")
     * 
     * @Min(1) @Max(144)
     * private Integer portCount;
     */

    @Column(name = "panel_type")
    @Size(max = 64)
    @NoHtml
    private String panelType; // COPPER, FIBER, HYBRID, COAX

    @Column(name = "connector_type")
    @Size(max = 64)
    @NoHtml
    private String connectorType; // RJ45, LC, SC, etc.

    // Rack positioning (patch panels require rack space)
    @Column(name = "rack_units")
    @Min(1)
    @Max(10)
    private Integer rackUnits;

    @Column(name = "rack_position")
    private Integer rackPosition; // Position in rack (from bottom)

    // Panel identification and labeling
    @Column(name = "panel_label")
    @Size(max = 64)
    @NoHtml
    private String panelLabel; // Physical label on panel

    @Column(name = "port_labeling_scheme")
    @Size(max = 64)
    @NoHtml
    private String portLabelingScheme; // 1-24, A1-C8, etc.

    // Installation and configuration
    @Column(name = "mounting_type")
    @Size(max = 32)
    @NoHtml
    private String mountingType; // FRONT_MOUNT, REAR_MOUNT, SWING_OUT

    @Column(name = "termination_type")
    @Size(max = 32)
    @NoHtml
    private String terminationType; // 110_BLOCK, KRONE, LSA, FIBER_ADAPTER

    // Performance characteristics
    @Column(name = "category_rating")
    @Size(max = 16)
    @NoHtml
    private String categoryRating; // CAT5E, CAT6, CAT6A

    @Column(name = "shielded", nullable = false)
    private boolean shielded = false;

    // Cable management
    @Column(name = "cable_management", nullable = false)
    private boolean cableManagement = false;

    @Column(name = "cable_management_type")
    @Size(max = 64)
    @NoHtml
    private String cableManagementType;

    // Related cable runs (patch panels terminate cable runs)
    @ManyToMany
    @JoinTable(name = "patch_panel_cable_runs", joinColumns = @JoinColumn(name = "patch_panel_id"), inverseJoinColumns = @JoinColumn(name = "cable_run_id"))
    private Set<CableRun> cableRuns = new HashSet<>();

    // Constructors
    public PatchPanel(UUID id, String name, ComponentTypeEntity componentType) {
        super(id, name, componentType);
    }

    public PatchPanel(UUID id, String name, String manufacturer, String model,
            String serialNumber, ComponentTypeEntity componentType) {
        super(id, name, manufacturer, model, serialNumber, componentType);
    }

    // Device abstract methods implementation
    @Override
    public boolean isManaged() {
        return false; // Patch panels are passive devices
    }

    @Override
    public String getDefaultProtocol() {
        return "NONE"; // No management protocol
    }

    @Override
    public String getDeviceType() {
        return "PATCH_PANEL";
    }

    @Override
    public Map<String, String> getSpecifications() {
        Map<String, String> specs = new HashMap<>();

        if (getPortCount() != null)
            specs.put("Port Count", Integer.toString(getPortCount()));
        if (panelType != null)
            specs.put("Panel Type", panelType);
        if (connectorType != null)
            specs.put("Connector Type", connectorType);
        if (categoryRating != null)
            specs.put("Category", categoryRating);
        if (rackUnits != null)
            specs.put("Rack Units", rackUnits + "U");
        if (shielded)
            specs.put("Shielding", "Yes");
        if (cableManagement)
            specs.put("Cable Management", "Yes");

        return specs;
    }

    // Business methods
    public void addCableRun(CableRun cableRun) {
        if (cableRun != null) {
            cableRuns.add(cableRun);
        }
    }

    public void removeCableRun(CableRun cableRun) {
        if (cableRun != null) {
            cableRuns.remove(cableRun);
        }
    }

    public boolean isFiberPanel() {
        return "FIBER".equals(panelType) || "HYBRID".equals(panelType);
    }

    public boolean isCopperPanel() {
        return "COPPER".equals(panelType) || "HYBRID".equals(panelType);
    }

    public boolean isHighDensity() {
        return getPortCount() != null && rackUnits != null &&
                (getPortCount() / (double) rackUnits) >= 24.0;
    }

    public boolean requiresRackSpace() {
        return true; // All patch panels require rack space
    }

    public int getRackSpaceRequired() {
        return rackUnits != null ? rackUnits : 1;
    }

    public String getEffectiveManufacturer() {
        return patchPanelModel != null ? patchPanelModel.getManufacturer() : getManufacturer();
    }

    public String getEffectiveModel() {
        return patchPanelModel != null ? patchPanelModel.getModelNumber() : getModel();
    }

    public String getPortDensityInfo() {
        if (getPortCount() != null && rackUnits != null) {
            double density = getPortCount() / (double) rackUnits;
            return String.format("%.1f ports/U", density);
        }
        return "Unknown density";
    }

    // Enhanced validation
    @Override
    public boolean isValidConfiguration() {
        if (!super.isValidConfiguration()) {
            return false;
        }

        if (getPortCount() != null && getPortCount() <= 0) {
            return false;
        }

        if (rackUnits != null && rackUnits <= 0) {
            return false;
        }

        // If we have a model, verify consistency
        if (patchPanelModel != null) {
            if (getPortCount() != null && !getPortCount().equals(patchPanelModel.getPortCount())) {
                return false;
            }
            if (rackUnits != null && !rackUnits.equals(patchPanelModel.getRackUnits())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "PatchPanel:" + getId() + "[" + getName() +
                (getModel() != null ? ", " + getModel() : "") +
                (getPortCount() != null ? ", " + getPortCount() + " ports" : "") +
                (rackUnits != null ? ", " + rackUnits + "U" : "") +
                (panelType != null ? ", " + panelType : "") + "]";
    }
}