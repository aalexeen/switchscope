package net.switchscope.service.component;

import lombok.RequiredArgsConstructor;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.error.NotFoundException;
import net.switchscope.mapper.component.catalog.ComponentTypeMapper;
import net.switchscope.model.component.ComponentCategoryEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.repository.component.ComponentCategoryRepository;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.service.DtoCrudService;
import net.switchscope.to.component.catalog.ComponentTypeTo;
import net.switchscope.web.payload.PartialUpdate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Service for ComponentType operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentTypeService implements DtoCrudService<ComponentTypeEntity, ComponentTypeTo> {

    private final ComponentTypeRepository repository;
    private final ComponentCategoryRepository categoryRepository;
    private final ComponentTypeMapper mapper;

    @Override
    public List<ComponentTypeEntity> getAll() {
        return repository.findAllWithAssociations();
    }

    @Override
    public ComponentTypeEntity getById(UUID id) {
        return repository.findByIdWithCategory(id)
                .orElseThrow(() -> new NotFoundException("Component type with id=" + id + " not found"));
    }

    /**
     * Persist a component type, resolving its category first. {@code category_id} is NOT NULL and
     * the mapper ignores the association, so mapping the DTO happens here rather than in the
     * controller: an entity handed over already mapped could be saved without a category.
     *
     * @param dto the component type to create, carrying {@code categoryId}
     * @return the stored component type
     */
    @Override
    @Transactional
    public ComponentTypeTo createFromDto(ComponentTypeTo dto) {
        ComponentTypeEntity entity = mapper.toEntity(dto);
        if (dto.getCategoryId() == null) {
            throw new IllegalRequestDataException("categoryId is required");
        }
        entity.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException(
                        "Component category with id=" + dto.getCategoryId() + " not found")));
        return mapper.toTo(repository.save(entity));
    }

    /**
     * Update entity using DTO with MapStruct's @MappingTarget pattern.
     * This is the recommended way to update entities as it:
     * - Preserves properties and associations
     * - Updates only fields specified in the mapper
     * - Follows the gold standard for partial updates
     */
    @Override
    @Transactional
    public ComponentTypeTo updateFromDto(UUID id, PartialUpdate<? extends ComponentTypeTo> update) {
        ComponentTypeTo dto = update.dto();

        // 1. Load existing entity with all associations
        ComponentTypeEntity existing = repository.findByIdWithCategory(id)
                .orElseThrow(() -> new NotFoundException("Component type with id=" + id + " not found"));

        // 2. Handle category change (FK relation not handled by mapper)
        if (dto.getCategoryId() != null &&
                (existing.getCategory() == null || !Objects.equals(dto.getCategoryId(), existing.getCategory().getId()))) {
            ComponentCategoryEntity newCategory = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Component category with id=" + dto.getCategoryId() + " not found"));
            existing.setCategory(newCategory);
        }

        // 3. Use mapper to update only specified fields (preserves properties)
        mapper.updateFromTo(existing, dto);

        // 4. Clear what the request sent as null, which the mapper's IGNORE strategy skipped
        update.applyNulls(existing);

        // 5. Save and map back inside the transaction that loaded the associations
        return mapper.toTo(repository.save(existing));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
