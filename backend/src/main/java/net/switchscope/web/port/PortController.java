package net.switchscope.web.port;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.port.EthernetPortMapper;
import net.switchscope.mapper.port.FiberPortMapper;
import net.switchscope.model.port.EthernetPort;
import net.switchscope.model.port.FiberPort;
import net.switchscope.model.port.Port;
import net.switchscope.service.port.PortService;
import net.switchscope.to.port.PortTo;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = PortController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PortController {

    static final String REST_URL = "/api/ports";

    private final PortService service;
    private final EthernetPortMapper ethernetPortMapper;
    private final FiberPortMapper fiberPortMapper;

    @GetMapping
    public List<PortTo> getAll() {
        log.info("getAll ports");
        return service.getAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PortTo get(@PathVariable UUID id) {
        log.info("get port {}", id);
        return mapToDto(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PortTo create(@RequestBody PortTo to) {
        log.info("create port {}", to);
        Port entity = mapToEntity(to);
        // Service should resolve foreign keys (device, connector)
        return mapToDto(service.create(entity));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PortTo update(@PathVariable UUID id, @RequestBody PortTo to) {
        log.info("update port {} with id={}", to, id);
        Port entity = service.getById(id);
        updateFromDto(entity, to);
        // Service should resolve foreign keys
        return mapToDto(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete port {}", id);
        service.delete(id);
    }

    // Helper methods for polymorphic mapping
    private PortTo mapToDto(Port port) {
        if (port instanceof EthernetPort) {
            return ethernetPortMapper.toTo((EthernetPort) port);
        } else if (port instanceof FiberPort) {
            return fiberPortMapper.toTo((FiberPort) port);
        } else {
            throw new IllegalArgumentException("Unknown port type: " + port.getClass().getName());
        }
    }

    private Port mapToEntity(PortTo to) {
        // Determine port type from TO class name or discriminator field
        String className = to.getClass().getSimpleName();
        if (className.contains("Ethernet")) {
            return ethernetPortMapper.toEntity((net.switchscope.to.port.EthernetPortTo) to);
        } else if (className.contains("Fiber")) {
            return fiberPortMapper.toEntity((net.switchscope.to.port.FiberPortTo) to);
        } else {
            throw new IllegalArgumentException("Unknown port TO type: " + className);
        }
    }

    private void updateFromDto(Port port, PortTo to) {
        if (port instanceof EthernetPort && to instanceof net.switchscope.to.port.EthernetPortTo) {
            ethernetPortMapper.updateFromTo((EthernetPort) port, (net.switchscope.to.port.EthernetPortTo) to);
        } else if (port instanceof FiberPort && to instanceof net.switchscope.to.port.FiberPortTo) {
            fiberPortMapper.updateFromTo((FiberPort) port, (net.switchscope.to.port.FiberPortTo) to);
        } else {
            throw new IllegalArgumentException("Port type mismatch: entity=" + port.getClass().getName() + ", to=" + to.getClass().getName());
        }
    }
}
