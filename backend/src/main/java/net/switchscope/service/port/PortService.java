package net.switchscope.service.port;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.error.NotFoundException;
import net.switchscope.mapper.port.EthernetPortMapper;
import net.switchscope.mapper.port.FiberPortMapper;
import net.switchscope.model.component.Component;
import net.switchscope.model.component.connectivity.Connector;
import net.switchscope.model.component.device.Device;
import net.switchscope.model.port.EthernetPort;
import net.switchscope.model.port.FiberPort;
import net.switchscope.model.port.Port;
import net.switchscope.repository.component.ComponentRepository;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.repository.port.PortRepository;
import net.switchscope.service.CrudService;
import net.switchscope.to.port.EthernetPortTo;
import net.switchscope.to.port.FiberPortTo;
import net.switchscope.to.port.PortTo;
import net.switchscope.web.payload.PartialUpdate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortService implements CrudService<Port> {

    private final PortRepository repository;
    private final DeviceRepository deviceRepository;
    private final ComponentRepository componentRepository;
    private final EthernetPortMapper ethernetPortMapper;
    private final FiberPortMapper fiberPortMapper;

    @Override
    public List<Port> getAll() {
        return repository.findAllWithRelationships();
    }

    @Override
    public Port getById(UUID id) {
        return repository.findByIdWithRelationships(id)
                .orElseThrow(() -> new NotFoundException("Port with id=" + id + " not found"));
    }

    /**
     * All ports as DTOs, mapped inside the transaction so the lazy device and connector links are
     * still reachable.
     */
    public List<PortTo> getAllAsDto() {
        return repository.findAllWithRelationships().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * One port as a DTO, mapped inside the transaction.
     */
    public PortTo getByIdAsDto(UUID id) {
        return mapToDto(getById(id));
    }

    /**
     * Create a port from its polymorphic DTO.
     * <p>
     * The concrete type comes from the DTO's runtime class, which Jackson resolves from the
     * {@code portType} discriminator. Foreign keys are resolved here because the mappers ignore
     * {@code device} and {@code connector}, and {@code equipment_id} is NOT NULL.
     */
    @Transactional
    public PortTo createFromDto(PortTo dto) {
        Port entity = mapToEntity(dto);
        applyReferences(entity, dto);
        return mapToDto(repository.save(entity));
    }

    /**
     * Apply an update onto the stored port.
     * The entity is loaded first: merging a detached instance would null the device link.
     */
    @Transactional
    public PortTo updateFromDto(UUID id, PartialUpdate<? extends PortTo> update) {
        PortTo dto = update.dto();
        Port existing = getById(id);
        updateFromDto(existing, dto);
        applyReferences(existing, dto);
        update.applyNulls(existing);
        return mapToDto(repository.save(existing));
    }

    /**
     * Resolves the DTO's foreign-key ids. An id present in the DTO replaces the current reference,
     * an absent one leaves it untouched. {@code deviceId} is mandatory because {@code equipment_id}
     * is NOT NULL; {@code connectorId} is optional.
     */
    private void applyReferences(Port entity, PortTo dto) {
        if (dto.getDeviceId() != null) {
            Device device = deviceRepository.findById(dto.getDeviceId())
                    .orElseThrow(() -> new NotFoundException(
                            "Device with id=" + dto.getDeviceId() + " not found"));
            entity.setDevice(device);
        }
        if (dto.getConnectorId() != null) {
            Component component = componentRepository.findById(dto.getConnectorId())
                    .orElseThrow(() -> new NotFoundException(
                            "Connector with id=" + dto.getConnectorId() + " not found"));
            if (!(component instanceof Connector connector)) {
                throw new IllegalRequestDataException("connectorId=" + dto.getConnectorId() + " is a "
                        + component.getClass().getSimpleName() + ", expected Connector");
            }
            entity.setConnector(connector);
        }
        if (entity.getDevice() == null) {
            throw new IllegalRequestDataException("deviceId is required");
        }
    }

    private PortTo mapToDto(Port port) {
        if (port instanceof EthernetPort ethernetPort) {
            return ethernetPortMapper.toTo(ethernetPort);
        } else if (port instanceof FiberPort fiberPort) {
            return fiberPortMapper.toTo(fiberPort);
        }
        throw new IllegalArgumentException("Unknown port type: " + port.getClass().getName());
    }

    private Port mapToEntity(PortTo dto) {
        if (dto instanceof EthernetPortTo ethernetPortTo) {
            return ethernetPortMapper.toEntity(ethernetPortTo);
        } else if (dto instanceof FiberPortTo fiberPortTo) {
            return fiberPortMapper.toEntity(fiberPortTo);
        }
        throw new IllegalRequestDataException("Unsupported port DTO: " + dto.getClass().getSimpleName());
    }

    private void updateFromDto(Port port, PortTo dto) {
        if (port instanceof EthernetPort entity && dto instanceof EthernetPortTo to) {
            ethernetPortMapper.updateFromTo(entity, to);
        } else if (port instanceof FiberPort entity && dto instanceof FiberPortTo to) {
            fiberPortMapper.updateFromTo(entity, to);
        } else {
            throw new IllegalRequestDataException("Port type mismatch: entity="
                    + port.getClass().getSimpleName() + ", to=" + dto.getClass().getSimpleName());
        }
    }

    /**
     * @deprecated the entity-level call cannot resolve the DTO's foreign keys; use
     * {@link #createFromDto}. Kept only to satisfy {@code CrudService}.
     */
    @Override
    @Deprecated
    public Port create(Port entity) {
        throw new UnsupportedOperationException("Use createFromDto(dto)");
    }

    /**
     * @deprecated saving a detached entity merges nulls over the associations the mapper ignores;
     * use {@link #updateFromDto}. Kept only to satisfy {@code CrudService}.
     */
    @Override
    @Deprecated
    public Port update(UUID id, Port entity) {
        throw new UnsupportedOperationException("Use updateFromDto(id, dto)");
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }
}
