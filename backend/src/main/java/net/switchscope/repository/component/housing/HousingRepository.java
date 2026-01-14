package net.switchscope.repository.component.housing;

import net.switchscope.model.component.Component;
import net.switchscope.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Housing component entities
 * Currently supports Rack through Single Table Inheritance
 */
@Repository
public interface HousingRepository extends BaseRepository<Component> {

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
     * Find Rack components with eager loading
     *
     * @return list of racks
     */
    @Query("SELECT DISTINCT c FROM Rack c " +
           "LEFT JOIN FETCH c.componentStatus " +
           "LEFT JOIN FETCH c.componentType " +
           "LEFT JOIN FETCH c.componentNature " +
           "LEFT JOIN FETCH c.installation " +
           "LEFT JOIN FETCH c.parentComponent " +
           "ORDER BY c.name")
    List<Component> findRacks();

    /**
     * Find rack by name
     *
     * @param name rack name
     * @return optional rack
     */
    @Query("SELECT c FROM Rack c WHERE c.name = :name")
    Optional<Component> findRackByName(@Param("name") String name);

    /**
     * Find racks by location
     *
     * @param locationId location ID
     * @return list of racks at location
     */
    @Query("SELECT c FROM Rack c WHERE c.installation.location.id = :locationId ORDER BY c.name")
    List<Component> findRacksByLocationId(@Param("locationId") UUID locationId);

    /**
     * Find racks with available space
     *
     * @param requiredRackUnits required rack units
     * @return list of racks with available space
     */
    @Query("SELECT c FROM Rack c WHERE c.rackUnitsTotal >= :requiredRackUnits ORDER BY c.name")
    List<Component> findRacksWithAvailableSpace(@Param("requiredRackUnits") Integer requiredRackUnits);

    /**
     * Find racks by rack type
     *
     * @param rackTypeId rack type ID
     * @return list of racks
     */
    @Query("SELECT c FROM Rack c WHERE c.rackType.id = :rackTypeId ORDER BY c.name")
    List<Component> findRacksByRackTypeId(@Param("rackTypeId") UUID rackTypeId);

    /**
     * Find operational racks
     *
     * @return list of operational racks
     */
    @Query("SELECT c FROM Rack c WHERE c.componentStatus.operational = true ORDER BY c.name")
    List<Component> findOperationalRacks();

    /**
     * Count racks by location
     *
     * @param locationId location ID
     * @return rack count
     */
    @Query("SELECT COUNT(c) FROM Rack c WHERE c.installation.location.id = :locationId")
    long countRacksByLocationId(@Param("locationId") UUID locationId);

    /**
     * Count all racks
     *
     * @return total rack count
     */
    @Query("SELECT COUNT(c) FROM Rack c")
    long countAllRacks();
}
