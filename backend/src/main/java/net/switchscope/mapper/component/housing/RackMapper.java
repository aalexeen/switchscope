package net.switchscope.mapper.component.housing;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.mapper.component.ComponentMapper;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.to.component.housing.RackTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for Rack entity <-> RackTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface RackMapper extends ComponentMapper<Rack, RackTo> {

    // Entity -> TO mappings
    @Mapping(target = "componentStatusId", source = "componentStatus.id")
    @Mapping(target = "componentStatusCode", source = "componentStatus.code")
    @Mapping(target = "componentStatusDisplayName", source = "componentStatus.displayName")
    @Mapping(target = "componentTypeId", source = "componentType.id")
    @Mapping(target = "componentTypeCode", source = "componentType.code")
    @Mapping(target = "componentTypeDisplayName", source = "componentType.displayName")
    @Mapping(target = "componentNatureId", source = "componentNature.id")
    @Mapping(target = "componentNatureCode", source = "componentNature.code")
    @Mapping(target = "installationId", source = "installation.id")
    @Mapping(target = "parentComponentId", source = "parentComponent.id")
    @Mapping(target = "parentComponentName", source = "parentComponent.name")
    @Mapping(target = "componentPath", expression = "java(entity.getComponentPath())")
    @Mapping(target = "operational", expression = "java(entity.isOperational())")
    @Mapping(target = "installed", expression = "java(entity.isInstalled())")
    @Mapping(target = "locationAddress", expression = "java(entity.getLocationAddress())")
    // Rack-specific mappings
    @Mapping(target = "rackTypeId", source = "rackType.id")
    @Mapping(target = "rackTypeName", source = "rackType.name")
    @Mapping(target = "occupiedRackUnits", expression = "java(entity.getOccupiedRackUnits())")
    @Mapping(target = "availableRackUnits", expression = "java(entity.getAvailableRackUnits())")
    @Mapping(target = "utilizationPercentage", expression = "java(entity.getUtilizationPercentage())")
    @Mapping(target = "hasAvailableSpace", expression = "java(entity.hasAvailableSpace())")
    @Mapping(target = "dimensionsDescription", expression = "java(entity.getDimensionsDescription())")
    @Mapping(target = "wallMountable", expression = "java(entity.isWallMountable())")
    @Mapping(target = "outdoorRated", expression = "java(entity.isOutdoorRated())")
    @Mapping(target = "environmentalMonitoring", expression = "java(entity.hasEnvironmentalMonitoring())")
    @Override
    RackTo toTo(Rack entity);

    // TO -> Entity (create)
    @Mapping(target = "componentStatus", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "componentNature", ignore = true)
    @Mapping(target = "installation", ignore = true)
    @Mapping(target = "parentComponent", ignore = true)
    @Mapping(target = "childComponents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rackType", ignore = true)
    @Override
    Rack toEntity(RackTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentStatus", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "componentNature", ignore = true)
    @Mapping(target = "installation", ignore = true)
    @Mapping(target = "parentComponent", ignore = true)
    @Mapping(target = "childComponents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rackType", ignore = true)
    @Override
    Rack updateFromTo(@MappingTarget Rack entity, RackTo to);
}

