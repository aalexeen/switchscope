
package net.switchscope.repository.component;

import net.switchscope.model.component.ComponentStatusEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ComponentStatusEntity
 */
@Repository
public interface ComponentStatusRepository extends ComponentCatalogRepository<ComponentStatusEntity> {

    /**
     * Find by lifecycle phase
     *
     * @param lifecyclePhase lifecycle phase
     * @return list of statuses in phase
     */
    List<ComponentStatusEntity> findByLifecyclePhase(String lifecyclePhase);

    /**
     * Find active statuses by lifecycle phase
     *
     * @param lifecyclePhase lifecycle phase
     * @return list of active statuses in phase
     */
    List<ComponentStatusEntity> findByLifecyclePhaseAndActiveTrue(String lifecyclePhase);

    /**
     * Find statuses by lifecycle phase ordered
     *
     * @param phase lifecycle phase
     * @return ordered list of statuses in phase
     */
    @Query("SELECT cs FROM ComponentStatusEntity cs WHERE cs.lifecyclePhase = :phase AND cs.active = true ORDER BY cs.sortOrder, cs.displayName")
    List<ComponentStatusEntity> findByLifecyclePhaseOrdered(@Param("phase") String phase);

    /**
     * Find available statuses
     *
     * @return list of statuses that indicate availability
     */
    @Query("SELECT cs FROM ComponentStatusEntity cs WHERE cs.available = true AND cs.active = true ORDER BY cs.sortOrder")
    List<ComponentStatusEntity> findAvailableStatuses();

    /**
     * Find operational statuses
     *
     * @return list of statuses that indicate operational state
     */
    @Query("SELECT cs FROM ComponentStatusEntity cs WHERE cs.operational = true AND cs.active = true ORDER BY cs.sortOrder")
    List<ComponentStatusEntity> findOperationalStatuses();

    /**
     * Find statuses requiring attention
     *
     * @return list of statuses that require attention
     */
    @Query("SELECT cs FROM ComponentStatusEntity cs WHERE cs.requiresAttention = true AND cs.active = true ORDER BY cs.sortOrder")
    List<ComponentStatusEntity> findStatusesRequiringAttention();

    /**
     * Find transition statuses
     *
     * @return list of statuses that indicate transition state
     */
    @Query("SELECT cs FROM ComponentStatusEntity cs WHERE cs.inTransition = true AND cs.active = true ORDER BY cs.sortOrder")
    List<ComponentStatusEntity> findTransitionStatuses();

    /**
     * Find physically present statuses
     *
     * @return list of statuses that indicate physical presence
     */
    @Query("SELECT cs FROM ComponentStatusEntity cs WHERE cs.physicallyPresent = true AND cs.active = true ORDER BY cs.sortOrder")
    List<ComponentStatusEntity> findPhysicallyPresentStatuses();

    /**
     * Find reservable statuses
     *
     * @return list of statuses that allow reservation
     */
    @Query("SELECT cs FROM ComponentStatusEntity cs WHERE cs.canBeReserved = true AND cs.active = true ORDER BY cs.sortOrder")
    List<ComponentStatusEntity> findReservableStatuses();

    /**
     * Find statuses in inventory
     *
     * @return list of statuses that indicate item is in inventory
     */
    @Query("SELECT cs FROM ComponentStatusEntity cs WHERE cs.inInventory = true AND cs.active = true ORDER BY cs.sortOrder")
    List<ComponentStatusEntity> findInventoryStatuses();

    /**
     * Find statuses that can accept installations
     *
     * @return list of statuses that allow new installations
     */
    @Query("SELECT cs FROM ComponentStatusEntity cs WHERE cs.canAcceptInstallations = true AND cs.active = true ORDER BY cs.sortOrder")
    List<ComponentStatusEntity> findInstallationReadyStatuses();
}
