package net.switchscope.web.installation;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.installation.InstallationMapper;
import net.switchscope.model.installation.Installation;
import net.switchscope.service.installation.InstallationService;
import net.switchscope.to.installation.InstallationTo;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = InstallationController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class InstallationController {

    static final String REST_URL = "/api/installations";

    private final InstallationService service;
    private final InstallationMapper mapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<InstallationTo> getAll() {
        log.info("getAll installations");
        return service.getAll().stream()
                .map(mapper::toTo)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public InstallationTo get(@PathVariable UUID id) {
        log.info("get installation {}", id);
        return mapper.toTo(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public InstallationTo create(@RequestBody InstallationTo to) {
        log.info("create installation {}", to);
        Installation entity = mapper.toEntity(to);
        // Service should resolve foreign keys (location, component, installedItemType, status)
        return mapper.toTo(service.create(entity));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public InstallationTo update(@PathVariable UUID id, @RequestBody InstallationTo to) {
        log.info("update installation {} with id={}", to, id);
        Installation entity = service.getById(id);
        mapper.updateFromTo(entity, to);
        // Service should resolve foreign keys
        return mapper.toTo(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("delete installation {}", id);
        service.delete(id);
    }
}
