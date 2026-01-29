package net.switchscope.service.component.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.error.NotFoundException;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.repository.component.ComponentModelRepository;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.security.policy.UpdatePolicy;
import net.switchscope.security.policy.UpdatePolicyResolver;
import net.switchscope.security.policy.UpdatePolicyValidator;
import net.switchscope.service.CrudService;
import net.switchscope.to.component.catalog.ComponentModelTo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentModelService implements CrudService<ComponentModel> {

    private final ComponentModelRepository repository;
    private final ComponentTypeRepository componentTypeRepository;
    private final UpdatePolicyResolver policyResolver;
    private final UpdatePolicyValidator policyValidator;

    @Override
    public List<ComponentModel> getAll() {
        return repository.findAllWithAssociations();
    }

    @Override
    public ComponentModel getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public ComponentModel create(ComponentModel entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public ComponentModel update(UUID id, ComponentModel entity) {
        repository.getExisted(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }

    /**
     * Update component model with role-based policy validation.
     *
     * @param id            component model ID
     * @param dto           deserialized DTO
     * @param dtoClass      DTO class for policy lookup
     * @param presentFields JSON fields present in request (for null detection)
     * @param mapperFunction function to apply DTO to entity via mapper
     * @return updated component model
     */
    @Transactional
    public ComponentModel updateWithPolicyValidation(
            UUID id,
            ComponentModelTo dto,
            Class<? extends ComponentModelTo> dtoClass,
            Map<String, JsonNode> presentFields,
            BiConsumer<ComponentModel, ComponentModelTo> mapperFunction) {

        // 1. Load existing entity with associations
        ComponentModel entity = repository.findByIdWithComponentType(id)
                .orElseThrow(() -> new NotFoundException("Component model with id=" + id + " not found"));

        // 2. Validate field nullifications against policy
        UpdatePolicy policy = policyResolver.resolve();
        log.debug("Applying update policy: {} for component model {}", policy.getPolicyName(), id);
        policyValidator.validate(dtoClass, presentFields, policy);

        // 3. Handle componentTypeId FK change
        if (dto.getComponentTypeId() != null &&
                (entity.getComponentType() == null ||
                 !Objects.equals(dto.getComponentTypeId(), entity.getComponentType().getId()))) {
            ComponentTypeEntity newComponentType = componentTypeRepository.findById(dto.getComponentTypeId())
                    .orElseThrow(() -> new NotFoundException("Component type with id=" + dto.getComponentTypeId() + " not found"));
            entity.setComponentType(newComponentType);
        }

        // 4. Apply field updates via mapper
        mapperFunction.accept(entity, dto);

        // 5. Save and return
        return repository.save(entity);
    }
}
