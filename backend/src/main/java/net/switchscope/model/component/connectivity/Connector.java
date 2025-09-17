package net.switchscope.model.component.connectivity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.Component;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.catalog.connectiviy.ConnectorModel;
import net.switchscope.validation.NoHtml;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Connector entity - represents a physical connector attached to a cable run
 */
@Entity
@DiscriminatorValue("CONNECTOR")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Connector extends Component {

    // Link to connector model catalog
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connector_model_id")
    private ConnectorModel connectorModel;

    // Connector specifications
    @Column(name = "connector_type")
    @Size(max = 64)
    @NoHtml
    private String connectorType; // From model or manual override

    @Column(name = "connector_position")
    @Size(max = 32)
    @NoHtml
    private String connectorPosition; // START, END, INTERMEDIATE

    @Column(name = "gender")
    @Size(max = 16)
    @NoHtml
    private String gender; // MALE, FEMALE

    // Physical identification
    @Column(name = "connector_label")
    @Size(max = 64)
    @NoHtml
    private String connectorLabel; // Physical label on connector

    @Column(name = "color_code")
    @Size(max = 16)
    @NoHtml
    private String colorCode; // Color coding for identification

    // Installation and condition
    @Column(name = "termination_quality")
    @Size(max = 32)
    @NoHtml
    private String terminationQuality; // GOOD, ACCEPTABLE, POOR, REWORK_NEEDED

    @Column(name = "installation_notes")
    @Size(max = 512)
    @NoHtml
    private String installationNotes;

    // Relationship to cable run
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cable_run_id", nullable = false)
    private CableRun cableRun;

    // Constructors
    public Connector(UUID id, String name, ComponentTypeEntity componentType,
                    CableRun cableRun) {
        super(id, name, componentType);
        this.cableRun = cableRun;
    }

    public Connector(UUID id, String name, String manufacturer, String model,
                    String serialNumber, ComponentTypeEntity componentType,
                    CableRun cableRun) {
        super(id, name, manufacturer, model, serialNumber, componentType);
        this.cableRun = cableRun;
    }

    // Component abstract methods implementation
    @Override
    public boolean isInstallable() {
        return false; // Connectors are part of cable runs, not independently installable
    }

    @Override
    public Map<String, String> getSpecifications() {
        Map<String, String> specs = new HashMap<>();

        if (connectorType != null) specs.put("Connector Type", connectorType);
        if (gender != null) specs.put("Gender", gender);
        if (connectorPosition != null) specs.put("Position", connectorPosition);
        if (colorCode != null) specs.put("Color", colorCode);
        if (terminationQuality != null) specs.put("Quality", terminationQuality);

        return specs;
    }

    // Business methods
    public boolean isStartConnector() {
        return "START".equals(connectorPosition);
    }

    public boolean isEndConnector() {
        return "END".equals(connectorPosition);
    }

    public boolean isIntermediateConnector() {
        return "INTERMEDIATE".equals(connectorPosition);
    }

    public boolean needsRework() {
        return "REWORK_NEEDED".equals(terminationQuality);
    }

    public boolean isGoodQuality() {
        return "GOOD".equals(terminationQuality);
    }

    public String getEffectiveManufacturer() {
        return connectorModel != null ? connectorModel.getManufacturer() : getManufacturer();
    }

    public String getEffectiveModel() {
        return connectorModel != null ? connectorModel.getModelNumber() : getModel();
    }

    public String getEffectiveConnectorType() {
        return connectorModel != null ? connectorModel.getConnectorType() : connectorType;
    }

    @Override
    public boolean isValidConfiguration() {
        if (!super.isValidConfiguration()) {
            return false;
        }

        if (cableRun == null) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Connector:" + getId() + "[" + getName() +
               (getEffectiveConnectorType() != null ? ", " + getEffectiveConnectorType() : "") +
               (connectorPosition != null ? ", " + connectorPosition : "") +
               ", Cable: " + (cableRun != null ? cableRun.getName() : "None") + "]";
    }
}