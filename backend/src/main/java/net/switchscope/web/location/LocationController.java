package net.switchscope.web.location;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.location.LocationMapper;
import net.switchscope.model.location.Location;
import net.switchscope.service.location.LocationService;
import net.switchscope.to.location.LocationTo;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = LocationController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LocationController {

    static final String REST_URL = "/api/locations";

    private final LocationService service;
    private final LocationMapper mapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<LocationTo> getAll() {
        log.info("getAll locations");
        return service.getAll().stream()
                .map(mapper::toTo)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public LocationTo get(@PathVariable UUID id) {
        log.info("get location {}", id);
        return mapper.toTo(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public LocationTo create(@RequestBody LocationTo to) {
        log.info("create location {}", to);
        Location entity = mapper.toEntity(to);
        // Service should resolve foreign keys (type, parentLocation)
        return mapper.toTo(service.create(entity));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public LocationTo update(@PathVariable UUID id, @RequestBody LocationTo to) {
        log.info("update location {} with id={}", to, id);
        Location entity = service.getById(id);
        mapper.updateFromTo(entity, to);
        // Service should resolve foreign keys
        return mapper.toTo(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete location {}", id);
        service.delete(id);
    }
}
