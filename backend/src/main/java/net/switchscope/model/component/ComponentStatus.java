package net.switchscope.model.component;

/**
 * Status of infrastructure components (housing, connectivity, support)
 */
public enum ComponentStatus {

    ACTIVE("Active", "Component is operational and available"),
    INACTIVE("Inactive", "Component is temporarily not in use"),
    MAINTENANCE("Maintenance", "Component is under maintenance"),
    DAMAGED("Damaged", "Component is damaged but potentially repairable"),
    DECOMMISSIONED("Decommissioned", "Component is permanently out of service"),
    PLANNED("Planned", "Component is planned for installation"),
    INSTALLING("Installing", "Component is being installed"),
    ORDERED("Ordered", "Component is being ordered"),
    TESTING("Testing", "Component is being tested"),
    RESERVED("Reserved", "Component is reserved for specific use"),
    DISPOSED("Disposed", "Component is removed from inventory"),
    FAULTY("Faulty", "Component needs repair"),
    REPAIRED("Repaired", "Component is fully functional"),
    CONFIGURED("Configured", "Component is configured and ready for use"),
    UNKNOWN("Unknown", "Component status is unknown");

    private final String displayName;
    private final String description;

    ComponentStatus(String displayName, String description) {
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
     * Check if component is available for use
     */
    public boolean isAvailable() {
        return switch (this) {
            case ACTIVE, REPAIRED, CONFIGURED -> true;
            default -> false;
        };
    }

    /**
     * Check if component is operational (working condition)
     */
    public boolean isOperational() {
        return switch (this) {
            case ACTIVE, TESTING, REPAIRED, CONFIGURED -> true;
            default -> false;
        };
    }

    /**
     * Check if component can accept new installations
     */
    public boolean canAcceptInstallations() {
        return switch (this) {
            case ACTIVE, TESTING, REPAIRED, CONFIGURED -> true;
            default -> false;
        };
    }

    /**
     * Check if status requires attention
     */
    public boolean requiresAttention() {
        return switch (this) {
            case DAMAGED, FAULTY, MAINTENANCE, UNKNOWN, INSTALLING, ORDERED -> true;
            default -> false;
        };
    }

    /**
     * Check if component is physically present at location
     */
    public boolean isPhysicallyPresent() {
        return switch (this) {
            case ACTIVE, INACTIVE, MAINTENANCE, DAMAGED, TESTING, RESERVED, FAULTY, REPAIRED, CONFIGURED -> true;
            case DECOMMISSIONED, PLANNED, INSTALLING, ORDERED, DISPOSED, UNKNOWN -> false;
        };
    }

    /**
     * Check if component is in inventory/trackable
     */
    public boolean isInInventory() {
        return switch (this) {
            case DISPOSED -> false;
            default -> true;
        };
    }

    /**
     * Check if component is in a workflow/transitional state
     */
    public boolean isInTransition() {
        return switch (this) {
            case PLANNED, ORDERED, INSTALLING, TESTING, MAINTENANCE, REPAIRED, CONFIGURED -> true;
            default -> false;
        };
    }

    /**
     * Check if component can be used for planning/reservations
     */
    public boolean canBeReserved() {
        return switch (this) {
            case INACTIVE, REPAIRED, CONFIGURED -> true;
            default -> false;
        };
    }

    /**
     * Get color category for UI representation
     */
    public String getColorCategory() {
        return switch (this) {
            case ACTIVE, REPAIRED, CONFIGURED -> "success";
            case INACTIVE, RESERVED -> "warning";
            case DAMAGED, FAULTY, DECOMMISSIONED, DISPOSED -> "danger";
            case MAINTENANCE, INSTALLING, TESTING, ORDERED -> "info";
            case PLANNED -> "primary";
            default -> "secondary";
        };
    }

    /**
     * Get next logical statuses from current status
     */
    public ComponentStatus[] getNextPossibleStatuses() {
        return switch (this) {
            case PLANNED -> new ComponentStatus[]{ORDERED, DECOMMISSIONED};
            case ORDERED -> new ComponentStatus[]{INSTALLING, DECOMMISSIONED};
            case INSTALLING -> new ComponentStatus[]{TESTING, FAULTY, DECOMMISSIONED};
            case TESTING -> new ComponentStatus[]{ACTIVE, REPAIRED, FAULTY, CONFIGURED};
            case ACTIVE -> new ComponentStatus[]{INACTIVE, MAINTENANCE, FAULTY, DAMAGED, RESERVED, DECOMMISSIONED};
            case INACTIVE -> new ComponentStatus[]{ACTIVE, MAINTENANCE, RESERVED, DECOMMISSIONED};
            case MAINTENANCE -> new ComponentStatus[]{ACTIVE, REPAIRED, FAULTY, DAMAGED};
            case DAMAGED -> new ComponentStatus[]{MAINTENANCE, FAULTY, DECOMMISSIONED, DISPOSED};
            case FAULTY -> new ComponentStatus[]{MAINTENANCE, REPAIRED, DECOMMISSIONED, DISPOSED};
            case REPAIRED -> new ComponentStatus[]{ACTIVE, TESTING, RESERVED};
            case RESERVED -> new ComponentStatus[]{ACTIVE, INACTIVE, PLANNED};
            case DECOMMISSIONED -> new ComponentStatus[]{DISPOSED, PLANNED}; // Can be recommissioned
            case CONFIGURED -> new ComponentStatus[]{ACTIVE, RESERVED, TESTING};
            case DISPOSED -> new ComponentStatus[]{}; // Final state
            case UNKNOWN -> ComponentStatus.values(); // Can transition to any state after identification
        };
    }

    /**
     * Get lifecycle phase of the component
     */
    public String getLifecyclePhase() {
        return switch (this) {
            case PLANNED, ORDERED -> "procurement";
            case INSTALLING, TESTING, CONFIGURED -> "deployment";
            case ACTIVE, INACTIVE, RESERVED -> "operational";
            case MAINTENANCE, DAMAGED, FAULTY, REPAIRED -> "maintenance";
            case DECOMMISSIONED, DISPOSED -> "end-of-life";
            case UNKNOWN -> "unclassified";
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}