package net.switchscope.web.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.location.LocationMapper;
import net.switchscope.model.location.Location;
import net.switchscope.service.CrudService;
import net.switchscope.service.location.LocationService;
import net.switchscope.to.location.LocationTo;
import net.switchscope.web.AbstractCrudController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = LocationController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LocationController extends AbstractCrudController<Location, LocationTo> {

    static final String REST_URL = "/api/locations";

    private final LocationService service;
    private final LocationMapper mapper;

    @Override
    protected CrudService<Location> getService() {
        return service;
    }

    @Override
    protected BaseMapper<Location, LocationTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "location";
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping
    public List<LocationTo> getAll() {
        log.info("getAll {}", getEntityName());
        return service.getAllAsDto();
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping("/{id}")
    public LocationTo get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        return service.getByIdAsDto(id);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public LocationTo create(@RequestBody LocationTo dto) {
        log.info("create {} {}", getEntityName(), dto);
        Location entity = mapper.toEntity(dto);
        return service.createAndReturnDto(entity);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public LocationTo update(@PathVariable UUID id, @RequestBody LocationTo dto) {
        log.info("update {} {} with id={}", getEntityName(), dto, id);
        Location entity = mapper.toEntity(dto);
        return service.updateAndReturnDto(id, entity);
    }
}
