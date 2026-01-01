package net.switchscope.service.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentTypeService implements CrudService<ComponentTypeEntity> {

    private final ComponentTypeRepository repository;

    @Override
    public List<ComponentTypeEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public ComponentTypeEntity getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public ComponentTypeEntity create(ComponentTypeEntity entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public ComponentTypeEntity update(UUID id, ComponentTypeEntity entity) {
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
