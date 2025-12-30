package net.switchscope.mapper.component.catalog.device;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.component.catalog.device.AccessPointModel;
import net.switchscope.to.component.catalog.device.AccessPointModelTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for AccessPointModel entity <-> AccessPointModelTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface AccessPointModelMapper extends DeviceModelMapper<AccessPointModel, AccessPointModelTo> {

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
    // AccessPoint-specific computed fields
    @Mapping(target = "bandSummary", expression = "java(entity.getBandSummary())")
    @Mapping(target = "wifi6", expression = "java(entity.isWiFi6())")
    @Mapping(target = "enterprise", expression = "java(entity.isEnterprise())")
    @Mapping(target = "hasAdvancedFeatures", expression = "java(entity.hasAdvancedFeatures())")
    @Mapping(target = "powerEfficiency", expression = "java(entity.getPowerEfficiency())")
    @Override
    AccessPointModelTo toTo(AccessPointModel entity);

    // TO -> Entity (create)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deviceSpecs", ignore = true)
    @Override
    AccessPointModel toEntity(AccessPointModelTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deviceSpecs", ignore = true)
    @Override
    AccessPointModel updateFromTo(@MappingTarget AccessPointModel entity, AccessPointModelTo to);
}

