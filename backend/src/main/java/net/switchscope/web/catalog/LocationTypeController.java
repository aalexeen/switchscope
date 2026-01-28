package net.switchscope.web.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.location.catalog.LocationTypeMapper;
import net.switchscope.model.location.catalog.LocationTypeEntity;
import net.switchscope.security.policy.UpdatePolicy;
import net.switchscope.security.policy.UpdatePolicyResolver;
import net.switchscope.security.policy.UpdatePolicyValidator;
import net.switchscope.service.location.LocationTypeService;
import net.switchscope.to.location.catalog.LocationTypeTo;
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
 * Controller for LocationType catalog entities.
 * Custom implementation to support role-based field access validation.
 */
@Slf4j
@RestController
@RequestMapping(value = LocationTypeController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LocationTypeController {

    static final String REST_URL = "/api/catalogs/location-types";

    private final LocationTypeService service;
    private final LocationTypeMapper mapper;
    private final ObjectMapper objectMapper;

    // Policy validation
    private final UpdatePolicyResolver policyResolver;
    private final UpdatePolicyValidator policyValidator;

    @GetMapping
    @Transactional(readOnly = true)
    public List<LocationTypeTo> getAll() {
        log.info("getAll location types");
        return mapper.toToList(service.getAll());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public LocationTypeTo get(@PathVariable UUID id) {
        log.info("get location type {}", id);
        return mapper.toTo(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public LocationTypeTo create(@RequestBody LocationTypeTo dto) {
        log.info("create location type {}", dto);
        LocationTypeEntity entity = mapper.toEntity(dto);
        LocationTypeEntity created = service.create(entity);
        return mapper.toTo(created);
    }

    /**
     * Update location type with role-based field access validation.
     * Validates field nullification against update policy before applying changes.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SneakyThrows
    public LocationTypeTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update location type with id={}", id);

        // 1. Deserialize JSON into DTO
        LocationTypeTo dto = objectMapper.readValue(jsonPayload, LocationTypeTo.class);

        // 2. Validate field nullifications against policy
        Map<String, JsonNode> presentFields = extractPresentFields(jsonPayload);
        UpdatePolicy policy = policyResolver.resolve();
        log.debug("Applying update policy: {}", policy.getPolicyName());
        policyValidator.validate(LocationTypeTo.class, presentFields, policy);

        // 3. Delegate to service for actual update (uses updateFromDto pattern)
        LocationTypeEntity updated = service.updateFromDto(id, dto);

        return mapper.toTo(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        log.info("delete location type {}", id);
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
