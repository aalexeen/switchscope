package net.switchscope.web.component.device;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.device.RouterMapper;
import net.switchscope.model.component.device.Router;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.device.RouterService;
import net.switchscope.to.component.device.RouterTo;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = RouterController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RouterController extends AbstractCrudController<Router, RouterTo> {

    static final String REST_URL = "/api/devices/routers";

    private final RouterService service;
    private final RouterMapper mapper;

    @Override
    protected CrudService<Router> getService() {
        return service;
    }

    @Override
    protected BaseMapper<Router, RouterTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "router";
    }
}
