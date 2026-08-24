package net.switchscope.service.component.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.error.NotFoundException;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.repository.component.ComponentModelRepository;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.security.policy.UpdatePolicy;
import net.switchscope.security.policy.UpdatePolicyResolver;
import net.switchscope.security.policy.UpdatePolicyValidator;
import net.switchscope.service.CrudService;
import net.switchscope.to.PageTo;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.PageReader;
import net.switchscope.to.component.catalog.ComponentModelTo;
import net.switchscope.web.payload.PartialUpdate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentModelService implements CrudService<ComponentModel> {

    private final ComponentModelRepository repository;
    private final ComponentTypeRepository componentTypeRepository;
    private final UpdatePolicyResolver policyResolver;
    private final UpdatePolicyValidator policyValidator;
    private final PageReader pageReader;

    @Override
    public List<ComponentModel> getAll() {
        return repository.findAllWithAssociations();
    }

    /**
     * One page of catalog models of every class, mapped by the caller's own function while this
     * transaction is open - the mappers that pick a DTO per model class belong to the controller,
     * as they do for devices.
     *
     * @param query which page was asked for, in what order
     * @param toDto how one model becomes its DTO
     * @param <T>   the DTO type the caller produces
     * @return the requested page
     */
    public <T> PageTo<T> getPage(ListQuery query, Function<ComponentModel, T> toDto) {
        return pageReader.read(ComponentModel.class, query, toDto);
    }

    @Override
    public ComponentModel getById(UUID id) {
        return repository.getExisted(id);
    }

    /**
     * Persist a model whose {@code componentType} has been resolved from the DTO.
     * {@code component_type_id} is NOT NULL and the mappers ignore the association, so the caller
     * must go through here rather than building the entity and calling {@code save} directly.
     *
     * @param entity the mapped, not yet referenced entity
     * @param dto    the DTO carrying {@code componentTypeId}
     * @return the saved model
     */
    @Transactional
    public ComponentModel createFromDto(ComponentModel entity, ComponentModelTo dto) {
        if (dto.getComponentTypeId() == null) {
            throw new IllegalRequestDataException("componentTypeId is required");
        }
        entity.setComponentType(getComponentType(dto.getComponentTypeId()));
        return repository.save(entity);
    }

    private ComponentTypeEntity getComponentType(UUID componentTypeId) {
        return componentTypeRepository.findById(componentTypeId)
                .orElseThrow(() -> new NotFoundException(
                        "Component type with id=" + componentTypeId + " not found"));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }

    /**
     * Apply an update onto the stored component model.
     * <p>
     * The policy check has already happened: a {@link PartialUpdate} that clears anything can only
     * be produced by the reader that validates first, so what arrives here is a decision, not a
     * request to be re-examined.
     *
     * @param id             component model ID
     * @param update         the bound DTO and the fields the request carried
     * @param mapperFunction applies the DTO to the entity with the mapper of the concrete type
     * @return updated component model
     */
    @Transactional
    public ComponentModel updateFromDto(
            UUID id,
            PartialUpdate<? extends ComponentModelTo> update,
            BiConsumer<ComponentModel, ComponentModelTo> mapperFunction) {

        ComponentModelTo dto = update.dto();

        // 1. Load existing entity with associations
        ComponentModel entity = repository.findByIdWithComponentType(id)
                .orElseThrow(() -> new NotFoundException("Component model with id=" + id + " not found"));

        // 2. Handle componentTypeId FK change
        if (dto.getComponentTypeId() != null &&
                (entity.getComponentType() == null ||
                 !Objects.equals(dto.getComponentTypeId(), entity.getComponentType().getId()))) {
            ComponentTypeEntity newComponentType = componentTypeRepository.findById(dto.getComponentTypeId())
                    .orElseThrow(() -> new NotFoundException("Component type with id=" + dto.getComponentTypeId() + " not found"));
            entity.setComponentType(newComponentType);
        }

        // 3. Apply field updates via mapper
        mapperFunction.accept(entity, dto);

        // 4. Clear what the request sent as null, which the mapper's IGNORE strategy skipped
        update.applyNulls(entity);

        // 5. Save and return
        return repository.save(entity);
    }
}
