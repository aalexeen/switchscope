package net.switchscope.repository.location;

import net.switchscope.model.location.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Location entities
 */
@Repository
public interface LocationRepository extends net.switchscope.repository.BaseRepository<Location> {

    /**
     * Find all locations with type and parent eagerly loaded.
     * Note: Does NOT fetch childLocations to avoid N+1 on collections.
     *
     * @return list of all locations with relationships
     */
    @Query("SELECT DISTINCT l FROM Location l " +
           "LEFT JOIN FETCH l.type " +
           "LEFT JOIN FETCH l.parentLocation " +
           "ORDER BY l.name")
    List<Location> findAllWithRelationships();

    /**
     * Find all locations with ALL relationships eagerly loaded including childLocations.
     * Used for getAllAsDto() to avoid LazyInitializationException during DTO mapping.
     *
     * @return list of all locations with all relationships
     */
    @Query("SELECT DISTINCT l FROM Location l " +
           "LEFT JOIN FETCH l.type " +
           "LEFT JOIN FETCH l.parentLocation " +
           "LEFT JOIN FETCH l.childLocations " +
           "ORDER BY l.name")
    List<Location> findAllWithAllRelationships();

    /**
     * Find location by ID with all relationships eagerly loaded
     *
     * @param id location ID
     * @return optional location with relationships
     */
    @Query("SELECT l FROM Location l " +
           "LEFT JOIN FETCH l.type " +
           "LEFT JOIN FETCH l.parentLocation " +
           "WHERE l.id = :id")
    Optional<Location> findByIdWithRelationships(@Param("id") UUID id);

    /**
     * Find location by ID with ALL relationships eagerly loaded including childLocations.
     * Used for getByIdAsDto() to avoid LazyInitializationException during DTO mapping.
     *
     * @param id location ID
     * @return optional location with all relationships
     */
    @Query("SELECT l FROM Location l " +
           "LEFT JOIN FETCH l.type " +
           "LEFT JOIN FETCH l.parentLocation " +
           "LEFT JOIN FETCH l.childLocations " +
           "WHERE l.id = :id")
    Optional<Location> findByIdWithAllRelationships(@Param("id") UUID id);

    /**
     * Find location by name
     *
     * @param name location name
     * @return optional location
     */
    Optional<Location> findByName(String name);

    /**
     * Find locations by type
     *
     * @param locationTypeId location type ID
     * @return list of locations
     */
    List<Location> findByTypeId(UUID locationTypeId);

    /**
     * Find root locations (no parent)
     *
     * @return list of root locations
     */
    @Query("SELECT l FROM Location l WHERE l.parentLocation IS NULL ORDER BY l.name")
    List<Location> findRootLocations();

    /**
     * Find child locations
     *
     * @param parentLocationId parent location ID
     * @return list of child locations
     */
    @Query("SELECT l FROM Location l WHERE l.parentLocation.id = :parentLocationId ORDER BY l.name")
    List<Location> findByParentLocationId(@Param("parentLocationId") UUID parentLocationId);

    /**
     * Find locations by type code
     *
     * @param typeCode location type code
     * @return list of locations
     */
    @Query("SELECT l FROM Location l WHERE l.type.code = :typeCode ORDER BY l.name")
    List<Location> findByTypeCode(@Param("typeCode") String typeCode);

    /**
     * Find physical locations
     *
     * @return list of physical locations
     */
    @Query("SELECT l FROM Location l WHERE l.type.physicalLocation = true ORDER BY l.name")
    List<Location> findPhysicalLocations();

    /**
     * Find rack-like locations
     *
     * @return list of rack-like locations
     */
    @Query("SELECT l FROM Location l WHERE l.type.rackLike = true ORDER BY l.name")
    List<Location> findRackLikeLocations();

    /**
     * Find room-like locations
     *
     * @return list of room-like locations
     */
    @Query("SELECT l FROM Location l WHERE l.type.roomLike = true ORDER BY l.name")
    List<Location> findRoomLikeLocations();

    /**
     * Find building-like locations
     *
     * @return list of building-like locations
     */
    @Query("SELECT l FROM Location l WHERE l.type.buildingLike = true ORDER BY l.name")
    List<Location> findBuildingLikeLocations();

    /**
     * Find locations that can hold equipment
     *
     * @return list of locations that can hold equipment
     */
    @Query("SELECT l FROM Location l WHERE l.type.canHoldEquipment = true ORDER BY l.name")
    List<Location> findEquipmentHoldableLocations();

    /**
     * Find locations by name pattern (case-insensitive)
     *
     * @param pattern search pattern
     * @return list of matching locations
     */
    @Query("SELECT l FROM Location l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :pattern, '%')) ORDER BY l.name")
    List<Location> findByNameContainingIgnoreCase(@Param("pattern") String pattern);

    /**
     * Find locations by address pattern (case-insensitive)
     *
     * @param pattern search pattern
     * @return list of matching locations
     */
    @Query("SELECT l FROM Location l WHERE LOWER(l.address) LIKE LOWER(CONCAT('%', :pattern, '%')) ORDER BY l.name")
    List<Location> findByAddressContainingIgnoreCase(@Param("pattern") String pattern);

    /**
     * Count locations by type
     *
     * @param locationTypeId location type ID
     * @return location count
     */
    long countByTypeId(UUID locationTypeId);

    /**
     * Count child locations
     *
     * @param parentLocationId parent location ID
     * @return child count
     */
    @Query("SELECT COUNT(l) FROM Location l WHERE l.parentLocation.id = :parentLocationId")
    long countByParentLocationId(@Param("parentLocationId") UUID parentLocationId);
}

