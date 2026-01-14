package net.switchscope.web.catalog;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.catalog.ComponentTypeMapper;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.ComponentTypeService;
import net.switchscope.to.component.catalog.ComponentTypeTo;
import net.switchscope.web.AbstractCatalogController;

@RestController
@RequestMapping(value = ComponentTypeController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComponentTypeController extends AbstractCatalogController<ComponentTypeEntity, ComponentTypeTo> {

    static final String REST_URL = "/api/catalogs/component-types";

    private final ComponentTypeService service;
    private final ComponentTypeMapper mapper;

    @Override
    protected CrudService<ComponentTypeEntity> getService() {
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
}
