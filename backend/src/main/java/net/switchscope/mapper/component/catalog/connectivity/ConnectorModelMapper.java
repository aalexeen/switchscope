package net.switchscope.mapper.component.catalog.connectivity;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.mapper.component.catalog.ComponentModelMapper;
import net.switchscope.model.component.catalog.connectiviy.ConnectorModel;
import net.switchscope.to.component.catalog.connectivity.ConnectorModelTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for ConnectorModel entity <-> ConnectorModelTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface ConnectorModelMapper extends ComponentModelMapper<ConnectorModel, ConnectorModelTo> {

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
    // Connector-specific computed fields
    @Mapping(target = "compatibilitySummary", expression = "java(entity.getCompatibilitySummary())")
    @Mapping(target = "fiberOptic", expression = "java(entity.isFiberOptic())")
    @Mapping(target = "copper", expression = "java(entity.isCopper())")
    @Mapping(target = "highDensity", expression = "java(entity.isHighDensity())")
    // Map connector-specific temperature fields
    @Mapping(target = "connectorOperatingTemperatureMin", source = "operatingTemperatureMin")
    @Mapping(target = "connectorOperatingTemperatureMax", source = "operatingTemperatureMax")
    @Override
    ConnectorModelTo toTo(ConnectorModel entity);

    // TO -> Entity (create)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "operatingTemperatureMin", source = "connectorOperatingTemperatureMin")
    @Mapping(target = "operatingTemperatureMax", source = "connectorOperatingTemperatureMax")
    @Override
    ConnectorModel toEntity(ConnectorModelTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "operatingTemperatureMin", source = "connectorOperatingTemperatureMin")
    @Mapping(target = "operatingTemperatureMax", source = "connectorOperatingTemperatureMax")
    @Override
    ConnectorModel updateFromTo(@MappingTarget ConnectorModel entity, ConnectorModelTo to);
}

