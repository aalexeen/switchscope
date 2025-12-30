package net.switchscope.mapper.component.device;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.component.device.Router;
import net.switchscope.to.component.device.RouterTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for Router entity <-> RouterTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface RouterMapper extends DeviceMapper<Router, RouterTo> {

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
    @Mapping(target = "managed", expression = "java(entity.isManaged())")
    @Mapping(target = "reachable", expression = "java(entity.isReachable())")
    @Mapping(target = "monitoringStatus", expression = "java(entity.getMonitoringStatus())")
    @Mapping(target = "networkConfiguration", expression = "java(entity.getNetworkConfiguration())")
    @Mapping(target = "portCount", expression = "java(entity.getPorts().size())")
    // Router-specific mappings
    @Mapping(target = "enterpriseRouter", expression = "java(entity.isEnterpriseRouter())")
    @Mapping(target = "vpnCapability", expression = "java(entity.hasVpnCapability())")
    @Override
    RouterTo toTo(Router entity);

    // TO -> Entity (create)
    @Mapping(target = "componentStatus", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "componentNature", ignore = true)
    @Mapping(target = "installation", ignore = true)
    @Mapping(target = "parentComponent", ignore = true)
    @Mapping(target = "childComponents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ports", ignore = true)
    @Override
    Router toEntity(RouterTo to);

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
    @Mapping(target = "ports", ignore = true)
    @Override
    Router updateFromTo(@MappingTarget Router entity, RouterTo to);
}

