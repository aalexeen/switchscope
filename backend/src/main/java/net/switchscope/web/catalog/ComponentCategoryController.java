package net.switchscope.web.catalog;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.component.catalog.ComponentCategoryMapper;
import net.switchscope.model.component.ComponentCategoryEntity;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.security.permission.RequiresPermission;
import net.switchscope.service.component.ComponentCategoryService;
import net.switchscope.to.component.catalog.ComponentCategoryTo;
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
 * Controller for ComponentCategory catalog entities.
 * Custom implementation to support role-based field access validation.
 */
@Slf4j
@RestController
@RequestMapping(value = ComponentCategoryController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("catalog.component-category")
public class ComponentCategoryController {

    static final String REST_URL = "/api/catalogs/component-categories";

    private final ComponentCategoryService service;
    private final ComponentCategoryMapper mapper;
    private final PartialUpdateReader partialUpdateReader;

    @RequiresPermission("read")
    @GetMapping
    public List<ComponentCategoryTo> getAll() {
        log.info("getAll component categories");
        return mapper.toToList(service.getAll());
    }

    @RequiresPermission("read")
    @GetMapping("/{id}")
    public ComponentCategoryTo get(@PathVariable UUID id) {
        log.info("get component category {}", id);
        return mapper.toTo(service.getById(id));
    }

    @RequiresPermission("create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ComponentCategoryTo create(@Valid @RequestBody ComponentCategoryTo dto) {
        log.info("create component category {}", dto);
        ComponentCategoryEntity entity = mapper.toEntity(dto);
        ComponentCategoryEntity created = service.create(entity);
        return mapper.toTo(created);
    }

    /**
     * Update component category with role-based field access validation.
     * Validates field nullification against update policy before applying changes.
     */
    @RequiresPermission("update")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ComponentCategoryTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update component category with id={}", id);
        PartialUpdate<ComponentCategoryTo> update = partialUpdateReader.read(jsonPayload, ComponentCategoryTo.class);
        ComponentCategoryEntity updated = service.updateFromDto(id, update);
        return mapper.toTo(updated);
    }

    @RequiresPermission("delete")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete component category {}", id);
        service.delete(id);
    }
}
