package net.switchscope.web.catalog;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.catalog.ComponentCategoryMapper;
import net.switchscope.model.component.ComponentCategoryEntity;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.service.DtoCrudService;
import net.switchscope.service.component.ComponentCategoryService;
import net.switchscope.to.component.catalog.ComponentCategoryTo;
import net.switchscope.web.AbstractCrudController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Component categories catalog entries.
 * <p>
 * Everything this route does is what {@link AbstractCrudController} does: the five endpoints were
 * written out here once per catalog and differed only in the type names and the words in their log
 * lines. What used to justify the copies - reading a raw body so that an explicit null could be
 * told from an omitted field - moved into the base class when the nine component routes needed the
 * same thing.
 */
@RestController
@RequestMapping(value = ComponentCategoryController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("catalog.component-category")
public class ComponentCategoryController extends AbstractCrudController<ComponentCategoryEntity, ComponentCategoryTo> {

    static final String REST_URL = "/api/catalogs/component-categories";

    private final ComponentCategoryService service;
    private final ComponentCategoryMapper mapper;

    @Override
    protected DtoCrudService<ComponentCategoryEntity, ComponentCategoryTo> getService() {
        return service;
    }

    @Override
    protected BaseMapper<ComponentCategoryEntity, ComponentCategoryTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "component category";
    }

    @Override
    protected Class<ComponentCategoryTo> getDtoClass() {
        return ComponentCategoryTo.class;
    }
}
