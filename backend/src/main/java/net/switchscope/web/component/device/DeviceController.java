package net.switchscope.web.component.device;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.mapper.component.device.AccessPointMapper;
import net.switchscope.mapper.component.device.NetworkSwitchMapper;
import net.switchscope.mapper.component.device.RouterMapper;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.model.component.device.Device;
import net.switchscope.model.component.device.NetworkSwitch;
import net.switchscope.model.component.device.Router;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.security.permission.RequiresPermission;
import net.switchscope.service.component.ComponentService;
import net.switchscope.service.component.device.DeviceService;
import net.switchscope.to.component.ComponentTo;
import net.switchscope.to.component.device.AccessPointTo;
import net.switchscope.to.component.device.DeviceTo;
import net.switchscope.to.component.device.NetworkSwitchTo;
import net.switchscope.to.component.device.RouterTo;
import net.switchscope.web.component.ComponentPayloadReader;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for Device entities.
 * Uses custom implementation due to polymorphic device hierarchy.
 * Similar to ComponentController pattern.
 */
@Slf4j
@RestController
@RequestMapping(value = DeviceController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("component.device")
public class DeviceController {

    static final String REST_URL = "/api/devices";

    private final DeviceService service;
    private final ComponentService componentService;
    private final ComponentPayloadReader payloadReader;

    // Polymorphic mappers for different device types
    private final NetworkSwitchMapper networkSwitchMapper;
    private final RouterMapper routerMapper;
    private final AccessPointMapper accessPointMapper;

    @RequiresPermission("read")
    @GetMapping
    public List<DeviceTo> getAll() {
        log.info("getAll devices");
        return service.getAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @RequiresPermission("read")
    @GetMapping("/{id}")
    public DeviceTo get(@PathVariable UUID id) {
        log.info("get device {}", id);
        return mapToDto(service.getById(id));
    }

    /**
     * Create a device of any type.
     * <p>
     * The concrete type is derived from {@code componentTypeId} - see {@link ComponentPayloadReader} -
     * and must denote a device (NETWORK_SWITCH, ROUTER or ACCESS_POINT); a rack or a cable run posted
     * here is rejected with 422. Creation itself is delegated to {@link ComponentService}, which
     * resolves the foreign keys the mappers ignore and maps the result back inside the transaction.
     */
    @RequiresPermission("create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceTo create(@RequestBody String jsonPayload) {
        DeviceTo to = payloadReader.readForCreate(jsonPayload, DeviceTo.class);
        log.info("create device {}", to);
        return asDeviceTo(componentService.createFromDto(to));
    }

    /**
     * Update a device.
     * <p>
     * Reads raw JSON and pins {@code componentClass} to the stored entity's type, so a client cannot
     * change the type of an existing row and a payload that omits the discriminator still binds.
     */
    @RequiresPermission("update")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SneakyThrows
    public DeviceTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update device with id={}", id);

        Device existing = service.getById(id);
        Class<? extends DeviceTo> dtoClass = getDtoClassForEntity(existing);
        DeviceTo to = payloadReader.readForUpdate(jsonPayload, existing.getDiscriminatorValue(), dtoClass);

        return asDeviceTo(componentService.updateFromDto(id, to));
    }

    @RequiresPermission("delete")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete device {}", id);
        service.delete(id);
    }

    // Helper methods for polymorphic mapping
    private DeviceTo mapToDto(Device device) {
        if (device instanceof NetworkSwitch networkSwitch) {
            return networkSwitchMapper.toTo(networkSwitch);
        } else if (device instanceof Router router) {
            return routerMapper.toTo(router);
        } else if (device instanceof AccessPoint accessPoint) {
            return accessPointMapper.toTo(accessPoint);
        } else {
            throw new IllegalArgumentException("Unknown device type: " + device.getClass().getName());
        }
    }

    private Class<? extends DeviceTo> getDtoClassForEntity(Device device) {
        if (device instanceof NetworkSwitch) {
            return NetworkSwitchTo.class;
        } else if (device instanceof Router) {
            return RouterTo.class;
        } else if (device instanceof AccessPoint) {
            return AccessPointTo.class;
        }
        throw new IllegalArgumentException("Unknown device type: " + device.getClass().getName());
    }

    private DeviceTo asDeviceTo(ComponentTo to) {
        if (to instanceof DeviceTo deviceTo) {
            return deviceTo;
        }
        throw new IllegalRequestDataException(
                "componentClass must denote a device, got " + to.getComponentClass());
    }
}
