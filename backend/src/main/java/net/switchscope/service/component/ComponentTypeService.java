package net.switchscope.service.component;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.repository.component.ComponentTypeRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentTypeService {

    private final ComponentTypeRepository repository;

    public List<ComponentTypeEntity> getAll() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ComponentTypeEntity getById(UUID id) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public ComponentTypeEntity create(ComponentTypeEntity entity) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public ComponentTypeEntity update(UUID id, ComponentTypeEntity entity) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    public void delete(UUID id) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
