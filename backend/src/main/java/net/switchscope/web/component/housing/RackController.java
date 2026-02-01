package net.switchscope.web.component.housing;

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
import net.switchscope.mapper.component.housing.RackMapper;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.housing.RackService;
import net.switchscope.to.component.housing.RackTo;
import net.switchscope.web.AbstractCrudController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = RackController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RackController extends AbstractCrudController<Rack, RackTo> {

    static final String REST_URL = "/api/housing/racks";

    private final RackService service;
    private final RackMapper mapper;

    @Override
    protected CrudService<Rack> getService() {
        return service;
    }

    @Override
    protected BaseMapper<Rack, RackTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "rack";
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping
    public List<RackTo> getAll() {
        log.info("getAll {}", getEntityName());
        return service.getAllAsDto();
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping("/{id}")
    public RackTo get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        return service.getByIdAsDto(id);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RackTo create(@RequestBody RackTo dto) {
        log.info("create {} {}", getEntityName(), dto);
        Rack entity = mapper.toEntity(dto);
        return service.createAndReturnDto(entity);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RackTo update(@PathVariable UUID id, @RequestBody RackTo dto) {
        log.info("update {} {} with id={}", getEntityName(), dto, id);
        Rack entity = mapper.toEntity(dto);
        return service.updateAndReturnDto(id, entity);
    }
}
