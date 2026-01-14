package net.switchscope.repository.component.connectivity;

import net.switchscope.model.component.Component;
import net.switchscope.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Connectivity component entities
 * Supports PatchPanel, Connector, and CableRun through Single Table Inheritance
 */
@Repository
public interface ConnectivityRepository extends BaseRepository<Component> {

    /**
     * Find component by serial number
     *
     * @param serialNumber serial number
     * @return optional component
     */
    Optional<Component> findBySerialNumber(String serialNumber);

    /**
     * Find components by manufacturer
     *
     * @param manufacturer manufacturer name
     * @return list of components
     */
    List<Component> findByManufacturer(String manufacturer);

    /**
     * Find PatchPanel components with eager loading
     *
     * @return list of patch panels
     */
    @Query("SELECT DISTINCT c FROM PatchPanel c " +
           "LEFT JOIN FETCH c.componentStatus " +
           "LEFT JOIN FETCH c.componentType " +
           "LEFT JOIN FETCH c.componentNature " +
           "LEFT JOIN FETCH c.installation " +
           "LEFT JOIN FETCH c.parentComponent " +
           "ORDER BY c.name")
    List<Component> findPatchPanels();

    /**
     * Find Connector components with eager loading
     *
     * @return list of connectors
     */
    @Query("SELECT DISTINCT c FROM Connector c " +
           "LEFT JOIN FETCH c.componentStatus " +
           "LEFT JOIN FETCH c.componentType " +
           "LEFT JOIN FETCH c.componentNature " +
           "LEFT JOIN FETCH c.installation " +
           "LEFT JOIN FETCH c.parentComponent " +
           "ORDER BY c.name")
    List<Component> findConnectors();

    /**
     * Find CableRun components with eager loading
     *
     * @return list of cable runs
     */
    @Query("SELECT DISTINCT c FROM CableRun c " +
           "LEFT JOIN FETCH c.componentStatus " +
           "LEFT JOIN FETCH c.componentType " +
           "LEFT JOIN FETCH c.componentNature " +
           "LEFT JOIN FETCH c.installation " +
           "LEFT JOIN FETCH c.parentComponent " +
           "ORDER BY c.name")
    List<Component> findCableRuns();

    /**
     * Find connectivity components by type
     *
     * @param componentType connectivity component type class
     * @return list of components
     */
    @Query("SELECT c FROM Component c WHERE TYPE(c) = :componentType ORDER BY c.name")
    List<Component> findByConnectivityType(@Param("componentType") Class<? extends Component> componentType);

    /**
     * Find all connectivity components
     *
     * @return list of all connectivity components
     */
    @Query("SELECT c FROM Component c WHERE TYPE(c) IN (PatchPanel, Connector, CableRun) ORDER BY c.name")
    List<Component> findAllConnectivityComponents();

    /**
     * Find connectivity components by location
     *
     * @param locationId location ID
     * @return list of connectivity components at location
     */
    @Query("SELECT c FROM Component c WHERE TYPE(c) IN (PatchPanel, Connector, CableRun) AND c.installation.location.id = :locationId ORDER BY c.name")
    List<Component> findByLocationId(@Param("locationId") UUID locationId);

    /**
     * Find operational connectivity components
     *
     * @return list of operational connectivity components
     */
    @Query("SELECT c FROM Component c WHERE TYPE(c) IN (PatchPanel, Connector, CableRun) AND c.componentStatus.operational = true ORDER BY c.name")
    List<Component> findOperationalConnectivityComponents();

    /**
     * Count connectivity components by type
     *
     * @param componentType connectivity component type class
     * @return component count
     */
    @Query("SELECT COUNT(c) FROM Component c WHERE TYPE(c) = :componentType")
    long countByConnectivityType(@Param("componentType") Class<? extends Component> componentType);

    /**
     * Count all connectivity components
     *
     * @return total count of connectivity components
     */
    @Query("SELECT COUNT(c) FROM Component c WHERE TYPE(c) IN (PatchPanel, Connector, CableRun)")
    long countAllConnectivityComponents();
}
