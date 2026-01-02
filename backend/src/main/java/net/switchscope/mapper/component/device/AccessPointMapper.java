package net.switchscope.mapper.component.device;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.to.component.device.AccessPointTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for AccessPoint entity <-> AccessPointTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface AccessPointMapper extends DeviceMapper<AccessPoint, AccessPointTo> {

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
    // AccessPoint-specific mappings
    @Mapping(target = "wifi6", expression = "java(entity.isWiFi6())")
    @Mapping(target = "modernSecurity", expression = "java(entity.hasModernSecurity())")
    @Mapping(target = "ssidCount", expression = "java(entity.getSsids().size())")
    @Override
    AccessPointTo toTo(AccessPoint entity);

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
    //@Mapping(target = "maxThroughputMbps", ignore = true)
    @Override
    AccessPoint toEntity(AccessPointTo to);

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
    //@Mapping(target = "maxThroughputMbps", ignore = true)
    @Override
    AccessPoint updateFromTo(@MappingTarget AccessPoint entity, AccessPointTo to);
}

