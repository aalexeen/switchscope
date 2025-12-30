package net.switchscope.mapper.port;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.model.port.Port;
import net.switchscope.to.port.PortTo;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Base mapper interface for Port entities.
 * Provides common mapping configurations for all port types.
 */
public interface PortMapper<E extends Port, T extends PortTo> extends BaseMapper<E, T> {

    // Common mappings for Entity -> TO
    @Mapping(target = "deviceId", source = "device.id")
    @Mapping(target = "deviceName", source = "device.name")
    @Mapping(target = "connectorId", source = "connector.id")
    @Mapping(target = "connectorName", source = "connector.name")
    // Computed fields
    @Mapping(target = "portType", expression = "java(entity.getPortType())")
    @Mapping(target = "specifications", expression = "java(entity.getSpecifications())")
    @Mapping(target = "up", expression = "java(entity.isUp())")
    @Mapping(target = "down", expression = "java(entity.isDown())")
    @Mapping(target = "adminEnabled", expression = "java(entity.isAdminEnabled())")
    @Mapping(target = "available", expression = "java(entity.isAvailable())")
    @Mapping(target = "connected", expression = "java(entity.isConnected())")
    @Mapping(target = "poeCapable", expression = "java(entity.isPoeCapable())")
    @Mapping(target = "poePowered", expression = "java(entity.isPoePowered())")
    @Mapping(target = "trunkPort", expression = "java(entity.isTrunkPort())")
    @Mapping(target = "accessPort", expression = "java(entity.isAccessPort())")
    @Mapping(target = "speedGbps", expression = "java(entity.getSpeedGbps())")
    @Mapping(target = "maxSpeedGbps", expression = "java(entity.getMaxSpeedGbps())")
    @Mapping(target = "utilizationPercent", expression = "java(entity.getUtilizationPercent())")
    @Mapping(target = "totalPackets", expression = "java(entity.getTotalPackets())")
    @Mapping(target = "totalBytes", expression = "java(entity.getTotalBytes())")
    @Mapping(target = "totalErrors", expression = "java(entity.getTotalErrors())")
    @Mapping(target = "errorRate", expression = "java(entity.getErrorRate())")
    @Override
    T toTo(E entity);

    // Common mappings for TO -> Entity (create)
    @Mapping(target = "device", ignore = true) // Set via service
    @Mapping(target = "connector", ignore = true) // Set via service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    E toEntity(T to);

    // Common mappings for TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "device", ignore = true)
    @Mapping(target = "connector", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    E updateFromTo(@MappingTarget E entity, T to);
}

