package net.switchscope.web.component.connectivity;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.connectivity.CableRunMapper;
import net.switchscope.model.component.connectivity.CableRun;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.connectivity.CableRunService;
import net.switchscope.to.component.connectivity.CableRunTo;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = CableRunController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CableRunController extends AbstractCrudController<CableRun, CableRunTo> {

    static final String REST_URL = "/api/connectivity/cable-runs";

    private final CableRunService service;
    private final CableRunMapper mapper;

    @Override
    protected CrudService<CableRun> getService() {
        return service;
    }

    @Override
    protected BaseMapper<CableRun, CableRunTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "cable run";
    }
}
