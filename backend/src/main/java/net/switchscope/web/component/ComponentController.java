package net.switchscope.web.component;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.model.component.Component;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.model.component.connectivity.Connector;
import net.switchscope.model.component.connectivity.PatchPanel;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.model.component.device.NetworkSwitch;
import net.switchscope.model.component.device.Router;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.security.permission.RequiresPermission;
import net.switchscope.service.component.ComponentService;
import net.switchscope.to.component.ComponentTo;
import net.switchscope.to.component.connectivity.CableRunTo;
import net.switchscope.to.component.connectivity.ConnectorTo;
import net.switchscope.to.component.connectivity.PatchPanelTo;
import net.switchscope.to.component.device.AccessPointTo;
import net.switchscope.to.component.device.NetworkSwitchTo;
import net.switchscope.to.component.device.RouterTo;
import net.switchscope.to.component.housing.RackTo;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.ListResponse;

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
@PermissionResource("component")
public class ComponentController {

    static final String REST_URL = "/api/components";

    private final ComponentService service;
    private final ComponentPayloadReader payloadReader;


    @RequiresPermission("read")
    @GetMapping
    public Object getAll(@ParameterObject ListQuery query) {
        log.info("getAll components ({})", query);
        return ListResponse.of(query, service::getAllAsDto, () -> service.getPage(query));
    }

    @RequiresPermission("read")
    @GetMapping("/{id}")
    public ComponentTo get(@PathVariable UUID id) {
        log.info("get component {}", id);
        return service.getByIdAsDto(id);
    }

    /**
     * Create a component of any type.
     * <p>
     * The concrete type is derived from {@code componentTypeId} - see {@link ComponentPayloadReader}.
     * The client sends no discriminator; if it sends one anyway it is checked, not trusted.
     */
    @RequiresPermission("create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ComponentTo create(@RequestBody String jsonPayload) {
        ComponentTo to = payloadReader.readForCreate(jsonPayload, ComponentTo.class);
        log.info("create component {}", to);
        return service.createFromDto(to);
    }

    /**
     * Update component.
     * <p>
     * Accepts raw JSON so that a field the payload omits and a field it sends as {@code null} stay
     * distinguishable; the discriminator is taken from the stored entity rather than the payload,
     * so a client cannot change the type of an existing row and a payload that omits
     * {@code componentClass} still binds.
     */
    @RequiresPermission("update")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ComponentTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update component with id={}", id);

        Component existing = service.getById(id);
        Class<? extends ComponentTo> dtoClass = getDtoClassForEntity(existing);
        log.debug("Entity type: {}, DTO class: {}", existing.getClass().getSimpleName(), dtoClass.getSimpleName());

        PartialUpdate<? extends ComponentTo> update =
                payloadReader.readForUpdate(jsonPayload, existing.getDiscriminatorValue(), dtoClass);

        return service.updateFromDto(id, update);
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

    @RequiresPermission("delete")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete component {}", id);
        service.delete(id);
    }

}
