package net.switchscope.service;

import java.util.List;
import java.util.UUID;

/**
 * Base interface for CRUD operations on entities.
 *
 * @param <T> the entity type
 */
public interface CrudService<T> {

    List<T> getAll();

    T getById(UUID id);

    T create(T entity);

    T update(UUID id, T entity);

    void delete(UUID id);
}

