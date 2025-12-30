package net.switchscope.mapper.component.catalog.connectivity;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.mapper.component.catalog.ComponentModelMapper;
import net.switchscope.model.component.catalog.connectiviy.PatchPanelModel;
import net.switchscope.to.component.catalog.connectivity.PatchPanelModelTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for PatchPanelModel entity <-> PatchPanelModelTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface PatchPanelModelMapper extends ComponentModelMapper<PatchPanelModel, PatchPanelModelTo> {

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
    // PatchPanel-specific computed fields
    @Mapping(target = "portSummary", expression = "java(entity.getPortSummary())")
    @Mapping(target = "specificationSummary", expression = "java(entity.getSpecificationSummary())")
    @Mapping(target = "fiberPanel", expression = "java(entity.isFiberPanel())")
    @Mapping(target = "copperPanel", expression = "java(entity.isCopperPanel())")
    @Mapping(target = "highDensity", expression = "java(entity.isHighDensity())")
    @Override
    PatchPanelModelTo toTo(PatchPanelModel entity);

    // TO -> Entity (create)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Override
    PatchPanelModel toEntity(PatchPanelModelTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Override
    PatchPanelModel updateFromTo(@MappingTarget PatchPanelModel entity, PatchPanelModelTo to);
}

