package net.switchscope.repository.installation;

import net.switchscope.model.installation.catalog.InstallationStatusEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for InstallationStatusEntity catalog
 */
@Repository
public interface InstallationStatusRepository extends net.switchscope.repository.component.ComponentBaseRepository<InstallationStatusEntity> {

    /**
     * Find statuses by physical presence flag
     *
     * @param physicallyPresent physical presence flag
     * @return list of statuses
     */
    List<InstallationStatusEntity> findByPhysicallyPresentAndActiveTrue(boolean physicallyPresent);

    /**
     * Find operational statuses
     *
     * @return list of operational statuses
     */
    @Query("SELECT ise FROM InstallationStatusEntity ise WHERE ise.operational = true AND ise.active = true ORDER BY ise.sortOrder, ise.displayName")
    List<InstallationStatusEntity> findOperationalStatuses();

    /**
     * Find statuses requiring attention
     *
     * @return list of statuses requiring attention
     */
    @Query("SELECT ise FROM InstallationStatusEntity ise WHERE ise.requiresAttention = true AND ise.active = true ORDER BY ise.sortOrder, ise.displayName")
    List<InstallationStatusEntity> findStatusesRequiringAttention();

    /**
     * Find final statuses
     *
     * @return list of final statuses
     */
    @Query("SELECT ise FROM InstallationStatusEntity ise WHERE ise.finalStatus = true AND ise.active = true ORDER BY ise.sortOrder, ise.displayName")
    List<InstallationStatusEntity> findFinalStatuses();

    /**
     * Find error statuses
     *
     * @return list of error statuses
     */
    @Query("SELECT ise FROM InstallationStatusEntity ise WHERE ise.errorStatus = true AND ise.active = true ORDER BY ise.sortOrder, ise.displayName")
    List<InstallationStatusEntity> findErrorStatuses();

    /**
     * Find statuses that allow equipment operation
     *
     * @return list of statuses allowing operation
     */
    @Query("SELECT ise FROM InstallationStatusEntity ise WHERE ise.allowsEquipmentOperation = true AND ise.active = true ORDER BY ise.sortOrder, ise.displayName")
    List<InstallationStatusEntity> findStatusesAllowingOperation();

    /**
     * Find statuses that allow maintenance
     *
     * @return list of statuses allowing maintenance
     */
    @Query("SELECT ise FROM InstallationStatusEntity ise WHERE ise.allowsMaintenance = true AND ise.active = true ORDER BY ise.sortOrder, ise.displayName")
    List<InstallationStatusEntity> findStatusesAllowingMaintenance();

    /**
     * Find statuses ordered by status order
     *
     * @return list of statuses ordered by status order
     */
    @Query("SELECT ise FROM InstallationStatusEntity ise WHERE ise.active = true ORDER BY ise.statusOrder, ise.displayName")
    List<InstallationStatusEntity> findAllActiveOrdered();
}

