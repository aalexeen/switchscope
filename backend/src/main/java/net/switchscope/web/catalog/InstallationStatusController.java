package net.switchscope.web.catalog;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.installation.catalog.InstallationStatusEntity;
import net.switchscope.service.CrudService;
import net.switchscope.service.installation.InstallationStatusService;
import net.switchscope.web.AbstractCatalogController;

@RestController
@RequestMapping(value = InstallationStatusController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class InstallationStatusController extends AbstractCatalogController<InstallationStatusEntity> {

    static final String REST_URL = "/api/catalogs/installation-statuses";

    private final InstallationStatusService service;

    @Override
    protected CrudService<InstallationStatusEntity> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "installation status";
    }
}
