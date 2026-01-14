package net.switchscope.web.component.connectivity;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.connectivity.PatchPanelMapper;
import net.switchscope.model.component.connectivity.PatchPanel;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.connectivity.PatchPanelService;
import net.switchscope.to.component.connectivity.PatchPanelTo;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = PatchPanelController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PatchPanelController extends AbstractCrudController<PatchPanel, PatchPanelTo> {

    static final String REST_URL = "/api/connectivity/patch-panels";

    private final PatchPanelService service;
    private final PatchPanelMapper mapper;

    @Override
    protected CrudService<PatchPanel> getService() {
        return service;
    }

    @Override
    protected BaseMapper<PatchPanel, PatchPanelTo> getMapper() {
        return mapper;
    }

    @Override
    protected String getEntityName() {
        return "patch panel";
    }
}
