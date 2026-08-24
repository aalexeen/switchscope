package net.switchscope.service.component.device;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.component.device.NetworkSwitchMapper;
import net.switchscope.model.component.device.NetworkSwitch;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.model.component.catalog.device.SwitchModel;
import net.switchscope.service.component.ComponentReferenceResolver;
import net.switchscope.service.DtoCrudService;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.to.component.device.NetworkSwitchTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NetworkSwitchService implements DtoCrudService<NetworkSwitch, NetworkSwitchTo> {

    private final DeviceRepository repository;
    private final NetworkSwitchMapper mapper;
    private final ComponentReferenceResolver resolver;

    @Override
    @SuppressWarnings("unchecked")
    public List<NetworkSwitch> getAll() {
        return (List<NetworkSwitch>) (List<?>) repository.findNetworkSwitchesWithModel();
    }

    @Override
    public NetworkSwitch getById(UUID id) {
        NetworkSwitch sw = repository.getExisted(id, NetworkSwitch.class);
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
        NetworkSwitch sw = repository.getExisted(id, NetworkSwitch.class);
        Hibernate.initialize(sw.getSwitchModel());
        Hibernate.initialize(sw.getPorts());
        return mapper.toTo(sw);
    }



    /**
     * Create a network switch from its DTO, resolving foreign-key ids into managed references first.
     * Mapping back happens inside the transaction so lazy associations are still reachable.
     */
    @Override
    @Transactional
    public NetworkSwitchTo createFromDto(NetworkSwitchTo dto) {
        NetworkSwitch entity = mapper.toEntity(dto);
        applyReferences(entity, dto);
        return mapper.toTo(repository.save(entity));
    }

    /**
     * Apply the DTO onto the stored network switch.
     * The entity is loaded first: merging the detached instance produced by the mapper would null
     * every association the mapper ignores, starting with the NOT NULL component type and status.
     */
    @Override
    @Transactional
    public NetworkSwitchTo updateFromDto(UUID id, PartialUpdate<? extends NetworkSwitchTo> update) {
        NetworkSwitchTo dto = update.dto();
        NetworkSwitch existing = getById(id);
        mapper.updateFromTo(existing, dto);
        applyReferences(existing, dto);
        update.applyNulls(existing);
        return mapper.toTo(repository.save(existing));
    }

    private void applyReferences(NetworkSwitch entity, NetworkSwitchTo dto) {
        resolver.applyCommonReferences(entity, dto);
        resolver.applyModelReference(dto.getSwitchModelId(), SwitchModel.class,
                entity::setSwitchModel, "switchModelId");
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id, NetworkSwitch.class);
    }
}
