package net.switchscope.model.installation;

/**
 * Types of items that can be installed in housing components
 */
public enum InstallableType {

    // Active devices
    DEVICE("Device", "Active network devices"),

    // Passive connectivity
    CONNECTIVITY_COMPONENT("Connectivity Component", "Passive connectivity infrastructure"),

    // Support components
    SUPPORT_COMPONENT("Support Component", "Supporting infrastructure components"),

    // Future extensibility
    POWER_COMPONENT("Power Component", "Power distribution and management"),
    SECURITY_COMPONENT("Security Component", "Security and access control"),
    ENVIRONMENTAL_COMPONENT("Environmental Component", "Environmental monitoring and control"),

    // Generic for unknown/custom types
    OTHER("Other", "Other installable components");

    private final String displayName;
    private final String description;

    InstallableType(String displayName, String description) {
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
     * Get the base entity class for this installable type
     */
    public String getEntityClass() {
        return switch (this) {
            case DEVICE -> "Device";
            case CONNECTIVITY_COMPONENT -> "ConnectivityComponent";
            case SUPPORT_COMPONENT -> "SupportComponent";
            case POWER_COMPONENT -> "PowerComponent";
            case SECURITY_COMPONENT -> "SecurityComponent";
            case ENVIRONMENTAL_COMPONENT -> "EnvironmentalComponent";
            case OTHER -> "Object";
        };
    }

    /**
     * Check if this type typically requires rack positioning
     */
    public boolean requiresRackPosition() {
        return switch (this) {
            case DEVICE, CONNECTIVITY_COMPONENT -> true;
            case SUPPORT_COMPONENT, POWER_COMPONENT -> false; // depends on specific component
            default -> false;
        };
    }

    /**
     * Check if this type typically has standardized rack units
     */
    public boolean hasStandardRackUnits() {
        return switch (this) {
            case DEVICE -> true;
            case CONNECTIVITY_COMPONENT -> true;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}
