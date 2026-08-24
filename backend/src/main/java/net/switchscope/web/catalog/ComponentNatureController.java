package net.switchscope.web.catalog;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.catalog.ComponentNatureMapper;
import net.switchscope.model.component.ComponentNatureEntity;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.service.DtoCrudService;
import net.switchscope.service.component.ComponentNatureService;
import net.switchscope.to.component.catalog.ComponentNatureTo;
import net.switchscope.web.AbstractCrudController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Component natures catalog entries.
 * <p>
 * Everything this route does is what {@link AbstractCrudController} does: the five endpoints were
 * written out here once per catalog and differed only in the type names and the words in their log
 * lines. What used to justify the copies - reading a raw body so that an explicit null could be
 * told from an omitted field - moved into the base class when the nine component routes needed the
 * same thing.
 */
@RestController
@RequestMapping(value = ComponentNatureController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("catalog.component-nature")
public class ComponentNatureController extends AbstractCrudController<ComponentNatureEntity, ComponentNatureTo> {

    static final String REST_URL = "/api/catalogs/component-natures";

    private final ComponentNatureService service;
    private final ComponentNatureMapper mapper;

    @Override
    protected DtoCrudService<ComponentNatureEntity, ComponentNatureTo> getService() {
        return service;
    }

    @Override
    protected BaseMapper<ComponentNatureEntity, ComponentNatureTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "component nature";
    }

    @Override
    protected Class<ComponentNatureTo> getDtoClass() {
        return ComponentNatureTo.class;
    }
}
