package net.switchscope.web.component.housing;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.housing.RackMapper;
import net.switchscope.model.component.housing.Rack;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.housing.RackService;
import net.switchscope.to.component.housing.RackTo;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = RackController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RackController extends AbstractCrudController<Rack, RackTo> {

    static final String REST_URL = "/api/housing/racks";

    private final RackService service;
    private final RackMapper mapper;

    @Override
    protected CrudService<Rack> getService() {
        return service;
    }

    @Override
    protected BaseMapper<Rack, RackTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "rack";
    }
}
