package net.switchscope.service.component.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.repository.component.ComponentModelRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentModelService implements CrudService<ComponentModel> {

    private final ComponentModelRepository repository;

    @Override
    public List<ComponentModel> getAll() {
        return repository.findAllWithAssociations();
    }

    @Override
    public ComponentModel getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public ComponentModel create(ComponentModel entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public ComponentModel update(UUID id, ComponentModel entity) {
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
