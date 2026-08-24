package net.switchscope.web.catalog;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.installation.catalog.InstallationStatusMapper;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.security.permission.RequiresPermission;
import net.switchscope.service.installation.InstallationStatusService;
import net.switchscope.to.installation.catalog.InstallationStatusTo;
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
 * Controller for InstallationStatus catalog entities.
 * Custom implementation to support role-based field access validation.
 */
@Slf4j
@RestController
@RequestMapping(value = InstallationStatusController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("catalog.installation-status")
public class InstallationStatusController {

    static final String REST_URL = "/api/catalogs/installation-statuses";

    private final InstallationStatusService service;
    private final InstallationStatusMapper mapper;
    private final PartialUpdateReader partialUpdateReader;

    @RequiresPermission("read")
    @GetMapping
    public List<InstallationStatusTo> getAll() {
        log.info("getAll installation statuses");
        return mapper.toToList(service.getAll());
    }

    @RequiresPermission("read")
    @GetMapping("/{id}")
    public InstallationStatusTo get(@PathVariable UUID id) {
        log.info("get installation status {}", id);
        return mapper.toTo(service.getById(id));
    }

    @RequiresPermission("create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public InstallationStatusTo create(@Valid @RequestBody InstallationStatusTo dto) {
        log.info("create installation status {}", dto);
        return service.createFromDto(dto);
    }

    /**
     * Update installation status with role-based field access validation.
     * Validates field nullification against update policy before applying changes.
     */
    @RequiresPermission("update")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public InstallationStatusTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update installation status with id={}", id);
        PartialUpdate<InstallationStatusTo> update = partialUpdateReader.read(jsonPayload, InstallationStatusTo.class);
        return service.updateFromDto(id, update);
    }

    @RequiresPermission("delete")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete installation status {}", id);
        service.delete(id);
    }
}
