package net.switchscope.service.location;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.model.location.Location;
import net.switchscope.repository.location.LocationRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository repository;

    public List<Location> getAll() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Location getById(UUID id) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public Location create(Location entity) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public Location update(UUID id, Location entity) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public void delete(UUID id) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

