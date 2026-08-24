package net.switchscope.service.component;

import lombok.RequiredArgsConstructor;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.error.NotFoundException;
import net.switchscope.model.component.Component;
import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.repository.component.ComponentModelRepository;
import net.switchscope.repository.component.ComponentNatureRepository;
import net.switchscope.repository.component.ComponentRepository;
import net.switchscope.repository.component.ComponentStatusRepository;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.repository.installation.InstallationRepository;
import net.switchscope.to.component.ComponentTo;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Function;

/**
 * Turns the foreign-key ids carried by a {@link ComponentTo} into managed entity references.
 * <p>
 * The component mappers ignore every association ({@code componentType}, {@code componentStatus},
 * {@code componentNature}, {@code installation}, {@code parentComponent} and the type-specific model
 * links), because the DTO side of each of them is a {@code UUID}. Without this step a component
 * built by {@code toEntity} has {@code component_type_id} and {@code component_status_id} null, both
 * of which are NOT NULL in the schema.
 * <p>
 * Semantics are the same for create and update: an id present in the DTO replaces the current
 * reference, an absent id leaves it untouched. That keeps partial updates from clearing associations
 * the caller did not mention. The two mandatory references are checked at the end, so a create that
 * omits them fails with 422 instead of a constraint violation.
 */
@Service
@RequiredArgsConstructor
public class ComponentReferenceResolver {

    private final ComponentTypeRepository componentTypeRepository;
    private final ComponentStatusRepository componentStatusRepository;
    private final ComponentNatureRepository componentNatureRepository;
    private final ComponentRepository componentRepository;
    private final ComponentModelRepository componentModelRepository;
    private final InstallationRepository installationRepository;

    /**
     * Applies the associations shared by every component type.
     *
     * @param entity the entity being created or updated
     * @param dto    the incoming DTO
     */
    public void applyCommonReferences(Component entity, ComponentTo dto) {
        if (dto.getComponentTypeId() != null) {
            entity.setComponentType(componentTypeRepository.findById(dto.getComponentTypeId())
                    .orElseThrow(() -> new NotFoundException(
                            "Component type with id=" + dto.getComponentTypeId() + " not found")));
        }
        if (dto.getComponentStatusId() != null) {
            entity.setComponentStatus(componentStatusRepository.findById(dto.getComponentStatusId())
                    .orElseThrow(() -> new NotFoundException(
                            "Component status with id=" + dto.getComponentStatusId() + " not found")));
        }
        if (dto.getComponentNatureId() != null) {
            entity.setComponentNature(componentNatureRepository.findById(dto.getComponentNatureId())
                    .orElseThrow(() -> new NotFoundException(
                            "Component nature with id=" + dto.getComponentNatureId() + " not found")));
        }
        if (dto.getInstallationId() != null) {
            entity.setInstallation(installationRepository.findById(dto.getInstallationId())
                    .orElseThrow(() -> new NotFoundException(
                            "Installation with id=" + dto.getInstallationId() + " not found")));
        }
        if (dto.getParentComponentId() != null) {
            if (dto.getParentComponentId().equals(entity.getId())) {
                throw new IllegalRequestDataException("Component cannot be its own parent");
            }
            entity.setParentComponent(componentRepository.findById(dto.getParentComponentId())
                    .orElseThrow(() -> new NotFoundException(
                            "Parent component with id=" + dto.getParentComponentId() + " not found")));
        }

        if (entity.getComponentType() == null) {
            throw new IllegalRequestDataException("componentTypeId is required");
        }
        if (entity.getComponentStatus() == null) {
            throw new IllegalRequestDataException("componentStatusId is required");
        }
    }

