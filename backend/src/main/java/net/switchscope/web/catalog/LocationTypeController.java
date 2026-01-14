package net.switchscope.web.catalog;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.location.catalog.LocationTypeMapper;
import net.switchscope.model.location.catalog.LocationTypeEntity;
import net.switchscope.service.CrudService;
import net.switchscope.service.location.LocationTypeService;
import net.switchscope.to.location.catalog.LocationTypeTo;
import net.switchscope.web.AbstractCatalogController;

@RestController
@RequestMapping(value = LocationTypeController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LocationTypeController extends AbstractCatalogController<LocationTypeEntity, LocationTypeTo> {

    static final String REST_URL = "/api/catalogs/location-types";

    private final LocationTypeService service;
    private final LocationTypeMapper mapper;

    @Override
    protected CrudService<LocationTypeEntity> getService() {
        return service;
    }

    @Override
    protected BaseMapper<LocationTypeEntity, LocationTypeTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "location type";
    }
}
