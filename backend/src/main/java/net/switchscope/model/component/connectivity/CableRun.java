package net.switchscope.model.component.connectivity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.Component;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.catalog.connectiviy.CableRunModel;
import net.switchscope.model.location.Location;
import net.switchscope.validation.NoHtml;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cable Run entity - represents a specific cable installation between locations
 */
@Entity
@DiscriminatorValue("CABLE_RUN")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CableRun extends Component {

    // Link to cable model catalog
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cable_model_id")
    private CableRunModel cableModel;

    // Cable specifications
    @Column(name = "cable_length_meters")
    @NotNull
    @DecimalMin(value = "0.1")
    @DecimalMax(value = "10000.0")
    private Double cableLengthMeters;

    @Column(name = "cable_type")
    @Size(max = 64)
    @NoHtml
    private String cableType; // From model or manual override

    @Column(name = "cable_category")
    @Size(max = 32)
    @NoHtml
    private String cableCategory;

    // Installation details
    /*@Column(name = "installation_date")
    private LocalDate installationDate;*/

    @Column(name = "installation_method")
    @Size(max = 64)
    @NoHtml
    private String installationMethod; // PULLED, BLOWN, DIRECT_BURIAL, AERIAL

    @Column(name = "cable_pathway")
    @Size(max = 128)
    @NoHtml
    private String cablePpathway; // CONDUIT, TRAY, RACEWAY, J_HOOKS

    // Testing and certification
    @Column(name = "tested", nullable = false)
    private boolean tested = false;

    @Column(name = "test_date")
    private LocalDate testDate;

    @Column(name = "test_result")
    @Size(max = 32)
    @NoHtml
    private String testResult; // PASS, FAIL, MARGINAL

    @Column(name = "test_notes")
    @Size(max = 512)
    @NoHtml
    private String testNotes;

    // Documentation
    @Column(name = "cable_id")
    @Size(max = 64)
    @NoHtml
    private String cableId; // Physical label/marking on cable

    @Column(name = "circuit_id")
    @Size(max = 64)
    @NoHtml
    private String circuitId; // Logical circuit identifier

    @Column(name = "installation_notes")
    @Size(max = 1024)
    @NoHtml
    private String installationNotes;

    // Relationships - CableRun can span multiple locations
    @ManyToMany
    @JoinTable(
        name = "cable_run_locations",
        joinColumns = @JoinColumn(name = "cable_run_id"),
        inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    @OrderColumn(name = "location_order") // Важно для сохранения порядка
    private List<Location> locations = new ArrayList<>();

    // Start and end locations (most common case)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_location_id")
    private Location startLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_location_id")
    private Location endLocation;

    // Related connectors
    @OneToMany(mappedBy = "cableRun", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Connector> connectors = new ArrayList<>();

    // Constructors
    public CableRun(UUID id, String name, ComponentTypeEntity componentType,
                   Double cableLengthMeters) {
        super(id, name, componentType);
        this.cableLengthMeters = cableLengthMeters;
    }

    public CableRun(UUID id, String name, String manufacturer, String model,
                   String serialNumber, ComponentTypeEntity componentType,
                   Double cableLengthMeters) {
        super(id, name, manufacturer, model, serialNumber, componentType);
        this.cableLengthMeters = cableLengthMeters;
    }

    // Component abstract methods implementation
    @Override
    public boolean isInstallable() {
        return true;
    }

    @Override
    public Map<String, String> getSpecifications() {
        Map<String, String> specs = new HashMap<>();

        specs.put("Length", cableLengthMeters + "m");
        if (cableType != null) specs.put("Cable Type", cableType);
        if (cableCategory != null) specs.put("Category", cableCategory);
        if (installationMethod != null) specs.put("Installation Method", installationMethod);
        if (tested) {
            specs.put("Tested", testResult != null ? testResult : "Yes");
            if (testDate != null) specs.put("Test Date", testDate.toString());
        }

        return specs;
    }

    // Business methods
    public void addLocation(Location location) {
        if (location != null && !locations.contains(location)) {
            locations.add(location);
        }
    }

    public void addLocationAtPosition(Location location, int position) {
        if (location != null && !locations.contains(location)) {
            if (position >= 0 && position <= locations.size()) {
                locations.add(position, location);
            } else {
                locations.add(location);
            }
        }
    }

    public void removeLocation(Location location) {
        if (location != null) {
            locations.remove(location);
        }
    }

    public Location getFirstLocation() {
        return locations.isEmpty() ? null : locations.get(0);
    }

    public Location getLastLocation() {
        return locations.isEmpty() ? null : locations.get(locations.size() - 1);
    }

    public List<Location> getLocationPath() {
        return new ArrayList<>(locations); // Возвращаем копию для безопасности
    }

    public String getOrderedLocationPath() {
        if (locations.isEmpty()) {
            return "No locations defined";
        }

        if (locations.size() == 2) {
            return locations.get(0).getName() + " → " + locations.get(1).getName();
        }

        return locations.stream()
                .map(Location::getName)
                .collect(Collectors.joining(" → "));
    }

    public boolean isPointToPoint() {
        return locations.size() == 2;
    }

    public boolean isMultiPoint() {
        return locations.size() > 2;
    }

    public boolean isValidCablePath() {
        if (locations.size() < 2) {
            return false; // Кабель должен соединять минимум 2 локации
        }

        // Проверка на дубликаты локаций в пути
        Set<Location> uniqueLocations = new HashSet<>(locations);
        return uniqueLocations.size() == locations.size();
    }

    public void addConnector(Connector connector) {
        if (connector != null && !connectors.contains(connector)) {
            connectors.add(connector);
            connector.setCableRun(this);
        }
    }

    public void removeConnector(Connector connector) {
        if (connector != null && connectors.contains(connector)) {
            connectors.remove(connector);
            connector.setCableRun(null);
        }
    }

    public boolean isPassed() {
        return tested && "PASS".equals(testResult);
    }

    public boolean requiresTesting() {
        return !tested || testDate == null ||
               (testDate.isBefore(LocalDate.now().minusYears(1)));
    }

    public String getEffectiveManufacturer() {
        return cableModel != null ? cableModel.getManufacturer() : getManufacturer();
    }

    public String getEffectiveModel() {
        return cableModel != null ? cableModel.getModelNumber() : getModel();
    }

    public String getEffectiveCableType() {
        return cableModel != null ? cableModel.getCableType() : cableType;
    }

    @Override
    public boolean isValidConfiguration() {
        if (!super.isValidConfiguration()) {
            return false;
        }

        if (cableLengthMeters == null || cableLengthMeters <= 0) {
            return false;
        }

        // Must have at least start and end locations
        if (startLocation == null || endLocation == null) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "CableRun:" + getId() + "[" + getName() +
               ", " + cableLengthMeters + "m" +
               (getEffectiveCableType() != null ? ", " + getEffectiveCableType() : "") +
               ", " + getLocationPath() + "]";
    }
}