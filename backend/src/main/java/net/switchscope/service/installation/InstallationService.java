package net.switchscope.service.installation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.installation.Installation;
import net.switchscope.repository.installation.InstallationRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstallationService implements CrudService<Installation> {

    private final InstallationRepository repository;

    @Override
    public List<Installation> getAll() {
        List<Installation> installations = repository.findAllWithRelationships();
        // Initialize required associations while the transactional session is open
        installations.forEach(this::initializeForMapping);
        return installations;
    }

    @Override
    public Installation getById(UUID id) {
        Installation installation = repository.findByIdWithRelationships(id)
                .orElseThrow(() -> new IllegalArgumentException("Installation not found with id: " + id));
        initializeForMapping(installation);
        return installation;
    }

    @Override
    @Transactional
    public Installation create(Installation entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Installation update(UUID id, Installation entity) {
        repository.getExisted(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }

    /**
     * Touches lazily-loaded associations that are needed by mappers to avoid
     * LazyInitializationException after the transaction closes.
     */
    private void initializeForMapping(Installation installation) {
        if (installation == null) {
            return;
        }
        if (installation.getLocation() != null) {
            installation.getLocation().getFullPath();
        }
        if (installation.getComponent() != null) {
            installation.getComponent().getName();
        }
        if (installation.getInstalledItemType() != null) {
            installation.getInstalledItemType().getCode();
        }
        if (installation.getStatus() != null) {
            installation.getStatus().getCode();
        }
    }
}
