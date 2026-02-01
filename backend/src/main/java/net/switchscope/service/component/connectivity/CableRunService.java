package net.switchscope.service.component.connectivity;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.connectivity.CableRunMapper;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.repository.component.connectivity.ConnectivityRepository;
import net.switchscope.service.CrudService;
import net.switchscope.to.component.connectivity.CableRunTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CableRunService implements CrudService<CableRun> {

    private final ConnectivityRepository repository;
    private final CableRunMapper mapper;

    @Override
    @SuppressWarnings("unchecked")
    public List<CableRun> getAll() {
        return (List<CableRun>) (List<?>) repository.findCableRuns();
    }

    @Override
    public CableRun getById(UUID id) {
        CableRun cableRun = (CableRun) repository.getExisted(id);
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
        CableRun cableRun = (CableRun) repository.getExisted(id);
        initializeLazyCollections(cableRun);
        return mapper.toTo(cableRun);
    }

    /**
     * Create cable run and return as DTO within transaction.
     *
     * @param entity cable run entity to create
     * @return created cable run as DTO
     */
    @Transactional
    public CableRunTo createAndReturnDto(CableRun entity) {
        CableRun saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    /**
     * Update cable run and return as DTO within transaction.
     *
     * @param id cable run ID
     * @param entity cable run entity with updates
     * @return updated cable run as DTO
     */
    @Transactional
    public CableRunTo updateAndReturnDto(UUID id, CableRun entity) {
        repository.getExisted(id);
        entity.setId(id);
        CableRun saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    private void initializeLazyCollections(CableRun cableRun) {
        Hibernate.initialize(cableRun.getLocations());
        Hibernate.initialize(cableRun.getConnectors());
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
