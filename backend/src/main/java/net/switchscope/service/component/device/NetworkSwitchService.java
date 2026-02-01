package net.switchscope.service.component.device;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.device.NetworkSwitchMapper;
import net.switchscope.model.component.device.NetworkSwitch;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.service.CrudService;
import net.switchscope.to.component.device.NetworkSwitchTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NetworkSwitchService implements CrudService<NetworkSwitch> {

    private final DeviceRepository repository;
    private final NetworkSwitchMapper mapper;

    @Override
    @SuppressWarnings("unchecked")
    public List<NetworkSwitch> getAll() {
        return (List<NetworkSwitch>) (List<?>) repository.findNetworkSwitchesWithModel();
    }

    @Override
    public NetworkSwitch getById(UUID id) {
        NetworkSwitch sw = (NetworkSwitch) repository.getExisted(id);
        Hibernate.initialize(sw.getSwitchModel());
        Hibernate.initialize(sw.getPorts());
        return sw;
    }

    /**
     * Get all network switches and map to DTOs within transaction.
     * Uses findNetworkSwitchesWithModel() and initializes ports for portCount.
     *
     * @return list of network switch DTOs
     */
    @SuppressWarnings("unchecked")
    public List<NetworkSwitchTo> getAllAsDto() {
        List<NetworkSwitch> switches = (List<NetworkSwitch>) (List<?>) repository.findNetworkSwitchesWithModel();
        // Initialize ports for portCount calculation
        switches.forEach(sw -> Hibernate.initialize(sw.getPorts()));
        return mapper.toToList(switches);
    }

    /**
     * Get network switch by ID and map to DTO within transaction.
     *
     * @param id network switch ID
     * @return network switch DTO
     */
    public NetworkSwitchTo getByIdAsDto(UUID id) {
        NetworkSwitch sw = (NetworkSwitch) repository.getExisted(id);
        Hibernate.initialize(sw.getSwitchModel());
        Hibernate.initialize(sw.getPorts());
        return mapper.toTo(sw);
    }

    /**
     * Create network switch and return as DTO within transaction.
     *
     * @param entity network switch entity to create
     * @return created network switch as DTO
     */
    @Transactional
    public NetworkSwitchTo createAndReturnDto(NetworkSwitch entity) {
        NetworkSwitch saved = repository.save(entity);
        return mapper.toTo(saved);
    }

    /**
     * Update network switch and return as DTO within transaction.
     *
     * @param id network switch ID
     * @param entity network switch entity with updates
     * @return updated network switch as DTO
     */
    @Transactional
    public NetworkSwitchTo updateAndReturnDto(UUID id, NetworkSwitch entity) {
        repository.getExisted(id);
        entity.setId(id);
        NetworkSwitch saved = repository.save(entity);
        return mapper.toTo(saved);
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
