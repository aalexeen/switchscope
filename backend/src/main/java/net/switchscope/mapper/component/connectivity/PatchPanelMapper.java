package net.switchscope.mapper.component.connectivity;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.mapper.component.ComponentMapper;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.model.component.connectivity.PatchPanel;
import net.switchscope.to.component.connectivity.PatchPanelTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for PatchPanel entity <-> PatchPanelTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface PatchPanelMapper extends ComponentMapper<PatchPanel, PatchPanelTo> {

    // Entity -> TO mappings
    @Mapping(target = "componentStatusId", source = "componentStatus.id")
    @Mapping(target = "componentStatusCode", source = "componentStatus.code")
    @Mapping(target = "componentStatusDisplayName", source = "componentStatus.displayName")
    @Mapping(target = "componentTypeId", source = "componentType.id")
    @Mapping(target = "componentTypeCode", source = "componentType.code")
    @Mapping(target = "componentTypeDisplayName", source = "componentType.displayName")
    @Mapping(target = "componentNatureId", source = "componentNature.id")
    @Mapping(target = "componentNatureCode", source = "componentNature.code")
    @Mapping(target = "installationId", source = "installation.id")
    @Mapping(target = "parentComponentId", source = "parentComponent.id")
    @Mapping(target = "parentComponentName", source = "parentComponent.name")
    @Mapping(target = "componentPath", expression = "java(entity.getComponentPath())")
    @Mapping(target = "operational", expression = "java(entity.isOperational())")
    @Mapping(target = "installed", expression = "java(entity.isInstalled())")
    @Mapping(target = "locationAddress", expression = "java(entity.getLocationAddress())")
    // PatchPanel-specific mappings
    @Mapping(target = "patchPanelModelId", source = "patchPanelModel.id")
    @Mapping(target = "patchPanelModelName", source = "patchPanelModel.name")
    @Mapping(target = "cableRunIds", source = "cableRuns", qualifiedByName = "cableRunsToIds")
    @Mapping(target = "fiberPanel", expression = "java(entity.isFiberPanel())")
    @Mapping(target = "copperPanel", expression = "java(entity.isCopperPanel())")
    @Mapping(target = "highDensity", expression = "java(entity.isHighDensity())")
    @Mapping(target = "portDensityInfo", expression = "java(entity.getPortDensityInfo())")
    @Mapping(target = "cableRunCount", expression = "java(entity.getCableRuns().size())")
    @Override
    PatchPanelTo toTo(PatchPanel entity);

    // TO -> Entity (create)
    @Mapping(target = "componentStatus", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "componentNature", ignore = true)
    @Mapping(target = "installation", ignore = true)
    @Mapping(target = "parentComponent", ignore = true)
    @Mapping(target = "childComponents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patchPanelModel", ignore = true)
    @Mapping(target = "cableRuns", ignore = true)
    @Mapping(target = "ports", ignore = true)
    @Override
    PatchPanel toEntity(PatchPanelTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentStatus", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "componentNature", ignore = true)
    @Mapping(target = "installation", ignore = true)
    @Mapping(target = "parentComponent", ignore = true)
    @Mapping(target = "childComponents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patchPanelModel", ignore = true)
    @Mapping(target = "cableRuns", ignore = true)
    @Mapping(target = "ports", ignore = true)
    @Override
    PatchPanel updateFromTo(@MappingTarget PatchPanel entity, PatchPanelTo to);

    @Named("cableRunsToIds")
    default Set<UUID> cableRunsToIds(Set<CableRun> cableRuns) {
        if (cableRuns == null) return Set.of();
        return cableRuns.stream()
                .map(CableRun::getId)
                .collect(Collectors.toSet());
    }
}

