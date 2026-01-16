package net.switchscope.mapper.installation.catalog;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.installation.catalog.InstallationStatusEntity;
import net.switchscope.to.installation.catalog.InstallationStatusTo;

/**
 * Mapper for InstallationStatusEntity <-> InstallationStatusTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface InstallationStatusMapper extends BaseMapper<InstallationStatusEntity, InstallationStatusTo> {

    // Entity -> TO mappings
    @Mapping(target = "nextPossibleStatusCodes", source = "nextPossibleStatusCodes")
    // Computed fields
    @Mapping(target = "statusCategory", expression = "java(entity.getStatusCategory())")
    @Mapping(target = "progressStatus", expression = "java(entity.isProgressStatus())")
    @Mapping(target = "successStatus", expression = "java(entity.isSuccessStatus())")
    @Mapping(target = "warningStatus", expression = "java(entity.isWarningStatus())")
    @Mapping(target = "terminalStatus", expression = "java(entity.isTerminalStatus())")
    @Mapping(target = "allowsStatusChange", expression = "java(entity.allowsStatusChange())")
    @Mapping(target = "hasAutoTransition", expression = "java(entity.hasAutoTransition())")
    @Mapping(target = "urgencyLevel", expression = "java(entity.getUrgencyLevel())")
    @Override
    InstallationStatusTo toTo(InstallationStatusEntity entity);

    // TO -> Entity (create)
    @Mapping(target = "nextPossibleStatusCodes", ignore = true) // Set via service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    InstallationStatusEntity toEntity(InstallationStatusTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nextPossibleStatusCodes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    InstallationStatusEntity updateFromTo(@MappingTarget InstallationStatusEntity entity, InstallationStatusTo to);
}
