package net.switchscope.service.location;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.error.NotFoundException;
import net.switchscope.mapper.location.catalog.LocationTypeMapper;
import net.switchscope.model.location.catalog.LocationTypeEntity;
import net.switchscope.repository.location.LocationTypeRepository;
import net.switchscope.service.DtoCrudService;
import net.switchscope.to.location.catalog.LocationTypeTo;
import net.switchscope.web.payload.PartialUpdate;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationTypeService implements DtoCrudService<LocationTypeEntity, LocationTypeTo> {

    private final LocationTypeRepository repository;
    private final LocationTypeMapper mapper;

    @Override
    public List<LocationTypeEntity> getAll() {
        return repository.findAllWithChildTypes();
    }

    @Override
    public LocationTypeEntity getById(UUID id) {
        return repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new NotFoundException("Location type with id=" + id + " not found"));
    }

    /**
     * Get all location types and map to DTOs within transaction.
     * Initializes allowedParentTypes for each entity to avoid LazyInitializationException.
     *
     * @return list of location type DTOs
     */
    public List<LocationTypeTo> getAllAsDto() {
        List<LocationTypeEntity> entities = repository.findAllWithChildTypes();
        // Initialize allowedParentTypes (not fetched in findAllWithChildTypes to avoid Cartesian product)
        entities.forEach(e -> Hibernate.initialize(e.getAllowedParentTypes()));
        return mapper.toToList(entities);
    }

    /**
     * Get location type by ID and map to DTO within transaction.
     *
     * @param id location type ID
     * @return location type DTO
     */
    public LocationTypeTo getByIdAsDto(UUID id) {
        LocationTypeEntity entity = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new NotFoundException("Location type with id=" + id + " not found"));
        return mapper.toTo(entity);
    }

    @Override
    @Transactional
    public LocationTypeTo updateFromDto(UUID id, PartialUpdate<? extends LocationTypeTo> update) {
        LocationTypeEntity existing = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new NotFoundException("Location type with id=" + id + " not found"));
        mapper.updateFromTo(existing, update.dto());
        applyAllowedChildTypes(existing, update.dto());
        update.applyNulls(existing);
        return mapper.toTo(repository.save(existing));
    }

    /**
     * Create from the DTO rather than from an entity the controller mapped, because the allowed
     * child types travel as ids and the mapper cannot resolve them - it would leave a create that
     * names them with none.
     *
     * @param dto the location type to create
     * @return the stored location type
     */
    @Override
    @Transactional
    public LocationTypeTo createFromDto(LocationTypeTo dto) {
        LocationTypeEntity entity = mapper.toEntity(dto);
        applyAllowedChildTypes(entity, dto);
        return mapper.toTo(repository.save(entity));
    }

    /**
     * Fills the hierarchy from the ids the payload carries. A null set means the field was not
     * mentioned and the stored hierarchy stands; an empty one means the caller asked for no allowed
     * children. The self-reference is checked here rather than left to the database, which has no
     * constraint against it and would store a type that may contain itself.
     */
    private void applyAllowedChildTypes(LocationTypeEntity entity, LocationTypeTo dto) {
        Set<UUID> ids = dto.getAllowedChildTypeIds();
        if (ids == null) {
            return;
        }
        Set<LocationTypeEntity> children = new LinkedHashSet<>();
        for (UUID childId : ids) {
            if (childId == null) {
                throw new IllegalRequestDataException("allowedChildTypeIds contains a null id");
            }
            if (childId.equals(entity.getId())) {
                throw new IllegalRequestDataException("A location type cannot be its own child type");
            }
            children.add(repository.findById(childId).orElseThrow(() -> new NotFoundException(
                    "allowedChildTypeIds contains id=" + childId + ", which does not exist")));
        }
        entity.getAllowedChildTypes().clear();
        entity.getAllowedChildTypes().addAll(children);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
