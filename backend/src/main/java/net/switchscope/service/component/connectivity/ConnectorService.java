package net.switchscope.service.component.connectivity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.connectivity.Connector;
import net.switchscope.repository.component.connectivity.ConnectivityRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConnectorService implements CrudService<Connector> {

    private final ConnectivityRepository repository;

    @Override
    @SuppressWarnings("unchecked")
    public List<Connector> getAll() {
        return (List<Connector>) (List<?>) repository.findConnectors();
    }

    @Override
    public Connector getById(UUID id) {
        return (Connector) repository.getExisted(id);
    }

    @Override
    @Transactional
    public Connector create(Connector entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Connector update(UUID id, Connector entity) {
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
