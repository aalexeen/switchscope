package net.switchscope.model.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.NamedEntity;
import net.switchscope.validation.NoHtml;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location extends NamedEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @NotNull
    private LocationType type;

    @Column(name = "address")
    @Size(max = 512)
    @NoHtml
    private String address;

    // Self-reference for parent-child hierarchy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_location_id")
    @JsonIgnore // Avoid circular references in JSON
    private Location parentLocation;

    @OneToMany(mappedBy = "parentLocation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> childLocations = new ArrayList<>();

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Device> equipment = new ArrayList<>();

    // Constructors
    public Location(Integer id, String name, LocationType type) {
        this(id, name, type, null, null);
    }

    public Location(Integer id, String name, LocationType type, String address) {
        this(id, name, type, address, null);
    }

    public Location(Integer id, String name, LocationType type, String address, Location parentLocation) {
        super(id, name);
        this.type = type;
        this.address = address;
        this.parentLocation = parentLocation;
    }

    // Convenience methods
    public void addChildLocation(Location child) {
        if (child != null) {
            childLocations.add(child);
            child.setParentLocation(this);
        }
    }

    public void removeChildLocation(Location child) {
        if (child != null) {
            childLocations.remove(child);
            child.setParentLocation(null);
        }
    }

    public void addEquipment(Equipment equipment) {
        if (equipment != null) {
            this.equipment.add(equipment);
            equipment.setLocation(this);
        }
    }

    public void removeEquipment(Equipment equipment) {
        if (equipment != null) {
            this.equipment.remove(equipment);
            equipment.setLocation(null);
        }
    }

    // Helper methods
    public boolean isRootLocation() {
        return parentLocation == null;
    }

    public boolean hasChildren() {
        return !childLocations.isEmpty();
    }

    public boolean hasEquipment() {
        return !equipment.isEmpty();
    }

    /**
     * Get full path from root to current location
     * @return string like "Building/Floor1/Room101"
     */
    public String getFullPath() {
        if (parentLocation == null) {
            return name;
        }
        return parentLocation.getFullPath() + "/" + name;
    }

    /**
     * Get nesting level (0 - root location)
     */
    public int getLevel() {
        if (parentLocation == null) {
            return 0;
        }
        return parentLocation.getLevel() + 1;
    }

    @Override
    public String toString() {
        return "Location:" + id + "[" + name + ", " + type + "]";
    }
}

