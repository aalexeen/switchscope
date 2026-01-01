package net.switchscope.web.catalog;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.ComponentStatusService;
import net.switchscope.web.AbstractCatalogController;

@RestController
@RequestMapping(value = ComponentStatusController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComponentStatusController extends AbstractCatalogController<ComponentStatusEntity> {

    static final String REST_URL = "/api/catalogs/component-statuses";

    private final ComponentStatusService service;

    @Override
    protected CrudService<ComponentStatusEntity> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "component status";
    }
}
