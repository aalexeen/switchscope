package net.switchscope.mapper.component.catalog.device;

import net.switchscope.mapper.component.catalog.ComponentModelMapper;
import net.switchscope.model.component.catalog.device.DeviceModel;
import net.switchscope.to.component.catalog.device.DeviceModelTo;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Base mapper interface for DeviceModel catalog entities.
 * Extends ComponentModelMapper with device-specific mappings.
 */
public interface DeviceModelMapper<E extends DeviceModel, T extends DeviceModelTo> extends ComponentModelMapper<E, T> {

    // Device-specific mappings for Entity -> TO
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
    // Device-specific computed fields
    @Mapping(target = "managementSummary", expression = "java(entity.getManagementSummary())")
    @Mapping(target = "powerSummary", expression = "java(entity.getPowerSummary())")
    @Mapping(target = "deviceTypeSummary", expression = "java(entity.getDeviceTypeSummary())")
    @Override
    T toTo(E entity);

    // Device-specific mappings for TO -> Entity (create)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deviceSpecs", ignore = true)
    @Override
    E toEntity(T to);

    // Device-specific mappings for TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deviceSpecs", ignore = true)
    @Override
    E updateFromTo(@MappingTarget E entity, T to);
}

