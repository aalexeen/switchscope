package net.switchscope.repository.installation;

import net.switchscope.model.installation.Installation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Installation entities
 */
@Repository
public interface InstallationRepository extends net.switchscope.repository.BaseRepository<Installation> {

    /**
     * Find all installations with all relationships eagerly loaded
     *
     * @return list of all installations with relationships
     */
    @Query("SELECT DISTINCT i FROM Installation i " +
           "LEFT JOIN FETCH i.location " +
           "LEFT JOIN FETCH i.component " +
           "LEFT JOIN FETCH i.installedItemType " +
           "LEFT JOIN FETCH i.status " +
           "ORDER BY i.installedAt DESC")
    List<Installation> findAllWithRelationships();

    /**
     * Find installation by ID with all relationships eagerly loaded
     *
     * @param id installation ID
     * @return optional installation with relationships
     */
    @Query("SELECT i FROM Installation i " +
           "LEFT JOIN FETCH i.location " +
           "LEFT JOIN FETCH i.component " +
           "LEFT JOIN FETCH i.installedItemType " +
           "LEFT JOIN FETCH i.status " +
           "WHERE i.id = :id")
    Optional<Installation> findByIdWithRelationships(@Param("id") UUID id);

    /**
     * Find installation by installed item ID
     *
     * @param installedItemId installed item ID (UUID)
     * @return optional installation
     */
    Optional<Installation> findByInstalledItemId(UUID installedItemId);

    /**
     * Find all installations for a location
     *
     * @param locationId location ID
     * @return list of installations
     */
    List<Installation> findByLocationId(UUID locationId);

    /**
     * Find all installations for a location ordered by rack position
     *
     * @param locationId location ID
     * @return list of installations ordered by rack position
     */
    @Query("SELECT i FROM Installation i WHERE i.location.id = :locationId ORDER BY i.rackPosition")
    List<Installation> findByLocationIdOrdered(@Param("locationId") UUID locationId);

    /**
     * Find installations by status
     *
     * @param statusId status ID
     * @return list of installations
     */
    List<Installation> findByStatusId(UUID statusId);

    /**
     * Find currently installed items
     *
     * @return list of currently installed items
     */
    @Query("SELECT i FROM Installation i WHERE i.status.physicallyPresent = true AND i.status.finalStatus = false ORDER BY i.location.name, i.rackPosition")
    List<Installation> findCurrentlyInstalled();

    /**
     * Find operational installations
     *
     * @return list of operational installations
     */
    @Query("SELECT i FROM Installation i WHERE i.status.operational = true ORDER BY i.location.name, i.rackPosition")
    List<Installation> findOperational();

    /**
     * Find installations requiring attention
     *
     * @return list of installations requiring attention
     */
    @Query("SELECT i FROM Installation i WHERE i.status.requiresAttention = true ORDER BY i.location.name, i.rackPosition")
    List<Installation> findRequiringAttention();

    /**
     * Find installations by installable type
     *
     * @param installableTypeId installable type ID
     * @return list of installations
     */
    @Query("SELECT i FROM Installation i WHERE i.installedItemType.id = :installableTypeId ORDER BY i.location.name, i.rackPosition")
    List<Installation> findByInstallableTypeId(@Param("installableTypeId") UUID installableTypeId);

    /**
     * Find rack-mounted installations
     *
     * @return list of rack-mounted installations
     */
    @Query("SELECT i FROM Installation i WHERE i.rackPosition IS NOT NULL ORDER BY i.location.name, i.rackPosition")
    List<Installation> findRackMounted();

    /**
     * Find installations at specific rack position
     *
     * @param locationId location ID
     * @param rackPosition rack position
     * @return list of installations
     */
    @Query("SELECT i FROM Installation i WHERE i.location.id = :locationId AND i.rackPosition = :rackPosition")
    List<Installation> findByLocationAndRackPosition(@Param("locationId") UUID locationId, @Param("rackPosition") Integer rackPosition);

    /**
     * Count installations by location
     *
     * @param locationId location ID
     * @return installation count
     */
    long countByLocationId(UUID locationId);

    /**
     * Count currently installed items by location
     *
     * @param locationId location ID
     * @return count of currently installed items
     */
    @Query("SELECT COUNT(i) FROM Installation i WHERE i.location.id = :locationId AND i.status.physicallyPresent = true AND i.status.finalStatus = false")
    long countCurrentlyInstalledByLocationId(@Param("locationId") UUID locationId);
}

