package net.switchscope.mapper.component.device;

import net.switchscope.mapper.component.ComponentMapper;
import net.switchscope.model.component.device.Device;
import net.switchscope.to.component.device.DeviceTo;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Base mapper interface for Device entities.
 * Extends ComponentMapper with device-specific mappings.
 */
public interface DeviceMapper<E extends Device, T extends DeviceTo> extends ComponentMapper<E, T> {

    // Device-specific mappings for Entity -> TO
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
    // Device-specific computed fields
    @Mapping(target = "managed", expression = "java(entity.isManaged())")
    @Mapping(target = "reachable", expression = "java(entity.isReachable())")
    @Mapping(target = "monitoringStatus", expression = "java(entity.getMonitoringStatus())")
    @Mapping(target = "networkConfiguration", expression = "java(entity.getNetworkConfiguration())")
    @Mapping(target = "portCount", expression = "java(entity.hasPorts() ? entity.getPorts().size() : 0)")
    @Override
    T toTo(E entity);

    // Device-specific mappings for TO -> Entity (create)
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
    E toEntity(T to);

    // Device-specific mappings for TO -> Entity (update)
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
    E updateFromTo(@MappingTarget E entity, T to);
}

