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
import net.switchscope.service.CrudService;
import net.switchscope.service.component.connectivity.PatchPanelService;
import net.switchscope.to.component.connectivity.PatchPanelTo;
import net.switchscope.web.AbstractCrudController;

import java.util.List;
import java.util.UUID;

@Slf4j
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

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PatchPanelTo create(@RequestBody PatchPanelTo dto) {
        log.info("create {} {}", getEntityName(), dto);
        PatchPanel entity = mapper.toEntity(dto);
        return service.createAndReturnDto(entity);
    }

    /**
     * Override to use service DTO method for lazy-safe mapping.
     */
    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PatchPanelTo update(@PathVariable UUID id, @RequestBody PatchPanelTo dto) {
        log.info("update {} {} with id={}", getEntityName(), dto, id);
        PatchPanel entity = mapper.toEntity(dto);
        return service.updateAndReturnDto(id, entity);
    }
}
