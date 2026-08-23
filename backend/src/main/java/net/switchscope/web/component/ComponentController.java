package net.switchscope.web.component;

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
import net.switchscope.mapper.component.connectivity.CableRunMapper;
import net.switchscope.mapper.component.connectivity.ConnectorMapper;
import net.switchscope.mapper.component.connectivity.PatchPanelMapper;
import net.switchscope.mapper.component.device.AccessPointMapper;
import net.switchscope.mapper.component.device.NetworkSwitchMapper;
import net.switchscope.mapper.component.device.RouterMapper;
import net.switchscope.mapper.component.housing.RackMapper;
import net.switchscope.model.component.Component;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.model.component.connectivity.Connector;
import net.switchscope.model.component.connectivity.PatchPanel;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.model.component.device.NetworkSwitch;
import net.switchscope.model.component.device.Router;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.service.component.ComponentService;
import net.switchscope.to.component.ComponentTo;
import net.switchscope.to.component.connectivity.CableRunTo;
import net.switchscope.to.component.connectivity.ConnectorTo;
import net.switchscope.to.component.connectivity.PatchPanelTo;
import net.switchscope.to.component.device.AccessPointTo;
import net.switchscope.to.component.device.NetworkSwitchTo;
import net.switchscope.to.component.device.RouterTo;
import net.switchscope.to.component.housing.RackTo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for Component entities.
 * Uses custom implementation due to polymorphic component hierarchy (Single Table Inheritance).
 * Similar to PortController and ComponentModelController patterns.
 */
@Slf4j
@RestController
@RequestMapping(value = ComponentController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComponentController {

    static final String REST_URL = "/api/components";

    private final ComponentService service;
    private final ObjectMapper objectMapper;

    // Polymorphic mappers for different component types
    private final NetworkSwitchMapper networkSwitchMapper;
    private final RouterMapper routerMapper;
    private final AccessPointMapper accessPointMapper;
    private final CableRunMapper cableRunMapper;
    private final ConnectorMapper connectorMapper;
    private final PatchPanelMapper patchPanelMapper;
    private final RackMapper rackMapper;

    @GetMapping
    public List<ComponentTo> getAll() {
        log.info("getAll components");
        return service.getAllAsDto();
    }

    @GetMapping("/{id}")
    public ComponentTo get(@PathVariable UUID id) {
        log.info("get component {}", id);
        return service.getByIdAsDto(id);
    }

    /**
     * Create a component of any type.
     * <p>
     * {@link ComponentTo} is abstract, so the payload must carry the {@code componentClass}
     * discriminator (NETWORK_SWITCH, ROUTER, ...); Jackson uses it to pick the concrete DTO.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ComponentTo create(@Valid @RequestBody ComponentTo to) {
        log.info("create component {}", to);
        return service.createFromDto(to);
    }

    /**
     * Update component.
     * Accepts raw JSON and determines concrete DTO type from existing entity in DB.
     * The discriminator is taken from the stored entity rather than the payload, so a client cannot
     * change the type of an existing row - and a payload that omits {@code componentClass} still binds.
     * Validates field nullification against role-based update policy.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SneakyThrows
    public ComponentTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update component with id={}", id);

        // 1. Get entity type to determine DTO class
        Component existing = service.getById(id);
        Class<? extends ComponentTo> dtoClass = getDtoClassForEntity(existing);
        log.debug("Entity type: {}, DTO class: {}", existing.getClass().getSimpleName(), dtoClass.getSimpleName());

        // 2. Deserialize JSON to concrete DTO type, pinning the discriminator to the stored type
        ObjectNode root = readObject(jsonPayload);
        root.put("componentClass", existing.getDiscriminatorValue());
        ComponentTo dto = objectMapper.treeToValue(root, dtoClass);

        // 3. Extract present fields for policy validation
        Map<String, JsonNode> presentFields = extractPresentFields(root);

        // 4. Delegate to service (handles validation, FK changes, mapping, save, and DTO conversion in transaction)
        return service.updateWithPolicyValidationAndReturnDto(
                id,
                dto,
                dtoClass,
                presentFields,
                this::updateFromDto
        );
    }

    @SneakyThrows
    private ObjectNode readObject(String jsonPayload) {
        JsonNode root = objectMapper.readTree(jsonPayload);
        if (!(root instanceof ObjectNode objectNode)) {
            throw new IllegalRequestDataException("Request body must be a JSON object");
        }
        return objectNode;
    }

    /**
     * Extracts all fields present in JSON payload with their values.
     * Used to detect explicitly set null values vs absent fields.
     */
    private Map<String, JsonNode> extractPresentFields(ObjectNode root) {
        Map<String, JsonNode> fields = new HashMap<>();
        Iterator<String> fieldNames = root.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            fields.put(fieldName, root.get(fieldName));
        }
        return fields;
    }

    /**
     * Determines the concrete DTO class based on entity type.
     */
    private Class<? extends ComponentTo> getDtoClassForEntity(Component entity) {
        if (entity instanceof NetworkSwitch) {
            return NetworkSwitchTo.class;
        } else if (entity instanceof Router) {
            return RouterTo.class;
        } else if (entity instanceof AccessPoint) {
            return AccessPointTo.class;
        } else if (entity instanceof CableRun) {
            return CableRunTo.class;
        } else if (entity instanceof Connector) {
            return ConnectorTo.class;
        } else if (entity instanceof PatchPanel) {
            return PatchPanelTo.class;
        } else if (entity instanceof Rack) {
            return RackTo.class;
        } else {
            throw new IllegalArgumentException("Unknown component entity type: " + entity.getClass().getName());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete component {}", id);
        service.delete(id);
    }

    private void updateFromDto(Component component, ComponentTo to) {
        if (component instanceof NetworkSwitch entity && to instanceof NetworkSwitchTo dto) {
            networkSwitchMapper.updateFromTo(entity, dto);
        } else if (component instanceof Router entity && to instanceof RouterTo dto) {
            routerMapper.updateFromTo(entity, dto);
        } else if (component instanceof AccessPoint entity && to instanceof AccessPointTo dto) {
            accessPointMapper.updateFromTo(entity, dto);
        } else if (component instanceof CableRun entity && to instanceof CableRunTo dto) {
            cableRunMapper.updateFromTo(entity, dto);
        } else if (component instanceof Connector entity && to instanceof ConnectorTo dto) {
            connectorMapper.updateFromTo(entity, dto);
        } else if (component instanceof PatchPanel entity && to instanceof PatchPanelTo dto) {
            patchPanelMapper.updateFromTo(entity, dto);
        } else if (component instanceof Rack entity && to instanceof RackTo dto) {
            rackMapper.updateFromTo(entity, dto);
        } else {
            throw new IllegalArgumentException("Component type mismatch: entity=" + component.getClass().getName()
                    + ", to=" + to.getClass().getName());
        }
    }
}
