package net.switchscope.mapper.component.connectivity;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.mapper.component.ComponentMapper;
import net.switchscope.model.component.connectivity.Connector;
import net.switchscope.to.component.connectivity.ConnectorTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for Connector entity <-> ConnectorTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface ConnectorMapper extends ComponentMapper<Connector, ConnectorTo> {

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
    // Connector-specific mappings
    @Mapping(target = "connectorModelId", source = "connectorModel.id")
    @Mapping(target = "connectorModelName", source = "connectorModel.name")
    @Mapping(target = "cableRunId", source = "cableRun.id")
    @Mapping(target = "cableRunName", source = "cableRun.name")
    @Mapping(target = "portId", source = "port.id")
    @Mapping(target = "portName", source = "port.name")
    @Mapping(target = "startConnector", expression = "java(entity.isStartConnector())")
    @Mapping(target = "endConnector", expression = "java(entity.isEndConnector())")
    @Mapping(target = "intermediateConnector", expression = "java(entity.isIntermediateConnector())")
    @Mapping(target = "needsRework", expression = "java(entity.needsRework())")
    @Mapping(target = "goodQuality", expression = "java(entity.isGoodQuality())")
    @Mapping(target = "connectedToPort", expression = "java(entity.getPort() != null)")
    @Override
    ConnectorTo toTo(Connector entity);

    // TO -> Entity (create)
    @Mapping(target = "componentStatus", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "componentNature", ignore = true)
    @Mapping(target = "installation", ignore = true)
    @Mapping(target = "parentComponent", ignore = true)
    @Mapping(target = "childComponents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "connectorModel", ignore = true)
    @Mapping(target = "cableRun", ignore = true)
    @Mapping(target = "port", ignore = true)
    @Override
    Connector toEntity(ConnectorTo to);

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
    @Mapping(target = "connectorModel", ignore = true)
    @Mapping(target = "cableRun", ignore = true)
    @Mapping(target = "port", ignore = true)
    @Override
    Connector updateFromTo(@MappingTarget Connector entity, ConnectorTo to);
}

