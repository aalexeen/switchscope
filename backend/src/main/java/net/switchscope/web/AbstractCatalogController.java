package net.switchscope.web;

import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.service.CrudService;
import net.switchscope.service.UpdatableCrudService;
import net.switchscope.to.BaseTo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Abstract controller for catalog (reference) entities.
 * Provides read access for all authenticated users and write access only for ADMIN role.
 * Uses DTOs for API contract while working with Entities internally.
 * Transaction management is delegated to service layer.
 * <p>
 * Supports the "gold standard" update pattern when service implements {@link UpdatableCrudService}:
 * - Service loads existing entity with associations
 * - Uses MapStruct's @MappingTarget to update only specified fields
 * - Preserves properties, collections, and other associations
 *
 * @param <E> the entity type
 * @param <T> the DTO (Transfer Object) type
 */
@Slf4j
public abstract class AbstractCatalogController<E, T extends BaseTo> {

    protected abstract CrudService<E> getService();

    protected abstract BaseMapper<E, T> getMapper();

    protected abstract String getEntityName();

    @GetMapping
    public List<T> getAll() {
        log.info("getAll {}", getEntityName());
        List<E> entities = getService().getAll();
        return getMapper().toToList(entities);
    }

    @GetMapping("/{id}")
    public T get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        E entity = getService().getById(id);
        return getMapper().toTo(entity);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public T create(@RequestBody T dto) {
        log.info("create {} {}", getEntityName(), dto);
        E entity = getMapper().toEntity(dto);
        E created = getService().create(entity);
        return getMapper().toTo(created);
    }

    /**
     * Update an existing entity.
     * <p>
     * If the service implements {@link UpdatableCrudService}, uses the "gold standard" pattern:
     * - Service loads existing entity with associations
     * - Uses mapper.updateFromTo() for partial update
     * - Preserves properties and associations
     * <p>
     * Otherwise, falls back to the legacy pattern (may lose associations).
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("unchecked")
    public T update(@PathVariable UUID id, @RequestBody T dto) {
        log.info("update {} {} with id={}", getEntityName(), dto, id);

        CrudService<E> service = getService();
        E updated;

        // Use updateFromDto if service supports it (gold standard pattern)
        if (service instanceof UpdatableCrudService) {
            UpdatableCrudService<E, T> updatableService = (UpdatableCrudService<E, T>) service;
            updated = updatableService.updateFromDto(id, dto);
        } else {
            // Fallback to legacy pattern (may lose properties/associations)
            log.warn("Service {} does not implement UpdatableCrudService, using legacy update pattern",
                    service.getClass().getSimpleName());
            E entity = getMapper().toEntity(dto);
            updated = service.update(id, entity);
        }

        return getMapper().toTo(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        log.info("delete {} {}", getEntityName(), id);
        getService().delete(id);
    }
}
