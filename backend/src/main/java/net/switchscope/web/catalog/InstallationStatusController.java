package net.switchscope.web.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.installation.catalog.InstallationStatusMapper;
import net.switchscope.model.installation.catalog.InstallationStatusEntity;
import net.switchscope.security.policy.UpdatePolicy;
import net.switchscope.security.policy.UpdatePolicyResolver;
import net.switchscope.security.policy.UpdatePolicyValidator;
import net.switchscope.service.installation.InstallationStatusService;
import net.switchscope.to.installation.catalog.InstallationStatusTo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for InstallationStatus catalog entities.
 * Custom implementation to support role-based field access validation.
 */
@Slf4j
@RestController
@RequestMapping(value = InstallationStatusController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class InstallationStatusController {

    static final String REST_URL = "/api/catalogs/installation-statuses";

    private final InstallationStatusService service;
    private final InstallationStatusMapper mapper;
    private final ObjectMapper objectMapper;

    // Policy validation
    private final UpdatePolicyResolver policyResolver;
    private final UpdatePolicyValidator policyValidator;

    @GetMapping
    @Transactional(readOnly = true)
    public List<InstallationStatusTo> getAll() {
        log.info("getAll installation statuses");
        return mapper.toToList(service.getAll());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public InstallationStatusTo get(@PathVariable UUID id) {
        log.info("get installation status {}", id);
        return mapper.toTo(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public InstallationStatusTo create(@RequestBody InstallationStatusTo dto) {
        log.info("create installation status {}", dto);
        InstallationStatusEntity entity = mapper.toEntity(dto);
        InstallationStatusEntity created = service.create(entity);
        return mapper.toTo(created);
    }

    /**
     * Update installation status with role-based field access validation.
     * Validates field nullification against update policy before applying changes.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SneakyThrows
    public InstallationStatusTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update installation status with id={}", id);

        // 1. Deserialize JSON into DTO
        InstallationStatusTo dto = objectMapper.readValue(jsonPayload, InstallationStatusTo.class);

        // 2. Validate field nullifications against policy
        Map<String, JsonNode> presentFields = extractPresentFields(jsonPayload);
        UpdatePolicy policy = policyResolver.resolve();
        log.debug("Applying update policy: {}", policy.getPolicyName());
        policyValidator.validate(InstallationStatusTo.class, presentFields, policy);

        // 3. Delegate to service for actual update (uses updateFromDto pattern)
        InstallationStatusEntity updated = service.updateFromDto(id, dto);

        return mapper.toTo(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        log.info("delete installation status {}", id);
        service.delete(id);
    }

    /**
     * Extracts all fields present in JSON payload with their values.
     * Used to detect explicitly set null values vs absent fields.
     */
    @SneakyThrows
    private Map<String, JsonNode> extractPresentFields(String jsonPayload) {
        Map<String, JsonNode> fields = new HashMap<>();
        JsonNode root = objectMapper.readTree(jsonPayload);
        Iterator<String> fieldNames = root.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            fields.put(fieldName, root.get(fieldName));
        }
        return fields;
    }
}
