package net.switchscope.service;

import net.switchscope.to.BaseTo;

import java.util.UUID;

/**
 * CRUD service whose write operations are expressed in terms of DTOs rather than entities.
 * <p>
 * Entity-level {@code create}/{@code update} cannot express the write path correctly for entities
 * that own foreign keys: the mappers deliberately ignore association fields (the DTO carries ids,
 * the model carries entities), so an entity produced by {@code mapper.toEntity(dto)} has all of its
 * associations null. Passing such a detached instance to {@code save()} performs a merge that nulls
 * those columns on the stored row.
 * <p>
 * Implementations therefore take the DTO, resolve its foreign-key ids into managed references, and -
 * for updates - apply the DTO onto the entity loaded from the database instead of merging a detached
 * one. Mapping back to the DTO happens inside the same transaction, so lazy associations are still
 * reachable.
 *
 * @param <E> the entity type
 * @param <T> the DTO (Transfer Object) type
 */
public interface DtoCrudService<E, T extends BaseTo> extends CrudService<E> {

    /**
     * Create a new entity from the DTO and return it mapped back to a DTO.
     *
     * @param dto the values to create from; its {@code id} is ignored
     * @return the created entity as a DTO
     */
    T createFromDto(T dto);

    /**
     * Apply the DTO onto the stored entity and return it mapped back to a DTO.
     *
     * @param id  the entity id
     * @param dto the values to apply
     * @return the updated entity as a DTO
     */
    T updateFromDto(UUID id, T dto);
}
