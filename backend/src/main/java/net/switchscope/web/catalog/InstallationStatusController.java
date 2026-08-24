package net.switchscope.web.catalog;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.installation.catalog.InstallationStatusMapper;
import net.switchscope.model.installation.catalog.InstallationStatusEntity;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.service.DtoCrudService;
import net.switchscope.service.installation.InstallationStatusService;
import net.switchscope.to.installation.catalog.InstallationStatusTo;
import net.switchscope.web.AbstractCrudController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Installation statuses catalog entries.
 * <p>
 * Everything this route does is what {@link AbstractCrudController} does: the five endpoints were
 * written out here once per catalog and differed only in the type names and the words in their log
 * lines. What used to justify the copies - reading a raw body so that an explicit null could be
 * told from an omitted field - moved into the base class when the nine component routes needed the
 * same thing.
 */
@RestController
@RequestMapping(value = InstallationStatusController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("catalog.installation-status")
public class InstallationStatusController extends AbstractCrudController<InstallationStatusEntity, InstallationStatusTo> {

    static final String REST_URL = "/api/catalogs/installation-statuses";

    private final InstallationStatusService service;
    private final InstallationStatusMapper mapper;

    @Override
    protected DtoCrudService<InstallationStatusEntity, InstallationStatusTo> getService() {
        return service;
    }

    @Override
    protected BaseMapper<InstallationStatusEntity, InstallationStatusTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "installation status";
    }

    @Override
    protected Class<InstallationStatusTo> getDtoClass() {
        return InstallationStatusTo.class;
    }
}
