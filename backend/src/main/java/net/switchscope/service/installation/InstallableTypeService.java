package net.switchscope.service.installation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.mapper.installation.catalog.InstallableTypeMapper;
import net.switchscope.model.installation.catalog.InstallableTypeEntity;
import net.switchscope.repository.installation.InstallableTypeRepository;
import net.switchscope.service.UpdatableCrudService;
import net.switchscope.service.component.InstallableComponentRegistry;
import net.switchscope.to.installation.catalog.InstallableTypeTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstallableTypeService implements UpdatableCrudService<InstallableTypeEntity, InstallableTypeTo> {

    private final InstallableTypeRepository repository;
    private final InstallableTypeMapper mapper;
    private final InstallableComponentRegistry registry;

    @Override
    public List<InstallableTypeEntity> getAll() {
        List<InstallableTypeEntity> entities = repository.findAllWithAssociations();
        entities.forEach(entity -> entity.setRegistry(registry));
        return entities;
    }

    @Override
    public InstallableTypeEntity getById(UUID id) {
        InstallableTypeEntity entity = repository.getExisted(id);
        entity.setRegistry(registry);
        return entity;
    }

    @Override
    @Transactional
    public InstallableTypeEntity create(InstallableTypeEntity entity) {
        // TODO: implement validation
        InstallableTypeEntity saved = repository.save(entity);
        saved.setRegistry(registry);
        return saved;
    }

    @Override
    @Transactional
    public InstallableTypeEntity update(UUID id, InstallableTypeEntity entity) {
        repository.getExisted(id);
        entity.setId(id);
        InstallableTypeEntity saved = repository.save(entity);
        saved.setRegistry(registry);
        return saved;
    }

    @Override
    @Transactional
    public InstallableTypeEntity updateFromDto(UUID id, InstallableTypeTo dto) {
        InstallableTypeEntity existing = repository.getExisted(id);
        mapper.updateFromTo(existing, dto);
        InstallableTypeEntity saved = repository.save(existing);
        saved.setRegistry(registry);
        return saved;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
