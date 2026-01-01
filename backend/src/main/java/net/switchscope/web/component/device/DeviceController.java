package net.switchscope.web.component.device;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.device.Device;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.device.DeviceService;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = DeviceController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DeviceController extends AbstractCrudController<Device> {

    static final String REST_URL = "/api/devices";

    private final DeviceService service;

    @Override
    protected CrudService<Device> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "device";
    }
}
