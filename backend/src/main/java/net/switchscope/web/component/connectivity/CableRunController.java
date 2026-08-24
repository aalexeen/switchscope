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

import org.springdoc.core.annotations.ParameterObject;
import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.connectivity.CableRunMapper;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.service.DtoCrudService;
import net.switchscope.service.component.connectivity.CableRunService;
import net.switchscope.to.component.connectivity.CableRunTo;
import net.switchscope.web.AbstractCrudController;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.ListResponse;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = CableRunController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("component.cable-run")
public class CableRunController extends AbstractCrudController<CableRun, CableRunTo> {

    static final String REST_URL = "/api/connectivity/cable-runs";

    private final CableRunService service;
    private final CableRunMapper mapper;

    @Override
    protected DtoCrudService<CableRun, CableRunTo> getService() {
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

    @Override
    protected Class<CableRunTo> getDtoClass() {
        return CableRunTo.class;
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping
    public Object getAll(@ParameterObject ListQuery query) {
        log.info("getAll {} ({})", getEntityName(), query);
        return ListResponse.of(query, service::getAllAsDto, () -> service.getPage(query));
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


}
