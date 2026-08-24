package net.switchscope.web.catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.catalog.ComponentTypeMapper;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.service.DtoCrudService;
import net.switchscope.service.component.ComponentTypeService;
import net.switchscope.service.component.InstallableComponentRegistry;
import net.switchscope.to.component.catalog.ComponentTypeTo;
import net.switchscope.web.AbstractCrudController;
import net.switchscope.web.page.ListQuery;
import net.switchscope.web.page.ListResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Component types, which are read with one thing added that the mapper cannot know.
 * <p>
 * The five endpoints are {@link AbstractCrudController}'s. Only the two reads are overridden, to
 * mark which entries can actually be instantiated - see {@link #markImplementation}. A create is
 * deliberately not marked: it answers with what was stored, exactly as it did before this
 * controller was a subclass.
 */
@Slf4j
@RestController
@RequestMapping(value = ComponentTypeController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("catalog.component-type")
public class ComponentTypeController extends AbstractCrudController<ComponentTypeEntity, ComponentTypeTo> {

    static final String REST_URL = "/api/catalogs/component-types";

    private final ComponentTypeService service;
    private final ComponentTypeMapper mapper;
    private final InstallableComponentRegistry registry;

    @Override
    protected DtoCrudService<ComponentTypeEntity, ComponentTypeTo> getService() {
        return service;
    }

    @Override
    protected BaseMapper<ComponentTypeEntity, ComponentTypeTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "component type";
    }

    @Override
    protected Class<ComponentTypeTo> getDtoClass() {
        return ComponentTypeTo.class;
    }

    @Override
    @GetMapping
    public Object getAll(@ParameterObject ListQuery query) {
        log.info("getAll {} ({})", getEntityName(), query);
        return ListResponse.of(query, this::allMarked,
                () -> service.getPage(query).map(this::markImplementation));
    }

    private List<ComponentTypeTo> allMarked() {
        List<ComponentTypeTo> tos = mapper.toToList(service.getAll());
        tos.forEach(this::markImplementation);
        return tos;
    }

    @Override
    @GetMapping("/{id}")
    public ComponentTypeTo get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        return markImplementation(mapper.toTo(service.getById(id)));
    }

    /**
     * Tells the client which catalog entries can actually be instantiated, and under which
     * discriminator. The UI needs this to decide whether to offer a "create" action for a type and
     * which type-specific form to render; the same mapping is what the server uses to derive the
     * discriminator on POST, so the two cannot drift apart.
     */
    private ComponentTypeTo markImplementation(ComponentTypeTo to) {
        boolean implemented = registry.isImplemented(to.getCode());
        to.setImplemented(implemented);
        to.setComponentClass(implemented ? to.getCode() : null);
        return to;
    }
}
