package net.switchscope.service.location;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        return repository.findAllWithAssociations();
    }

    @Override
    public LocationTypeEntity getById(UUID id) {
        return repository.getExisted(id);
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
