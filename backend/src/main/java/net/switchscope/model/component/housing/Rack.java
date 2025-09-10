package net.switchscope.model.component.housing;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.component.Component;
import net.switchscope.model.component.ComponentType;
import net.switchscope.model.installation.Installation;
import net.switchscope.model.location.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("RACK")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rack extends Component {

    @Column(name = "rack_units_total")
    private Integer rackUnitsTotal = 42; // Standard 42U rack

    @Enumerated(EnumType.STRING)
    @Column(name = "rack_type")
    private RackType rackType = RackType.STANDARD_19_INCH;

    @Column(name = "front_door_type")
    private String frontDoorType;

    @Column(name = "rear_door_type")
    private String rearDoorType;

    @Column(name = "has_side_panels")
    private Boolean hasSidePanels = true;

    @Column(name = "power_outlets")
    private Integer powerOutlets;

    @Column(name = "cooling_type")
    private String coolingType;

    // Constructors
    public Rack(Integer id, String name, Location location) {
        super(id, name);
        setLocation(location);
    }

    public Rack(Integer id, String name, Location location,
                Integer rackUnitsTotal, RackType rackType) {
        super(id, name);
        setLocation(location);
        this.rackUnitsTotal = rackUnitsTotal;
        this.rackType = rackType;
    }

    public Rack(Integer id, String name, String manufacturer, String model,
                Integer rackUnitsTotal, RackType rackType) {
        super(id, name, manufacturer, model, null);
        this.rackUnitsTotal = rackUnitsTotal;
        this.rackType = rackType;
    }

    // Implementing abstract methods from Component
    @Override
    public ComponentType getComponentType() {
        return ComponentType.RACK;
    }

    @Override
    public boolean canHoldOtherComponents() {
        return true;
    }

    @Override
    public boolean isInstallable() {
        return true;
    }

    @Override
    public boolean requiresPhysicalSpace() {
        return true;
    }

    @Override
    public Map<String, String> getSpecifications() {
        Map<String, String> specs = new HashMap<>();
        specs.put("Total Rack Units", String.valueOf(rackUnitsTotal));
        specs.put("Rack Type", rackType != null ? rackType.toString() : "N/A");
        specs.put("Front Door", frontDoorType != null ? frontDoorType : "N/A");
        specs.put("Rear Door", rearDoorType != null ? rearDoorType : "N/A");
        specs.put("Side Panels", hasSidePanels != null ? hasSidePanels.toString() : "N/A");
        if (powerOutlets != null) {
            specs.put("Power Outlets", String.valueOf(powerOutlets));
        }
        if (coolingType != null) {
            specs.put("Cooling Type", coolingType);
        }
        return specs;
    }

    // Helper method to get active installations through Installation entity
    private List<Installation> getActiveInstallations() {
        if (getInstallation() != null) {
            return getInstallation().getActiveInstallationsForComponent(this);
        }
        return List.of();
    }

    // Rack-specific methods combining old detailed logic with new architecture
    public boolean hasAvailableSpace() {
        return getAvailableSpace() > 0;
    }

    public int getOccupiedSpace() {
        return getActiveInstallations().stream()
                .filter(Installation::isRackMounted)
                .mapToInt(inst -> inst.getRackUnitHeight() != null ? inst.getRackUnitHeight() : 1)
                .sum();
    }

    public int getAvailableSpace() {
        return rackUnitsTotal - getOccupiedSpace();
    }

    /**
     * Get all occupied rack positions
     */
    public Set<Integer> getOccupiedPositions() {
        return getActiveInstallations().stream()
                .filter(Installation::isRackMounted)
                .map(Installation::getRackPosition)
                .collect(Collectors.toSet());
    }

    /**
     * Check if specific rack position is available
     */
    public boolean isPositionAvailable(int position) {
        if (position < 1 || position > rackUnitsTotal) {
            return false;
        }
        return !getOccupiedPositions().contains(position);
    }

    /**
     * Check if range of positions is available for equipment of given height
     */
    public boolean isPositionRangeAvailable(int startPosition, int height) {
        if (startPosition < 1 || startPosition + height - 1 > rackUnitsTotal) {
            return false;
        }

        Set<Integer> occupied = getOccupiedPositions();
        for (int pos = startPosition; pos < startPosition + height; pos++) {
            if (occupied.contains(pos)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Find first available position for equipment of given height
     */
    public Integer findFirstAvailablePosition(int height) {
        for (int pos = 1; pos <= rackUnitsTotal - height + 1; pos++) {
            if (isPositionRangeAvailable(pos, height)) {
                return pos;
            }
        }
        return null;
    }

    /**
     * Get rack utilization percentage
     */
    public double getUtilizationPercentage() {
        if (rackUnitsTotal == 0) return 0.0;
        return (double) getOccupiedSpace() / rackUnitsTotal * 100.0;
    }

    // Alternative methods for compatibility with new architecture
    public int getOccupiedRackUnits() {
        return getOccupiedSpace();
    }

    public int getAvailableRackUnits() {
        return getAvailableSpace();
    }

    public boolean hasAvailableRackSpace() {
        return hasAvailableSpace();
    }

    @Override
    public boolean isValidConfiguration() {
        return super.isValidConfiguration() &&
               rackUnitsTotal != null && rackUnitsTotal > 0 &&
               rackType != null;
    }

    @Override
    public String toString() {
        return "Rack:" + getId() + "[" + getName() + ", " +
               getOccupiedSpace() + "/" + rackUnitsTotal + "U]";
    }
}