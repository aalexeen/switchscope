package net.switchscope.web.component;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.Component;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.ComponentService;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = ComponentController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComponentController extends AbstractCrudController<Component> {

    static final String REST_URL = "/api/components";

    private final ComponentService service;

    @Override
    protected CrudService<Component> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "component";
    }
}
