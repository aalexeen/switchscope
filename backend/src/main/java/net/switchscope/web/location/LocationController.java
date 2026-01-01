package net.switchscope.web.location;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.location.Location;
import net.switchscope.service.CrudService;
import net.switchscope.service.location.LocationService;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = LocationController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LocationController extends AbstractCrudController<Location> {

    static final String REST_URL = "/api/locations";

    private final LocationService service;

    @Override
    protected CrudService<Location> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "location";
    }
}
