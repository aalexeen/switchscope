package net.switchscope.mapper.component.catalog;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.to.component.catalog.ComponentModelTo;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Base mapper interface for ComponentModel catalog entities.
 * Provides common mapping configurations for all component model types.
 */
public interface ComponentModelMapper<E extends ComponentModel, T extends ComponentModelTo> extends BaseMapper<E, T> {

    // Common mappings for Entity -> TO
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
    @Override
    T toTo(E entity);

    // Common mappings for TO -> Entity (create)
    @Mapping(target = "componentType", ignore = true) // Set via service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Override
    E toEntity(T to);

    // Common mappings for TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Override
    E updateFromTo(@MappingTarget E entity, T to);
}

