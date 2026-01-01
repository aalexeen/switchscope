package net.switchscope.web.component.housing;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.housing.RackService;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = RackController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RackController extends AbstractCrudController<Rack> {

    static final String REST_URL = "/api/housing/racks";

    private final RackService service;

    @Override
    protected CrudService<Rack> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "rack";
    }
}
