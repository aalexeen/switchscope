package net.switchscope.web.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.SneakyThrows;
import net.switchscope.mapper.component.catalog.connectivity.CableRunModelMapper;
import net.switchscope.mapper.component.catalog.connectivity.ConnectorModelMapper;
import net.switchscope.mapper.component.catalog.connectivity.PatchPanelModelMapper;
import net.switchscope.mapper.component.catalog.device.AccessPointModelMapper;
import net.switchscope.mapper.component.catalog.device.RouterModelMapper;
import net.switchscope.mapper.component.catalog.device.SwitchModelMapper;
import net.switchscope.mapper.component.catalog.housing.RackModelMapper;
import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.model.component.catalog.connectiviy.CableRunModel;
import net.switchscope.model.component.catalog.connectiviy.ConnectorModel;
import net.switchscope.model.component.catalog.connectiviy.PatchPanelModel;
import net.switchscope.model.component.catalog.device.AccessPointModel;
import net.switchscope.model.component.catalog.device.RouterModel;
import net.switchscope.model.component.catalog.device.SwitchModel;
import net.switchscope.model.component.catalog.housing.RackModelEntity;
import net.switchscope.service.component.catalog.ComponentModelService;
import net.switchscope.to.component.catalog.ComponentModelTo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for ComponentModel catalog entities.
 * Uses custom implementation due to polymorphic model hierarchy.
 * Similar to PortController pattern.
 */
