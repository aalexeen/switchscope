package net.switchscope.model.component;

/**
 * Nature/type of component based on its operational characteristics
 */
public enum ComponentNature {

    ACTIVE("Active Equipment", "Equipment with processing logic, software, and management capabilities"),
    PASSIVE("Passive Infrastructure", "Equipment without processing logic - cables, panels, racks, etc."),
    SUPPORT("Support Infrastructure", "Supporting equipment - power, cooling, monitoring");

    private final String displayName;
    private final String description;

    ComponentNature(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if this component nature requires management/configuration
     */
    public boolean requiresManagement() {
        return switch (this) {
            case ACTIVE -> true;
            case PASSIVE, SUPPORT -> false;
        };
    }

    /**
     * Check if this component nature can have IP address
     */
    public boolean canHaveIpAddress() {
        return switch (this) {
            case ACTIVE -> true;
            case PASSIVE -> false;
            case SUPPORT -> true; // Some support equipment like UPS can be managed
        };
    }

    /**
     * Check if this component nature has firmware
     */
    public boolean hasFirmware() {
        return switch (this) {
            case ACTIVE -> true;
            case PASSIVE -> false;
            case SUPPORT -> true; // Smart PDUs, UPS, etc.
        };
    }

    /**
     * Check if this component nature processes network traffic
     */
    public boolean processesNetworkTraffic() {
        return switch (this) {
            case ACTIVE -> true;
            case PASSIVE, SUPPORT -> false;
        };
    }

    /**
     * Check if this component nature can be monitored via SNMP
     */
    public boolean supportsSnmpMonitoring() {
        return switch (this) {
            case ACTIVE -> true;
            case PASSIVE -> false;
            case SUPPORT -> true; // Smart infrastructure
        };
    }

    /**
     * Get typical power consumption category
     */
    public String getPowerConsumptionCategory() {
        return switch (this) {
            case ACTIVE -> "high"; // Switches, routers consume significant power
            case PASSIVE -> "none"; // Patch panels, racks don't consume power
            case SUPPORT -> "variable"; // Depends on specific support equipment
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}