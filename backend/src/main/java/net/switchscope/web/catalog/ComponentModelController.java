package net.switchscope.web.catalog;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
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

    // Polymorphic mappers for different model types
    private final SwitchModelMapper switchModelMapper;
    private final RouterModelMapper routerModelMapper;
    private final AccessPointModelMapper accessPointModelMapper;
    private final CableRunModelMapper cableRunModelMapper;
    private final ConnectorModelMapper connectorModelMapper;
    private final PatchPanelModelMapper patchPanelModelMapper;
    private final RackModelMapper rackModelMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<ComponentModelTo> getAll() {
        log.info("getAll component models");
        return service.getAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
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

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ComponentModelTo update(@PathVariable UUID id, @RequestBody ComponentModelTo to) {
        log.info("update component model {} with id={}", to, id);
        ComponentModel entity = service.getById(id);
        updateFromDto(entity, to);
        return mapToDto(service.update(id, entity));
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
            return switchModelMapper.toTo((SwitchModel) model);
        } else if (model instanceof RouterModel) {
            return routerModelMapper.toTo((RouterModel) model);
        } else if (model instanceof AccessPointModel) {
            return accessPointModelMapper.toTo((AccessPointModel) model);
        } else if (model instanceof CableRunModel) {
            return cableRunModelMapper.toTo((CableRunModel) model);
        } else if (model instanceof ConnectorModel) {
            return connectorModelMapper.toTo((ConnectorModel) model);
        } else if (model instanceof PatchPanelModel) {
            return patchPanelModelMapper.toTo((PatchPanelModel) model);
        } else if (model instanceof RackModelEntity) {
            return rackModelMapper.toTo((RackModelEntity) model);
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
}
