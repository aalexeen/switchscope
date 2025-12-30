package net.switchscope.mapper.port;

import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.port.FiberPort;
import net.switchscope.to.port.FiberPortTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for FiberPort entity <-> FiberPortTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface FiberPortMapper extends PortMapper<FiberPort, FiberPortTo> {

    // Entity -> TO mappings
    @Mapping(target = "deviceId", source = "device.id")
    @Mapping(target = "deviceName", source = "device.name")
    @Mapping(target = "connectorId", source = "connector.id")
    @Mapping(target = "connectorName", source = "connector.name")
    // Port computed fields
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
    // Fiber-specific computed fields
    @Mapping(target = "singleMode", expression = "java(entity.isSingleMode())")
    @Mapping(target = "multiMode", expression = "java(entity.isMultiMode())")
    @Mapping(target = "hasOpticalSignal", expression = "java(entity.hasOpticalSignal())")
    @Mapping(target = "opticalLinkHealthy", expression = "java(entity.isOpticalLinkHealthy())")
    @Mapping(target = "opticalLinkQuality", expression = "java(entity.getOpticalLinkQuality())")
    @Override
    FiberPortTo toTo(FiberPort entity);

    // TO -> Entity (create)
    @Mapping(target = "device", ignore = true)
    @Mapping(target = "connector", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    FiberPort toEntity(FiberPortTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "device", ignore = true)
    @Mapping(target = "connector", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    FiberPort updateFromTo(@MappingTarget FiberPort entity, FiberPortTo to);
}

