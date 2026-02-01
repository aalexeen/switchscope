package net.switchscope.service.location;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.error.NotFoundException;
import net.switchscope.mapper.location.catalog.LocationTypeMapper;
import net.switchscope.model.location.catalog.LocationTypeEntity;
import net.switchscope.repository.location.LocationTypeRepository;
import net.switchscope.service.UpdatableCrudService;
import net.switchscope.to.location.catalog.LocationTypeTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationTypeService implements UpdatableCrudService<LocationTypeEntity, LocationTypeTo> {

    private final LocationTypeRepository repository;
    private final LocationTypeMapper mapper;

    @Override
    public List<LocationTypeEntity> getAll() {
        return repository.findAllWithChildTypes();
    }

    @Override
    public LocationTypeEntity getById(UUID id) {
        return repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new NotFoundException("Location type with id=" + id + " not found"));
    }

    /**
     * Get all location types and map to DTOs within transaction.
     * Initializes allowedParentTypes for each entity to avoid LazyInitializationException.
     *
     * @return list of location type DTOs
     */
    public List<LocationTypeTo> getAllAsDto() {
        List<LocationTypeEntity> entities = repository.findAllWithChildTypes();
        // Initialize allowedParentTypes (not fetched in findAllWithChildTypes to avoid Cartesian product)
        entities.forEach(e -> Hibernate.initialize(e.getAllowedParentTypes()));
        return mapper.toToList(entities);
    }

    /**
     * Get location type by ID and map to DTO within transaction.
     *
     * @param id location type ID
     * @return location type DTO
     */
    public LocationTypeTo getByIdAsDto(UUID id) {
        LocationTypeEntity entity = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new NotFoundException("Location type with id=" + id + " not found"));
        return mapper.toTo(entity);
    }

    /**
     * Create location type and return as DTO within transaction.
     *
     * @param entity location type entity to create
     * @return created location type as DTO
     */
    @Transactional
    public LocationTypeTo createAndReturnDto(LocationTypeEntity entity) {
        LocationTypeEntity saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    @Override
    @Transactional
    public LocationTypeEntity create(LocationTypeEntity entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public LocationTypeEntity update(UUID id, LocationTypeEntity entity) {
        repository.getExisted(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public LocationTypeEntity updateFromDto(UUID id, LocationTypeTo dto) {
        LocationTypeEntity existing = repository.getExisted(id);
        mapper.updateFromTo(existing, dto);
        return repository.save(existing);
    }

    /**
     * Update location type and return DTO (mapping within transaction to avoid LazyInitializationException).
     */
    @Transactional
    public LocationTypeTo updateAndMapToDto(UUID id, LocationTypeTo dto) {
        LocationTypeEntity existing = repository.getExisted(id);
        mapper.updateFromTo(existing, dto);
        LocationTypeEntity saved = repository.save(existing);
        return mapper.toTo(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
