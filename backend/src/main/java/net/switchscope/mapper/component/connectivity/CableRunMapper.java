package net.switchscope.mapper.component.connectivity;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.mapper.component.ComponentMapper;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.model.component.connectivity.Connector;
import net.switchscope.model.location.Location;
import net.switchscope.to.component.connectivity.CableRunTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for CableRun entity <-> CableRunTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface CableRunMapper extends ComponentMapper<CableRun, CableRunTo> {

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
    // CableRun-specific mappings
    @Mapping(target = "cableModelId", source = "cableModel.id")
    @Mapping(target = "cableModelName", source = "cableModel.name")
    @Mapping(target = "startLocationId", source = "startLocation.id")
    @Mapping(target = "startLocationName", source = "startLocation.name")
    @Mapping(target = "endLocationId", source = "endLocation.id")
    @Mapping(target = "endLocationName", source = "endLocation.name")
    @Mapping(target = "locationIds", source = "locations", qualifiedByName = "locationsToIds")
    @Mapping(target = "connectorIds", source = "connectors", qualifiedByName = "connectorsToIds")
    @Mapping(target = "passed", expression = "java(entity.isPassed())")
    @Mapping(target = "requiresTesting", expression = "java(entity.requiresTesting())")
    @Mapping(target = "pointToPoint", expression = "java(entity.isPointToPoint())")
    @Mapping(target = "multiPoint", expression = "java(entity.isMultiPoint())")
    @Mapping(target = "orderedLocationPath", expression = "java(entity.getOrderedLocationPath())")
    @Mapping(target = "connectorCount", expression = "java(entity.getConnectors().size())")
    @Override
    CableRunTo toTo(CableRun entity);

    // TO -> Entity (create)
    @Mapping(target = "componentStatus", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "componentNature", ignore = true)
    @Mapping(target = "installation", ignore = true)
    @Mapping(target = "parentComponent", ignore = true)
    @Mapping(target = "childComponents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "cableModel", ignore = true)
    @Mapping(target = "startLocation", ignore = true)
    @Mapping(target = "endLocation", ignore = true)
    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "connectors", ignore = true)
    @Override
    CableRun toEntity(CableRunTo to);

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
    @Mapping(target = "cableModel", ignore = true)
    @Mapping(target = "startLocation", ignore = true)
    @Mapping(target = "endLocation", ignore = true)
    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "connectors", ignore = true)
    @Override
    CableRun updateFromTo(@MappingTarget CableRun entity, CableRunTo to);

    @Named("locationsToIds")
    default List<UUID> locationsToIds(List<Location> locations) {
        if (locations == null) return new ArrayList<>();
        return locations.stream()
                .map(Location::getId)
                .collect(Collectors.toList());
    }

    @Named("connectorsToIds")
    default List<UUID> connectorsToIds(List<Connector> connectors) {
        if (connectors == null) return new ArrayList<>();
        return connectors.stream()
                .map(Connector::getId)
                .collect(Collectors.toList());
    }
}

