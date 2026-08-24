package net.switchscope.service.component.connectivity;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.connectivity.CableRunMapper;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.repository.component.connectivity.ConnectivityRepository;
import net.switchscope.model.component.catalog.connectiviy.CableRunModel;
import net.switchscope.repository.location.LocationRepository;
import net.switchscope.service.component.ComponentReferenceResolver;
import net.switchscope.service.DtoCrudService;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.to.component.connectivity.CableRunTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CableRunService implements DtoCrudService<CableRun, CableRunTo> {

    private final ConnectivityRepository repository;
    private final CableRunMapper mapper;
    private final ComponentReferenceResolver resolver;
    private final LocationRepository locationRepository;

    @Override
    @SuppressWarnings("unchecked")
    public List<CableRun> getAll() {
        return (List<CableRun>) (List<?>) repository.findCableRuns();
    }

    @Override
    public CableRun getById(UUID id) {
        CableRun cableRun = repository.getExisted(id, CableRun.class);
        initializeLazyCollections(cableRun);
        return cableRun;
    }

    /**
     * Get all cable runs and map to DTOs within transaction.
     *
     * @return list of cable run DTOs
     */
    @SuppressWarnings("unchecked")
    public List<CableRunTo> getAllAsDto() {
        List<CableRun> cableRuns = (List<CableRun>) (List<?>) repository.findCableRuns();
        // Initialize lazy collections for mapping
        cableRuns.forEach(this::initializeLazyCollections);
        return mapper.toToList(cableRuns);
    }

    /**
     * Get cable run by ID and map to DTO within transaction.
     *
     * @param id cable run ID
     * @return cable run DTO
     */
    public CableRunTo getByIdAsDto(UUID id) {
        CableRun cableRun = repository.getExisted(id, CableRun.class);
        initializeLazyCollections(cableRun);
        return mapper.toTo(cableRun);
    }



    private void initializeLazyCollections(CableRun cableRun) {
        Hibernate.initialize(cableRun.getLocations());
        Hibernate.initialize(cableRun.getConnectors());
    }

    /**
     * Create a cable run from its DTO, resolving foreign-key ids into managed references first.
     * Mapping back happens inside the transaction so lazy associations are still reachable.
     */
    @Override
    @Transactional
    public CableRunTo createFromDto(CableRunTo dto) {
        CableRun entity = mapper.toEntity(dto);
        applyReferences(entity, dto);
        return mapper.toTo(repository.save(entity));
    }

    /**
     * Apply the DTO onto the stored cable run.
     * The entity is loaded first: merging the detached instance produced by the mapper would null
     * every association the mapper ignores, starting with the NOT NULL component type and status.
     */
    @Override
    @Transactional
    public CableRunTo updateFromDto(UUID id, PartialUpdate<CableRunTo> update) {
        CableRunTo dto = update.dto();
        CableRun existing = getById(id);
        mapper.updateFromTo(existing, dto);
        applyReferences(existing, dto);
        update.applyNulls(existing);
        return mapper.toTo(repository.save(existing));
    }

    private void applyReferences(CableRun entity, CableRunTo dto) {
        resolver.applyCommonReferences(entity, dto);
        resolver.applyModelReference(dto.getCableModelId(), CableRunModel.class,
                entity::setCableModel, "cableModelId");
        resolver.applyReference(dto.getStartLocationId(), locationRepository::findById,
                entity::setStartLocation, "startLocationId");
        resolver.applyReference(dto.getEndLocationId(), locationRepository::findById,
                entity::setEndLocation, "endLocationId");
    }

    /**
     * @deprecated entity-level create cannot resolve the DTO's foreign keys; use
     * {@link #createFromDto}. Kept only to satisfy {@code CrudService}.
     */
    @Override
    @Deprecated
    public CableRun create(CableRun entity) {
        throw new UnsupportedOperationException("Use createFromDto(dto)");
    }

    /**
     * @deprecated saving the detached entity built by the mapper merges nulls over every
     * association the mapper ignores; use {@link #updateFromDto}. Kept only to satisfy
     * {@code CrudService}.
     */
    @Override
    @Deprecated
    public CableRun update(UUID id, CableRun entity) {
        throw new UnsupportedOperationException("Use updateFromDto(id, dto)");
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id, CableRun.class);
    }
}
