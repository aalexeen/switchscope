package net.switchscope.mapper.component.catalog;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.to.component.catalog.ComponentStatusTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for ComponentStatusEntity <-> ComponentStatusTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface ComponentStatusMapper extends BaseMapper<ComponentStatusEntity, ComponentStatusTo> {

    @Mapping(target = "nextPossibleStatusIds", source = "nextPossibleStatuses", qualifiedByName = "statusesToIds")
    @Mapping(target = "nextPossibleStatusCodes", expression = "java(entity.getNextPossibleStatusCodes())")
    @Override
    ComponentStatusTo toTo(ComponentStatusEntity entity);

    @Mapping(target = "nextPossibleStatuses", ignore = true)
    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    ComponentStatusEntity toEntity(ComponentStatusTo to);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nextPossibleStatuses", ignore = true)
    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    ComponentStatusEntity updateFromTo(@MappingTarget ComponentStatusEntity entity, ComponentStatusTo to);

    @Named("statusesToIds")
    default Set<UUID> statusesToIds(Set<ComponentStatusEntity> statuses) {
        if (statuses == null) return new HashSet<>();
        return statuses.stream()
                .map(ComponentStatusEntity::getId)
                .collect(Collectors.toSet());
    }
}

