package net.switchscope.service.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.model.component.ComponentCategoryEntity;
import net.switchscope.repository.component.ComponentCategoryRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentCategoryService implements CrudService<ComponentCategoryEntity> {

    private final ComponentCategoryRepository repository;

    @Override
    public List<ComponentCategoryEntity> getAll() {
        return repository.findAllWithAssociations();
    }

    @Override
    public ComponentCategoryEntity getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public ComponentCategoryEntity create(ComponentCategoryEntity entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public ComponentCategoryEntity update(UUID id, ComponentCategoryEntity entity) {
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
