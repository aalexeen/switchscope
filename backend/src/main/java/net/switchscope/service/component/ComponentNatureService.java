package net.switchscope.service.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.mapper.component.catalog.ComponentNatureMapper;
import net.switchscope.model.component.ComponentNatureEntity;
import net.switchscope.repository.component.ComponentNatureRepository;
import net.switchscope.service.UpdatableCrudService;
import net.switchscope.to.component.catalog.ComponentNatureTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentNatureService implements UpdatableCrudService<ComponentNatureEntity, ComponentNatureTo> {

    private final ComponentNatureRepository repository;
    private final ComponentNatureMapper mapper;

    @Override
    public List<ComponentNatureEntity> getAll() {
        return repository.findAllWithAssociations();
    }

    @Override
    public ComponentNatureEntity getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public ComponentNatureEntity create(ComponentNatureEntity entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public ComponentNatureEntity update(UUID id, ComponentNatureEntity entity) {
        repository.getExisted(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public ComponentNatureEntity updateFromDto(UUID id, ComponentNatureTo dto) {
        ComponentNatureEntity existing = repository.getExisted(id);
        mapper.updateFromTo(existing, dto);
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
