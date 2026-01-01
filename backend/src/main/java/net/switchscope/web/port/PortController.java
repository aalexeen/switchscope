package net.switchscope.web.port;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.port.Port;
import net.switchscope.service.CrudService;
import net.switchscope.service.port.PortService;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = PortController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PortController extends AbstractCrudController<Port> {

    static final String REST_URL = "/api/ports";

    private final PortService service;

    @Override
    protected CrudService<Port> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "port";
    }
}
