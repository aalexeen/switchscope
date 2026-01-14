package net.switchscope.web.component.device;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.device.AccessPointMapper;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.device.AccessPointService;
import net.switchscope.to.component.device.AccessPointTo;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = AccessPointController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AccessPointController extends AbstractCrudController<AccessPoint, AccessPointTo> {

    static final String REST_URL = "/api/devices/access-points";

    private final AccessPointService service;
    private final AccessPointMapper mapper;

    @Override
    protected CrudService<AccessPoint> getService() {
        return service;
    }

    @Override
    protected BaseMapper<AccessPoint, AccessPointTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "access point";
    }
}
