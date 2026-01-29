package net.switchscope.web.component.device;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.component.device.AccessPointMapper;
import net.switchscope.mapper.component.device.NetworkSwitchMapper;
import net.switchscope.mapper.component.device.RouterMapper;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.model.component.device.Device;
import net.switchscope.model.component.device.NetworkSwitch;
import net.switchscope.model.component.device.Router;
import net.switchscope.service.component.device.DeviceService;
import net.switchscope.to.component.device.DeviceTo;

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
public class DeviceController {

    static final String REST_URL = "/api/devices";

    private final DeviceService service;

    // Polymorphic mappers for different device types
    private final NetworkSwitchMapper networkSwitchMapper;
    private final RouterMapper routerMapper;
    private final AccessPointMapper accessPointMapper;

    @GetMapping
    public List<DeviceTo> getAll() {
        log.info("getAll devices");
        return service.getAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DeviceTo get(@PathVariable UUID id) {
        log.info("get device {}", id);
        return mapToDto(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceTo create(@RequestBody DeviceTo to) {
        log.info("create device {}", to);
        Device entity = mapToEntity(to);
        return mapToDto(service.create(entity));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DeviceTo update(@PathVariable UUID id, @RequestBody DeviceTo to) {
        log.info("update device {} with id={}", to, id);
        Device entity = service.getById(id);
        updateFromDto(entity, to);
        return mapToDto(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete device {}", id);
        service.delete(id);
    }

    // Helper methods for polymorphic mapping
    @SuppressWarnings("unchecked")
    private DeviceTo mapToDto(Device device) {
        if (device instanceof NetworkSwitch) {
            return networkSwitchMapper.toTo((NetworkSwitch) device);
        } else if (device instanceof Router) {
            return routerMapper.toTo((Router) device);
        } else if (device instanceof AccessPoint) {
            return accessPointMapper.toTo((AccessPoint) device);
        } else {
            throw new IllegalArgumentException("Unknown device type: " + device.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    private Device mapToEntity(DeviceTo to) {
        // Determine device type from TO class name
        String className = to.getClass().getSimpleName();
        if (className.contains("NetworkSwitch")) {
            return networkSwitchMapper.toEntity((net.switchscope.to.component.device.NetworkSwitchTo) to);
        } else if (className.contains("Router")) {
            return routerMapper.toEntity((net.switchscope.to.component.device.RouterTo) to);
        } else if (className.contains("AccessPoint")) {
            return accessPointMapper.toEntity((net.switchscope.to.component.device.AccessPointTo) to);
        } else {
            throw new IllegalArgumentException("Unknown device TO type: " + className);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateFromDto(Device device, DeviceTo to) {
        if (device instanceof NetworkSwitch && to instanceof net.switchscope.to.component.device.NetworkSwitchTo) {
            networkSwitchMapper.updateFromTo((NetworkSwitch) device, (net.switchscope.to.component.device.NetworkSwitchTo) to);
        } else if (device instanceof Router && to instanceof net.switchscope.to.component.device.RouterTo) {
            routerMapper.updateFromTo((Router) device, (net.switchscope.to.component.device.RouterTo) to);
        } else if (device instanceof AccessPoint && to instanceof net.switchscope.to.component.device.AccessPointTo) {
            accessPointMapper.updateFromTo((AccessPoint) device, (net.switchscope.to.component.device.AccessPointTo) to);
        } else {
            throw new IllegalArgumentException("Device type mismatch: entity=" + device.getClass().getName() + ", to=" + to.getClass().getName());
        }
    }
}
