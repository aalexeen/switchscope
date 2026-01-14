package net.switchscope.web.catalog;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.catalog.ComponentCategoryMapper;
import net.switchscope.model.component.ComponentCategoryEntity;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.ComponentCategoryService;
import net.switchscope.to.component.catalog.ComponentCategoryTo;
import net.switchscope.web.AbstractCatalogController;

@RestController
@RequestMapping(value = ComponentCategoryController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComponentCategoryController extends AbstractCatalogController<ComponentCategoryEntity, ComponentCategoryTo> {

    static final String REST_URL = "/api/catalogs/component-categories";

    private final ComponentCategoryService service;
    private final ComponentCategoryMapper mapper;

    @Override
    protected CrudService<ComponentCategoryEntity> getService() {
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
}
