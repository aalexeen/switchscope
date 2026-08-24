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
import net.switchscope.to.PageTo;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.PageReader;
import net.switchscope.web.page.Restriction;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceService implements CrudService<Device> {

    /**
     * The three classes this route is about. A patch panel is a {@code Device} in the model and
     * deliberately not one here: the whole list says so by unioning three queries, and a page has
     * to say the same thing or it would hold rows the list never returned.
     */
    private static final Restriction<Device> DEVICE_CLASSES = (root, cb) ->
            root.type().in(NetworkSwitch.class, Router.class, AccessPoint.class);

    private final DeviceRepository repository;
    private final PageReader pageReader;

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

    /**
     * One page of devices, mapped by the caller's own function while this transaction is open.
     * <p>
     * The mapping is handed in rather than done here because a device's DTO is chosen by its class
     * and the three mappers that do the choosing belong to the controller. Running the function
     * here is what keeps that choice legal: made in the controller, executed where the row is still
     * attached.
     *
     * @param query which page was asked for, in what order
     * @param toDto how one device becomes its DTO
     * @param <T>   the DTO type the caller produces
     * @return the requested page
     */
    public <T> PageTo<T> getPage(ListQuery query, Function<Device, T> toDto) {
        return pageReader.read(Device.class, DEVICE_CLASSES, query, device -> {
            initializeForMapping(device);
            return toDto.apply(device);
        });
    }

    @Override
    public Device getById(UUID id) {
        Device device = repository.getExisted(id);
        initializeForMapping(device);
        return device;
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
