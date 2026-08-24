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

import org.springdoc.core.annotations.ParameterObject;
import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.device.RouterMapper;
import net.switchscope.model.component.device.Router;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.service.DtoCrudService;
import net.switchscope.service.component.device.RouterService;
import net.switchscope.to.component.device.RouterTo;
import net.switchscope.web.AbstractCrudController;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.ListResponse;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = RouterController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("component.router")
public class RouterController extends AbstractCrudController<Router, RouterTo> {

    static final String REST_URL = "/api/devices/routers";

    private final RouterService service;
    private final RouterMapper mapper;

    @Override
    protected DtoCrudService<Router, RouterTo> getService() {
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

    @Override
    protected Class<RouterTo> getDtoClass() {
        return RouterTo.class;
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
    public RouterTo get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        return service.getByIdAsDto(id);
    }


}
