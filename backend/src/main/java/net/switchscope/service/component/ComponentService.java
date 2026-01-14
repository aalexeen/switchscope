package net.switchscope.service.component;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.Component;
import net.switchscope.repository.component.ComponentRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentService implements CrudService<Component> {

    private final ComponentRepository repository;

    @Override
    public List<Component> getAll() {
        return repository.findAllWithAssociations();
    }

    @Override
    public Component getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public Component create(Component entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Component update(UUID id, Component entity) {
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
