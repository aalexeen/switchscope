package net.switchscope.web.component.connectivity;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.connectivity.ConnectorMapper;
import net.switchscope.model.component.connectivity.Connector;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.connectivity.ConnectorService;
import net.switchscope.to.component.connectivity.ConnectorTo;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = ConnectorController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ConnectorController extends AbstractCrudController<Connector, ConnectorTo> {

    static final String REST_URL = "/api/connectivity/connectors";

    private final ConnectorService service;
    private final ConnectorMapper mapper;

    @Override
    protected CrudService<Connector> getService() {
        return service;
    }

    @Override
    protected BaseMapper<Connector, ConnectorTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "connector";
    }
}
