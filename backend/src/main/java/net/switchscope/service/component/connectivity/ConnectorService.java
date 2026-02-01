package net.switchscope.service.component.connectivity;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.connectivity.ConnectorMapper;
import net.switchscope.model.component.connectivity.Connector;
import net.switchscope.repository.component.connectivity.ConnectivityRepository;
import net.switchscope.service.CrudService;
import net.switchscope.to.component.connectivity.ConnectorTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConnectorService implements CrudService<Connector> {

    private final ConnectivityRepository repository;
    private final ConnectorMapper mapper;

    @Override
    @SuppressWarnings("unchecked")
    public List<Connector> getAll() {
        return (List<Connector>) (List<?>) repository.findConnectors();
    }

    @Override
    public Connector getById(UUID id) {
        Connector connector = (Connector) repository.getExisted(id);
        Hibernate.initialize(connector.getConnectorModel());
        Hibernate.initialize(connector.getCableRun());
        Hibernate.initialize(connector.getPort());
        return connector;
    }

    /**
     * Get all connectors and map to DTOs within transaction.
     *
     * @return list of connector DTOs
     */
    @SuppressWarnings("unchecked")
    public List<ConnectorTo> getAllAsDto() {
        List<Connector> connectors = (List<Connector>) (List<?>) repository.findConnectors();
        return mapper.toToList(connectors);
    }

    /**
     * Get connector by ID and map to DTO within transaction.
     *
     * @param id connector ID
     * @return connector DTO
     */
    public ConnectorTo getByIdAsDto(UUID id) {
        Connector connector = (Connector) repository.getExisted(id);
        Hibernate.initialize(connector.getConnectorModel());
        Hibernate.initialize(connector.getCableRun());
        Hibernate.initialize(connector.getPort());
        return mapper.toTo(connector);
    }

    /**
     * Create connector and return as DTO within transaction.
     *
     * @param entity connector entity to create
     * @return created connector as DTO
     */
    @Transactional
    public ConnectorTo createAndReturnDto(Connector entity) {
        Connector saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    /**
     * Update connector and return as DTO within transaction.
     *
     * @param id connector ID
     * @param entity connector entity with updates
     * @return updated connector as DTO
     */
    @Transactional
    public ConnectorTo updateAndReturnDto(UUID id, Connector entity) {
        repository.getExisted(id);
        entity.setId(id);
        Connector saved = repository.save(entity);
        return mapper.toTo(saved);
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
