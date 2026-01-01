package net.switchscope.service.location;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.location.Location;
import net.switchscope.repository.location.LocationRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService implements CrudService<Location> {

    private final LocationRepository repository;

    @Override
    public List<Location> getAll() {
        return repository.findAll();
    }

    @Override
    public Location getById(UUID id) {
        return repository.getExisted(id);
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
