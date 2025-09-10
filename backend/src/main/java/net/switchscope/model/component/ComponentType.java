package net.switchscope.model.component;

/**
 * Enumeration of all component types in the system
 */
public enum ComponentType {
    // Housing components
    RACK("Rack", "housing"),
    CHASSIS("Chassis", "housing"),
    ENCLOSURE("Enclosure", "housing"),

    // Connectivity components
    SWITCH("Switch", "connectivity"),
    ROUTER("Router", "connectivity"),
    PATCH_PANEL("Patch Panel", "connectivity"),
    CABLE("Cable", "connectivity"),
    CONNECTOR("Connector", "connectivity"),

    // Support components
    POWER_SUPPLY("Power Supply", "support"),
    COOLING_FAN("Cooling Fan", "support"),
    UPS("UPS", "support"),
    PDU("PDU", "support"),

    // Module components
    LINE_CARD("Line Card", "module"),
    TRANSCEIVER("Transceiver", "module"),
    POWER_MODULE("Power Module", "module");

    private final String displayName;
    private final String category;

    ComponentType(String displayName, String category) {
        this.displayName = displayName;
        this.category = category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCategory() {
        return category;
    }

    public boolean isHousingComponent() {
        return "housing".equals(category);
    }

    public boolean isConnectivityComponent() {
        return "connectivity".equals(category);
    }

    public boolean isSupportComponent() {
        return "support".equals(category);
    }
}