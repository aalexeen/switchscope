package net.switchscope.web.catalog;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.catalog.ComponentModel;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.catalog.ComponentModelService;
import net.switchscope.web.AbstractCatalogController;

@RestController
@RequestMapping(value = ComponentModelController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComponentModelController extends AbstractCatalogController<ComponentModel> {

    static final String REST_URL = "/api/catalogs/component-models";

    private final ComponentModelService service;

    @Override
    protected CrudService<ComponentModel> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "component model";
    }
}
