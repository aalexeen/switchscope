package net.switchscope.web.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.mapper.component.catalog.ComponentCategoryMapper;
import net.switchscope.model.component.ComponentCategoryEntity;
import net.switchscope.security.policy.UpdatePolicy;
import net.switchscope.security.policy.UpdatePolicyResolver;
import net.switchscope.security.policy.UpdatePolicyValidator;
import net.switchscope.service.component.ComponentCategoryService;
import net.switchscope.to.component.catalog.ComponentCategoryTo;
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
 * Controller for ComponentCategory catalog entities.
 * Custom implementation to support role-based field access validation.
 */
@Slf4j
@RestController
@RequestMapping(value = ComponentCategoryController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComponentCategoryController {

    static final String REST_URL = "/api/catalogs/component-categories";

    private final ComponentCategoryService service;
    private final ComponentCategoryMapper mapper;
    private final ObjectMapper objectMapper;

    // Policy validation
    private final UpdatePolicyResolver policyResolver;
    private final UpdatePolicyValidator policyValidator;

    @GetMapping
    @Transactional(readOnly = true)
    public List<ComponentCategoryTo> getAll() {
        log.info("getAll component categories");
        return mapper.toToList(service.getAll());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ComponentCategoryTo get(@PathVariable UUID id) {
        log.info("get component category {}", id);
        return mapper.toTo(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ComponentCategoryTo create(@RequestBody ComponentCategoryTo dto) {
        log.info("create component category {}", dto);
        ComponentCategoryEntity entity = mapper.toEntity(dto);
        ComponentCategoryEntity created = service.create(entity);
        return mapper.toTo(created);
    }

    /**
     * Update component category with role-based field access validation.
     * Validates field nullification against update policy before applying changes.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SneakyThrows
    public ComponentCategoryTo update(@PathVariable UUID id, @RequestBody String jsonPayload) {
        log.info("update component category with id={}", id);

        // 1. Deserialize JSON into DTO
        ComponentCategoryTo dto = objectMapper.readValue(jsonPayload, ComponentCategoryTo.class);

        // 2. Validate field nullifications against policy
        Map<String, JsonNode> presentFields = extractPresentFields(jsonPayload);
        UpdatePolicy policy = policyResolver.resolve();
        log.debug("Applying update policy: {}", policy.getPolicyName());
        policyValidator.validate(ComponentCategoryTo.class, presentFields, policy);

        // 3. Delegate to service for actual update (uses updateFromDto pattern)
        ComponentCategoryEntity updated = service.updateFromDto(id, dto);

        return mapper.toTo(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        log.info("delete component category {}", id);
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
