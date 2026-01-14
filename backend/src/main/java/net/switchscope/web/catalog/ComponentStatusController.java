package net.switchscope.web.catalog;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.catalog.ComponentStatusMapper;
import net.switchscope.model.component.ComponentStatusEntity;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.ComponentStatusService;
import net.switchscope.to.component.catalog.ComponentStatusTo;
import net.switchscope.web.AbstractCatalogController;

@RestController
@RequestMapping(value = ComponentStatusController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComponentStatusController extends AbstractCatalogController<ComponentStatusEntity, ComponentStatusTo> {

    static final String REST_URL = "/api/catalogs/component-statuses";

    private final ComponentStatusService service;
    private final ComponentStatusMapper mapper;

    @Override
    protected CrudService<ComponentStatusEntity> getService() {
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
}
