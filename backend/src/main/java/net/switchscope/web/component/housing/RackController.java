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

import org.springdoc.core.annotations.ParameterObject;
import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.housing.RackMapper;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.service.DtoCrudService;
import net.switchscope.service.component.housing.RackService;
import net.switchscope.to.component.housing.RackTo;
import net.switchscope.web.AbstractCrudController;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.ListResponse;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = RackController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("component.rack")
public class RackController extends AbstractCrudController<Rack, RackTo> {

    static final String REST_URL = "/api/housing/racks";

    private final RackService service;
    private final RackMapper mapper;

    @Override
    protected DtoCrudService<Rack, RackTo> getService() {
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

    @Override
    protected Class<RackTo> getDtoClass() {
        return RackTo.class;
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
    public RackTo get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        return service.getByIdAsDto(id);
    }


}
