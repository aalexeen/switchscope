package net.switchscope.service.component.connectivity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.repository.component.connectivity.ConnectivityRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CableRunService implements CrudService<CableRun> {

    private final ConnectivityRepository repository;

    @Override
    @SuppressWarnings("unchecked")
    public List<CableRun> getAll() {
        return (List<CableRun>) (List<?>) repository.findCableRuns();
    }

    @Override
    public CableRun getById(UUID id) {
        return (CableRun) repository.getExisted(id);
    }

    @Override
    @Transactional
    public CableRun create(CableRun entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public CableRun update(UUID id, CableRun entity) {
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
