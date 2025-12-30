package net.switchscope.mapper.component.catalog.housing;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.mapper.component.catalog.ComponentModelMapper;
import net.switchscope.model.component.catalog.housing.RackModelEntity;
import net.switchscope.to.component.catalog.housing.RackModelTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for RackModelEntity <-> RackModelTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface RackModelMapper extends ComponentModelMapper<RackModelEntity, RackModelTo> {

    // Entity -> TO mappings
    @Mapping(target = "componentTypeId", source = "componentType.id")
    @Mapping(target = "componentTypeCode", source = "componentType.code")
    @Mapping(target = "componentTypeDisplayName", source = "componentType.displayName")
    @Mapping(target = "modelDesignation", expression = "java(entity.getModelDesignation())")
    @Mapping(target = "lifecycleStatus", expression = "java(entity.getLifecycleStatus())")
    @Mapping(target = "currentlySupported", expression = "java(entity.isCurrentlySupported())")
    @Mapping(target = "availableForPurchase", expression = "java(entity.isAvailableForPurchase())")
    @Mapping(target = "operatingTemperatureRange", expression = "java(entity.getOperatingTemperatureRange())")
    @Mapping(target = "humidityRange", expression = "java(entity.getHumidityRange())")
    @Mapping(target = "categoryName", expression = "java(entity.getCategoryName())")
    // Rack-specific computed fields
    @Mapping(target = "rackCategory", expression = "java(entity.getRackCategory())")
    @Mapping(target = "sizeCategory", expression = "java(entity.getSizeCategory())")
    @Mapping(target = "dimensionsDescription", expression = "java(entity.getDimensionsDescription())")
    @Mapping(target = "featuresSummary", expression = "java(entity.getFeaturesSummary())")
    @Mapping(target = "compact", expression = "java(entity.isCompact())")
    @Mapping(target = "fullHeight", expression = "java(entity.isFullHeight())")
    @Mapping(target = "halfHeight", expression = "java(entity.isHalfHeight())")
    @Override
    RackModelTo toTo(RackModelEntity entity);

    // TO -> Entity (create)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Override
    RackModelEntity toEntity(RackModelTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Override
    RackModelEntity updateFromTo(@MappingTarget RackModelEntity entity, RackModelTo to);
}

