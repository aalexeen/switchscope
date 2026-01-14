
package net.switchscope.repository.component;

import net.switchscope.model.component.ComponentCategoryEntity;
import net.switchscope.model.component.ComponentTypeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ComponentTypeEntity
 */
@Repository
public interface ComponentTypeRepository extends ComponentCatalogRepository<ComponentTypeEntity> {

    /**
     * Find by code and category code
     *
     * @param code         type code
     * @param categoryCode category code
     * @return optional type entity
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.code = :code AND ct.category.code = :categoryCode")
    Optional<ComponentTypeEntity> findByCodeAndCategoryCode(@Param("code") String code, @Param("categoryCode") String categoryCode);

    /**
     * Find by category entity
     *
     * @param category category entity
     * @return list of types in category
     */
    List<ComponentTypeEntity> findByCategory(ComponentCategoryEntity category);

    /**
     * Find active types by category
     *
     * @param category category entity
     * @return list of active types in category
     */
    List<ComponentTypeEntity> findByCategoryAndActiveTrue(ComponentCategoryEntity category);

    /**
     * Find by category code
     *
     * @param categoryCode category code
     * @return list of types in category
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.category.code = :categoryCode")
    List<ComponentTypeEntity> findByCategoryCode(@Param("categoryCode") String categoryCode);

    /**
     * Find active types by category code
     *
     * @param categoryCode category code
     * @return list of active types in category
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.category.code = :categoryCode AND ct.active = true")
    List<ComponentTypeEntity> findByCategoryCodeAndActiveTrue(@Param("categoryCode") String categoryCode);

    /**
     * Find active types by category code ordered
     *
     * @param categoryCode category code
     * @return ordered list of active types in category
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.category.code = :categoryCode AND ct.active = true ORDER BY ct.sortOrder, ct.displayName")
    List<ComponentTypeEntity> findByCategoryCodeActiveOrdered(@Param("categoryCode") String categoryCode);

    /**
     * Find all active types ordered by category and type
     *
     * @return ordered list of all active types
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.active = true ORDER BY ct.category.sortOrder, ct.sortOrder, ct.displayName")
    List<ComponentTypeEntity> findAllActiveOrderedByCategory();

    /**
     * Find rack mountable types
     *
     * @return list of types that require rack space
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.requiresRackSpace = true AND ct.active = true ORDER BY ct.typicalRackUnits")
    List<ComponentTypeEntity> findRackMountableTypes();

    /**
     * Find container types
     *
     * @return list of types that can contain other components
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.canContainComponents = true AND ct.active = true ORDER BY ct.category.sortOrder, ct.sortOrder")
    List<ComponentTypeEntity> findContainerTypes();

    /**
     * Find managed types
     *
     * @return list of types that require management
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.requiresManagement = true AND ct.active = true ORDER BY ct.category.sortOrder, ct.sortOrder")
    List<ComponentTypeEntity> findManagedTypes();

    /**
     * Find network equipment types
     *
     * @return list of types that process network traffic
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.processesNetworkTraffic = true AND ct.active = true ORDER BY ct.sortOrder")
    List<ComponentTypeEntity> findNetworkEquipmentTypes();

    /**
     * Find power consuming types
     *
     * @return list of types that require power
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.requiresPower = true AND ct.active = true ORDER BY ct.typicalPowerConsumptionWatts DESC")
    List<ComponentTypeEntity> findPowerConsumingTypes();

    /**
     * Find cooling required types
     *
     * @return list of types that need cooling
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.needsCooling = true AND ct.active = true ORDER BY ct.sortOrder")
    List<ComponentTypeEntity> findCoolingRequiredTypes();

    /**
     * Find types by rack units
     *
     * @param rackUnits number of rack units
     * @return list of types with specified rack units
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.typicalRackUnits = :rackUnits AND ct.active = true ORDER BY ct.sortOrder")
    List<ComponentTypeEntity> findByRackUnits(@Param("rackUnits") Integer rackUnits);

    /**
     * Find types by power consumption range
     *
     * @param minWatts minimum power consumption
     * @param maxWatts maximum power consumption
     * @return list of types within power range
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE ct.typicalPowerConsumptionWatts BETWEEN :minWatts AND :maxWatts AND ct.active = true ORDER BY ct.typicalPowerConsumptionWatts")
    List<ComponentTypeEntity> findByPowerConsumptionRange(@Param("minWatts") Integer minWatts, @Param("maxWatts") Integer maxWatts);

    /**
     * Find types by property key
     *
     * @param propertyKey property key
     * @return list of types having the property
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct JOIN ct.properties p WHERE KEY(p) = :propertyKey AND ct.active = true ORDER BY ct.sortOrder")
    List<ComponentTypeEntity> findByPropertyKey(@Param("propertyKey") String propertyKey);

    /**
     * Find types by property value
     *
     * @param key   property key
     * @param value property value
     * @return list of types with matching property
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct JOIN ct.properties p WHERE KEY(p) = :key AND VALUE(p) = :value AND ct.active = true ORDER BY ct.sortOrder")
    List<ComponentTypeEntity> findByPropertyValue(@Param("key") String key, @Param("value") String value);

    /**
     * Find types allowing specific child type
     *
     * @param childTypeCode child type code
     * @return list of parent types that can contain the child type
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE :childTypeCode MEMBER OF ct.allowedChildTypeCodes AND ct.active = true ORDER BY ct.sortOrder")
    List<ComponentTypeEntity> findTypesAllowingChildType(@Param("childTypeCode") String childTypeCode);

    /**
     * Find types allowing specific child category
     *
     * @param childCategoryCode child category code
     * @return list of parent types that can contain the child category
     */
    @Query("SELECT ct FROM ComponentTypeEntity ct WHERE :childCategoryCode MEMBER OF ct.allowedChildCategoryCodes AND ct.active = true ORDER BY ct.sortOrder")
    List<ComponentTypeEntity> findTypesAllowingChildCategory(@Param("childCategoryCode") String childCategoryCode);

    /**
     * Check if type exists by code and category
     *
     * @param code     type code
     * @param category category entity
     * @return true if exists
     */
    boolean existsByCodeAndCategory(String code, ComponentCategoryEntity category);

    /**
     * Count active types by category entity
     *
     * @param category category entity
     * @return count of active types
     */
    @Query("SELECT COUNT(ct) FROM ComponentTypeEntity ct WHERE ct.category = :category AND ct.active = true")
    long countActiveByCategoryEntity(@Param("category") ComponentCategoryEntity category);

    /**
     * Count active types by category code
     *
     * @param categoryCode category code
     * @return count of active types
     */
    @Query("SELECT COUNT(ct) FROM ComponentTypeEntity ct WHERE ct.category.code = :categoryCode AND ct.active = true")
    long countActiveByCategoryCode(@Param("categoryCode") String categoryCode);

    /**
     * Find all component types with eager loading of category association
     *
     * @return list of all component types with category loaded
     */
    @Override
    @Query("SELECT DISTINCT ct FROM ComponentTypeEntity ct " +
           "LEFT JOIN FETCH ct.category " +
           "ORDER BY ct.sortOrder, ct.displayName")
    List<ComponentTypeEntity> findAllWithAssociations();
}