    /**
     * Resolves a catalog model reference of a concrete subtype and applies it through {@code setter}.
     * Does nothing when {@code modelId} is null, so a DTO that omits the field keeps the stored link.
     *
     * @param modelId    the id from the DTO, may be null
     * @param modelClass the expected {@link ComponentModel} subtype
     * @param setter     how to attach the resolved model to the entity
     * @param fieldName  DTO field name, used in error messages
     * @param <M>        the model subtype
     */
    public <M extends ComponentModel> void applyModelReference(
            UUID modelId, Class<M> modelClass, java.util.function.Consumer<M> setter, String fieldName) {
        if (modelId == null) {
            return;
        }
        ComponentModel model = componentModelRepository.findById(modelId)
                .orElseThrow(() -> new NotFoundException(
                        "Component model with id=" + modelId + " not found"));
        if (!modelClass.isInstance(model)) {
            // Single Table Inheritance: any model id resolves, so the subtype has to be checked here.
            // Casting blindly would surface as a ClassCastException, i.e. a 500 for a client mistake.
            throw new IllegalRequestDataException(fieldName + "=" + modelId + " is a "
                    + model.getClass().getSimpleName() + ", expected " + modelClass.getSimpleName());
        }
        setter.accept(modelClass.cast(model));
    }

    /**
     * Resolves a component reference of a concrete subtype (patch panel, cable run, ...).
     * Does nothing when {@code componentId} is null.
     *
     * @param componentId the id from the DTO, may be null
     * @param targetClass the expected {@link Component} subtype
     * @param setter      how to attach the resolved component to the entity
     * @param fieldName   DTO field name, used in error messages
     * @param <C>         the component subtype
     */
    public <C extends Component> void applyComponentReference(
            UUID componentId, Class<C> targetClass, java.util.function.Consumer<C> setter, String fieldName) {
        if (componentId == null) {
            return;
        }
        Component component = componentRepository.findById(componentId)
                .orElseThrow(() -> new NotFoundException(
                        "Component with id=" + componentId + " not found"));
        if (!targetClass.isInstance(component)) {
            throw new IllegalRequestDataException(fieldName + "=" + componentId + " is a "
                    + component.getClass().getSimpleName() + ", expected " + targetClass.getSimpleName());
        }
        setter.accept(targetClass.cast(component));
    }

    /**
     * Resolves an optional reference through the given finder.
     * Does nothing when {@code id} is null.
     *
     * @param id        the id from the DTO, may be null
     * @param finder    how to load the referenced entity
     * @param setter    how to attach it to the entity
     * @param fieldName DTO field name, used in error messages
     * @param <R>       the referenced type
     */
    public <R> void applyReference(UUID id, Function<UUID, java.util.Optional<R>> finder,
            java.util.function.Consumer<R> setter, String fieldName) {
        if (id == null) {
            return;
        }
        setter.accept(finder.apply(id)
                .orElseThrow(() -> new NotFoundException(fieldName + "=" + id + " not found")));
    }

    /**
     * Fills an owned association with the entities the ids name, in the order they were given.
     * <p>
     * A null collection means the payload did not mention the field, and the stored association is
     * left alone - the same rule the single references follow. An empty collection is a different
     * request and is honoured: detach everything. Note that this is the only way to empty such an
     * association, since a DTO field named {@code <name>Ids} has no entity property of that name
     * for the null-clearing pass to find.
     * <p>
     * Every id is resolved before anything is written, so a payload naming one id that does not
     * exist leaves the association as it was instead of half-applied. The stored collection is
     * emptied and refilled rather than replaced, because Hibernate tracks the instance it handed
     * out - assigning a new one loses the ordering column and, on a managed entity, the change.
     *
     * @param ids       the ids from the DTO, may be null
     * @param finder    how to load one referenced entity
     * @param target    the entity's own collection
     * @param fieldName DTO field name, used in error messages
     * @param <R>       the referenced type
     */
    public <R> void applyCollection(java.util.Collection<UUID> ids,
            Function<UUID, java.util.Optional<R>> finder,
            java.util.Collection<R> target, String fieldName) {
        if (ids == null) {
            return;
        }
        java.util.List<R> resolved = ids.stream()
                .map(id -> {
                    if (id == null) {
                        throw new IllegalRequestDataException(fieldName + " contains a null id");
                    }
                    return finder.apply(id).orElseThrow(() -> new NotFoundException(
                            fieldName + " contains id=" + id + ", which does not exist"));
                })
                .toList();
        target.clear();
        target.addAll(resolved);
    }
}
