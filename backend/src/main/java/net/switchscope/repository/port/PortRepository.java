package net.switchscope.repository.port;

import net.switchscope.model.port.Port;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Port entities
 * Supports all port types through Single Table Inheritance (ETHERNET, FIBER)
 */
@Repository
public interface PortRepository extends net.switchscope.repository.BaseRepository<Port> {

    /**
     * Find port by equipment and port number
     *
     * @param equipmentId equipment ID
     * @param portNumber port number
     * @return optional port
     */
    Optional<Port> findByDeviceIdAndPortNumber(UUID equipmentId, Integer portNumber);

    /**
     * Find all ports for a device
     *
     * @param equipmentId equipment ID
     * @return list of ports
     */
    List<Port> findByDeviceId(UUID equipmentId);

    /**
     * Find all ports for a device ordered by port number
     *
     * @param equipmentId equipment ID
     * @return list of ports ordered by port number
     */
    @Query("SELECT p FROM Port p WHERE p.device.id = :equipmentId ORDER BY p.portNumber")
    List<Port> findByDeviceIdOrdered(@Param("equipmentId") UUID equipmentId);

    /**
     * Find ports by status
     *
     * @param status port status (UP, DOWN, etc.)
     * @return list of ports
     */
    List<Port> findByStatus(String status);

    /**
     * Find ports by operational status
     *
     * @param operationalStatus operational status
     * @return list of ports
     */
    List<Port> findByOperationalStatus(String operationalStatus);

    /**
     * Find active ports (operational status = UP)
     *
     * @return list of active ports
     */
    @Query("SELECT p FROM Port p WHERE p.operationalStatus = 'UP' ORDER BY p.device.name, p.portNumber")
    List<Port> findActivePorts();

    /**
     * Find ports by port type
     *
     * @param portType port type discriminator (ETHERNET, FIBER)
     * @return list of ports
     */
    @Query("SELECT p FROM Port p WHERE TYPE(p) = :portType ORDER BY p.device.name, p.portNumber")
    List<Port> findByPortType(@Param("portType") Class<? extends Port> portType);

    /**
     * Find Ethernet ports
     *
     * @return list of Ethernet ports
     */
    @Query("SELECT p FROM EthernetPort p ORDER BY p.device.name, p.portNumber")
    List<Port> findEthernetPorts();

    /**
     * Find Fiber ports
     *
     * @return list of Fiber ports
     */
    @Query("SELECT p FROM FiberPort p ORDER BY p.device.name, p.portNumber")
    List<Port> findFiberPorts();

    /**
     * Find ports with PoE enabled
     *
     * @return list of PoE-enabled ports
     */
    @Query("SELECT p FROM Port p WHERE p.poeEnabled = true ORDER BY p.device.name, p.portNumber")
    List<Port> findPoeEnabledPorts();

    /**
     * Find available ports (no connector attached)
     *
     * @return list of available ports
     */
    @Query("SELECT p FROM Port p WHERE p.connector IS NULL AND p.adminStatus = 'UP' ORDER BY p.device.name, p.portNumber")
    List<Port> findAvailablePorts();

    /**
     * Find connected ports (with connector)
     *
     * @return list of connected ports
     */
    @Query("SELECT p FROM Port p WHERE p.connector IS NOT NULL ORDER BY p.device.name, p.portNumber")
    List<Port> findConnectedPorts();

    /**
     * Find ports by VLAN
     *
     * @param vlanId VLAN ID
     * @return list of ports
     */
    @Query("SELECT p FROM Port p WHERE p.accessVlan = :vlanId OR p.nativeVlan = :vlanId ORDER BY p.device.name, p.portNumber")
    List<Port> findByVlan(@Param("vlanId") Integer vlanId);

    /**
     * Find trunk ports
     *
     * @return list of trunk ports
     */
    @Query("SELECT p FROM Port p WHERE p.portMode = 'TRUNK' ORDER BY p.device.name, p.portNumber")
    List<Port> findTrunkPorts();

    /**
     * Find access ports
     *
     * @return list of access ports
     */
    @Query("SELECT p FROM Port p WHERE p.portMode = 'ACCESS' ORDER BY p.device.name, p.portNumber")
    List<Port> findAccessPorts();

    /**
     * Count ports by device
     *
     * @param equipmentId equipment ID
     * @return port count
     */
    long countByDeviceId(UUID equipmentId);

    /**
     * Count active ports by device
     *
     * @param equipmentId equipment ID
     * @return active port count
     */
    @Query("SELECT COUNT(p) FROM Port p WHERE p.device.id = :equipmentId AND p.operationalStatus = 'UP'")
    long countActiveByDeviceId(@Param("equipmentId") UUID equipmentId);
}

