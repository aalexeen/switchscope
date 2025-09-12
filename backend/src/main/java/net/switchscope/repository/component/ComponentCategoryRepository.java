package net.switchscope.repository.component;

import net.switchscope.model.component.ComponentCategoryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ComponentCategoryEntity
 */
@Repository
public interface ComponentCategoryRepository extends ComponentCatalogRepository<ComponentCategoryEntity> {

    /**
     * Find container categories (can contain other components)
     *
     * @return list of container categories
     */
    @Query("SELECT cc FROM ComponentCategoryEntity cc WHERE cc.canContainComponents = true AND cc.active = true ORDER BY cc.sortOrder")
    List<ComponentCategoryEntity> findContainerCategories();

    /**
     * Find infrastructure categories
     *
     * @return list of infrastructure categories
     */
    @Query("SELECT cc FROM ComponentCategoryEntity cc WHERE cc.infrastructure = true AND cc.active = true ORDER BY cc.sortOrder")
    List<ComponentCategoryEntity> findInfrastructureCategories();

    /**
     * Find power consuming categories
     *
     * @return list of categories that require power
     */
    @Query("SELECT cc FROM ComponentCategoryEntity cc WHERE cc.requiresPower = true AND cc.active = true ORDER BY cc.sortOrder")
    List<ComponentCategoryEntity> findPowerConsumingCategories();

    /**
     * Find categories that need cooling
     *
     * @return list of categories that need cooling
     */
    @Query("SELECT cc FROM ComponentCategoryEntity cc WHERE cc.needsCooling = true AND cc.active = true ORDER BY cc.sortOrder")
    List<ComponentCategoryEntity> findCoolingRequiredCategories();

    /**
     * Find categories that generate heat
     *
     * @return list of categories that generate heat
     */
    @Query("SELECT cc FROM ComponentCategoryEntity cc WHERE cc.generatesHeat = true AND cc.active = true ORDER BY cc.sortOrder")
    List<ComponentCategoryEntity> findHeatGeneratingCategories();

    /**
     * Find categories that require physical space
     *
     * @return list of categories requiring physical space
     */
    @Query("SELECT cc FROM ComponentCategoryEntity cc WHERE cc.requiresPhysicalSpace = true AND cc.active = true ORDER BY cc.sortOrder")
    List<ComponentCategoryEntity> findPhysicalSpaceRequiredCategories();

    /**
     * Find categories by property
     *
     * @param propertyKey property key
     * @return list of categories having the property
     */
    @Query("SELECT cc FROM ComponentCategoryEntity cc JOIN cc.properties p WHERE KEY(p) = :propertyKey AND cc.active = true")
    List<ComponentCategoryEntity> findByPropertyKey(@Param("propertyKey") String propertyKey);

    /**
     * Find categories by property value
     *
     * @param key   property key
     * @param value property value
     * @return list of categories with matching property
     */
    @Query("SELECT cc FROM ComponentCategoryEntity cc JOIN cc.properties p WHERE KEY(p) = :key AND VALUE(p) = :value AND cc.active = true")
    List<ComponentCategoryEntity> findByPropertyValue(@Param("key") String key, @Param("value") String value);
}