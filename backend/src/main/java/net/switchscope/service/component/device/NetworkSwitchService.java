package net.switchscope.service.component.device;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.device.NetworkSwitch;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NetworkSwitchService implements CrudService<NetworkSwitch> {

    private final DeviceRepository repository;

    @Override
    @SuppressWarnings("unchecked")
    public List<NetworkSwitch> getAll() {
        return (List<NetworkSwitch>) (List<?>) repository.findNetworkSwitches();
    }

    @Override
    public NetworkSwitch getById(UUID id) {
        return (NetworkSwitch) repository.getExisted(id);
    }

    @Override
    @Transactional
    public NetworkSwitch create(NetworkSwitch entity) {
        // TODO: implement validation
        return repository.save(entity);
    }

    @Override
    @Transactional
    public NetworkSwitch update(UUID id, NetworkSwitch entity) {
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
