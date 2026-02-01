package net.switchscope.web.component.connectivity;

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
import net.switchscope.mapper.component.connectivity.CableRunMapper;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.connectivity.CableRunService;
import net.switchscope.to.component.connectivity.CableRunTo;
import net.switchscope.web.AbstractCrudController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = CableRunController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CableRunController extends AbstractCrudController<CableRun, CableRunTo> {

    static final String REST_URL = "/api/connectivity/cable-runs";

    private final CableRunService service;
    private final CableRunMapper mapper;

    @Override
    protected CrudService<CableRun> getService() {
        return service;
    }

    @Override
    protected BaseMapper<CableRun, CableRunTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "cable run";
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping
    public List<CableRunTo> getAll() {
        log.info("getAll {}", getEntityName());
        return service.getAllAsDto();
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping("/{id}")
    public CableRunTo get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        return service.getByIdAsDto(id);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CableRunTo create(@RequestBody CableRunTo dto) {
        log.info("create {} {}", getEntityName(), dto);
        CableRun entity = mapper.toEntity(dto);
        return service.createAndReturnDto(entity);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CableRunTo update(@PathVariable UUID id, @RequestBody CableRunTo dto) {
        log.info("update {} {} with id={}", getEntityName(), dto, id);
        CableRun entity = mapper.toEntity(dto);
        return service.updateAndReturnDto(id, entity);
    }
}
