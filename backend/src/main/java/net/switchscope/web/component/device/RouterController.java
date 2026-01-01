package net.switchscope.web.component.device;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.device.Router;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.device.RouterService;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = RouterController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RouterController extends AbstractCrudController<Router> {

    static final String REST_URL = "/api/devices/routers";

    private final RouterService service;

    @Override
    protected CrudService<Router> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "router";
    }
}
