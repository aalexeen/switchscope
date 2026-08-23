package net.switchscope.web.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.error.NotFoundException;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.service.component.InstallableComponentRegistry;
import net.switchscope.to.component.ComponentTo;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Binds a component request body to the right concrete {@link ComponentTo} subtype.
 * <p>
 * {@code ComponentTo} is polymorphic over the {@code componentClass} property, but the client is
 * not the authority on that value:
 * <ul>
 *   <li>on <b>create</b> the type follows from {@code componentTypeId}, which the client must send
 *       anyway because {@code component_type_id} is NOT NULL. The component type's {@code code}
 *       equals the JPA {@code @DiscriminatorValue} of the corresponding entity - the same string the
 *       {@code @InstallableComponent} annotation carries - so the discriminator is derived, not
 *       asked for. That makes it impossible to store, say, a NetworkSwitch row whose component type
 *       says FIREWALL;</li>
 *   <li>on <b>update</b> the type is whatever is already stored, and must not change.</li>
 * </ul>
 * A client may still send {@code componentClass} explicitly; it is then checked against the derived
 * value rather than trusted.
 * <p>
 * The catalog is deliberately richer than the Java model - it lists types such as FIREWALL or PDU
 * that have no entity yet - so an unimplemented type is reported as a 422 naming the type, not as a
 * deserialization failure.
 */
@Component
@RequiredArgsConstructor
public class ComponentPayloadReader {

    private static final String DISCRIMINATOR = "componentClass";

    private final ObjectMapper objectMapper;
    private final ComponentTypeRepository componentTypeRepository;
    private final InstallableComponentRegistry registry;
    private final Validator validator;

    /**
     * Binds a create payload, deriving the concrete type from {@code componentTypeId}.
     *
     * @param jsonPayload the raw request body
     * @param baseType    the expected base DTO ({@code ComponentTo} or {@code DeviceTo})
     * @return the bound DTO, already validated
     */
    public <T extends ComponentTo> T readForCreate(String jsonPayload, Class<T> baseType) {
        ObjectNode root = asObject(jsonPayload);
        String derived = deriveDiscriminator(root);

        JsonNode supplied = root.get(DISCRIMINATOR);
        if (supplied != null && !supplied.isNull() && !derived.equals(supplied.asText())) {
            throw new IllegalRequestDataException(DISCRIMINATOR + "=" + supplied.asText()
                    + " contradicts componentTypeId, which denotes " + derived
                    + "; omit " + DISCRIMINATOR + " and it will be derived");
        }
        root.put(DISCRIMINATOR, derived);

        T dto = bind(root, baseType, derived);
        validate(dto);
        return dto;
    }

    /**
     * Binds an update payload onto the type of the stored entity, ignoring any type the payload
     * claims. Bean validation is not applied: updates are partial by design.
     *
     * @param jsonPayload   the raw request body
     * @param discriminator the stored entity's discriminator
     * @param dtoClass      the concrete DTO class matching the stored entity
     * @return the bound DTO
     */
    public <T extends ComponentTo> T readForUpdate(String jsonPayload, String discriminator, Class<T> dtoClass) {
        ObjectNode root = asObject(jsonPayload);
        root.put(DISCRIMINATOR, discriminator);
        return bind(root, dtoClass, discriminator);
    }

    /**
     * The field names present at the top level of the payload, used to tell an explicitly null field
     * from an absent one during policy validation.
     */
    public java.util.Map<String, JsonNode> presentFields(String jsonPayload) {
        ObjectNode root = asObject(jsonPayload);
        java.util.Map<String, JsonNode> fields = new java.util.HashMap<>();
        root.fieldNames().forEachRemaining(name -> fields.put(name, root.get(name)));
        return fields;
    }

    @SneakyThrows
    private ObjectNode asObject(String jsonPayload) {
        JsonNode root = objectMapper.readTree(jsonPayload);
        if (!(root instanceof ObjectNode objectNode)) {
            throw new IllegalRequestDataException("Request body must be a JSON object");
        }
        return objectNode;
    }

    /**
     * componentTypeId -> component type -> its code, which is the discriminator.
     */
    private String deriveDiscriminator(ObjectNode root) {
        JsonNode node = root.get("componentTypeId");
        if (node == null || node.isNull() || node.asText().isBlank()) {
            throw new IllegalRequestDataException("componentTypeId is required");
        }
        UUID componentTypeId;
        try {
            componentTypeId = UUID.fromString(node.asText());
        } catch (IllegalArgumentException e) {
            throw new IllegalRequestDataException("componentTypeId is not a valid UUID: " + node.asText());
        }
        ComponentTypeEntity componentType = componentTypeRepository.findById(componentTypeId)
                .orElseThrow(() -> new NotFoundException(
                        "Component type with id=" + componentTypeId + " not found"));

        String code = componentType.getCode();
        if (!registry.isImplemented(code)) {
            throw new IllegalRequestDataException("Component type '" + code
                    + "' has no implementation and cannot be instantiated; implemented types are "
                    + registry.getImplementedCodes());
        }
        return code;
    }

    @SneakyThrows
    private <T extends ComponentTo> T bind(ObjectNode root, Class<T> targetType, String discriminator) {
        ComponentTo dto = objectMapper.treeToValue(root, ComponentTo.class);
        if (!targetType.isInstance(dto)) {
            throw new IllegalRequestDataException("Component type '" + discriminator + "' is not a "
                    + targetType.getSimpleName().replace("To", ""));
        }
        return targetType.cast(dto);
    }

    private void validate(ComponentTo dto) {
        Set<ConstraintViolation<ComponentTo>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new IllegalRequestDataException(violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .sorted()
                    .collect(Collectors.joining("; ")));
        }
    }
}
