package net.switchscope.model.location;

/**
 * Types of network sites in equipment placement hierarchy
 */
public enum LocationType {

    // Top level
    BUILDING("Building", "Separate building or structure"),
    CAMPUS("Campus", "Group of buildings or territory"),
    DATACENTER("Datacenter", "Data processing center"),

    // Middle level
    FLOOR("Floor", "Floor in building"),
    WING("Wing", "Wing or section of building"),
    ZONE("Zone", "Functional zone"),

    // Lower level
    ROOM("Room", "Individual room"),
    OFFICE("Office", "Work office"),
    SERVER_ROOM("Server Room", "Server room"),
    NETWORK_CLOSET("Network Closet", "Telecommunications closet"),

    // Special types
    RACK("Rack", "Server rack or cabinet"),
    CABINET("Cabinet", "Telecommunications cabinet"),
    PATCH_PANEL_SECTION("Patch Panel Section", "Patch panel section"),

    // Logical
    VIRTUAL("Virtual", "Virtual or logical site"),
    EXTERNAL("External", "External site (outside organization)"),
    UNKNOWN("Unknown", "Type not determined");

    private final String displayName;
    private final String description;

    LocationType(String displayName, String description) {
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
     * Checks if this site type can contain child sites
     */
    public boolean canHaveChildren() {
        return switch (this) {
            case PATCH_PANEL_SECTION, VIRTUAL, EXTERNAL -> false;
            default -> true;
        };
    }

    /**
     * Checks if this site type can hold equipment directly
     */
    public boolean canHoldEquipment() {
        return switch (this) {
            case CAMPUS, BUILDING, FLOOR, WING, ZONE -> false;
            default -> true;
        };
    }

    /**
     * Returns priority for sorting (lower numbers - higher in hierarchy)
     */
    public int getHierarchyLevel() {
        return switch (this) {
            case CAMPUS -> 1;
            case BUILDING, DATACENTER -> 2;
            case FLOOR, WING, ZONE -> 3;
            case ROOM, OFFICE, SERVER_ROOM, NETWORK_CLOSET -> 4;
            case RACK, CABINET -> 5;
            case PATCH_PANEL_SECTION -> 6;
            case VIRTUAL, EXTERNAL, UNKNOWN -> 99;
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}
