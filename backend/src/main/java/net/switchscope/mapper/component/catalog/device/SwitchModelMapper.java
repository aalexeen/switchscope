package net.switchscope.mapper.component.catalog.device;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.component.catalog.device.SwitchModel;
import net.switchscope.to.component.catalog.device.SwitchModelTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for SwitchModel entity <-> SwitchModelTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface SwitchModelMapper extends DeviceModelMapper<SwitchModel, SwitchModelTo> {

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
    @Mapping(target = "managementSummary", expression = "java(entity.getManagementSummary())")
    @Mapping(target = "powerSummary", expression = "java(entity.getPowerSummary())")
    @Mapping(target = "deviceTypeSummary", expression = "java(entity.getDeviceTypeSummary())")
    // Switch-specific computed fields
    @Mapping(target = "portSummary", expression = "java(entity.getPortSummary())")
    @Mapping(target = "managed", expression = "java(entity.isManaged())")
    @Mapping(target = "enterprise", expression = "java(entity.isEnterprise())")
    @Mapping(target = "hasPoePlus", expression = "java(entity.hasPoePlus())")
    @Mapping(target = "supportsHighSpeed", expression = "java(entity.supportsHighSpeed())")
    @Mapping(target = "powerEfficiency", expression = "java(entity.getPowerEfficiency())")
    @Override
    SwitchModelTo toTo(SwitchModel entity);

    // TO -> Entity (create)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deviceSpecs", ignore = true)
    @Override
    SwitchModel toEntity(SwitchModelTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deviceSpecs", ignore = true)
    @Override
    SwitchModel updateFromTo(@MappingTarget SwitchModel entity, SwitchModelTo to);
}

