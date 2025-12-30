package net.switchscope.service.location;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.model.location.catalog.LocationTypeEntity;
import net.switchscope.repository.location.LocationTypeRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationTypeService {

    private final LocationTypeRepository repository;

    public List<LocationTypeEntity> getAll() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public LocationTypeEntity getById(UUID id) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public LocationTypeEntity create(LocationTypeEntity entity) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public LocationTypeEntity update(UUID id, LocationTypeEntity entity) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public void delete(UUID id) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

