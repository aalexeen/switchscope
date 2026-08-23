package net.switchscope.web.port;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.model.port.Port;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.security.permission.RequiresPermission;
import net.switchscope.service.port.PortService;
import net.switchscope.to.port.EthernetPortTo;
import net.switchscope.to.port.FiberPortTo;
import net.switchscope.to.port.PortTo;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = PortController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("port")
public class PortController {

    static final String REST_URL = "/api/ports";

    private final PortService service;
    private final ObjectMapper objectMapper;

    @RequiresPermission("read")
    @GetMapping
    public List<PortTo> getAll() {
        log.info("getAll ports");
        return service.getAllAsDto();
    }

    @RequiresPermission("read")
    @GetMapping("/{id}")
    public PortTo get(@PathVariable UUID id) {
        log.info("get port {}", id);
        return service.getByIdAsDto(id);
    }

    /**
     * Create a port of any type.
     * <p>
     * {@link PortTo} is abstract, so the payload must carry the {@code portType} discriminator
     * (ETHERNET or FIBER); Jackson uses it to pick the concrete DTO.
     */
    @RequiresPermission("create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PortTo create(@Valid @RequestBody PortTo to) {
        log.info("create port {}", to);
        return service.createFromDto(to);
    }

    /**
     * Update a port.
     * <p>
     * Reads raw JSON and pins {@code portType} to the stored entity's type, so a client cannot
     * change the type of an existing row and a payload that omits the discriminator still binds.
     */
    @RequiresPermission("update")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SneakyThrows
    public PortTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update port with id={}", id);

        Port existing = service.getById(id);
        Class<? extends PortTo> dtoClass = getDtoClassForEntity(existing);

        JsonNode root = objectMapper.readTree(jsonPayload);
        if (!(root instanceof ObjectNode objectNode)) {
            throw new IllegalRequestDataException("Request body must be a JSON object");
        }
        objectNode.put("portType", existing.getPortType());
        PortTo to = objectMapper.treeToValue(objectNode, dtoClass);

        return service.updateFromDto(id, to);
    }

    @RequiresPermission("delete")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete port {}", id);
        service.delete(id);
    }

    private Class<? extends PortTo> getDtoClassForEntity(Port port) {
        return switch (port.getPortType()) {
            case "ETHERNET" -> EthernetPortTo.class;
            case "FIBER" -> FiberPortTo.class;
            default -> throw new IllegalArgumentException("Unknown port type: " + port.getPortType());
        };
    }
}
