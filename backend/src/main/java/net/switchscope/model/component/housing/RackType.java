package net.switchscope.model.component.housing;

/**
 * Types of equipment racks
 */
public enum RackType {

    STANDARD_19_INCH("Standard 19\"", "Standard 19-inch rack", 19.0),
    STANDARD_23_INCH("Standard 23\"", "Standard 23-inch rack", 23.0),
    WALL_MOUNT("Wall Mount", "Wall-mounted rack", 19.0),
    OPEN_FRAME("Open Frame", "Open frame rack", 19.0),
    ENCLOSED("Enclosed", "Fully enclosed rack", 19.0),
    NETWORK_CABINET("Network Cabinet", "Network equipment cabinet", 19.0),
    SERVER_CABINET("Server Cabinet", "Server equipment cabinet", 19.0),
    OUTDOOR("Outdoor", "Weather-resistant outdoor rack", 19.0),
    CUSTOM("Custom", "Custom rack dimensions", null);

    private final String displayName;
    private final String description;
    private final Double standardWidth; // in inches

    RackType(String displayName, String description, Double standardWidth) {
        this.displayName = displayName;
        this.description = description;
        this.standardWidth = standardWidth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Double getStandardWidth() {
        return standardWidth;
    }

    /**
     * Check if this rack type supports standard rack units
     */
    public boolean supportsStandardRackUnits() {
        return switch (this) {
            case STANDARD_19_INCH, STANDARD_23_INCH, OPEN_FRAME,
                 ENCLOSED, NETWORK_CABINET, SERVER_CABINET -> true;
            case WALL_MOUNT, OUTDOOR, CUSTOM -> false;
        };
    }

    /**
     * Check if this rack type is suitable for network equipment
     */
    public boolean isSuitableForNetworkEquipment() {
        return switch (this) {
            case STANDARD_19_INCH, NETWORK_CABINET, WALL_MOUNT,
                 OPEN_FRAME, ENCLOSED -> true;
            default -> false;
        };
    }

    /**
     * Check if this rack type is suitable for server equipment
     */
    public boolean isSuitableForServerEquipment() {
        return switch (this) {
            case STANDARD_19_INCH, STANDARD_23_INCH, SERVER_CABINET,
                 ENCLOSED, OPEN_FRAME -> true;
            default -> false;
        };
    }

    /**
     * Get typical rack unit capacity
     */
    public int getTypicalCapacity() {
        return switch (this) {
            case STANDARD_19_INCH, STANDARD_23_INCH, ENCLOSED,
                 NETWORK_CABINET, SERVER_CABINET -> 42;
            case OPEN_FRAME -> 45;
            case WALL_MOUNT -> 12;
            case OUTDOOR -> 24;
            case CUSTOM -> 42; // Default assumption
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}
