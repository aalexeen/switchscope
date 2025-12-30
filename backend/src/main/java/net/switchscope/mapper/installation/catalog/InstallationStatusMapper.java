package net.switchscope.mapper.installation.catalog;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

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
    @Mapping(target = "nextPossibleStatusIds", source = "nextPossibleStatuses", qualifiedByName = "statusesToIds")
    @Mapping(target = "previousStatusIds", source = "previousStatuses", qualifiedByName = "statusesToIds")
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
    @Mapping(target = "nextPossibleStatuses", ignore = true) // Set via service
    @Mapping(target = "previousStatuses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    InstallationStatusEntity toEntity(InstallationStatusTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nextPossibleStatuses", ignore = true)
    @Mapping(target = "previousStatuses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    InstallationStatusEntity updateFromTo(@MappingTarget InstallationStatusEntity entity, InstallationStatusTo to);

    @Named("statusesToIds")
    default Set<UUID> statusesToIds(Set<InstallationStatusEntity> statuses) {
        if (statuses == null)
            return new HashSet<>();
        return statuses.stream()
                .map(InstallationStatusEntity::getId)
                .collect(Collectors.toSet());
    }
}

