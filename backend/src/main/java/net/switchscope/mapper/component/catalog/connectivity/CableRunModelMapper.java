package net.switchscope.mapper.component.catalog.connectivity;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.mapper.component.catalog.ComponentModelMapper;
import net.switchscope.model.component.catalog.connectiviy.CableRunModel;
import net.switchscope.to.component.catalog.connectivity.CableRunModelTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for CableRunModel entity <-> CableRunModelTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface CableRunModelMapper extends ComponentModelMapper<CableRunModel, CableRunModelTo> {

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
    // CableRun-specific computed fields
    @Mapping(target = "performanceSummary", expression = "java(entity.getPerformanceSummary())")
    @Mapping(target = "fiberOptic", expression = "java(entity.isFiberOptic())")
    @Mapping(target = "copper", expression = "java(entity.isCopper())")
    @Mapping(target = "plenumSafe", expression = "java(entity.isPlenumSafe())")
    // Map cable-specific temperature fields
    @Mapping(target = "cableOperatingTemperatureMin", source = "operatingTemperatureMin")
    @Mapping(target = "cableOperatingTemperatureMax", source = "operatingTemperatureMax")
    @Override
    CableRunModelTo toTo(CableRunModel entity);

    // TO -> Entity (create)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "operatingTemperatureMin", source = "cableOperatingTemperatureMin")
    @Mapping(target = "operatingTemperatureMax", source = "cableOperatingTemperatureMax")
    @Override
    CableRunModel toEntity(CableRunModelTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "operatingTemperatureMin", source = "cableOperatingTemperatureMin")
    @Mapping(target = "operatingTemperatureMax", source = "cableOperatingTemperatureMax")
    @Override
    CableRunModel updateFromTo(@MappingTarget CableRunModel entity, CableRunModelTo to);
}

