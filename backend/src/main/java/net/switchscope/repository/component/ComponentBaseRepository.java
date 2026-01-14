package net.switchscope.repository.component;

import net.switchscope.error.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base repository interface for component catalogs
 * Provides common CRUD operations for component-related entities
 */
@NoRepositoryBean
public interface ComponentBaseRepository<T> extends JpaRepository<T, UUID> {

    /**
     * Find entity by unique code
     *
     * @param code the unique code
     * @return optional entity
     */
    Optional<T> findByCode(String code);

    /**
     * Check if entity exists by code
     *
     * @param code the unique code
     * @return true if exists
     */
    boolean existsByCode(String code);

    /**
     * Find all active entities
     *
     * @return list of active entities
     */
    List<T> findByActiveTrue();

    /**
     * Find all active entities ordered
     *
     * @return list of active entities ordered by sortOrder and displayName
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.active = true ORDER BY e.sortOrder, e.displayName")
    List<T> findAllActiveOrdered();

    /**
     * Count active entities
     *
     * @return count of active entities
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.active = true")
    long countActive();

    /**
     * Delete entity by id with custom UUID return type
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.id = :id")
    int deleteByIdCustom(@Param("id") UUID id);

    /**
     * Delete entity and throw exception if not found
     *
     * @param id entity id
     */
    @SuppressWarnings("all")
    default void deleteExisted(UUID id) {
        if (deleteByIdCustom(id) == 0) {
            throw new NotFoundException("Entity with id=" + id + " not found");
        }
    }

    /**
     * Get entity or throw exception if not found
     *
     * @param id entity id
     * @return entity
     */
    default T getExisted(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Entity with id=" + id + " not found"));
    }

    /**
     * Get entity by code or throw exception if not found
     *
     * @param code entity code
     * @return entity
     */
    default T getExistedByCode(String code) {
        return findByCode(code).orElseThrow(() -> new NotFoundException("Entity with code=" + code + " not found"));
    }

    /**
     * Activate entity
     *
     * @param id entity id
     */
    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.active = true WHERE e.id = :id")
    int activate(@Param("id") UUID id);

    /**
     * Deactivate entity
     *
     * @param id entity id
     */
    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.active = false WHERE e.id = :id")
    int deactivate(@Param("id") UUID id);

    /**
     * Activate entity and throw exception if not found
     *
     * @param id entity id
     */
    default void activateExisted(UUID id) {
        if (activate(id) == 0) {
            throw new NotFoundException("Entity with id=" + id + " not found");
        }
    }

    /**
     * Deactivate entity and throw exception if not found
     *
     * @param id entity id
     */
    default void deactivateExisted(UUID id) {
        if (deactivate(id) == 0) {
            throw new NotFoundException("Entity with id=" + id + " not found");
        }
    }

    /**
     * Find all entities with eager loading of associations
     * Note: Override this in specific repositories if they have associations to fetch
     *
     * @return list of all entities
     */
    @Query("SELECT DISTINCT e FROM #{#entityName} e ORDER BY e.sortOrder, e.displayName")
    List<T> findAllWithAssociations();
}