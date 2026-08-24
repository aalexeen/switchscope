package net.switchscope.web.catalog;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.installation.catalog.InstallableTypeMapper;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.security.permission.RequiresPermission;
import net.switchscope.service.installation.InstallableTypeService;
import net.switchscope.to.installation.catalog.InstallableTypeTo;
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
 * Controller for InstallableType catalog entities.
 * Custom implementation to support role-based field access validation.
 */
@Slf4j
@RestController
@RequestMapping(value = InstallableTypeController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("catalog.installable-type")
public class InstallableTypeController {

    static final String REST_URL = "/api/catalogs/installable-types";

    private final InstallableTypeService service;
    private final InstallableTypeMapper mapper;
    private final PartialUpdateReader partialUpdateReader;

    @RequiresPermission("read")
    @GetMapping
    public List<InstallableTypeTo> getAll() {
        log.info("getAll installable types");
        return mapper.toToList(service.getAll());
    }

    @RequiresPermission("read")
    @GetMapping("/{id}")
    public InstallableTypeTo get(@PathVariable UUID id) {
        log.info("get installable type {}", id);
        return mapper.toTo(service.getById(id));
    }

    @RequiresPermission("create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public InstallableTypeTo create(@Valid @RequestBody InstallableTypeTo dto) {
        log.info("create installable type {}", dto);
        return service.createFromDto(dto);
    }

    /**
     * Update installable type with role-based field access validation.
     * Validates field nullification against update policy before applying changes.
     */
    @RequiresPermission("update")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public InstallableTypeTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update installable type with id={}", id);
        PartialUpdate<InstallableTypeTo> update = partialUpdateReader.read(jsonPayload, InstallableTypeTo.class);
        return service.updateFromDto(id, update);
    }

    @RequiresPermission("delete")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete installable type {}", id);
        service.delete(id);
    }
}
