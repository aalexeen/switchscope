package net.switchscope.service.component.housing;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.housing.RackMapper;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.repository.component.housing.HousingRepository;
import net.switchscope.service.CrudService;
import net.switchscope.to.component.housing.RackTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RackService implements CrudService<Rack> {

    private final HousingRepository repository;
    private final RackMapper mapper;

    @Override
    @SuppressWarnings("unchecked")
    public List<Rack> getAll() {
        return (List<Rack>) (List<?>) repository.findRacks();
    }

    @Override
    public Rack getById(UUID id) {
        Rack rack = (Rack) repository.getExisted(id);
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
        Rack rack = (Rack) repository.getExisted(id);
        Hibernate.initialize(rack.getRackType());
        return mapper.toTo(rack);
    }

    /**
     * Create rack and return as DTO within transaction.
     *
     * @param entity rack entity to create
     * @return created rack as DTO
     */
    @Transactional
    public RackTo createAndReturnDto(Rack entity) {
        Rack saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    /**
     * Update rack and return as DTO within transaction.
     *
     * @param id rack ID
     * @param entity rack entity with updates
     * @return updated rack as DTO
     */
    @Transactional
    public RackTo updateAndReturnDto(UUID id, Rack entity) {
        repository.getExisted(id);
        entity.setId(id);
        Rack saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    @Override
    @Transactional
    public Rack create(Rack entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Rack update(UUID id, Rack entity) {
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
