package net.switchscope.web.catalog;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.ComponentTypeService;
import net.switchscope.web.AbstractCatalogController;

@RestController
@RequestMapping(value = ComponentTypeController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComponentTypeController extends AbstractCatalogController<ComponentTypeEntity> {

    static final String REST_URL = "/api/catalogs/component-types";

    private final ComponentTypeService service;

    @Override
    protected CrudService<ComponentTypeEntity> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "component type";
    }
}
