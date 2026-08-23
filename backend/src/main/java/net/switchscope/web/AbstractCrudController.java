package net.switchscope.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.security.permission.RequiresPermission;
import net.switchscope.service.DtoCrudService;
import net.switchscope.to.BaseTo;

import java.util.List;
import java.util.UUID;

/**
 * Abstract controller for basic CRUD operations.
 * Provides standard REST endpoints for entities.
 * Uses DTOs for API contract while working with Entities internally.
 * Transaction management is delegated to service layer.
 * <p>
 * Writes go through {@link DtoCrudService}, which resolves the DTO's foreign-key ids and applies
 * updates onto the stored entity. The controller must not build an entity with the mapper and hand
 * it to the service: the mapper ignores associations, so saving that detached instance would null
 * the corresponding columns.
 *
 * @param <E> the entity type
 * @param <T> the DTO (Transfer Object) type
 */
@Slf4j
public abstract class AbstractCrudController<E, T extends BaseTo> {

    protected abstract DtoCrudService<E, T> getService();

    protected abstract BaseMapper<E, T> getMapper();

    protected abstract String getEntityName();

    @RequiresPermission("read")
    @GetMapping
    public List<T> getAll() {
        log.info("getAll {}", getEntityName());
        List<E> entities = getService().getAll();
        return getMapper().toToList(entities);
    }

    @RequiresPermission("read")
    @GetMapping("/{id}")
    public T get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        E entity = getService().getById(id);
        return getMapper().toTo(entity);
    }

    @RequiresPermission("create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public T create(@Valid @RequestBody T dto) {
        log.info("create {} {}", getEntityName(), dto);
        return getService().createFromDto(dto);
    }

    @RequiresPermission("update")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public T update(@PathVariable UUID id, @RequestBody T dto) {
        log.info("update {} {} with id={}", getEntityName(), dto, id);
        return getService().updateFromDto(id, dto);
    }

    @RequiresPermission("delete")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete {} {}", getEntityName(), id);
        getService().delete(id);
    }
}
