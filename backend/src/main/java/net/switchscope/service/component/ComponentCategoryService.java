package net.switchscope.service.component;

import lombok.RequiredArgsConstructor;
import net.switchscope.error.NotFoundException;
import net.switchscope.mapper.component.catalog.ComponentCategoryMapper;
import net.switchscope.model.component.ComponentCategoryEntity;
import net.switchscope.repository.component.ComponentCategoryRepository;
import net.switchscope.service.UpdatableCrudService;
import net.switchscope.to.component.catalog.ComponentCategoryTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for ComponentCategory operations.
 * Implements UpdatableCrudService for proper partial updates using DTOs.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentCategoryService implements UpdatableCrudService<ComponentCategoryEntity, ComponentCategoryTo> {

    private final ComponentCategoryRepository repository;
    private final ComponentCategoryMapper mapper;

    @Override
    public List<ComponentCategoryEntity> getAll() {
        return repository.findAllWithAssociations();
    }

    @Override
    public ComponentCategoryEntity getById(UUID id) {
        return repository.findByIdWithComponentTypes(id)
                .orElseThrow(() -> new NotFoundException("Component category with id=" + id + " not found"));
    }

    @Override
    @Transactional
    public ComponentCategoryEntity create(ComponentCategoryEntity entity) {
        return repository.save(entity);
    }

    /**
     * @deprecated Use {@link #updateFromDto(UUID, ComponentCategoryTo)} instead.
     * This method exists for backward compatibility with CrudService interface.
     */
    @Override
    @Deprecated
    @Transactional
    public ComponentCategoryEntity update(UUID id, ComponentCategoryEntity entity) {
        // Fallback: load existing and manually copy fields
        ComponentCategoryEntity existing = repository.findByIdWithComponentTypes(id)
                .orElseThrow(() -> new NotFoundException("Component category with id=" + id + " not found"));

        // Copy only editable fields, preserving associations
        existing.setName(entity.getName());
        existing.setDisplayName(entity.getDisplayName());
        existing.setDescription(entity.getDescription());
        existing.setActive(entity.isActive());
        existing.setSortOrder(entity.getSortOrder());
        existing.setColorClass(entity.getColorClass());
        existing.setIconClass(entity.getIconClass());
        existing.setSystemCategory(entity.isSystemCategory());
        existing.setInfrastructure(entity.isInfrastructure());
        // Note: code is immutable after creation
        // Note: properties and componentTypes are preserved

        return repository.save(existing);
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
    public ComponentCategoryEntity updateFromDto(UUID id, ComponentCategoryTo dto) {
        // 1. Load existing entity with all associations
        ComponentCategoryEntity existing = repository.findByIdWithComponentTypes(id)
                .orElseThrow(() -> new NotFoundException("Component category with id=" + id + " not found"));

        // 2. Use mapper to update only specified fields (preserves properties, componentTypes)
        mapper.updateFromTo(existing, dto);

        // 3. Save and return
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
