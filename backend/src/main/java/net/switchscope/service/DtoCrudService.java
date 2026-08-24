package net.switchscope.service;

import net.switchscope.to.BaseTo;
import net.switchscope.to.PageTo;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.payload.PartialUpdate;

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
 * <p>
 * This is the only write contract in the project. It replaced {@code UpdatableCrudService}, which
 * differed from it in one thing - returning the entity instead of the DTO - and thereby made every
 * controller map the answer itself, outside the transaction that had loaded it. The update takes
 * {@code PartialUpdate<? extends T>} rather than {@code PartialUpdate<T>} so that a service for a
 * polymorphic family can serve its subtypes: {@code ComponentService} is handed a
 * {@code PartialUpdate<RackTo>} for a rack, and a {@code PartialUpdate<RackTo>} is not a
 * {@code PartialUpdate<ComponentTo>}.
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
    /**
     * One page of this service's rows, mapped to DTOs inside the read transaction.
     * <p>
     * Declared here rather than beside {@code getAll} in {@link CrudService} because the answer is
     * a DTO, and for the same reason {@code createFromDto} returns one: the mapping has to happen
     * while the transaction that loaded the row is open. A page handed back as entities would be
     * mapped by the controller after it closed, which is a {@code LazyInitializationException} on
     * the first association the DTO carries.
     * <p>
     * Every implementation is one call to {@link net.switchscope.web.page.PageReader}, differing in
     * three things and only three: which entity the page is rooted at, which rows of that entity
     * belong to this route, and what a row needs before it can be mapped. Those are what a service
     * knows and a shared reader cannot.
     *
     * @param query which page was asked for, in what order
     * @return the requested page
     */
    PageTo<T> getPage(ListQuery query);

    T createFromDto(T dto);

    /**
     * Apply an update onto the stored entity and return it mapped back to a DTO.
     * <p>
     * The update carries more than the DTO: it also carries which fields the request actually
     * mentioned, because the mappers run with {@code NullValuePropertyMappingStrategy.IGNORE} and
     * cannot tell a field left out from one deliberately cleared. Implementations map, resolve
     * references, then call {@link PartialUpdate#applyNulls} - in that order, on the entity loaded
     * inside the transaction.
     *
     * @param id     the entity id
     * @param update the values to apply, and which of them the request carried
     * @return the updated entity as a DTO
     */
    T updateFromDto(UUID id, PartialUpdate<? extends T> update);
}
