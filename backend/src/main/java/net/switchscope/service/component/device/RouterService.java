package net.switchscope.service.component.device;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.device.RouterMapper;
import net.switchscope.model.component.device.Router;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.service.component.ComponentReferenceResolver;
import net.switchscope.service.DtoCrudService;
import net.switchscope.to.component.device.RouterTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouterService implements DtoCrudService<Router, RouterTo> {

    private final DeviceRepository repository;
    private final RouterMapper mapper;
    private final ComponentReferenceResolver resolver;

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
     * Create a router from its DTO, resolving foreign-key ids into managed references first.
     * Mapping back happens inside the transaction so lazy associations are still reachable.
     */
    @Override
    @Transactional
    public RouterTo createFromDto(RouterTo dto) {
        Router entity = mapper.toEntity(dto);
        applyReferences(entity, dto);
        return mapper.toTo(repository.save(entity));
    }

    /**
     * Apply the DTO onto the stored router.
     * The entity is loaded first: merging the detached instance produced by the mapper would null
     * every association the mapper ignores, starting with the NOT NULL component type and status.
     */
    @Override
    @Transactional
    public RouterTo updateFromDto(UUID id, RouterTo dto) {
        Router existing = getById(id);
        mapper.updateFromTo(existing, dto);
        applyReferences(existing, dto);
        return mapper.toTo(repository.save(existing));
    }

    private void applyReferences(Router entity, RouterTo dto) {
        resolver.applyCommonReferences(entity, dto);
    }

    /**
     * @deprecated entity-level create cannot resolve the DTO's foreign keys; use
     * {@link #createFromDto}. Kept only to satisfy {@code CrudService}.
     */
    @Override
    @Deprecated
    public Router create(Router entity) {
        throw new UnsupportedOperationException("Use createFromDto(dto)");
    }

    /**
     * @deprecated saving the detached entity built by the mapper merges nulls over every
     * association the mapper ignores; use {@link #updateFromDto}. Kept only to satisfy
     * {@code CrudService}.
     */
    @Override
    @Deprecated
    public Router update(UUID id, Router entity) {
        throw new UnsupportedOperationException("Use updateFromDto(id, dto)");
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
