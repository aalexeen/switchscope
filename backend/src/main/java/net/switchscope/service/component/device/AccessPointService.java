package net.switchscope.service.component.device;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.device.AccessPointMapper;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.service.component.ComponentReferenceResolver;
import net.switchscope.service.DtoCrudService;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.to.component.device.AccessPointTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessPointService implements DtoCrudService<AccessPoint, AccessPointTo> {

    private final DeviceRepository repository;
    private final AccessPointMapper mapper;
    private final ComponentReferenceResolver resolver;

    @Override
    @SuppressWarnings("unchecked")
    public List<AccessPoint> getAll() {
        return (List<AccessPoint>) (List<?>) repository.findAccessPoints();
    }

    @Override
    public AccessPoint getById(UUID id) {
        AccessPoint ap = repository.getExisted(id, AccessPoint.class);
        Hibernate.initialize(ap.getPorts());
        Hibernate.initialize(ap.getSsids());
        return ap;
    }

    /**
     * Get all access points and map to DTOs within transaction.
     * Initializes ports and ssids for count calculations.
     *
     * @return list of access point DTOs
     */
    @SuppressWarnings("unchecked")
    public List<AccessPointTo> getAllAsDto() {
        List<AccessPoint> accessPoints = (List<AccessPoint>) (List<?>) repository.findAccessPoints();
        // Initialize lazy collections for mapping
        accessPoints.forEach(ap -> {
            Hibernate.initialize(ap.getPorts());
            Hibernate.initialize(ap.getSsids());
        });
        return mapper.toToList(accessPoints);
    }

    /**
     * Get access point by ID and map to DTO within transaction.
     *
     * @param id access point ID
     * @return access point DTO
     */
    public AccessPointTo getByIdAsDto(UUID id) {
        AccessPoint ap = repository.getExisted(id, AccessPoint.class);
        Hibernate.initialize(ap.getPorts());
        Hibernate.initialize(ap.getSsids());
        return mapper.toTo(ap);
    }



    /**
     * Create a access point from its DTO, resolving foreign-key ids into managed references first.
     * Mapping back happens inside the transaction so lazy associations are still reachable.
     */
    @Override
    @Transactional
    public AccessPointTo createFromDto(AccessPointTo dto) {
        AccessPoint entity = mapper.toEntity(dto);
        applyReferences(entity, dto);
        return mapper.toTo(repository.save(entity));
    }

    /**
     * Apply the DTO onto the stored access point.
     * The entity is loaded first: merging the detached instance produced by the mapper would null
     * every association the mapper ignores, starting with the NOT NULL component type and status.
     */
    @Override
    @Transactional
    public AccessPointTo updateFromDto(UUID id, PartialUpdate<? extends AccessPointTo> update) {
        AccessPointTo dto = update.dto();
        AccessPoint existing = getById(id);
        mapper.updateFromTo(existing, dto);
        applyReferences(existing, dto);
        update.applyNulls(existing);
        return mapper.toTo(repository.save(existing));
    }

    private void applyReferences(AccessPoint entity, AccessPointTo dto) {
        resolver.applyCommonReferences(entity, dto);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id, AccessPoint.class);
    }
}
