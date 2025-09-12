package net.switchscope.repository.component;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Common interface for component catalog repositories that have hierarchical relationships
 * and system/user-defined entities
 */
@NoRepositoryBean
public interface ComponentCatalogRepository<T> extends ComponentBaseRepository<T> {

    /**
     * Find all system entities (cannot be deleted)
     *
     * @return list of system entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.systemCategory = true OR e.systemType = true")
    List<T> findSystemEntities();

    /**
     * Find all user-defined entities (can be deleted)
     *
     * @return list of user-defined entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE (e.systemCategory = false OR e.systemCategory IS NULL) AND (e.systemType = false OR e.systemType IS NULL)")
    List<T> findUserDefinedEntities();

    /**
     * Find entities with specific sort order
     *
     * @param sortOrder sort order value
     * @return list of entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.sortOrder = :sortOrder AND e.active = true ORDER BY e.displayName")
    List<T> findBySortOrder(@Param("sortOrder") Integer sortOrder);

    /**
     * Find entities by display name pattern (case-insensitive)
     *
     * @param pattern search pattern
     * @return list of matching entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE LOWER(e.displayName) LIKE LOWER(CONCAT('%', :pattern, '%')) AND e.active = true ORDER BY e.displayName")
    List<T> findByDisplayNameContainingIgnoreCase(@Param("pattern") String pattern);

    /**
     * Find entities by code pattern (case-insensitive)
     *
     * @param pattern search pattern
     * @return list of matching entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE LOWER(e.code) LIKE LOWER(CONCAT('%', :pattern, '%')) AND e.active = true ORDER BY e.code")
    List<T> findByCodeContainingIgnoreCase(@Param("pattern") String pattern);

    /**
     * Count total entities (active and inactive)
     *
     * @return total count
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e")
    long countTotal();

    /**
     * Count inactive entities
     *
     * @return count of inactive entities
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.active = false")
    long countInactive();

    /**
     * Count system entities
     *
     * @return count of system entities
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.systemCategory = true OR e.systemType = true")
    long countSystem();

    /**
     * Count user-defined entities
     *
     * @return count of user-defined entities
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE (e.systemCategory = false OR e.systemCategory IS NULL) AND (e.systemType = false OR e.systemType IS NULL)")
    long countUserDefined();
}