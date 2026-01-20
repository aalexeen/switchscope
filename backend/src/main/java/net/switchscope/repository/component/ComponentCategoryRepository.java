package net.switchscope.repository.component;

import net.switchscope.model.component.ComponentCategoryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ComponentCategoryEntity
 */
@Repository
public interface ComponentCategoryRepository extends ComponentCatalogRepository<ComponentCategoryEntity> {

    /**
     * Find category by id with eager loading of componentTypes
     *
     * @param id category id
     * @return optional category with componentTypes loaded
     */
    @Query("SELECT cc FROM ComponentCategoryEntity cc LEFT JOIN FETCH cc.componentTypes WHERE cc.id = :id")
    Optional<ComponentCategoryEntity> findByIdWithComponentTypes(@Param("id") UUID id);

    /**
     * Find all categories with eager loading of componentTypes
     *
     * @return list of categories with componentTypes loaded
     */
    @Override
    @Query("SELECT DISTINCT cc FROM ComponentCategoryEntity cc LEFT JOIN FETCH cc.componentTypes ORDER BY cc.sortOrder, cc.displayName")
    List<ComponentCategoryEntity> findAllWithAssociations();

    /**
     * Find infrastructure categories
     *
     * @return list of infrastructure categories
     */
    @Query("SELECT cc FROM ComponentCategoryEntity cc WHERE cc.infrastructure = true AND cc.active = true ORDER BY cc.sortOrder")
    List<ComponentCategoryEntity> findInfrastructureCategories();

    /**
     * Find system categories
     *
     * @return list of system categories
     */
    @Query("SELECT cc FROM ComponentCategoryEntity cc WHERE cc.systemCategory = true AND cc.active = true ORDER BY cc.sortOrder")
    List<ComponentCategoryEntity> findSystemCategories();

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