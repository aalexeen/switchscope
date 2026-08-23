package net.switchscope.web.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import net.switchscope.error.IllegalRequestDataException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Reading a request body as a tree before binding it.
 * <p>
 * Every write endpoint that has to tell an <em>absent</em> field from one explicitly set to
 * {@code null} needs the same three steps, because after ordinary deserialisation into a POJO the
 * two are indistinguishable - both leave the property null. Those three steps were written out by
 * hand in eleven controllers and copied verbatim eight times as a private
 * {@code extractPresentFields}; this is that code, once.
 * <p>
 * Deliberately knows nothing about permissions or about any particular DTO hierarchy: the
 * discriminator pinning that polymorphic endpoints need is composed on top (see
 * {@code ComponentPayloadReader}), and the policy check that update endpoints need is composed on
 * top as well (see {@link PartialUpdateReader}).
 */
@Component
@RequiredArgsConstructor
public class JsonPayload {

    private final ObjectMapper objectMapper;

    /**
     * Parses the body and insists it is a JSON object.
     *
     * @param json the raw request body
     * @return the parsed object node
     * @throws IllegalRequestDataException if the body is not parseable or is not an object
     */
    public ObjectNode asObject(String json) {
        JsonNode root;
        try {
            root = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalRequestDataException("Request body is not valid JSON: " + e.getOriginalMessage());
        }
        if (!(root instanceof ObjectNode objectNode)) {
            throw new IllegalRequestDataException("Request body must be a JSON object");
        }
        return objectNode;
    }

    /**
     * The top-level field names the payload actually carries, mapped to their values.
     * <p>
     * This is the whole point of reading the body as a tree: a name present here with a null value
     * is a request to clear the field, and a name that is absent is a request to leave it alone.
     * Nesting is not walked - every consumer of this decides field by field at the top level, and a
     * nested null is part of a value being replaced wholesale rather than a clear of its own.
     *
     * @param root the parsed body
     * @return field name to value, in payload order
     */
    public Map<String, JsonNode> presentFields(ObjectNode root) {
        Map<String, JsonNode> fields = new LinkedHashMap<>();
        root.fieldNames().forEachRemaining(name -> fields.put(name, root.get(name)));
        return fields;
    }

    /**
     * Empties every property of a bound DTO that the payload did not mention.
     *
     * <h2>Why binding alone is not enough</h2>
     * The update mappers run with {@code NullValuePropertyMappingStrategy.IGNORE}, which asks the
     * DTO one question: is this property null? A DTO with field initialisers answers wrongly.
     * {@code AccessPointTo.ssids} is declared {@code = new HashSet<>()}, so a body that never
     * mentions {@code ssids} still arrives carrying an empty set - and MapStruct's generated update
     * dutifully clears the stored SSIDs and adds nothing. Measured, not theorised: a
     * {@code PUT /api/devices/access-points/{id}} of {@code {"description": "probe"}} emptied three
     * SSIDs. The same shape is waiting in every {@code = new ArrayList<>()} and every
     * {@code Boolean flag = false} across the DTOs - 126 initialisers in all.
     * <p>
     * Rather than strip those initialisers, which other code reads as a promise that a collection
     * is never null, the DTO is put back into the state binding should have produced: absent means
     * null. Primitives are left alone, having no way to say absent - which is why the field-access
     * layer treats a primitive-backed column as unclearable in the first place.
     *
     * @param dto           the freshly bound DTO
     * @param presentFields names of the fields the payload actually carried
     */
    public void blankAbsentProperties(Object dto, Set<String> presentFields) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(dto);
        for (PropertyDescriptor property : wrapper.getPropertyDescriptors()) {
            String name = property.getName();
            if (presentFields.contains(name) || "class".equals(name)) {
                continue;
            }
            Class<?> type = property.getPropertyType();
            if (type == null || type.isPrimitive()
                    || !wrapper.isWritableProperty(name) || !wrapper.isReadableProperty(name)) {
                continue;
            }
            if (wrapper.getPropertyValue(name) != null) {
                wrapper.setPropertyValue(name, null);
            }
        }
    }

    /**
     * Binds a prepared tree to the target type, pinning the discriminator when the target is one
     * subtype of a polymorphic DTO.
     *
     * @param root       the payload
     * @param targetType the DTO type to bind to
     * @param <T>        the DTO type
     * @return the bound DTO
     * @throws IllegalRequestDataException if the payload does not fit the target type
     */
    public <T> T bind(ObjectNode root, Class<T> targetType) {
        pinDiscriminator(root, targetType);
        try {
            return objectMapper.treeToValue(root, targetType);
        } catch (JsonProcessingException e) {
            throw new IllegalRequestDataException("Request body does not bind to "
                    + targetType.getSimpleName() + ": " + e.getOriginalMessage());
        }
    }

    /**
     * Writes the target type's own discriminator into the payload.
     * <p>
     * {@code RackTo} and its siblings inherit {@code @JsonTypeInfo} from {@code ComponentTo}, so
     * Jackson demands {@code componentClass} even when the declared target already says which
     * subtype it is - a partial body without it is an {@code InvalidTypeIdException}, which is why
     * {@code PUT /api/housing/racks/{id}} with {@code {"description": "x"}} used to answer 500.
     * Three controllers had each written this out by hand for their own hierarchy, taking the value
     * from the stored row; this is the same move made once, taking it from the target type, which
     * for those three is the same value because their target type is derived from the stored row.
     * <p>
     * The payload's own value is overwritten rather than merely defaulted, which is the point: the
     * URL says what kind of thing is being updated, so a client cannot turn a rack into a router by
     * saying so in the body.
     */
    public void pinDiscriminator(ObjectNode root, Class<?> targetType) {
        JsonTypeInfo typeInfo = org.springframework.core.annotation.AnnotationUtils
                .findAnnotation(targetType, JsonTypeInfo.class);
        if (typeInfo == null || typeInfo.use() != JsonTypeInfo.Id.NAME) {
            return;
        }
        String property = typeInfo.property().isEmpty()
                ? typeInfo.use().getDefaultPropertyName() : typeInfo.property();
        String name = subtypeNameOf(targetType);
        if (name != null) {
            root.put(property, name);
        }
    }

    /**
     * The name this concrete type is registered under, from its own {@code @JsonTypeName} or from
     * the {@code @JsonSubTypes} list on whichever ancestor declares it. Null for an abstract base,
     * which is registered under no name and must keep whatever the payload says.
     */
    private static String subtypeNameOf(Class<?> targetType) {
        JsonTypeName typeName = targetType.getAnnotation(JsonTypeName.class);
        if (typeName != null && !typeName.value().isEmpty()) {
            return typeName.value();
        }
        for (Class<?> current = targetType; current != null; current = current.getSuperclass()) {
            JsonSubTypes subTypes = current.getAnnotation(JsonSubTypes.class);
            if (subTypes == null) {
                continue;
            }
            for (JsonSubTypes.Type type : subTypes.value()) {
                if (type.value() == targetType && !type.name().isEmpty()) {
                    return type.name();
                }
            }
        }
        return null;
    }
}
