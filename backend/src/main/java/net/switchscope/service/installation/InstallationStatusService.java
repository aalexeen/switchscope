package net.switchscope.service.installation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.mapper.installation.catalog.InstallationStatusMapper;
import net.switchscope.model.installation.catalog.InstallationStatusEntity;
import net.switchscope.repository.installation.InstallationStatusRepository;
import net.switchscope.service.DtoCrudService;
import net.switchscope.to.installation.catalog.InstallationStatusTo;
import net.switchscope.web.payload.PartialUpdate;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstallationStatusService implements DtoCrudService<InstallationStatusEntity, InstallationStatusTo> {

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
    public InstallationStatusTo createFromDto(InstallationStatusTo dto) {
        return mapper.toTo(repository.save(mapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public InstallationStatusTo updateFromDto(UUID id, PartialUpdate<? extends InstallationStatusTo> update) {
        InstallationStatusEntity existing = repository.getExisted(id);
        mapper.updateFromTo(existing, update.dto());
        update.applyNulls(existing);
        return mapper.toTo(repository.save(existing));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
