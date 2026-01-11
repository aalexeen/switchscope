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
        return repository.findAllWithRelationships();
    }

    @Override
    public Installation getById(UUID id) {
        return repository.findByIdWithRelationships(id)
                .orElseThrow(() -> new IllegalArgumentException("Installation not found with id: " + id));
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
}
