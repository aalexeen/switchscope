package net.switchscope.web.catalog;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.security.permission.RequiresPermission;
import net.switchscope.service.location.LocationTypeService;
import net.switchscope.to.location.catalog.LocationTypeTo;
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
 * Controller for LocationType catalog entities.
 * Custom implementation to support role-based field access validation.
 */
@Slf4j
@RestController
@RequestMapping(value = LocationTypeController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("catalog.location-type")
public class LocationTypeController {

    static final String REST_URL = "/api/catalogs/location-types";

    private final LocationTypeService service;
    private final PartialUpdateReader partialUpdateReader;

    @RequiresPermission("read")
    @GetMapping
    public List<LocationTypeTo> getAll() {
        log.info("getAll location types");
        return service.getAllAsDto();
    }

    @RequiresPermission("read")
    @GetMapping("/{id}")
    public LocationTypeTo get(@PathVariable UUID id) {
        log.info("get location type {}", id);
        return service.getByIdAsDto(id);
    }

    @RequiresPermission("create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public LocationTypeTo create(@Valid @RequestBody LocationTypeTo dto) {
        log.info("create location type {}", dto);
        return service.createFromDto(dto);
    }

    /**
     * Update location type with role-based field access validation.
     * Validates field nullification against update policy before applying changes.
     */
    @RequiresPermission("update")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public LocationTypeTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update location type with id={}", id);
        PartialUpdate<LocationTypeTo> update = partialUpdateReader.read(jsonPayload, LocationTypeTo.class);
        return service.updateFromDto(id, update);
    }

    @RequiresPermission("delete")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete location type {}", id);
        service.delete(id);
    }
}
