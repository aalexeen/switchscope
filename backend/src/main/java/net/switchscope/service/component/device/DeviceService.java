package net.switchscope.service.component.device;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.model.component.device.Device;
import net.switchscope.model.component.device.NetworkSwitch;
import net.switchscope.model.component.device.Router;
import net.switchscope.repository.component.device.DeviceRepository;
import net.switchscope.service.CrudService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceService implements CrudService<Device> {

    private final DeviceRepository repository;

    @Override
    public List<Device> getAll() {
        // Only return actual network devices (not PatchPanels which also extend Device)
        List<Device> devices = Stream.concat(
                Stream.concat(
                    repository.findNetworkSwitches().stream(),
                    repository.findRouters().stream()
                ),
                repository.findAccessPoints().stream()
        ).collect(Collectors.toList());

        devices.forEach(this::initializeForMapping);
        return devices;
    }

    @Override
    public Device getById(UUID id) {
        Device device = repository.getExisted(id);
        initializeForMapping(device);
        return device;
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

    /**
     * Touch lazily-loaded associations needed by mappers while the transactional
     * session is still open to avoid LazyInitializationException.
     */
    private void initializeForMapping(Device device) {
        if (device == null) {
            return;
        }

        if (device.getComponentStatus() != null) {
            device.getComponentStatus().getCode();
        }
        if (device.getComponentType() != null) {
            device.getComponentType().getCode();
        }
        if (device.getComponentNature() != null) {
            device.getComponentNature().getCode();
        }
        if (device.getInstallation() != null) {
            if (device.getInstallation().getLocation() != null) {
                device.getInstallation().getLocation().getFullPath();
            }
            if (device.getInstallation().getStatus() != null) {
                device.getInstallation().getStatus().getCode();
            }
            if (device.getInstallation().getInstalledItemType() != null) {
                device.getInstallation().getInstalledItemType().getCode();
            }
        }
        if (device.getParentComponent() != null) {
            device.getParentComponent().getName();
        }
        if (device instanceof net.switchscope.model.component.device.NetworkSwitch ns) {
            if (ns.getSwitchModel() != null) {
                ns.getSwitchModel().getManufacturer();
            }
            // Ports are lazy; size() triggers initialization if needed for mapper
            ns.getPorts().size();
        } else if (device instanceof net.switchscope.model.component.device.Router r) {
            r.getPorts().size();
            // WAN interfaces ElementCollection (lazy by default)
            r.getWanInterfaceTypes().size();
        } else if (device instanceof net.switchscope.model.component.device.AccessPoint ap) {
            ap.getPorts().size();
            ap.getSsids().size();
        }
    }
}
