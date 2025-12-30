package net.switchscope.service.component.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.repository.component.ComponentModelRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentModelService {

    private final ComponentModelRepository repository;

    public List<ComponentModel> getAll() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ComponentModel getById(UUID id) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public ComponentModel create(ComponentModel entity) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public ComponentModel update(UUID id, ComponentModel entity) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public void delete(UUID id) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

