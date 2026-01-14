package net.switchscope.web.location;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.location.LocationMapper;
import net.switchscope.model.location.Location;
import net.switchscope.service.CrudService;
import net.switchscope.service.location.LocationService;
import net.switchscope.to.location.LocationTo;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = LocationController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LocationController extends AbstractCrudController<Location, LocationTo> {

    static final String REST_URL = "/api/locations";

    private final LocationService service;
    private final LocationMapper mapper;

    @Override
    protected CrudService<Location> getService() {
        return service;
    }

    @Override
    protected BaseMapper<Location, LocationTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "location";
    }
}
