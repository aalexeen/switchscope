package net.switchscope.service.component.device;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessPointService implements CrudService<AccessPoint> {

    private final DeviceRepository repository;

    @Override
    public List<AccessPoint> getAll() {
        // TODO: filter by type
        return repository.findAll().stream()
                .filter(d -> d instanceof AccessPoint)
                .map(d -> (AccessPoint) d)
                .toList();
    }

    @Override
    public AccessPoint getById(UUID id) {
        return (AccessPoint) repository.getExisted(id);
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
