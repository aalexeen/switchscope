package net.switchscope.model.installation;

/**
 * Status of equipment installation
 */
public enum InstallationStatus {

    PLANNED("Planned", "Installation is planned"),
    IN_PROGRESS("In Progress", "Installation is currently being performed"),
    INSTALLED("Installed", "Equipment is successfully installed and operational"),
    TEMPORARILY_REMOVED("Temporarily Removed", "Equipment temporarily removed for maintenance"),
    REMOVED("Removed", "Equipment permanently removed from this location"),
    FAILED("Failed", "Installation failed or equipment malfunctioned"),
    PENDING_REMOVAL("Pending Removal", "Equipment scheduled for removal");

    private final String displayName;
    private final String description;

    InstallationStatus(String displayName, String description) {
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
     * Check if equipment is physically present at the location
     */
    public boolean isPhysicallyPresent() {
        return switch (this) {
            case INSTALLED, TEMPORARILY_REMOVED, IN_PROGRESS -> true;
            case PLANNED, REMOVED, FAILED, PENDING_REMOVAL -> false;
        };
    }

    /**
     * Check if equipment is operational
     */
    public boolean isOperational() {
        return switch (this) {
            case INSTALLED -> true;
            default -> false;
        };
    }

    /**
     * Check if status requires attention
     */
    public boolean requiresAttention() {
        return switch (this) {
            case FAILED, PENDING_REMOVAL, IN_PROGRESS -> true;
            default -> false;
        };
    }

    /**
     * Get color category for UI representation
     */
    public String getColorCategory() {
        return switch (this) {
            case INSTALLED -> "success";
            case IN_PROGRESS, PLANNED -> "info";
            case TEMPORARILY_REMOVED, PENDING_REMOVAL -> "warning";
            case REMOVED, FAILED -> "danger";
        };
    }

    /**
     * Get next logical statuses from current status
     */
    public InstallationStatus[] getNextPossibleStatuses() {
        return switch (this) {
            case PLANNED -> new InstallationStatus[]{IN_PROGRESS, FAILED};
            case IN_PROGRESS -> new InstallationStatus[]{INSTALLED, FAILED};
            case INSTALLED -> new InstallationStatus[]{TEMPORARILY_REMOVED, PENDING_REMOVAL, FAILED};
            case TEMPORARILY_REMOVED -> new InstallationStatus[]{INSTALLED, REMOVED, FAILED};
            case PENDING_REMOVAL -> new InstallationStatus[]{REMOVED, INSTALLED};
            case REMOVED, FAILED -> new InstallationStatus[]{PLANNED}; // Can be reinstalled
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}
