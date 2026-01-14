package net.switchscope.service.installation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.model.installation.catalog.InstallableTypeEntity;
import net.switchscope.repository.installation.InstallableTypeRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstallableTypeService implements CrudService<InstallableTypeEntity> {

    private final InstallableTypeRepository repository;

    @Override
    public List<InstallableTypeEntity> getAll() {
        return repository.findAllWithAssociations();
    }

    @Override
    public InstallableTypeEntity getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public InstallableTypeEntity create(InstallableTypeEntity entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public InstallableTypeEntity update(UUID id, InstallableTypeEntity entity) {
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
