package net.switchscope.repository.component;

import net.switchscope.model.component.Component;
import net.switchscope.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Component entities
 * Supports all component types through Single Table Inheritance
 */
@Repository
public interface ComponentRepository extends BaseRepository<Component> {

    /**
     * Find component by serial number
     *
     * @param serialNumber serial number
     * @return optional component
     */
    Optional<Component> findBySerialNumber(String serialNumber);

    /**
     * Find component by manufacturer and serial number
     *
     * @param manufacturer manufacturer
     * @param serialNumber serial number
     * @return optional component
     */
    Optional<Component> findByManufacturerAndSerialNumber(String manufacturer, String serialNumber);

    /**
     * Check if component exists by manufacturer and serial number
     *
     * @param manufacturer manufacturer
     * @param serialNumber serial number
     * @return true if exists
     */
    boolean existsByManufacturerAndSerialNumber(String manufacturer, String serialNumber);

    /**
     * Find components by manufacturer
     *
     * @param manufacturer manufacturer name
     * @return list of components
     */
    List<Component> findByManufacturer(String manufacturer);

    /**
     * Find components by model
     *
     * @param model model name
     * @return list of components
     */
    List<Component> findByModel(String model);

    /**
     * Find components by manufacturer and model
     *
     * @param manufacturer manufacturer name
     * @param model model name
     * @return list of components
     */
    List<Component> findByManufacturerAndModel(String manufacturer, String model);

    /**
     * Find components by name pattern (case-insensitive)
     *
     * @param pattern search pattern
     * @return list of matching components
     */
    @Query("SELECT c FROM Component c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :pattern, '%')) ORDER BY c.name")
    List<Component> findByNameContainingIgnoreCase(@Param("pattern") String pattern);

    /**
     * Find components by component type
     *
     * @param componentTypeId component type ID
     * @return list of components
     */
    @Query("SELECT c FROM Component c WHERE c.componentType.id = :componentTypeId ORDER BY c.name")
    List<Component> findByComponentTypeId(@Param("componentTypeId") UUID componentTypeId);

    /**
     * Find components by component status
     *
     * @param componentStatusId component status ID
     * @return list of components
     */
    @Query("SELECT c FROM Component c WHERE c.componentStatus.id = :componentStatusId ORDER BY c.name")
    List<Component> findByComponentStatusId(@Param("componentStatusId") UUID componentStatusId);

    /**
     * Find operational components
     *
     * @return list of operational components
     */
    @Query("SELECT c FROM Component c WHERE c.componentStatus.operational = true ORDER BY c.name")
    List<Component> findOperationalComponents();

    /**
     * Find components requiring attention
     *
     * @return list of components requiring attention
     */
    @Query("SELECT c FROM Component c WHERE c.componentStatus.requiresAttention = true ORDER BY c.name")
    List<Component> findComponentsRequiringAttention();

    /**
     * Find components by installation location
     *
     * @param locationId location ID
     * @return list of components at location
     */
    @Query("SELECT c FROM Component c WHERE c.installation.location.id = :locationId ORDER BY c.name")
    List<Component> findByInstallationLocationId(@Param("locationId") UUID locationId);

    /**
     * Count components by manufacturer
     *
     * @param manufacturer manufacturer name
     * @return component count
     */
    long countByManufacturer(String manufacturer);

    /**
     * Count components by component type
     *
     * @param componentTypeId component type ID
     * @return component count
     */
    @Query("SELECT COUNT(c) FROM Component c WHERE c.componentType.id = :componentTypeId")
    long countByComponentTypeId(@Param("componentTypeId") UUID componentTypeId);

    /**
     * Count operational components
     *
     * @return count of operational components
     */
    @Query("SELECT COUNT(c) FROM Component c WHERE c.componentStatus.operational = true")
    long countOperationalComponents();
}

