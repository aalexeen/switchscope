package net.switchscope.web.component.connectivity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.mapper.component.connectivity.PatchPanelMapper;
import net.switchscope.model.component.connectivity.PatchPanel;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.service.DtoCrudService;
import net.switchscope.service.component.connectivity.PatchPanelService;
import net.switchscope.to.component.connectivity.PatchPanelTo;
import net.switchscope.web.AbstractCrudController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = PatchPanelController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("component.patch-panel")
public class PatchPanelController extends AbstractCrudController<PatchPanel, PatchPanelTo> {

    static final String REST_URL = "/api/connectivity/patch-panels";

    private final PatchPanelService service;
    private final PatchPanelMapper mapper;

    @Override
    protected DtoCrudService<PatchPanel, PatchPanelTo> getService() {
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

    @Override
    protected Class<PatchPanelTo> getDtoClass() {
        return PatchPanelTo.class;
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping
    public List<PatchPanelTo> getAll() {
        log.info("getAll {}", getEntityName());
        return service.getAllAsDto();
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @GetMapping("/{id}")
    public PatchPanelTo get(@PathVariable UUID id) {
        log.info("get {} {}", getEntityName(), id);
        return service.getByIdAsDto(id);
    }


}
