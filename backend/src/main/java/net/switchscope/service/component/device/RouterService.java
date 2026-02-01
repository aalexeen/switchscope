package net.switchscope.service.component.device;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.device.RouterMapper;
import net.switchscope.model.component.device.Router;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.service.CrudService;
import net.switchscope.to.component.device.RouterTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouterService implements CrudService<Router> {

    private final DeviceRepository repository;
    private final RouterMapper mapper;

    @Override
    @SuppressWarnings("unchecked")
    public List<Router> getAll() {
        return (List<Router>) (List<?>) repository.findRouters();
    }

    @Override
    public Router getById(UUID id) {
        Router router = (Router) repository.getExisted(id);
        Hibernate.initialize(router.getPorts());
        return router;
    }

    /**
     * Get all routers and map to DTOs within transaction.
     * Initializes ports for portCount calculation.
     *
     * @return list of router DTOs
     */
    @SuppressWarnings("unchecked")
    public List<RouterTo> getAllAsDto() {
        List<Router> routers = (List<Router>) (List<?>) repository.findRouters();
        // Initialize ports for portCount calculation
        routers.forEach(r -> Hibernate.initialize(r.getPorts()));
        return mapper.toToList(routers);
    }

    /**
     * Get router by ID and map to DTO within transaction.
     *
     * @param id router ID
     * @return router DTO
     */
    public RouterTo getByIdAsDto(UUID id) {
        Router router = (Router) repository.getExisted(id);
        Hibernate.initialize(router.getPorts());
        return mapper.toTo(router);
    }

    /**
     * Create router and return as DTO within transaction.
     *
     * @param entity router entity to create
     * @return created router as DTO
     */
    @Transactional
    public RouterTo createAndReturnDto(Router entity) {
        Router saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    /**
     * Update router and return as DTO within transaction.
     *
     * @param id router ID
     * @param entity router entity with updates
     * @return updated router as DTO
     */
    @Transactional
    public RouterTo updateAndReturnDto(UUID id, Router entity) {
        repository.getExisted(id);
        entity.setId(id);
        Router saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    @Override
    @Transactional
    public Router create(Router entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Router update(UUID id, Router entity) {
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
