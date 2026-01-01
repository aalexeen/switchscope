package net.switchscope.web.component.connectivity;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.model.component.connectivity.PatchPanel;
import net.switchscope.service.CrudService;
import net.switchscope.service.component.connectivity.PatchPanelService;
import net.switchscope.web.AbstractCrudController;

@RestController
@RequestMapping(value = PatchPanelController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PatchPanelController extends AbstractCrudController<PatchPanel> {

    static final String REST_URL = "/api/connectivity/patch-panels";

    private final PatchPanelService service;

    @Override
    protected CrudService<PatchPanel> getService() {
        return service;
    }

    @Override
    protected String getEntityName() {
        return "patch panel";
    }
}
