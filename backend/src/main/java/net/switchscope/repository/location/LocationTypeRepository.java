package net.switchscope.repository.location;

import net.switchscope.model.location.catalog.LocationTypeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for LocationTypeEntity catalog
 */
@Repository
public interface LocationTypeRepository extends net.switchscope.repository.component.ComponentBaseRepository<LocationTypeEntity> {

    /**
     * Find types that can have children
     *
     * @return list of types that can have children
     */
    @Query("SELECT lte FROM LocationTypeEntity lte WHERE lte.canHaveChildren = true AND lte.active = true ORDER BY lte.hierarchyLevel, lte.displayName")
    List<LocationTypeEntity> findTypesWithChildren();

    /**
     * Find types that can hold equipment
     *
     * @return list of types that can hold equipment
     */
    @Query("SELECT lte FROM LocationTypeEntity lte WHERE lte.canHoldEquipment = true AND lte.active = true ORDER BY lte.hierarchyLevel, lte.displayName")
    List<LocationTypeEntity> findEquipmentHoldableTypes();

    /**
     * Find physical location types
     *
     * @return list of physical location types
     */
    @Query("SELECT lte FROM LocationTypeEntity lte WHERE lte.physicalLocation = true AND lte.active = true ORDER BY lte.hierarchyLevel, lte.displayName")
    List<LocationTypeEntity> findPhysicalTypes();

    /**
     * Find rack-like types
     *
     * @return list of rack-like types
     */
    @Query("SELECT lte FROM LocationTypeEntity lte WHERE lte.rackLike = true AND lte.active = true ORDER BY lte.hierarchyLevel, lte.displayName")
    List<LocationTypeEntity> findRackLikeTypes();

    /**
     * Find room-like types
     *
     * @return list of room-like types
     */
    @Query("SELECT lte FROM LocationTypeEntity lte WHERE lte.roomLike = true AND lte.active = true ORDER BY lte.hierarchyLevel, lte.displayName")
    List<LocationTypeEntity> findRoomLikeTypes();

    /**
     * Find building-like types
     *
     * @return list of building-like types
     */
    @Query("SELECT lte FROM LocationTypeEntity lte WHERE lte.buildingLike = true AND lte.active = true ORDER BY lte.hierarchyLevel, lte.displayName")
    List<LocationTypeEntity> findBuildingLikeTypes();

    /**
     * Find types by hierarchy level
     *
     * @param hierarchyLevel hierarchy level
     * @return list of types
     */
    @Query("SELECT lte FROM LocationTypeEntity lte WHERE lte.hierarchyLevel = :hierarchyLevel AND lte.active = true ORDER BY lte.displayName")
    List<LocationTypeEntity> findByHierarchyLevel(@Param("hierarchyLevel") Integer hierarchyLevel);

    /**
     * Find types ordered by hierarchy level
     *
     * @return list of types ordered by hierarchy level
     */
    @Query("SELECT lte FROM LocationTypeEntity lte WHERE lte.active = true ORDER BY lte.hierarchyLevel, lte.displayName")
    List<LocationTypeEntity> findAllActiveOrderedByHierarchy();
}

