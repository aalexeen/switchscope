package net.switchscope.service.component.device;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.model.component.device.Router;
import net.switchscope.repository.component.device.DeviceRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouterService {

    private final DeviceRepository repository;

    public List<Router> getAll() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Router getById(UUID id) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public Router create(Router entity) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public Router update(UUID id, Router entity) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public void delete(UUID id) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

