package net.switchscope.mapper.component;

import net.switchscope.mapper.BaseMapper;
import net.switchscope.model.component.Component;
import net.switchscope.to.component.ComponentTo;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Base mapper interface for Component entities.
 * Provides common mapping configurations for all component types.
 */
public interface ComponentMapper<E extends Component, T extends ComponentTo> extends BaseMapper<E, T> {

    // Common mappings for Entity -> TO
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
    @Override
    T toTo(E entity);

    // Common mappings for TO -> Entity (create)
    @Mapping(target = "componentStatus", ignore = true) // Set via service
    @Mapping(target = "componentType", ignore = true)   // Set via service
    @Mapping(target = "componentNature", ignore = true) // Set via service
    @Mapping(target = "installation", ignore = true)    // Set via service
    @Mapping(target = "parentComponent", ignore = true) // Set via service
    @Mapping(target = "childComponents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    E toEntity(T to);

    // Common mappings for TO -> Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "componentStatus", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "componentNature", ignore = true)
    @Mapping(target = "installation", ignore = true)
    @Mapping(target = "parentComponent", ignore = true)
    @Mapping(target = "childComponents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    E updateFromTo(@MappingTarget E entity, T to);
}

