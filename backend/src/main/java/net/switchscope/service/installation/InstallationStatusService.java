package net.switchscope.service.installation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.mapper.installation.catalog.InstallationStatusMapper;
import net.switchscope.model.installation.catalog.InstallationStatusEntity;
import net.switchscope.repository.installation.InstallationStatusRepository;
import net.switchscope.service.UpdatableCrudService;
import net.switchscope.to.installation.catalog.InstallationStatusTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstallationStatusService implements UpdatableCrudService<InstallationStatusEntity, InstallationStatusTo> {

    private final InstallationStatusRepository repository;
    private final InstallationStatusMapper mapper;

    @Override
    public List<InstallationStatusEntity> getAll() {
        return repository.findAllWithAssociations();
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
    public InstallationStatusEntity updateFromDto(UUID id, InstallationStatusTo dto) {
        InstallationStatusEntity existing = repository.getExisted(id);
        mapper.updateFromTo(existing, dto);
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
