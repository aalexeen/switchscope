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
}