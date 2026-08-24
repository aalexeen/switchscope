package net.switchscope.web.catalog;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.component.catalog.ComponentTypeMapper;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.security.permission.RequiresPermission;
import net.switchscope.service.component.ComponentTypeService;
import net.switchscope.service.component.InstallableComponentRegistry;
import net.switchscope.to.component.catalog.ComponentTypeTo;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.web.payload.PartialUpdateReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller for ComponentType catalog entities.
 * Custom implementation to support role-based field access validation.
 */
@Slf4j
@RestController
@RequestMapping(value = ComponentTypeController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("catalog.component-type")
public class ComponentTypeController {

    static final String REST_URL = "/api/catalogs/component-types";

    private final ComponentTypeService service;
    private final ComponentTypeMapper mapper;
    private final InstallableComponentRegistry registry;
    private final PartialUpdateReader partialUpdateReader;

    @RequiresPermission("read")
    @GetMapping
    public List<ComponentTypeTo> getAll() {
        log.info("getAll component types");
        List<ComponentTypeTo> tos = mapper.toToList(service.getAll());
        tos.forEach(this::markImplementation);
        return tos;
    }

    @RequiresPermission("read")
    @GetMapping("/{id}")
    public ComponentTypeTo get(@PathVariable UUID id) {
        log.info("get component type {}", id);
        return markImplementation(mapper.toTo(service.getById(id)));
    }

    /**
     * Tells the client which catalog entries can actually be instantiated, and under which
     * discriminator. The UI needs this to decide whether to offer a "create" action for a type and
     * which type-specific form to render; the same mapping is what the server uses to derive the
     * discriminator on POST, so the two cannot drift apart.
     */
    private ComponentTypeTo markImplementation(ComponentTypeTo to) {
        boolean implemented = registry.isImplemented(to.getCode());
        to.setImplemented(implemented);
        to.setComponentClass(implemented ? to.getCode() : null);
        return to;
    }

    @RequiresPermission("create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ComponentTypeTo create(@Valid @RequestBody ComponentTypeTo dto) {
        log.info("create component type {}", dto);
        return service.createFromDto(dto);
    }

    /**
     * Update component type with role-based field access validation.
     * Validates field nullification against update policy before applying changes.
     */
    @RequiresPermission("update")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ComponentTypeTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update component type with id={}", id);
        PartialUpdate<ComponentTypeTo> update = partialUpdateReader.read(jsonPayload, ComponentTypeTo.class);
        return service.updateFromDto(id, update);
    }

    @RequiresPermission("delete")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete component type {}", id);
        service.delete(id);
    }
}
