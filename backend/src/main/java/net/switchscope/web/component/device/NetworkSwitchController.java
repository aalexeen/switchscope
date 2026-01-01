package net.switchscope.web.component.device;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.device.NetworkSwitch;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.device.NetworkSwitchService;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = NetworkSwitchController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NetworkSwitchController extends AbstractCrudController<NetworkSwitch> {

    static final String REST_URL = "/api/devices/switches";

    private final NetworkSwitchService service;

    @Override
    protected CrudService<NetworkSwitch> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "network switch";
    }
}