@Slf4j
@RestController
@RequestMapping(value = ComponentModelController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComponentModelController {

    static final String REST_URL = "/api/catalogs/component-models";

    private final ComponentModelService service;
    private final ObjectMapper objectMapper;

    // Polymorphic mappers for different model types
    private final SwitchModelMapper switchModelMapper;
    private final RouterModelMapper routerModelMapper;
    private final AccessPointModelMapper accessPointModelMapper;
    private final CableRunModelMapper cableRunModelMapper;
    private final ConnectorModelMapper connectorModelMapper;
    private final PatchPanelModelMapper patchPanelModelMapper;
    private final RackModelMapper rackModelMapper;

    @GetMapping
    public List<ComponentModelTo> getAll() {
        log.info("getAll component models");
        return service.getAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ComponentModelTo get(@PathVariable UUID id) {
        log.info("get component model {}", id);
        return mapToDto(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ComponentModelTo create(@RequestBody ComponentModelTo to) {
        log.info("create component model {}", to);
        ComponentModel entity = mapToEntity(to);
        return mapToDto(service.create(entity));
    }

    /**
     * Update component model.
     * Accepts raw JSON and determines concrete DTO type from existing entity in DB.
     * This avoids Jackson polymorphic deserialization issues with abstract ComponentModelTo.
     * Validates field nullification against role-based update policy.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SneakyThrows
    public ComponentModelTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update component model with id={}", id);

        // 1. Get entity type to determine DTO class
        ComponentModel existing = service.getById(id);
        Class<? extends ComponentModelTo> dtoClass = getDtoClassForEntity(existing);
        log.debug("Entity type: {}, DTO class: {}", existing.getClass().getSimpleName(), dtoClass.getSimpleName());

        // 2. Deserialize JSON to concrete DTO type
        ComponentModelTo dto = objectMapper.readValue(jsonPayload, dtoClass);

        // 3. Extract present fields for policy validation
        Map<String, JsonNode> presentFields = extractPresentFields(jsonPayload);

        // 4. Delegate to service (handles validation, FK changes, mapping, save in transaction)
        ComponentModel updated = service.updateWithPolicyValidation(
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete component model {}", id);
        service.delete(id);
    }

    // Helper methods for polymorphic mapping
    @SuppressWarnings("unchecked")
    private ComponentModelTo mapToDto(ComponentModel model) {
        if (model instanceof SwitchModel) {
            ComponentModelTo to = switchModelMapper.toTo((SwitchModel) model);
            to.setDiscriminatorType("SWITCH_MODEL");
            return to;
        } else if (model instanceof RouterModel) {
            ComponentModelTo to = routerModelMapper.toTo((RouterModel) model);
            to.setDiscriminatorType("ROUTER_MODEL");
            return to;
        } else if (model instanceof AccessPointModel) {
            ComponentModelTo to = accessPointModelMapper.toTo((AccessPointModel) model);
            to.setDiscriminatorType("ACCESS_POINT_MODEL");
            return to;
        } else if (model instanceof CableRunModel) {
            ComponentModelTo to = cableRunModelMapper.toTo((CableRunModel) model);
            to.setDiscriminatorType("CABLE_RUN_MODEL");
            return to;
        } else if (model instanceof ConnectorModel) {
            ComponentModelTo to = connectorModelMapper.toTo((ConnectorModel) model);
            to.setDiscriminatorType("CONNECTOR_MODEL");
            return to;
        } else if (model instanceof PatchPanelModel) {
            ComponentModelTo to = patchPanelModelMapper.toTo((PatchPanelModel) model);
            to.setDiscriminatorType("PATCH_PANEL_MODEL");
            return to;
        } else if (model instanceof RackModelEntity) {
            ComponentModelTo to = rackModelMapper.toTo((RackModelEntity) model);
            to.setDiscriminatorType("RACK_MODEL");
            return to;
        } else {
            throw new IllegalArgumentException("Unknown component model type: " + model.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    private ComponentModel mapToEntity(ComponentModelTo to) {
        // Determine model type from TO class name or discriminator field
        String className = to.getClass().getSimpleName();
        if (className.contains("Switch")) {
            return switchModelMapper.toEntity((net.switchscope.to.component.catalog.device.SwitchModelTo) to);
        } else if (className.contains("Router")) {
            return routerModelMapper.toEntity((net.switchscope.to.component.catalog.device.RouterModelTo) to);
        } else if (className.contains("AccessPoint")) {
            return accessPointModelMapper.toEntity((net.switchscope.to.component.catalog.device.AccessPointModelTo) to);
        } else if (className.contains("CableRun")) {
            return cableRunModelMapper.toEntity((net.switchscope.to.component.catalog.connectivity.CableRunModelTo) to);
        } else if (className.contains("Connector")) {
            return connectorModelMapper.toEntity((net.switchscope.to.component.catalog.connectivity.ConnectorModelTo) to);
        } else if (className.contains("PatchPanel")) {
            return patchPanelModelMapper.toEntity((net.switchscope.to.component.catalog.connectivity.PatchPanelModelTo) to);
        } else if (className.contains("Rack")) {
            return rackModelMapper.toEntity((net.switchscope.to.component.catalog.housing.RackModelTo) to);
        } else {
            throw new IllegalArgumentException("Unknown component model TO type: " + className);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateFromDto(ComponentModel model, ComponentModelTo to) {
        if (model instanceof SwitchModel && to instanceof net.switchscope.to.component.catalog.device.SwitchModelTo) {
            switchModelMapper.updateFromTo((SwitchModel) model, (net.switchscope.to.component.catalog.device.SwitchModelTo) to);
        } else if (model instanceof RouterModel && to instanceof net.switchscope.to.component.catalog.device.RouterModelTo) {
            routerModelMapper.updateFromTo((RouterModel) model, (net.switchscope.to.component.catalog.device.RouterModelTo) to);
        } else if (model instanceof AccessPointModel && to instanceof net.switchscope.to.component.catalog.device.AccessPointModelTo) {
            accessPointModelMapper.updateFromTo((AccessPointModel) model, (net.switchscope.to.component.catalog.device.AccessPointModelTo) to);
        } else if (model instanceof CableRunModel && to instanceof net.switchscope.to.component.catalog.connectivity.CableRunModelTo) {
            cableRunModelMapper.updateFromTo((CableRunModel) model, (net.switchscope.to.component.catalog.connectivity.CableRunModelTo) to);
        } else if (model instanceof ConnectorModel && to instanceof net.switchscope.to.component.catalog.connectivity.ConnectorModelTo) {
            connectorModelMapper.updateFromTo((ConnectorModel) model, (net.switchscope.to.component.catalog.connectivity.ConnectorModelTo) to);
        } else if (model instanceof PatchPanelModel && to instanceof net.switchscope.to.component.catalog.connectivity.PatchPanelModelTo) {
            patchPanelModelMapper.updateFromTo((PatchPanelModel) model, (net.switchscope.to.component.catalog.connectivity.PatchPanelModelTo) to);
        } else if (model instanceof RackModelEntity && to instanceof net.switchscope.to.component.catalog.housing.RackModelTo) {
            rackModelMapper.updateFromTo((RackModelEntity) model, (net.switchscope.to.component.catalog.housing.RackModelTo) to);
        } else {
            throw new IllegalArgumentException("Component model type mismatch: entity=" + model.getClass().getName() + ", to=" + to.getClass().getName());
        }
    }

    /**
     * Determines the concrete DTO class based on entity type.
     * Used for deserializing JSON into the correct DTO subclass.
     */
    private Class<? extends ComponentModelTo> getDtoClassForEntity(ComponentModel entity) {
        if (entity instanceof SwitchModel) {
            return net.switchscope.to.component.catalog.device.SwitchModelTo.class;
        } else if (entity instanceof RouterModel) {
            return net.switchscope.to.component.catalog.device.RouterModelTo.class;
        } else if (entity instanceof AccessPointModel) {
            return net.switchscope.to.component.catalog.device.AccessPointModelTo.class;
        } else if (entity instanceof CableRunModel) {
            return net.switchscope.to.component.catalog.connectivity.CableRunModelTo.class;
        } else if (entity instanceof ConnectorModel) {
            return net.switchscope.to.component.catalog.connectivity.ConnectorModelTo.class;
        } else if (entity instanceof PatchPanelModel) {
            return net.switchscope.to.component.catalog.connectivity.PatchPanelModelTo.class;
        } else if (entity instanceof RackModelEntity) {
            return net.switchscope.to.component.catalog.housing.RackModelTo.class;
        } else {
            throw new IllegalArgumentException("Unknown component model entity type: " + entity.getClass().getName());
        }
    }
}
