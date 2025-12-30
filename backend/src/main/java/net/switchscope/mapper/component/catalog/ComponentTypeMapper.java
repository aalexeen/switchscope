package net.switchscope.mapper.component.catalog;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.to.component.catalog.ComponentTypeTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for ComponentTypeEntity <-> ComponentTypeTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface ComponentTypeMapper extends BaseMapper<ComponentTypeEntity, ComponentTypeTo> {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryCode", source = "category.code")
    @Mapping(target = "categoryDisplayName", source = "category.displayName")
    @Mapping(target = "housingComponent", expression = "java(entity.isHousingComponent())")
    @Mapping(target = "connectivityComponent", expression = "java(entity.isConnectivityComponent())")
    @Mapping(target = "supportComponent", expression = "java(entity.isSupportComponent())")
    @Mapping(target = "moduleComponent", expression = "java(entity.isModuleComponent())")
    @Mapping(target = "networkingEquipment", expression = "java(entity.isNetworkingEquipment())")
    @Mapping(target = "powerConsumptionCategory", expression = "java(entity.getPowerConsumptionCategory())")
    @Mapping(target = "canBeDeleted", expression = "java(entity.canBeDeleted())")
    @Override
    ComponentTypeTo toTo(ComponentTypeEntity entity);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    ComponentTypeEntity toEntity(ComponentTypeTo to);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    ComponentTypeEntity updateFromTo(@MappingTarget ComponentTypeEntity entity, ComponentTypeTo to);
}

