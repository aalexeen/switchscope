package net.switchscope.service.installation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.mapper.installation.catalog.InstallableTypeMapper;
import net.switchscope.model.installation.catalog.InstallableTypeEntity;
import net.switchscope.repository.installation.InstallableTypeRepository;
import net.switchscope.service.DtoCrudService;
import net.switchscope.service.component.InstallableComponentRegistry;
import net.switchscope.to.installation.catalog.InstallableTypeTo;
import net.switchscope.web.payload.PartialUpdate;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstallableTypeService implements DtoCrudService<InstallableTypeEntity, InstallableTypeTo> {

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
    public InstallableTypeTo createFromDto(InstallableTypeTo dto) {
        InstallableTypeEntity saved = repository.save(mapper.toEntity(dto));
        // the registry is a collaborator rather than a column, and the DTO's derived fields are
        // read off it, so it has to be back on the entity before anything maps it
        saved.setRegistry(registry);
        return mapper.toTo(saved);
    }

    @Override
    @Transactional
    public InstallableTypeTo updateFromDto(UUID id, PartialUpdate<? extends InstallableTypeTo> update) {
        InstallableTypeEntity existing = repository.getExisted(id);
        mapper.updateFromTo(existing, update.dto());
        update.applyNulls(existing);
        InstallableTypeEntity saved = repository.save(existing);
        saved.setRegistry(registry);
        return mapper.toTo(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
