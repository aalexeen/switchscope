package net.switchscope.service.component;

import lombok.RequiredArgsConstructor;
import net.switchscope.error.NotFoundException;
import net.switchscope.mapper.component.catalog.ComponentCategoryMapper;
import net.switchscope.model.component.ComponentCategoryEntity;
import net.switchscope.repository.component.ComponentCategoryRepository;
import net.switchscope.service.DtoCrudService;
import net.switchscope.to.component.catalog.ComponentCategoryTo;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.to.PageTo;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.PageReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for ComponentCategory operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentCategoryService implements DtoCrudService<ComponentCategoryEntity, ComponentCategoryTo> {

    private final ComponentCategoryRepository repository;
    private final ComponentCategoryMapper mapper;
    private final PageReader pageReader;

    @Override
    public List<ComponentCategoryEntity> getAll() {
        return repository.findAllWithAssociations();
    }

    @Override
    public PageTo<ComponentCategoryTo> getPage(ListQuery query) {
        return pageReader.read(ComponentCategoryEntity.class, query, mapper::toTo);
    }

    @Override
    public ComponentCategoryEntity getById(UUID id) {
        return repository.findByIdWithComponentTypes(id)
                .orElseThrow(() -> new NotFoundException("Component category with id=" + id + " not found"));
    }

    @Override
    @Transactional
    public ComponentCategoryTo createFromDto(ComponentCategoryTo dto) {
        return mapper.toTo(repository.save(mapper.toEntity(dto)));
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
    public ComponentCategoryTo updateFromDto(UUID id, PartialUpdate<? extends ComponentCategoryTo> update) {
        ComponentCategoryTo dto = update.dto();

        // 1. Load existing entity with all associations
        ComponentCategoryEntity existing = repository.findByIdWithComponentTypes(id)
                .orElseThrow(() -> new NotFoundException("Component category with id=" + id + " not found"));

        // 2. Use mapper to update only specified fields (preserves properties, componentTypes)
        mapper.updateFromTo(existing, dto);

        // 3. Clear what the request sent as null, which the mapper's IGNORE strategy skipped
        update.applyNulls(existing);

        // 4. Save and map back inside the transaction that loaded the associations
        return mapper.toTo(repository.save(existing));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
