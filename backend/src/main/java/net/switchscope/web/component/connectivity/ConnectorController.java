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
import net.switchscope.mapper.component.connectivity.ConnectorMapper;
import net.switchscope.model.component.connectivity.Connector;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.connectivity.ConnectorService;
import net.switchscope.to.component.connectivity.ConnectorTo;
import net.switchscope.web.AbstractCrudController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = ConnectorController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ConnectorController extends AbstractCrudController<Connector, ConnectorTo> {

    static final String REST_URL = "/api/connectivity/connectors";

    private final ConnectorService service;
    private final ConnectorMapper mapper;

    @Override
    protected CrudService<Connector> getService() {
        return service;
    }

    @Override
    protected BaseMapper<Connector, ConnectorTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "connector";
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping
    public List<ConnectorTo> getAll() {
        log.info("getAll {}", getEntityName());
        return service.getAllAsDto();
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping("/{id}")
    public ConnectorTo get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        return service.getByIdAsDto(id);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ConnectorTo create(@RequestBody ConnectorTo dto) {
        log.info("create {} {}", getEntityName(), dto);
        Connector entity = mapper.toEntity(dto);
        return service.createAndReturnDto(entity);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ConnectorTo update(@PathVariable UUID id, @RequestBody ConnectorTo dto) {
        log.info("update {} {} with id={}", getEntityName(), dto, id);
        Connector entity = mapper.toEntity(dto);
        return service.updateAndReturnDto(id, entity);
    }
}
