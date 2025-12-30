package net.switchscope.mapper.component.catalog.device;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.component.catalog.device.RouterModel;
import net.switchscope.to.component.catalog.device.RouterModelTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for RouterModel entity <-> RouterModelTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface RouterModelMapper extends DeviceModelMapper<RouterModel, RouterModelTo> {

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
    // Router-specific computed fields
    @Mapping(target = "interfaceSummary", expression = "java(entity.getInterfaceSummary())")
    @Mapping(target = "enterpriseRouter", expression = "java(entity.isEnterpriseRouter())")
    @Mapping(target = "hasAdvancedRouting", expression = "java(entity.hasAdvancedRouting())")
    @Mapping(target = "hasVpnCapability", expression = "java(entity.hasVpnCapability())")
    @Mapping(target = "powerEfficiency", expression = "java(entity.getPowerEfficiency())")
    @Override
    RouterModelTo toTo(RouterModel entity);

    // TO -> Entity (create)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deviceSpecs", ignore = true)
    @Mapping(target = "supportedWanInterfaces", ignore = true)
    @Override
    RouterModel toEntity(RouterModelTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deviceSpecs", ignore = true)
    @Mapping(target = "supportedWanInterfaces", ignore = true)
    @Override
    RouterModel updateFromTo(@MappingTarget RouterModel entity, RouterModelTo to);
}

