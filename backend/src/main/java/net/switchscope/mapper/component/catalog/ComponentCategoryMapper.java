package net.switchscope.mapper.component.catalog;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.component.ComponentCategoryEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.to.component.catalog.ComponentCategoryTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for ComponentCategoryEntity <-> ComponentCategoryTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface ComponentCategoryMapper extends BaseMapper<ComponentCategoryEntity, ComponentCategoryTo> {

    @Mapping(target = "properties", source = "properties")
    @Mapping(target = "componentTypeIds", source = "componentTypes", qualifiedByName = "componentTypesToIds")
    @Mapping(target = "componentTypeCount", expression = "java(entity.getComponentTypes().size())")
    @Mapping(target = "hasActiveComponentTypes", expression = "java(entity.hasActiveComponentTypes())")
    @Mapping(target = "canBeDeleted", expression = "java(entity.canBeDeleted())")
    @Mapping(target = "housingCategory", expression = "java(entity.isHousingCategory())")
    @Mapping(target = "connectivityCategory", expression = "java(entity.isConnectivityCategory())")
    @Mapping(target = "supportCategory", expression = "java(entity.isSupportCategory())")
    @Mapping(target = "moduleCategory", expression = "java(entity.isModuleCategory())")
    @Override
    ComponentCategoryTo toTo(ComponentCategoryEntity entity);

    @Mapping(target = "componentTypes", ignore = true)
    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    ComponentCategoryEntity toEntity(ComponentCategoryTo to);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentTypes", ignore = true)
    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    ComponentCategoryEntity updateFromTo(@MappingTarget ComponentCategoryEntity entity, ComponentCategoryTo to);

    @Named("componentTypesToIds")
    default List<UUID> componentTypesToIds(List<ComponentTypeEntity> componentTypes) {
        if (componentTypes == null) return new ArrayList<>();
        return componentTypes.stream()
                .map(ComponentTypeEntity::getId)
                .collect(Collectors.toList());
    }
}

