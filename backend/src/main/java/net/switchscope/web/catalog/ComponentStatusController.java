package net.switchscope.web.catalog;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.catalog.ComponentStatusMapper;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.service.DtoCrudService;
import net.switchscope.service.component.ComponentStatusService;
import net.switchscope.to.component.catalog.ComponentStatusTo;
import net.switchscope.web.AbstractCrudController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Component statuses catalog entries.
 * <p>
 * Everything this route does is what {@link AbstractCrudController} does: the five endpoints were
 * written out here once per catalog and differed only in the type names and the words in their log
 * lines. What used to justify the copies - reading a raw body so that an explicit null could be
 * told from an omitted field - moved into the base class when the nine component routes needed the
 * same thing.
 */
@RestController
@RequestMapping(value = ComponentStatusController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("catalog.component-status")
public class ComponentStatusController extends AbstractCrudController<ComponentStatusEntity, ComponentStatusTo> {

    static final String REST_URL = "/api/catalogs/component-statuses";

    private final ComponentStatusService service;
    private final ComponentStatusMapper mapper;

    @Override
    protected DtoCrudService<ComponentStatusEntity, ComponentStatusTo> getService() {
        return service;
    }

    @Override
    protected BaseMapper<ComponentStatusEntity, ComponentStatusTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "component status";
    }

    @Override
    protected Class<ComponentStatusTo> getDtoClass() {
        return ComponentStatusTo.class;
    }
}
