package net.switchscope.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.service.CrudService;
import net.switchscope.to.BaseTo;

import java.util.List;
import java.util.UUID;

/**
 * Abstract controller for basic CRUD operations.
 * Provides standard REST endpoints for entities.
 * Uses DTOs for API contract while working with Entities internally.
 * Transaction management is delegated to service layer.
 *
 * @param <E> the entity type
 * @param <T> the DTO (Transfer Object) type
 */
@Slf4j
public abstract class AbstractCrudController<E, T extends BaseTo> {

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
    public T create(@RequestBody T dto) {
        log.info("create {} {}", getEntityName(), dto);
        E entity = getMapper().toEntity(dto);
        E created = getService().create(entity);
        return getMapper().toTo(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public T update(@PathVariable UUID id, @RequestBody T dto) {
        log.info("update {} {} with id={}", getEntityName(), dto, id);
        E entity = getMapper().toEntity(dto);
        E updated = getService().update(id, entity);
        return getMapper().toTo(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete {} {}", getEntityName(), id);
        getService().delete(id);
    }
}

