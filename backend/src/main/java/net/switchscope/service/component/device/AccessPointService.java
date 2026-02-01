package net.switchscope.service.component.device;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.device.AccessPointMapper;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.service.CrudService;
import net.switchscope.to.component.device.AccessPointTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessPointService implements CrudService<AccessPoint> {

    private final DeviceRepository repository;
    private final AccessPointMapper mapper;

    @Override
    @SuppressWarnings("unchecked")
    public List<AccessPoint> getAll() {
        return (List<AccessPoint>) (List<?>) repository.findAccessPoints();
    }

    @Override
    public AccessPoint getById(UUID id) {
        AccessPoint ap = (AccessPoint) repository.getExisted(id);
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
        AccessPoint ap = (AccessPoint) repository.getExisted(id);
        Hibernate.initialize(ap.getPorts());
        Hibernate.initialize(ap.getSsids());
        return mapper.toTo(ap);
    }

    /**
     * Create access point and return as DTO within transaction.
     *
     * @param entity access point entity to create
     * @return created access point as DTO
     */
    @Transactional
    public AccessPointTo createAndReturnDto(AccessPoint entity) {
        AccessPoint saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    /**
     * Update access point and return as DTO within transaction.
     *
     * @param id access point ID
     * @param entity access point entity with updates
     * @return updated access point as DTO
     */
    @Transactional
    public AccessPointTo updateAndReturnDto(UUID id, AccessPoint entity) {
        repository.getExisted(id);
        entity.setId(id);
        AccessPoint saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    @Override
    @Transactional
    public AccessPoint create(AccessPoint entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public AccessPoint update(UUID id, AccessPoint entity) {
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
