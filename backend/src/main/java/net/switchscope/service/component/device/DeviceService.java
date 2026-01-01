package net.switchscope.service.component.device;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.device.Device;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceService implements CrudService<Device> {

    private final DeviceRepository repository;

    @Override
    public List<Device> getAll() {
        return repository.findAll();
    }

    @Override
    public Device getById(UUID id) {
        return repository.getExisted(id);
    }

    @Override
    @Transactional
    public Device create(Device entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Device update(UUID id, Device entity) {
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
