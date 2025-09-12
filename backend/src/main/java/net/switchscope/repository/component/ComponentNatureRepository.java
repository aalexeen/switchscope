package net.switchscope.repository.component;

import net.switchscope.model.component.ComponentNatureEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Repository for ComponentNatureEntity
 */
@Repository
public interface ComponentNatureRepository extends ComponentCatalogRepository<ComponentNatureEntity> {

    /**
     * Find natures requiring management
     *
     * @return list of natures that require management
     */
    @Query("SELECT cn FROM ComponentNatureEntity cn WHERE cn.requiresManagement = true AND cn.active = true ORDER BY cn.sortOrder")
    List<ComponentNatureEntity> findManagementRequiredNatures();

    /**
     * Find IP capable natures
     *
     * @return list of natures that can have IP addresses
     */
    @Query("SELECT cn FROM ComponentNatureEntity cn WHERE cn.canHaveIpAddress = true AND cn.active = true ORDER BY cn.sortOrder")
    List<ComponentNatureEntity> findIpCapableNatures();

    /**
     * Find firmware capable natures
     *
     * @return list of natures that have firmware
     */
    @Query("SELECT cn FROM ComponentNatureEntity cn WHERE cn.hasFirmware = true AND cn.active = true ORDER BY cn.sortOrder")
    List<ComponentNatureEntity> findFirmwareCapableNatures();

    /**
     * Find network processing natures
     *
     * @return list of natures that process network traffic
     */
    @Query("SELECT cn FROM ComponentNatureEntity cn WHERE cn.processesNetworkTraffic = true AND cn.active = true ORDER BY cn.sortOrder")
    List<ComponentNatureEntity> findNetworkProcessingNatures();

    /**
     * Find SNMP capable natures
     *
     * @return list of natures that support SNMP monitoring
     */
    @Query("SELECT cn FROM ComponentNatureEntity cn WHERE cn.supportsSnmpMonitoring = true AND cn.active = true ORDER BY cn.sortOrder")
    List<ComponentNatureEntity> findSnmpCapableNatures();

    /**
     * Find natures by power consumption category
     *
     * @param category power consumption category
     * @return list of natures with specified power consumption
     */
    @Query("SELECT cn FROM ComponentNatureEntity cn WHERE cn.powerConsumptionCategory = :category AND cn.active = true ORDER BY cn.sortOrder")
    List<ComponentNatureEntity> findByPowerConsumptionCategory(@Param("category") String category);

    /**
     * Find natures by property key
     *
     * @param propertyKey property key
     * @return list of natures having the property
     */
    @Query("SELECT cn FROM ComponentNatureEntity cn JOIN cn.properties p WHERE KEY(p) = :propertyKey AND cn.active = true ORDER BY cn.sortOrder")
    List<ComponentNatureEntity> findByPropertyKey(@Param("propertyKey") String propertyKey);

    /**
     * Find natures by property value
     *
     * @param key   property key
     * @param value property value
     * @return list of natures with matching property
     */
    @Query("SELECT cn FROM ComponentNatureEntity cn JOIN cn.properties p WHERE KEY(p) = :key AND VALUE(p) = :value AND cn.active = true ORDER BY cn.sortOrder")
    List<ComponentNatureEntity> findByPropertyValue(@Param("key") String key, @Param("value") String value);
}
