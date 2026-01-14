package net.switchscope.web.catalog;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.catalog.ComponentNatureMapper;
import net.switchscope.model.component.ComponentNatureEntity;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.ComponentNatureService;
import net.switchscope.to.component.catalog.ComponentNatureTo;
import net.switchscope.web.AbstractCatalogController;

@RestController
@RequestMapping(value = ComponentNatureController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComponentNatureController extends AbstractCatalogController<ComponentNatureEntity, ComponentNatureTo> {

    static final String REST_URL = "/api/catalogs/component-natures";

    private final ComponentNatureService service;
    private final ComponentNatureMapper mapper;

    @Override
    protected CrudService<ComponentNatureEntity> getService() {
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
}
