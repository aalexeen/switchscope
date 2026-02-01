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
import net.switchscope.mapper.component.device.RouterMapper;
import net.switchscope.model.component.device.Router;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.device.RouterService;
import net.switchscope.to.component.device.RouterTo;
import net.switchscope.web.AbstractCrudController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = RouterController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RouterController extends AbstractCrudController<Router, RouterTo> {

    static final String REST_URL = "/api/devices/routers";

    private final RouterService service;
    private final RouterMapper mapper;

    @Override
    protected CrudService<Router> getService() {
        return service;
    }

    @Override
    protected BaseMapper<Router, RouterTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "router";
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping
    public List<RouterTo> getAll() {
        log.info("getAll {}", getEntityName());
        return service.getAllAsDto();
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping("/{id}")
    public RouterTo get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        return service.getByIdAsDto(id);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RouterTo create(@RequestBody RouterTo dto) {
        log.info("create {} {}", getEntityName(), dto);
        Router entity = mapper.toEntity(dto);
        return service.createAndReturnDto(entity);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RouterTo update(@PathVariable UUID id, @RequestBody RouterTo dto) {
        log.info("update {} {} with id={}", getEntityName(), dto, id);
        Router entity = mapper.toEntity(dto);
        return service.updateAndReturnDto(id, entity);
    }
}
