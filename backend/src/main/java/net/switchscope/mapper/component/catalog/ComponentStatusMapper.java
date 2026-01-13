package net.switchscope.mapper.component.catalog;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.to.component.catalog.ComponentStatusTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for ComponentStatusEntity <-> ComponentStatusTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface ComponentStatusMapper extends BaseMapper<ComponentStatusEntity, ComponentStatusTo> {

    @Mapping(target = "properties", source = "properties")
    @Mapping(target = "nextPossibleStatusCodes", source = "nextPossibleStatusCodes")
    @Override
    ComponentStatusTo toTo(ComponentStatusEntity entity);

    @Mapping(target = "nextPossibleStatusCodes", ignore = true)
    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    ComponentStatusEntity toEntity(ComponentStatusTo to);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nextPossibleStatusCodes", ignore = true)
    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    ComponentStatusEntity updateFromTo(@MappingTarget ComponentStatusEntity entity, ComponentStatusTo to);
}

