package net.switchscope.web.catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.location.catalog.LocationTypeMapper;
import net.switchscope.model.location.catalog.LocationTypeEntity;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.service.DtoCrudService;
import net.switchscope.service.location.LocationTypeService;
import net.switchscope.to.location.catalog.LocationTypeTo;
import net.switchscope.web.AbstractCrudController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Location types, whose reads map inside the service's transaction.
 * <p>
 * The five endpoints are {@link AbstractCrudController}'s. The two reads are overridden for the
 * reason {@code RackController} overrides them: the base class maps after the service has returned,
 * and a location type carries its allowed child types as a lazy association, which by then is no
 * longer reachable.
 */
@Slf4j
@RestController
@RequestMapping(value = LocationTypeController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("catalog.location-type")
public class LocationTypeController extends AbstractCrudController<LocationTypeEntity, LocationTypeTo> {

    static final String REST_URL = "/api/catalogs/location-types";

    private final LocationTypeService service;
    private final LocationTypeMapper mapper;

    @Override
    protected DtoCrudService<LocationTypeEntity, LocationTypeTo> getService() {
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

    @Override
    protected Class<LocationTypeTo> getDtoClass() {
        return LocationTypeTo.class;
    }

    @Override
    @GetMapping
    public List<LocationTypeTo> getAll() {
        log.info("getAll {}", getEntityName());
        return service.getAllAsDto();
    }

    @Override
    @GetMapping("/{id}")
    public LocationTypeTo get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        return service.getByIdAsDto(id);
    }
}
