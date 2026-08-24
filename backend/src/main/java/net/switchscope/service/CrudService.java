package net.switchscope.service;

import java.util.List;
import java.util.UUID;

/**
 * What every service can do without a DTO: read and delete.
 * <p>
 * Creating and updating are not here, and were removed rather than left unused. Entity-shaped
 * {@code create(E)} and {@code update(UUID, E)} cannot express this project's write path: the
 * mappers ignore association fields, so an entity built from a DTO has all of its foreign keys
 * null, and saving that detached instance merges those nulls onto the stored row. Every service
 * that had to implement them either threw {@code UnsupportedOperationException} or copied fields
 * by hand into an entity the caller had already built - two ways of admitting the same thing. The
 * write path is {@link DtoCrudService}.
 *
 * @param <T> the entity type
 */
public interface CrudService<T> {

    List<T> getAll();

    T getById(UUID id);

    void delete(UUID id);
}
