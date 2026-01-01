package net.switchscope.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

/**
 * Abstract controller for basic CRUD operations.
 * Provides standard REST endpoints for entities.
 *
 * @param <T> the entity type
 */
@Slf4j
public abstract class AbstractCrudController<T> {

    protected abstract CrudService<T> getService();

    protected abstract String getEntityName();

    @GetMapping
    public List<T> getAll() {
        log.info("getAll {}", getEntityName());
        return getService().getAll();
    }

    @GetMapping("/{id}")
    public T get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        return getService().getById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public T create(@RequestBody T entity) {
        log.info("create {} {}", getEntityName(), entity);
        return getService().create(entity);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public T update(@PathVariable UUID id, @RequestBody T entity) {
        log.info("update {} {} with id={}", getEntityName(), entity, id);
        return getService().update(id, entity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete {} {}", getEntityName(), id);
        getService().delete(id);
    }
}

