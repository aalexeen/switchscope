package net.switchscope.mapper.installation;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.installation.Installation;
import net.switchscope.to.installation.InstallationTo;

/**
 * Mapper for Installation entity <-> InstallationTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface InstallationMapper extends BaseMapper<Installation, InstallationTo> {

    // Entity -> TO mappings
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "locationName", source = "location.name")
    @Mapping(target = "locationFullPath", expression = "java(entity.getLocation().getFullPath())")
    @Mapping(target = "componentId", source = "сomponent.id")
    @Mapping(target = "componentName", source = "сomponent.name")
    @Mapping(target = "installedItemTypeId", source = "installedItemType.id")
    @Mapping(target = "installedItemTypeCode", source = "installedItemType.code")
    @Mapping(target = "installedItemTypeDisplayName", source = "installedItemType.displayName")
    @Mapping(target = "statusId", source = "status.id")
    @Mapping(target = "statusCode", source = "status.code")
    @Mapping(target = "statusDisplayName", source = "status.displayName")
    @Mapping(target = "statusColorCategory", source = "status.colorCategory")
    // Computed fields
    @Mapping(target = "installationLocationPath", expression = "java(entity.getInstallationLocationPath())")
    @Mapping(target = "fullLocationDescription", expression = "java(entity.getFullLocationDescription())")
    @Mapping(target = "directLocationInstallation", expression = "java(entity.isDirectLocationInstallation())")
    @Mapping(target = "componentHousedInstallation", expression = "java(entity.isComponentHousedInstallation())")
    @Mapping(target = "currentlyInstalled", expression = "java(entity.isCurrentlyInstalled())")
    @Mapping(target = "operational", expression = "java(entity.isOperational())")
    @Mapping(target = "requiresAttention", expression = "java(entity.requiresAttention())")
    @Mapping(target = "rackMounted", expression = "java(entity.isRackMounted())")
    @Mapping(target = "rackRequired", expression = "java(entity.isRackRequired())")
    @Mapping(target = "supportsHotSwap", expression = "java(entity.supportsHotSwap())")
    @Mapping(target = "requiresShutdown", expression = "java(entity.requiresShutdown())")
    @Mapping(target = "occupiedRackUnits", expression = "java(entity.getOccupiedRackUnits())")
    @Mapping(target = "occupiedRackPositions", expression = "java(entity.getOccupiedRackPositions())")
    @Mapping(target = "installationPriority", expression = "java(entity.getInstallationPriority())")
    @Mapping(target = "estimatedInstallationTime", expression = "java(entity.getEstimatedInstallationTime())")
    @Mapping(target = "allowsEquipmentOperation", expression = "java(entity.allowsEquipmentOperation())")
    @Mapping(target = "allowsMaintenance", expression = "java(entity.allowsMaintenance())")
    @Mapping(target = "statusUrgencyLevel", expression = "java(entity.getStatusUrgencyLevel())")
    @Override
    InstallationTo toTo(Installation entity);

    // TO -> Entity (create)
    @Mapping(target = "location", ignore = true) // Set via service
    @Mapping(target = "сomponent", ignore = true) // Set via service
    @Mapping(target = "installedItemType", ignore = true) // Set via service
    @Mapping(target = "status", ignore = true) // Set via service
    @Mapping(target = "lastStatusChange", ignore = true)
    @Mapping(target = "statusChangedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    Installation toEntity(InstallationTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "сomponent", ignore = true)
    @Mapping(target = "installedItemType", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastStatusChange", ignore = true)
    @Mapping(target = "statusChangedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    Installation updateFromTo(@MappingTarget Installation entity, InstallationTo to);
}

