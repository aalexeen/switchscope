package net.switchscope.service;

import net.switchscope.to.BaseTo;

import java.util.UUID;

/**
 * Extended CRUD service interface that supports partial updates using DTOs.
 * This is the recommended approach for update operations as it:
 * - Preserves existing entity associations and collections
 * - Uses MapStruct's @MappingTarget for selective field updates
 * - Follows the "gold standard" pattern for Spring Boot services
 *
 * @param <E> the entity type
 * @param <T> the DTO (Transfer Object) type
 */
public interface UpdatableCrudService<E, T extends BaseTo> extends CrudService<E> {

    /**
     * Update an existing entity using data from a DTO.
     * This method:
     * 1. Loads the existing entity with all associations
     * 2. Uses mapper.updateFromTo() to update only specified fields
     * 3. Preserves properties, collections, and other associations
     * 4. Saves and returns the updated entity
     *
     * @param id  the entity id
     * @param dto the DTO containing updated field values
     * @return the updated entity with all associations loaded
     */
    E updateFromDto(UUID id, T dto);
}
