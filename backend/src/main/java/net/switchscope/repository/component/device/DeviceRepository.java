package net.switchscope.repository.component.device;

import net.switchscope.model.component.device.Device;
import net.switchscope.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Device entities
 * Supports all device types through Single Table Inheritance (NETWORK_SWITCH, ROUTER, ACCESS_POINT)
 */
@Repository
public interface DeviceRepository extends BaseRepository<Device> {

    /**
     * Find device by serial number
     *
     * @param serialNumber serial number
     * @return optional device
     */
    Optional<Device> findBySerialNumber(String serialNumber);

    /**
     * Find device by management IP
     *
     * @param managementIp management IP address
     * @return optional device
     */
    Optional<Device> findByManagementIp(String managementIp);

    /**
     * Find devices by management VLAN
     *
     * @param managementVlan management VLAN ID
     * @return list of devices
     */
    List<Device> findByManagementVlan(Integer managementVlan);

    /**
     * Find devices by manufacturer
     *
     * @param manufacturer manufacturer name
     * @return list of devices
     */
    List<Device> findByManufacturer(String manufacturer);

    /**
     * Find devices by model
     *
     * @param model model name
     * @return list of devices
     */
    List<Device> findByModel(String model);

    /**
     * Find devices with management IP
     *
     * @return list of devices with management IP
     */
    @Query("SELECT d FROM Device d WHERE d.managementIp IS NOT NULL AND d.managementIp != '' ORDER BY d.name")
    List<Device> findManagedDevices();

    /**
     * Find devices by firmware version
     *
     * @param firmwareVersion firmware version
     * @return list of devices
     */
    List<Device> findByFirmwareVersion(String firmwareVersion);

    /**
     * Find devices that need firmware update
     *
     * @param targetVersion target firmware version
     * @return list of devices needing update
     */
    @Query("SELECT d FROM Device d WHERE d.firmwareVersion IS NOT NULL AND d.firmwareVersion != :targetVersion ORDER BY d.name")
    List<Device> findDevicesNeedingFirmwareUpdate(@Param("targetVersion") String targetVersion);

    /**
     * Find devices by SNMP community
     *
     * @param snmpCommunity SNMP community string
     * @return list of devices
     */
    @Query("SELECT d FROM Device d WHERE d.snmpCommunityRead = :snmpCommunity OR d.snmpCommunityWrite = :snmpCommunity ORDER BY d.name")
    List<Device> findBySnmpCommunity(@Param("snmpCommunity") String snmpCommunity);

    /**
     * Find devices by SNMP version
     *
     * @param snmpVersion SNMP version
     * @return list of devices
     */
    @Query("SELECT d FROM Device d WHERE d.snmpVersion = :snmpVersion ORDER BY d.name")
    List<Device> findBySnmpVersion(@Param("snmpVersion") String snmpVersion);

    /**
     * Find NetworkSwitch devices
     *
     * @return list of network switches
     */
    @Query("SELECT d FROM NetworkSwitch d ORDER BY d.name")
    List<Device> findNetworkSwitches();

    /**
     * Find Router devices
     *
     * @return list of routers
     */
    @Query("SELECT d FROM Router d ORDER BY d.name")
    List<Device> findRouters();

    /**
     * Find AccessPoint devices
     *
     * @return list of access points
     */
    @Query("SELECT d FROM AccessPoint d ORDER BY d.name")
    List<Device> findAccessPoints();

    /**
     * Find devices by type discriminator
     *
     * @param deviceType device type class
     * @return list of devices
     */
    @Query("SELECT d FROM Device d WHERE TYPE(d) = :deviceType ORDER BY d.name")
    List<Device> findByDeviceType(@Param("deviceType") Class<? extends Device> deviceType);

    /**
     * Find devices requiring maintenance
     *
     * @return list of devices requiring maintenance
     */
    @Query("SELECT d FROM Device d WHERE d.nextMaintenanceDate IS NOT NULL AND d.nextMaintenanceDate < CURRENT_TIMESTAMP ORDER BY d.name")
    List<Device> findDevicesRequiringMaintenance();

    /**
     * Find devices by component type
     *
     * @param componentTypeId component type ID
     * @return list of devices
     */
    @Query("SELECT d FROM Device d WHERE d.componentType.id = :componentTypeId ORDER BY d.name")
    List<Device> findByComponentTypeId(@Param("componentTypeId") UUID componentTypeId);

    /**
     * Find devices by installation location
     *
     * @param locationId location ID
     * @return list of devices at location
     */
    @Query("SELECT d FROM Device d WHERE d.installation.location.id = :locationId ORDER BY d.name")
    List<Device> findByInstallationLocationId(@Param("locationId") UUID locationId);

    /**
     * Count devices by type
     *
     * @param deviceType device type class
     * @return device count
     */
    @Query("SELECT COUNT(d) FROM Device d WHERE TYPE(d) = :deviceType")
    long countByDeviceType(@Param("deviceType") Class<? extends Device> deviceType);

    /**
     * Count devices by manufacturer
     *
     * @param manufacturer manufacturer name
     * @return device count
     */
    long countByManufacturer(String manufacturer);
}
