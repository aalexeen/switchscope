package net.switchscope.service.installation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.model.installation.catalog.InstallationStatusEntity;
import net.switchscope.repository.installation.InstallationStatusRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstallationStatusService implements CrudService<InstallationStatusEntity> {

    private final InstallationStatusRepository repository;

    @Override
    public List<InstallationStatusEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public InstallationStatusEntity getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public InstallationStatusEntity create(InstallationStatusEntity entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public InstallationStatusEntity update(UUID id, InstallationStatusEntity entity) {
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
