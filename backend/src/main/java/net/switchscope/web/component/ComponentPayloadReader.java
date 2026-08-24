package net.switchscope.web.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import net.switchscope.error.IllegalRequestDataException;
import net.switchscope.error.NotFoundException;
import net.switchscope.model.component.ComponentTypeEntity;
import net.switchscope.repository.component.ComponentTypeRepository;
import net.switchscope.service.component.InstallableComponentRegistry;
import net.switchscope.to.component.ComponentTo;
import net.switchscope.web.payload.JsonPayload;
import net.switchscope.web.payload.PartialUpdate;
import net.switchscope.web.payload.PartialUpdateReader;
import net.switchscope.web.payload.PayloadValidator;
import org.springframework.stereotype.Component;

import java.util.UUID;

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

    private final JsonPayload json;
    private final PartialUpdateReader partialUpdateReader;
    private final ComponentTypeRepository componentTypeRepository;
    private final InstallableComponentRegistry registry;
    private final PayloadValidator payloadValidator;

    /**
     * Binds a create payload, deriving the concrete type from {@code componentTypeId}.
     *
     * @param jsonPayload the raw request body
     * @param baseType    the expected base DTO ({@code ComponentTo} or {@code DeviceTo})
     * @return the bound DTO, already validated
     */
    public <T extends ComponentTo> T readForCreate(String jsonPayload, Class<T> baseType) {
        ObjectNode root = json.asObject(jsonPayload);
        String derived = deriveDiscriminator(root);

        JsonNode supplied = root.get(DISCRIMINATOR);
        if (supplied != null && !supplied.isNull() && !derived.equals(supplied.asText())) {
            throw new IllegalRequestDataException(DISCRIMINATOR + "=" + supplied.asText()
                    + " contradicts componentTypeId, which denotes " + derived
                    + "; omit " + DISCRIMINATOR + " and it will be derived");
        }
        root.put(DISCRIMINATOR, derived);

        T dto = bind(root, baseType, derived);
        payloadValidator.validateWhole(dto);
        return dto;
    }

    /**
     * Binds an update payload onto the type of the stored entity, ignoring any type the payload
     * claims, and checks the fields it sends as null against the caller's field-access policy.
     * Bean validation runs over the fields the body carried - see {@code PayloadValidator}.
     *
     * @param jsonPayload   the raw request body
     * @param discriminator the stored entity's discriminator
     * @param dtoClass      the concrete DTO class matching the stored entity
     * @return the bound DTO together with which fields the request carried
     */
    public <T extends ComponentTo> PartialUpdate<T> readForUpdate(
            String jsonPayload, String discriminator, Class<T> dtoClass) {
        ObjectNode root = json.asObject(jsonPayload);
        root.put(DISCRIMINATOR, discriminator);
        return partialUpdateReader.read(root, dtoClass);
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

    private <T extends ComponentTo> T bind(ObjectNode root, Class<T> targetType, String discriminator) {
        ComponentTo dto = json.bind(root, ComponentTo.class);
        if (!targetType.isInstance(dto)) {
            throw new IllegalRequestDataException("Component type '" + discriminator + "' is not a "
                    + targetType.getSimpleName().replace("To", ""));
        }
        return targetType.cast(dto);
    }
}
