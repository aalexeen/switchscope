package net.switchscope.service.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.repository.component.ComponentStatusRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentStatusService implements CrudService<ComponentStatusEntity> {

    private final ComponentStatusRepository repository;

    @Override
    public List<ComponentStatusEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public ComponentStatusEntity getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public ComponentStatusEntity create(ComponentStatusEntity entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public ComponentStatusEntity update(UUID id, ComponentStatusEntity entity) {
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
