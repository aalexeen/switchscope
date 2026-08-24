package net.switchscope.service.location;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.error.NotFoundException;
import net.switchscope.mapper.location.LocationMapper;
import net.switchscope.model.location.Location;
import net.switchscope.model.location.catalog.LocationTypeEntity;
import net.switchscope.repository.location.LocationRepository;
import net.switchscope.repository.location.LocationTypeRepository;
import net.switchscope.service.DtoCrudService;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.to.location.LocationTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService implements DtoCrudService<Location, LocationTo> {

    private final LocationRepository repository;
    private final LocationTypeRepository locationTypeRepository;
    private final LocationMapper mapper;

    @Override
    public List<Location> getAll() {
        return repository.findAllWithRelationships();
    }

    @Override
    public Location getById(UUID id) {
        return repository.findByIdWithRelationships(id)
                .orElseThrow(() -> new NotFoundException("Location with id=" + id + " not found"));
    }

    /**
     * Get all locations and map to DTOs within transaction.
     * Uses findAllWithAllRelationships() to avoid LazyInitializationException.
     *
     * @return list of location DTOs
     */
    public List<LocationTo> getAllAsDto() {
        List<Location> locations = repository.findAllWithAllRelationships();
        return mapper.toToList(locations);
    }

    /**
     * Get location by ID and map to DTO within transaction.
     *
     * @param id location ID
     * @return location DTO
     */
    public LocationTo getByIdAsDto(UUID id) {
        Location location = repository.findByIdWithAllRelationships(id)
                .orElseThrow(() -> new NotFoundException("Location with id=" + id + " not found"));
        return mapper.toTo(location);
    }

    /**
     * Create a location from its DTO and return the result as a DTO, all within one transaction.
     * <p>
     * The mapper deliberately ignores {@code type} and {@code parentLocation} (they are ids in the
     * DTO, entities in the model), so this method resolves them before saving - otherwise
     * {@code location_type_id} would be null and the insert would violate its NOT NULL constraint.
     *
     * @param dto location to create
     * @return created location as DTO
     */
    @Override
    @Transactional
    public LocationTo createFromDto(LocationTo dto) {
        Location entity = mapper.toEntity(dto);
        applyReferences(entity, dto);
        return mapper.toTo(repository.save(entity));
    }

    /**
     * Update a location from its DTO and return the result as a DTO, all within one transaction.
     * <p>
     * Loads the managed entity first and applies the DTO onto it, so that associations and fields
     * the DTO does not carry are preserved. Merging a detached instance built by the mapper would
     * wipe them.
     *
     * @param id  location ID
     * @param dto location values to apply
     * @return updated location as DTO
     */
    @Override
    @Transactional
    public LocationTo updateFromDto(UUID id, PartialUpdate<? extends LocationTo> update) {
        LocationTo dto = update.dto();
        Location existing = repository.findByIdWithAllRelationships(id)
                .orElseThrow(() -> new NotFoundException("Location with id=" + id + " not found"));
        mapper.updateFromTo(existing, dto);
        applyReferences(existing, dto);
        update.applyNulls(existing);
        return mapper.toTo(repository.save(existing));
    }

    /**
     * Resolves the DTO's foreign-key ids into entity references.
     * {@code typeId} is mandatory on create and, when present, replaces the current type on update;
     * {@code parentLocationId} is optional and only applied when the DTO carries it.
     */
    private void applyReferences(Location entity, LocationTo dto) {
        if (dto.getTypeId() != null) {
            entity.setType(getLocationType(dto.getTypeId()));
        } else if (entity.getType() == null) {
            throw new IllegalRequestDataException("typeId is required to create a location");
        }

        if (dto.getParentLocationId() != null) {
            if (dto.getParentLocationId().equals(entity.getId())) {
                throw new IllegalRequestDataException("Location cannot be its own parent");
            }
            entity.setParentLocation(repository.findById(dto.getParentLocationId())
                    .orElseThrow(() -> new NotFoundException(
                            "Parent location with id=" + dto.getParentLocationId() + " not found")));
        }
    }

    private LocationTypeEntity getLocationType(UUID typeId) {
        return locationTypeRepository.findById(typeId)
                .orElseThrow(() -> new NotFoundException("Location type with id=" + typeId + " not found"));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
