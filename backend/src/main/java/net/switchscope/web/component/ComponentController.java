package net.switchscope.web.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

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
        List<Component> components = service.getAll();
        log.debug("Found {} components", components.size());
        return components.stream()
                .map(this::mapToDto)
                .filter(dto -> {
                    if (dto == null) {
                        log.warn("Filtered out null DTO from result");
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ComponentTo get(@PathVariable UUID id) {
        log.info("get component {}", id);
        return mapToDto(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ComponentTo create(@RequestBody ComponentTo to) {
        log.info("create component {}", to);
        Component entity = mapToEntity(to);
        return mapToDto(service.create(entity));
    }

    /**
     * Update component.
     * Accepts raw JSON and determines concrete DTO type from existing entity in DB.
     * This avoids Jackson polymorphic deserialization issues with abstract ComponentTo.
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

        // 2. Deserialize JSON to concrete DTO type
        ComponentTo dto = objectMapper.readValue(jsonPayload, dtoClass);

        // 3. Extract present fields for policy validation
        Map<String, JsonNode> presentFields = extractPresentFields(jsonPayload);

        // 4. Delegate to service (handles validation, FK changes, mapping, save in transaction)
        Component updated = service.updateWithPolicyValidation(
                id,
                dto,
                dtoClass,
                presentFields,
                this::updateFromDto
        );

        return mapToDto(updated);
    }

    /**
     * Extracts all fields present in JSON payload with their values.
     * Used to detect explicitly set null values vs absent fields.
     */
    @SneakyThrows
    private Map<String, JsonNode> extractPresentFields(String jsonPayload) {
        Map<String, JsonNode> fields = new HashMap<>();
        JsonNode root = objectMapper.readTree(jsonPayload);
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

    // Helper methods for polymorphic mapping
    @SuppressWarnings("unchecked")
    private ComponentTo mapToDto(Component component) {
        if (component == null) {
            log.warn("Attempting to map null component");
            return null;
        }

        try {
            if (component instanceof NetworkSwitch) {
                return networkSwitchMapper.toTo((NetworkSwitch) component);
            } else if (component instanceof Router) {
                return routerMapper.toTo((Router) component);
            } else if (component instanceof AccessPoint) {
                return accessPointMapper.toTo((AccessPoint) component);
            } else if (component instanceof CableRun) {
                return cableRunMapper.toTo((CableRun) component);
            } else if (component instanceof Connector) {
                return connectorMapper.toTo((Connector) component);
            } else if (component instanceof PatchPanel) {
                return patchPanelMapper.toTo((PatchPanel) component);
            } else if (component instanceof Rack) {
                return rackMapper.toTo((Rack) component);
            } else {
                log.error("Unknown component type: {} for component id: {}", component.getClass().getName(), component.getId());
                throw new IllegalArgumentException("Unknown component type: " + component.getClass().getName());
            }
        } catch (Exception e) {
            log.error("Error mapping component id: {}, type: {}", component.getId(), component.getClass().getName(), e);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private Component mapToEntity(ComponentTo to) {
        // Determine component type from TO class name or discriminator field
        String className = to.getClass().getSimpleName();
        if (className.contains("NetworkSwitch")) {
            return networkSwitchMapper.toEntity((net.switchscope.to.component.device.NetworkSwitchTo) to);
        } else if (className.contains("Router")) {
            return routerMapper.toEntity((net.switchscope.to.component.device.RouterTo) to);
        } else if (className.contains("AccessPoint")) {
            return accessPointMapper.toEntity((net.switchscope.to.component.device.AccessPointTo) to);
        } else if (className.contains("CableRun")) {
            return cableRunMapper.toEntity((net.switchscope.to.component.connectivity.CableRunTo) to);
        } else if (className.contains("Connector")) {
            return connectorMapper.toEntity((net.switchscope.to.component.connectivity.ConnectorTo) to);
        } else if (className.contains("PatchPanel")) {
            return patchPanelMapper.toEntity((net.switchscope.to.component.connectivity.PatchPanelTo) to);
        } else if (className.contains("Rack")) {
            return rackMapper.toEntity((net.switchscope.to.component.housing.RackTo) to);
        } else {
            throw new IllegalArgumentException("Unknown component TO type: " + className);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateFromDto(Component component, ComponentTo to) {
        if (component instanceof NetworkSwitch && to instanceof net.switchscope.to.component.device.NetworkSwitchTo) {
            networkSwitchMapper.updateFromTo((NetworkSwitch) component, (net.switchscope.to.component.device.NetworkSwitchTo) to);
        } else if (component instanceof Router && to instanceof net.switchscope.to.component.device.RouterTo) {
            routerMapper.updateFromTo((Router) component, (net.switchscope.to.component.device.RouterTo) to);
        } else if (component instanceof AccessPoint && to instanceof net.switchscope.to.component.device.AccessPointTo) {
            accessPointMapper.updateFromTo((AccessPoint) component, (net.switchscope.to.component.device.AccessPointTo) to);
        } else if (component instanceof CableRun && to instanceof net.switchscope.to.component.connectivity.CableRunTo) {
            cableRunMapper.updateFromTo((CableRun) component, (net.switchscope.to.component.connectivity.CableRunTo) to);
        } else if (component instanceof Connector && to instanceof net.switchscope.to.component.connectivity.ConnectorTo) {
            connectorMapper.updateFromTo((Connector) component, (net.switchscope.to.component.connectivity.ConnectorTo) to);
        } else if (component instanceof PatchPanel && to instanceof net.switchscope.to.component.connectivity.PatchPanelTo) {
            patchPanelMapper.updateFromTo((PatchPanel) component, (net.switchscope.to.component.connectivity.PatchPanelTo) to);
        } else if (component instanceof Rack && to instanceof net.switchscope.to.component.housing.RackTo) {
            rackMapper.updateFromTo((Rack) component, (net.switchscope.to.component.housing.RackTo) to);
        } else {
            throw new IllegalArgumentException("Component type mismatch: entity=" + component.getClass().getName() + ", to=" + to.getClass().getName());
        }
    }
}
