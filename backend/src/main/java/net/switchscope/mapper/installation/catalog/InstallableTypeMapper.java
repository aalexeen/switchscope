package net.switchscope.mapper.installation.catalog;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.MapStructConfig;
import net.switchscope.model.installation.catalog.InstallableTypeEntity;
import net.switchscope.to.installation.catalog.InstallableTypeTo;

/**
 * Mapper for InstallableTypeEntity <-> InstallableTypeTo DTO.
 */
@Mapper(config = MapStructConfig.class)
public interface InstallableTypeMapper extends BaseMapper<InstallableTypeEntity, InstallableTypeTo> {

    // Entity -> TO mappings
    @Mapping(target = "deviceType", expression = "java(entity.isDeviceType())")
    @Mapping(target = "connectivityType", expression = "java(entity.isConnectivityType())")
    @Mapping(target = "category", expression = "java(entity.getCategory() != null ? entity.getCategory().name() : null)")
    @Mapping(target = "housingType", expression = "java(entity.isHousingType())")
    @Mapping(target = "powerType", expression = "java(entity.isPowerType())")
    @Mapping(target = "implemented", expression = "java(entity.isImplemented())")
    @Mapping(target = "requiresSpecialHandling", expression = "java(entity.requiresSpecialHandling())")
    @Mapping(target = "highPriority", expression = "java(entity.isHighPriority())")
    @Mapping(target = "lowPriority", expression = "java(entity.isLowPriority())")
    @Mapping(target = "priorityLevel", expression = "java(entity.getPriorityLevel())")
    @Mapping(target = "estimatedInstallationTime", expression = "java(entity.getEstimatedInstallationTime())")
    @Override
    InstallableTypeTo toTo(InstallableTypeEntity entity);

    // TO -> Entity (create)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    InstallableTypeEntity toEntity(InstallableTypeTo to);

    // TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    InstallableTypeEntity updateFromTo(@MappingTarget InstallableTypeEntity entity, InstallableTypeTo to);
}
