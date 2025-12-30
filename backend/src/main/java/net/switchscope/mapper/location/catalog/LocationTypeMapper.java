package net.switchscope.mapper.location.catalog;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.location.catalog.LocationTypeEntity;
import net.switchscope.to.location.catalog.LocationTypeTo;

/**
 * Mapper for LocationTypeEntity <-> LocationTypeTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface LocationTypeMapper extends BaseMapper<LocationTypeEntity, LocationTypeTo> {

    // Entity -> TO mappings
    @Mapping(target = "allowedChildTypeIds", source = "allowedChildTypes", qualifiedByName = "locationTypesToIds")
    @Mapping(target = "allowedParentTypeIds", source = "allowedParentTypes", qualifiedByName = "locationTypesToIds")
    // Computed fields
    @Mapping(target = "locationCategory", expression = "java(entity.getLocationCategory())")
    @Mapping(target = "topLevel", expression = "java(entity.isTopLevel())")
    @Mapping(target = "middleLevel", expression = "java(entity.isMiddleLevel())")
    @Mapping(target = "bottomLevel", expression = "java(entity.isBottomLevel())")
    @Mapping(target = "virtual", expression = "java(entity.isVirtual())")
    @Mapping(target = "specialType", expression = "java(entity.isSpecialType())")
    @Mapping(target = "hasCapacityLimits", expression = "java(entity.hasCapacityLimits())")
    @Mapping(target = "secureLocation", expression = "java(entity.isSecureLocation())")
    @Mapping(target = "datacenterLike", expression = "java(entity.isDatacenterLike())")
    @Override
    LocationTypeTo toTo(LocationTypeEntity entity);

    // TO -> Entity (create)
    @Mapping(target = "allowedChildTypes", ignore = true) // Set via service
    @Mapping(target = "allowedParentTypes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    LocationTypeEntity toEntity(LocationTypeTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "allowedChildTypes", ignore = true)
    @Mapping(target = "allowedParentTypes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    LocationTypeEntity updateFromTo(@MappingTarget LocationTypeEntity entity, LocationTypeTo to);

    @Named("locationTypesToIds")
    default Set<UUID> locationTypesToIds(Set<LocationTypeEntity> locationTypes) {
        if (locationTypes == null)
            return new HashSet<>();
        return locationTypes.stream()
                .map(LocationTypeEntity::getId)
                .collect(Collectors.toSet());
    }
}
