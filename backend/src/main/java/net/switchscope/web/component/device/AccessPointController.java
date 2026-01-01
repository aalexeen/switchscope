package net.switchscope.web.component.device;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.device.AccessPoint;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.device.AccessPointService;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = AccessPointController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AccessPointController extends AbstractCrudController<AccessPoint> {

    static final String REST_URL = "/api/devices/access-points";

    private final AccessPointService service;

    @Override
    protected CrudService<AccessPoint> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "access point";
    }
}
