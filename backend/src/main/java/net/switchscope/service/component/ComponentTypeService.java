package net.switchscope.service.component;

import lombok.RequiredArgsConstructor;
import net.switchscope.error.NotFoundException;
import net.switchscope.mapper.component.catalog.ComponentTypeMapper;
import net.switchscope.model.component.ComponentCategoryEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.repository.component.ComponentCategoryRepository;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.service.UpdatableCrudService;
import net.switchscope.to.component.catalog.ComponentTypeTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Service for ComponentType operations.
 * Implements UpdatableCrudService for proper partial updates using DTOs.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentTypeService implements UpdatableCrudService<ComponentTypeEntity, ComponentTypeTo> {

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

    @Override
    @Transactional
    public ComponentTypeEntity create(ComponentTypeEntity entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    /**
     * @deprecated Use {@link #updateFromDto(UUID, ComponentTypeTo)} instead.
     * This method exists for backward compatibility with CrudService interface.
     */
    @Override
    @Deprecated
    @Transactional
    public ComponentTypeEntity update(UUID id, ComponentTypeEntity entity) {
        // Fallback: load existing and manually copy fields
        ComponentTypeEntity existing = repository.findByIdWithCategory(id)
                .orElseThrow(() -> new NotFoundException("Component type with id=" + id + " not found"));

        // Copy only editable fields, preserving associations
        existing.setName(entity.getName());
        existing.setDisplayName(entity.getDisplayName());
        existing.setDescription(entity.getDescription());
        existing.setActive(entity.isActive());
        existing.setSortOrder(entity.getSortOrder());
        existing.setColorClass(entity.getColorClass());
        existing.setIconClass(entity.getIconClass());
        existing.setSystemType(entity.isSystemType());
        existing.setRequiresRackSpace(entity.isRequiresRackSpace());
        existing.setTypicalRackUnits(entity.getTypicalRackUnits());
        existing.setCanContainComponents(entity.isCanContainComponents());
        existing.setInstallable(entity.isInstallable());
        existing.setRequiresManagement(entity.isRequiresManagement());
        existing.setSupportsSnmp(entity.isSupportsSnmp());
        existing.setHasFirmware(entity.isHasFirmware());
        existing.setCanHaveIpAddress(entity.isCanHaveIpAddress());
        existing.setProcessesNetworkTraffic(entity.isProcessesNetworkTraffic());
        existing.setRequiresPower(entity.isRequiresPower());
        existing.setTypicalPowerConsumptionWatts(entity.getTypicalPowerConsumptionWatts());
        existing.setGeneratesHeat(entity.isGeneratesHeat());
        existing.setNeedsCooling(entity.isNeedsCooling());
        existing.setRecommendedMaintenanceIntervalMonths(entity.getRecommendedMaintenanceIntervalMonths());
        existing.setTypicalLifespanYears(entity.getTypicalLifespanYears());
        existing.setAllowedChildTypeCodes(entity.getAllowedChildTypeCodes());
        existing.setAllowedChildCategoryCodes(entity.getAllowedChildCategoryCodes());
        // Note: code is immutable after creation
        // Note: category and properties are preserved

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
    public ComponentTypeEntity updateFromDto(UUID id, ComponentTypeTo dto) {
        // 1. Load existing entity with all associations
        ComponentTypeEntity existing = repository.findByIdWithCategory(id)
                .orElseThrow(() -> new NotFoundException("Component type with id=" + id + " not found"));

        // 2. Handle category change (FK relation not handled by mapper)
        if (dto.getCategoryId() != null &&
                !Objects.equals(dto.getCategoryId(), existing.getCategory().getId())) {
            ComponentCategoryEntity newCategory = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Component category with id=" + dto.getCategoryId() + " not found"));
            existing.setCategory(newCategory);
        }

        // 3. Use mapper to update only specified fields (preserves properties)
        mapper.updateFromTo(existing, dto);

        // 4. Save and return
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
