package net.switchscope.service.component.connectivity;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.connectivity.ConnectorMapper;
import net.switchscope.model.component.connectivity.Connector;
import net.switchscope.repository.component.connectivity.ConnectivityRepository;
import net.switchscope.model.component.catalog.connectiviy.ConnectorModel;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.repository.port.PortRepository;
import net.switchscope.service.component.ComponentReferenceResolver;
import net.switchscope.service.DtoCrudService;
import net.switchscope.to.component.connectivity.ConnectorTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConnectorService implements DtoCrudService<Connector, ConnectorTo> {

    private final ConnectivityRepository repository;
    private final ConnectorMapper mapper;
    private final ComponentReferenceResolver resolver;
    private final PortRepository portRepository;

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
     * Create a connector from its DTO, resolving foreign-key ids into managed references first.
     * Mapping back happens inside the transaction so lazy associations are still reachable.
     */
    @Override
    @Transactional
    public ConnectorTo createFromDto(ConnectorTo dto) {
        Connector entity = mapper.toEntity(dto);
        applyReferences(entity, dto);
        return mapper.toTo(repository.save(entity));
    }

    /**
     * Apply the DTO onto the stored connector.
     * The entity is loaded first: merging the detached instance produced by the mapper would null
     * every association the mapper ignores, starting with the NOT NULL component type and status.
     */
    @Override
    @Transactional
    public ConnectorTo updateFromDto(UUID id, ConnectorTo dto) {
        Connector existing = getById(id);
        mapper.updateFromTo(existing, dto);
        applyReferences(existing, dto);
        return mapper.toTo(repository.save(existing));
    }

    private void applyReferences(Connector entity, ConnectorTo dto) {
        resolver.applyCommonReferences(entity, dto);
        resolver.applyModelReference(dto.getConnectorModelId(), ConnectorModel.class,
                entity::setConnectorModel, "connectorModelId");
        resolver.applyComponentReference(dto.getCableRunId(), CableRun.class,
                entity::setCableRun, "cableRunId");
        resolver.applyReference(dto.getPortId(), portRepository::findById,
                entity::setPort, "portId");
    }

    /**
     * @deprecated entity-level create cannot resolve the DTO's foreign keys; use
     * {@link #createFromDto}. Kept only to satisfy {@code CrudService}.
     */
    @Override
    @Deprecated
    public Connector create(Connector entity) {
        throw new UnsupportedOperationException("Use createFromDto(dto)");
    }

    /**
     * @deprecated saving the detached entity built by the mapper merges nulls over every
     * association the mapper ignores; use {@link #updateFromDto}. Kept only to satisfy
     * {@code CrudService}.
     */
    @Override
    @Deprecated
    public Connector update(UUID id, Connector entity) {
        throw new UnsupportedOperationException("Use updateFromDto(id, dto)");
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
