package net.switchscope.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

/**
 * Abstract controller for catalog (reference) entities.
 * Provides read access for all authenticated users and write access only for ADMIN role.
 *
 * @param <T> the entity type
 */
@Slf4j
public abstract class AbstractCatalogController<T> {

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
    @PreAuthorize("hasRole('ADMIN')")
    public T create(@RequestBody T entity) {
        log.info("create {} {}", getEntityName(), entity);
        return getService().create(entity);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public T update(@PathVariable UUID id, @RequestBody T entity) {
        log.info("update {} {} with id={}", getEntityName(), entity, id);
        return getService().update(id, entity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        log.info("delete {} {}", getEntityName(), id);
        getService().delete(id);
    }
}

