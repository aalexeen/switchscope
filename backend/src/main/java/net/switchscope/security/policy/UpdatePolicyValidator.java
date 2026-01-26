package net.switchscope.security.policy;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.error.PolicyViolationException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Validates field updates against the applicable update policy.
 * Checks if the user has permission to nullify fields based on their role.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdatePolicyValidator {

    private final FieldAccessMetadataCache metadataCache;

    /**
     * Validates that all null values in the update payload are permitted by the policy.
     *
     * @param dtoClass the DTO class being updated (for annotation lookup)
     * @param presentFields map of field names to their JSON values from the request
     * @param policy the update policy to apply
     * @throws PolicyViolationException if a field nullification is not permitted
     */
    public void validate(Class<?> dtoClass, Map<String, JsonNode> presentFields, UpdatePolicy policy) {
        log.debug("Validating update for {} with policy {}", dtoClass.getSimpleName(), policy.getPolicyName());

        for (Map.Entry<String, JsonNode> entry : presentFields.entrySet()) {
            String fieldName = entry.getKey();
            JsonNode value = entry.getValue();

            // Only check fields explicitly set to null
            if (value != null && value.isNull()) {
                FieldAccessLevel level = metadataCache.getAccessLevel(dtoClass, fieldName);
                log.debug("Field '{}' set to null, access level: {}", fieldName, level);

                // READ_ONLY fields are ignored during updates
                if (level == FieldAccessLevel.READ_ONLY) {
                    log.debug("Skipping READ_ONLY field '{}'", fieldName);
                    continue;
                }

                // REQUIRED fields cannot be nullified by anyone
                if (level == FieldAccessLevel.REQUIRED) {
                    log.warn("Attempt to nullify REQUIRED field '{}' by policy '{}'",
                            fieldName, policy.getPolicyName());
                    throw PolicyViolationException.requiredField(fieldName);
                }

                // Check policy for other fields
                if (!policy.canNullify(fieldName, level)) {
                    log.warn("Policy '{}' denied nullification of field '{}' with level {}",
                            policy.getPolicyName(), fieldName, level);
                    throw PolicyViolationException.insufficientPermissions(fieldName, policy.getPolicyName());
                }

                log.debug("Nullification of field '{}' permitted by policy '{}'",
                        fieldName, policy.getPolicyName());
            }
        }

        log.debug("Update validation passed for {}", dtoClass.getSimpleName());
    }
}
