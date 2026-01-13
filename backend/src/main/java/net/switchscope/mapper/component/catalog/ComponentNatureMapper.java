package net.switchscope.mapper.component.catalog;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.component.ComponentNatureEntity;
import net.switchscope.to.component.catalog.ComponentNatureTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for ComponentNatureEntity <-> ComponentNatureTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface ComponentNatureMapper extends BaseMapper<ComponentNatureEntity, ComponentNatureTo> {

    @Mapping(target = "properties", source = "properties")
    @Override
    ComponentNatureTo toTo(ComponentNatureEntity entity);

    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    ComponentNatureEntity toEntity(ComponentNatureTo to);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    ComponentNatureEntity updateFromTo(@MappingTarget ComponentNatureEntity entity, ComponentNatureTo to);
}

