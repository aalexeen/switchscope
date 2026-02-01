package net.switchscope.service.location;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.error.NotFoundException;
import net.switchscope.mapper.location.LocationMapper;
import net.switchscope.model.location.Location;
import net.switchscope.repository.location.LocationRepository;
import net.switchscope.service.CrudService;
import net.switchscope.to.location.LocationTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService implements CrudService<Location> {

    private final LocationRepository repository;
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
     * Create location and return as DTO within transaction.
     *
     * @param entity location entity to create
     * @return created location as DTO
     */
    @Transactional
    public LocationTo createAndReturnDto(Location entity) {
        Location saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    /**
     * Update location and return as DTO within transaction.
     *
     * @param id location ID
     * @param entity location entity with updates
     * @return updated location as DTO
     */
    @Transactional
    public LocationTo updateAndReturnDto(UUID id, Location entity) {
        repository.getExisted(id);
        entity.setId(id);
        Location saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    @Override
    @Transactional
    public Location create(Location entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Location update(UUID id, Location entity) {
        repository.getExisted(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
