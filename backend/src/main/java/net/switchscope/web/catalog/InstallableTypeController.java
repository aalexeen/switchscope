package net.switchscope.web.catalog;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.installation.catalog.InstallableTypeMapper;
import net.switchscope.model.installation.catalog.InstallableTypeEntity;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.service.DtoCrudService;
import net.switchscope.service.installation.InstallableTypeService;
import net.switchscope.to.installation.catalog.InstallableTypeTo;
import net.switchscope.web.AbstractCrudController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Installable types catalog entries.
 * <p>
 * Everything this route does is what {@link AbstractCrudController} does: the five endpoints were
 * written out here once per catalog and differed only in the type names and the words in their log
 * lines. What used to justify the copies - reading a raw body so that an explicit null could be
 * told from an omitted field - moved into the base class when the nine component routes needed the
 * same thing.
 */
@RestController
@RequestMapping(value = InstallableTypeController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("catalog.installable-type")
public class InstallableTypeController extends AbstractCrudController<InstallableTypeEntity, InstallableTypeTo> {

    static final String REST_URL = "/api/catalogs/installable-types";

    private final InstallableTypeService service;
    private final InstallableTypeMapper mapper;

    @Override
    protected DtoCrudService<InstallableTypeEntity, InstallableTypeTo> getService() {
        return service;
    }

    @Override
    protected BaseMapper<InstallableTypeEntity, InstallableTypeTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "installable type";
    }

    @Override
    protected Class<InstallableTypeTo> getDtoClass() {
        return InstallableTypeTo.class;
    }
}
