package net.switchscope.mapper.location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.location.Location;
import net.switchscope.to.location.LocationTo;

/**
 * Mapper for Location entity <-> LocationTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface LocationMapper extends BaseMapper<Location, LocationTo> {

    // Entity -> TO mappings
    @Mapping(target = "typeId", source = "type.id")
    @Mapping(target = "typeCode", source = "type.code")
    @Mapping(target = "typeDisplayName", source = "type.displayName")
    @Mapping(target = "parentLocationId", source = "parentLocation.id")
    @Mapping(target = "parentLocationName", source = "parentLocation.name")
    @Mapping(target = "childLocationIds", source = "childLocations", qualifiedByName = "childLocationsToIds")
    // Computed fields
    @Mapping(target = "fullPath", expression = "java(entity.getFullPath())")
    @Mapping(target = "fullPathWithTypes", expression = "java(entity.getFullPathWithTypes())")
    @Mapping(target = "level", expression = "java(entity.getLevel())")
    @Mapping(target = "hierarchyLevel", expression = "java(entity.getHierarchyLevel())")
    @Mapping(target = "locationCategory", expression = "java(entity.getLocationCategory())")
    @Mapping(target = "rootLocation", expression = "java(entity.isRootLocation())")
    @Mapping(target = "hasChildren", expression = "java(entity.hasChildren())")
    @Mapping(target = "physical", expression = "java(entity.isPhysical())")
    @Mapping(target = "virtual", expression = "java(entity.isVirtual())")
    @Mapping(target = "rackLike", expression = "java(entity.isRackLike())")
    @Mapping(target = "roomLike", expression = "java(entity.isRoomLike())")
    @Mapping(target = "buildingLike", expression = "java(entity.isBuildingLike())")
    @Mapping(target = "childrenCapacity", expression = "java(entity.getChildrenCapacity())")
    @Mapping(target = "equipmentCapacity", expression = "java(entity.getEquipmentCapacity())")
    @Mapping(target = "availableChildrenSlots", expression = "java(entity.getAvailableChildrenSlots())")
    @Mapping(target = "totalRackUnits", expression = "java(entity.getTotalRackUnits())")
    @Mapping(target = "hasBackupPower", expression = "java(entity.hasBackupPower())")
    @Mapping(target = "childLocationCount", expression = "java(entity.getChildLocations().size())")
    @Override
    LocationTo toTo(Location entity);

    // TO -> Entity (create)
    @Mapping(target = "type", ignore = true) // Set via service
    @Mapping(target = "parentLocation", ignore = true) // Set via service
    @Mapping(target = "childLocations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    Location toEntity(LocationTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "parentLocation", ignore = true)
    @Mapping(target = "childLocations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    Location updateFromTo(@MappingTarget Location entity, LocationTo to);

    @Named("childLocationsToIds")
    default List<UUID> childLocationsToIds(List<Location> childLocations) {
        if (childLocations == null)
            return new ArrayList<>();
        return childLocations.stream()
                .map(Location::getId)
                .collect(Collectors.toList());
    }
}
