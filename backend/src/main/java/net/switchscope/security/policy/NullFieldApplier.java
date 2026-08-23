package net.switchscope.security.policy;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.error.IllegalRequestDataException;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clears the fields a request explicitly sent as {@code null}, and decides whether it may.
 * <p>
 * The mappers run with {@code NullValuePropertyMappingStrategy.IGNORE}, which is what keeps a
 * partial update from wiping everything the payload happens not to mention - but it also means a
 * field the caller deliberately set to {@code null} is ignored just as thoroughly. Until this class
 * existed, {@link UpdatePolicyValidator} decided who may clear which field and then nobody acted on
 * the answer: the whole field-access layer was a permission check on an operation the application
 * could not perform.
 *
 * <h2>Why the decision happens here and not when the body is read</h2>
 * A policy governs a <em>change</em>, and whether a null is a change is only knowable against the
 * stored value. This matters immediately rather than in theory: the detail view saves by sending
 * the object exactly as it fetched it, empty optionals included, so a USER's ordinary save carries
 * {@code "description": null} for every record whose description is empty. Checking the payload
 * alone would refuse that save - and refuse it to protect a value that is already null. So the
 * fields that would actually change are worked out first, against the loaded entity, and only
 * those are put to the policy.
 *
 * <h2>The three gates, and why they are separate</h2>
 * <ul>
 *   <li>{@code READ_ONLY} is skipped, mirroring {@link UpdatePolicyValidator}, which passes over it
 *       without complaining;</li>
 *   <li>the caller must be allowed to clear the field - {@link UpdatePolicyValidator}, which
 *       refuses with 403. This is about who is asking;</li>
 *   <li>the property must be able to hold null - {@link EntityNullability}, which refuses with 422.
 *       This is about what is possible, and it is a separate gate because the DTO cannot answer it:
 *       an unannotated field defaults to admin-clearable whatever the column says, and
 *       {@code ComponentTo.manufacturer} is declared {@code ADMIN_NULLABLE} over a NOT NULL column.
 *       Writing that null would be a constraint violation - a 500 for a request the API had just
 *       accepted.</li>
 * </ul>
 * A field with nothing stored behind it - {@code typeCode}, {@code parentLocationName} and the rest
 * of the derived surface - is skipped in silence: there is nothing to clear, and a payload echoing
 * back what a GET returned should not be an error.
 *
 * <h2>Foreign keys</h2>
 * A DTO carries {@code componentNatureId} where the entity carries {@code componentNature}, so
 * clearing an association is the same operation with the {@code Id} suffix dropped. That is handled
 * here rather than in the reference resolvers: a resolver only ever acts on an id that is present
 * <em>and</em> non-null, so the two halves never overlap, and keeping clearing in one place means
 * one set of guards instead of one per resolver method.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NullFieldApplier {

    private static final String ID_SUFFIX = "Id";

    private final FieldAccessMetadataCache metadataCache;
    private final EntityNullability entityNullability;
    private final UpdatePolicyValidator policyValidator;

    /**
     * Applies every explicit null in the payload that would actually change the entity.
     *
     * @param entity        the managed entity being updated
     * @param dtoClass      the DTO class the payload was bound to, for field-access lookup
     * @param presentFields the payload's top-level fields, as read from the request body
     * @param policy        the caller's update policy
     * @throws net.switchscope.error.PolicyViolationException if the caller may not clear a field
     * @throws IllegalRequestDataException                    if a field that cannot hold null was
     *                                                        asked to
     */
    public void apply(Object entity, Class<?> dtoClass, Map<String, JsonNode> presentFields,
                      UpdatePolicy policy) {
        if (presentFields.isEmpty()) {
            return;
        }
        Class<?> entityClass = Hibernate.getClass(entity);
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);

        Map<String, String> clears = fieldsThatWouldChange(wrapper, entityClass, dtoClass, presentFields);
        if (clears.isEmpty()) {
            return;
        }

        Map<String, JsonNode> asPayload = new LinkedHashMap<>();
        clears.keySet().forEach(field -> asPayload.put(field, presentFields.get(field)));
        policyValidator.validate(dtoClass, asPayload, policy);

        clears.forEach((dtoField, property) -> {
            String impossible = entityNullability.reasonItCannotBeNull(entityClass, property);
            if (impossible != null) {
                throw new IllegalRequestDataException("Field '" + dtoField + "' cannot be set to null: "
                        + impossible);
            }
            wrapper.setPropertyValue(property, null);
            log.debug("Cleared {}.{} on behalf of field '{}'",
                    entityClass.getSimpleName(), property, dtoField);
        });
    }

    /**
     * The nulls in the payload that are requests to change something, as DTO field to entity
     * property. A null over a value that is already null asks for nothing and is left out, so that
     * it never has to be permitted.
     */
    private Map<String, String> fieldsThatWouldChange(BeanWrapper wrapper, Class<?> entityClass,
                                                      Class<?> dtoClass,
                                                      Map<String, JsonNode> presentFields) {
        Map<String, String> clears = new LinkedHashMap<>();
        for (Map.Entry<String, JsonNode> entry : presentFields.entrySet()) {
            JsonNode value = entry.getValue();
            if (value == null || !value.isNull()) {
                continue;
            }
            String dtoField = entry.getKey();

            if (metadataCache.getAccessLevel(dtoClass, dtoField) == FieldAccessLevel.READ_ONLY) {
                log.debug("Not clearing READ_ONLY field '{}'", dtoField);
                continue;
            }
            String property = storedPropertyFor(wrapper, entityClass, dtoField);
            if (property == null) {
                log.debug("Field '{}' has no stored property on {}; nothing to clear",
                        dtoField, entityClass.getSimpleName());
                continue;
            }
            if (wrapper.getPropertyValue(property) == null) {
                log.debug("Field '{}' is already empty; the null asks for no change", dtoField);
                continue;
            }
            clears.put(dtoField, property);
        }
        return clears;
    }

    /**
     * The entity property a DTO field clears, or {@code null} if the field is not stored.
     * Names match, except that a foreign key travels as {@code <name>Id} and is stored as
     * {@code <name>}.
     */
    private String storedPropertyFor(BeanWrapper wrapper, Class<?> entityClass, String dtoField) {
        if (isStoredAndWritable(wrapper, entityClass, dtoField)) {
            return dtoField;
        }
        if (dtoField.endsWith(ID_SUFFIX) && dtoField.length() > ID_SUFFIX.length()) {
            String association = dtoField.substring(0, dtoField.length() - ID_SUFFIX.length());
            if (isStoredAndWritable(wrapper, entityClass, association)) {
                return association;
            }
        }
        return null;
    }

    private boolean isStoredAndWritable(BeanWrapper wrapper, Class<?> entityClass, String property) {
        return wrapper.isWritableProperty(property) && entityNullability.isStored(entityClass, property);
    }
}
