package net.switchscope.repository.installation;

import net.switchscope.model.installation.catalog.InstallableTypeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for InstallableTypeEntity catalog
 */
@Repository
public interface InstallableTypeRepository extends net.switchscope.repository.component.ComponentBaseRepository<InstallableTypeEntity> {

    /**
     * Find types that require rack position
     *
     * @return list of types requiring rack position
     */
    @Query("SELECT ite FROM InstallableTypeEntity ite WHERE ite.requiresRackPosition = true AND ite.active = true ORDER BY ite.sortOrder, ite.displayName")
    List<InstallableTypeEntity> findRackRequiredTypes();

    /**
     * Find hot-swappable types
     *
     * @return list of hot-swappable types
     */
    @Query("SELECT ite FROM InstallableTypeEntity ite WHERE ite.hotSwappable = true AND ite.active = true ORDER BY ite.sortOrder, ite.displayName")
    List<InstallableTypeEntity> findHotSwappableTypes();

    /**
     * Find types that require shutdown
     *
     * @return list of types requiring shutdown
     */
    @Query("SELECT ite FROM InstallableTypeEntity ite WHERE ite.requiresShutdown = true AND ite.active = true ORDER BY ite.sortOrder, ite.displayName")
    List<InstallableTypeEntity> findShutdownRequiredTypes();

    /**
     * Find types that support power management
     *
     * @return list of types supporting power management
     */
    @Query("SELECT ite FROM InstallableTypeEntity ite WHERE ite.supportsPowerManagement = true AND ite.active = true ORDER BY ite.sortOrder, ite.displayName")
    List<InstallableTypeEntity> findPowerManagementTypes();

    /**
     * Find types that require environmental control
     *
     * @return list of types requiring environmental control
     */
    @Query("SELECT ite FROM InstallableTypeEntity ite WHERE ite.requiresEnvironmentalControl = true AND ite.active = true ORDER BY ite.sortOrder, ite.displayName")
    List<InstallableTypeEntity> findEnvironmentalControlTypes();

    /**
     * Find types by entity class
     *
     * @param entityClass entity class name
     * @return list of types
     */
    @Query("SELECT ite FROM InstallableTypeEntity ite WHERE ite.entityClass = :entityClass AND ite.active = true ORDER BY ite.sortOrder, ite.displayName")
    List<InstallableTypeEntity> findByEntityClass(@Param("entityClass") String entityClass);
}

