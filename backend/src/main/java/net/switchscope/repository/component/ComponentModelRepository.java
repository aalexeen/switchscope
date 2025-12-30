package net.switchscope.repository.component;

import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ComponentModel catalog entries
 * Supports all model types through Single Table Inheritance
 * 
 * Note: ComponentModel extends NamedEntity (not BaseCodedEntity),
 * so it has 'active' but not 'code', 'sortOrder', 'displayName'
 */
@Repository
public interface ComponentModelRepository extends BaseRepository<ComponentModel> {

    /**
     * Find model by manufacturer and model number
     *
     * @param manufacturer manufacturer name
     * @param modelNumber model number
     * @return optional model
     */
    Optional<ComponentModel> findByManufacturerAndModelNumber(String manufacturer, String modelNumber);

    /**
     * Check if model exists by manufacturer and model number
     *
     * @param manufacturer manufacturer name
     * @param modelNumber model number
     * @return true if exists
     */
    boolean existsByManufacturerAndModelNumber(String manufacturer, String modelNumber);

    /**
     * Find models by manufacturer
     *
     * @param manufacturer manufacturer name
     * @return list of models
     */
    List<ComponentModel> findByManufacturer(String manufacturer);

    /**
     * Find active models by manufacturer
     *
     * @param manufacturer manufacturer name
     * @return list of active models
     */
    List<ComponentModel> findByManufacturerAndActiveTrue(String manufacturer);

    /**
     * Find all active models
     *
     * @return list of active models
     */
    List<ComponentModel> findByActiveTrue();

    /**
     * Find models by component type
     *
     * @param componentTypeId component type ID
     * @return list of models
     */
    @Query("SELECT cm FROM ComponentModel cm WHERE cm.componentType.id = :componentTypeId AND cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findByComponentTypeId(@Param("componentTypeId") UUID componentTypeId);

    /**
     * Find active models ordered
     *
     * @return list of active models ordered by manufacturer and model number
     */
    @Query("SELECT cm FROM ComponentModel cm WHERE cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findAllActiveOrdered();

    /**
     * Find currently supported models (active, not discontinued, not EOL)
     *
     * @return list of supported models
     */
    @Query("SELECT cm FROM ComponentModel cm WHERE cm.active = true AND cm.discontinued = false AND cm.endOfLife = false ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findCurrentlySupported();

    /**
     * Find models available for purchase
     *
     * @return list of available models
     */
    @Query("SELECT cm FROM ComponentModel cm WHERE cm.active = true AND cm.discontinued = false ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findAvailableForPurchase();

    /**
     * Find models by manufacturer pattern (case-insensitive)
     *
     * @param pattern search pattern
     * @return list of matching models
     */
    @Query("SELECT cm FROM ComponentModel cm WHERE LOWER(cm.manufacturer) LIKE LOWER(CONCAT('%', :pattern, '%')) AND cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findByManufacturerContainingIgnoreCase(@Param("pattern") String pattern);

    /**
     * Find models by model number pattern (case-insensitive)
     *
     * @param pattern search pattern
     * @return list of matching models
     */
    @Query("SELECT cm FROM ComponentModel cm WHERE LOWER(cm.modelNumber) LIKE LOWER(CONCAT('%', :pattern, '%')) AND cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findByModelNumberContainingIgnoreCase(@Param("pattern") String pattern);

    /**
     * Find models by name pattern (case-insensitive)
     *
     * @param pattern search pattern
     * @return list of matching models
     */
    @Query("SELECT cm FROM ComponentModel cm WHERE LOWER(cm.name) LIKE LOWER(CONCAT('%', :pattern, '%')) AND cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findByNameContainingIgnoreCase(@Param("pattern") String pattern);

    /**
     * Find models that need replacement (discontinued or EOL)
     *
     * @return list of models needing replacement
     */
    @Query("SELECT cm FROM ComponentModel cm WHERE cm.discontinued = true OR cm.endOfLife = true OR (cm.discontinueDate IS NOT NULL AND cm.discontinueDate < CURRENT_TIMESTAMP) ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findNeedingReplacement();

    /**
     * Find SwitchModel entries
     *
     * @return list of switch models
     */
    @Query("SELECT cm FROM SwitchModel cm WHERE cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findSwitchModels();

    /**
     * Find RouterModel entries
     *
     * @return list of router models
     */
    @Query("SELECT cm FROM RouterModel cm WHERE cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findRouterModels();

    /**
     * Find AccessPointModel entries
     *
     * @return list of access point models
     */
    @Query("SELECT cm FROM AccessPointModel cm WHERE cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findAccessPointModels();

    /**
     * Find RackModelEntity entries
     *
     * @return list of rack models
     */
    @Query("SELECT cm FROM RackModelEntity cm WHERE cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findRackModels();

    /**
     * Find PatchPanelModel entries
     *
     * @return list of patch panel models
     */
    @Query("SELECT cm FROM PatchPanelModel cm WHERE cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findPatchPanelModels();

    /**
     * Find CableRunModel entries
     *
     * @return list of cable run models
     */
    @Query("SELECT cm FROM CableRunModel cm WHERE cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findCableRunModels();

    /**
     * Find ConnectorModel entries
     *
     * @return list of connector models
     */
    @Query("SELECT cm FROM ConnectorModel cm WHERE cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findConnectorModels();

    /**
     * Find models by type discriminator
     *
     * @param modelType model type class
     * @return list of models
     */
    @Query("SELECT cm FROM ComponentModel cm WHERE TYPE(cm) = :modelType AND cm.active = true ORDER BY cm.manufacturer, cm.modelNumber")
    List<ComponentModel> findByModelType(@Param("modelType") Class<? extends ComponentModel> modelType);

    /**
     * Count active models
     *
     * @return count of active models
     */
    @Query("SELECT COUNT(cm) FROM ComponentModel cm WHERE cm.active = true")
    long countActive();

    /**
     * Count models by manufacturer
     *
     * @param manufacturer manufacturer name
     * @return model count
     */
    long countByManufacturer(String manufacturer);

    /**
     * Count models by type
     *
     * @param modelType model type class
     * @return model count
     */
    @Query("SELECT COUNT(cm) FROM ComponentModel cm WHERE TYPE(cm) = :modelType")
    long countByModelType(@Param("modelType") Class<? extends ComponentModel> modelType);
}
