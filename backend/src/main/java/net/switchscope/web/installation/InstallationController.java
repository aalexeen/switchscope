package net.switchscope.web.installation;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.installation.InstallationMapper;
import net.switchscope.model.installation.Installation;
import net.switchscope.service.CrudService;
import net.switchscope.service.installation.InstallationService;
import net.switchscope.to.installation.InstallationTo;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = InstallationController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class InstallationController extends AbstractCrudController<Installation, InstallationTo> {

    static final String REST_URL = "/api/installations";

    private final InstallationService service;
    private final InstallationMapper mapper;

    @Override
    protected CrudService<Installation> getService() {
        return service;
    }

    @Override
    protected BaseMapper<Installation, InstallationTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "installation";
    }
}
