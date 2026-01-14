package net.switchscope.service.location;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.model.location.catalog.LocationTypeEntity;
import net.switchscope.repository.location.LocationTypeRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationTypeService implements CrudService<LocationTypeEntity> {

    private final LocationTypeRepository repository;

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
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
