package net.switchscope.web.component.device;

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
import net.switchscope.mapper.component.device.AccessPointMapper;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.device.AccessPointService;
import net.switchscope.to.component.device.AccessPointTo;
import net.switchscope.web.AbstractCrudController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = AccessPointController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AccessPointController extends AbstractCrudController<AccessPoint, AccessPointTo> {

    static final String REST_URL = "/api/devices/access-points";

    private final AccessPointService service;
    private final AccessPointMapper mapper;

    @Override
    protected CrudService<AccessPoint> getService() {
        return service;
    }

    @Override
    protected BaseMapper<AccessPoint, AccessPointTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "access point";
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping
    public List<AccessPointTo> getAll() {
        log.info("getAll {}", getEntityName());
        return service.getAllAsDto();
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping("/{id}")
    public AccessPointTo get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        return service.getByIdAsDto(id);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AccessPointTo create(@RequestBody AccessPointTo dto) {
        log.info("create {} {}", getEntityName(), dto);
        AccessPoint entity = mapper.toEntity(dto);
        return service.createAndReturnDto(entity);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccessPointTo update(@PathVariable UUID id, @RequestBody AccessPointTo dto) {
        log.info("update {} {} with id={}", getEntityName(), dto, id);
        AccessPoint entity = mapper.toEntity(dto);
        return service.updateAndReturnDto(id, entity);
    }
}
