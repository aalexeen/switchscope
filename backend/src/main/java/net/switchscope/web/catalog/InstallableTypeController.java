package net.switchscope.web.catalog;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.installation.catalog.InstallableTypeEntity;
import net.switchscope.service.CrudService;
import net.switchscope.service.installation.InstallableTypeService;
import net.switchscope.web.AbstractCatalogController;

@RestController
@RequestMapping(value = InstallableTypeController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class InstallableTypeController extends AbstractCatalogController<InstallableTypeEntity> {

    static final String REST_URL = "/api/catalogs/installable-types";

    private final InstallableTypeService service;

    @Override
    protected CrudService<InstallableTypeEntity> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "installable type";
    }
}
