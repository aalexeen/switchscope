package net.switchscope.service.component.housing;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.housing.RackMapper;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.repository.component.housing.HousingRepository;
import net.switchscope.model.component.catalog.housing.RackModelEntity;
import net.switchscope.service.component.ComponentReferenceResolver;
import net.switchscope.service.DtoCrudService;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.to.component.housing.RackTo;
import net.switchscope.to.PageTo;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.PageReader;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RackService implements DtoCrudService<Rack, RackTo> {

    private final HousingRepository repository;
    private final RackMapper mapper;
    private final ComponentReferenceResolver resolver;
    private final PageReader pageReader;

    @Override
    @SuppressWarnings("unchecked")
    public List<Rack> getAll() {
        return (List<Rack>) (List<?>) repository.findRacks();
    }

    /**
     * Rooted at {@code Rack} rather than at the repository's domain type, so the page holds
     * racks only - the components table holds every class of component.
     */
    @Override
    public PageTo<RackTo> getPage(ListQuery query) {
        return pageReader.read(Rack.class, query, mapper::toTo);
    }

    @Override
    public Rack getById(UUID id) {
        Rack rack = repository.getExisted(id, Rack.class);
        Hibernate.initialize(rack.getRackType());
        return rack;
    }

    /**
     * Get all racks and map to DTOs within transaction.
     *
     * @return list of rack DTOs
     */
    @SuppressWarnings("unchecked")
    public List<RackTo> getAllAsDto() {
        List<Rack> racks = (List<Rack>) (List<?>) repository.findRacks();
        return mapper.toToList(racks);
    }

    /**
     * Get rack by ID and map to DTO within transaction.
     *
     * @param id rack ID
     * @return rack DTO
     */
    public RackTo getByIdAsDto(UUID id) {
        Rack rack = repository.getExisted(id, Rack.class);
        Hibernate.initialize(rack.getRackType());
        return mapper.toTo(rack);
    }



    /**
     * Create a rack from its DTO, resolving foreign-key ids into managed references first.
     * Mapping back happens inside the transaction so lazy associations are still reachable.
     */
    @Override
    @Transactional
    public RackTo createFromDto(RackTo dto) {
        Rack entity = mapper.toEntity(dto);
        applyReferences(entity, dto);
        return mapper.toTo(repository.save(entity));
    }

    /**
     * Apply the DTO onto the stored rack.
     * The entity is loaded first: merging the detached instance produced by the mapper would null
     * every association the mapper ignores, starting with the NOT NULL component type and status.
     */
    @Override
    @Transactional
    public RackTo updateFromDto(UUID id, PartialUpdate<? extends RackTo> update) {
        RackTo dto = update.dto();
        Rack existing = getById(id);
        mapper.updateFromTo(existing, dto);
        applyReferences(existing, dto);
        update.applyNulls(existing);
        return mapper.toTo(repository.save(existing));
    }

    private void applyReferences(Rack entity, RackTo dto) {
        resolver.applyCommonReferences(entity, dto);
        resolver.applyModelReference(dto.getRackTypeId(), RackModelEntity.class,
                entity::setRackType, "rackTypeId");
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id, Rack.class);
    }
}
