package net.switchscope.web.payload;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.security.policy.NullFieldApplier;
import net.switchscope.security.policy.UpdatePolicy;
import net.switchscope.security.policy.UpdatePolicyResolver;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Turns an update request body into a {@link PartialUpdate}: the bound DTO, the fields the payload
 * actually carried, and the policy that governs clearing them.
 * <p>
 * Twenty PUT handlers needed the same four steps - parse, bind, collect present fields, resolve the
 * caller's policy - and eleven of them spelled them out, with the field collection copied verbatim
 * eight times. This is those steps once, which is also what makes it possible to say that every
 * update endpoint applies the same rules: there is one implementation of them left.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PartialUpdateReader {

    private final JsonPayload json;
    private final UpdatePolicyResolver policyResolver;
    private final NullFieldApplier nullFieldApplier;

    /**
     * Reads an ordinary update body.
     *
     * @param body     the raw request body
     * @param dtoClass the DTO type to bind to
     * @param <T>      the DTO type
     * @return the validated update
     */
    public <T> PartialUpdate<T> read(String body, Class<T> dtoClass) {
        return read(json.asObject(body), dtoClass);
    }

    /**
     * Reads an update body a caller has already prepared - the polymorphic endpoints pin the stored
     * discriminator into the tree first, so that a client cannot change the type of an existing row
     * and a payload that omits the discriminator still binds.
     *
     * @param root     the payload, with any discriminator already pinned
     * @param dtoClass the concrete DTO type to bind to
     * @param <T>      the DTO type
     * @return the validated update
     */
    public <T> PartialUpdate<T> read(ObjectNode root, Class<T> dtoClass) {
        // Before the fields are counted, so that a discriminator the target type supplies counts as
        // one the request carried - otherwise the next line would blank it straight back out.
        json.pinDiscriminator(root, dtoClass);

        Map<String, JsonNode> presentFields = json.presentFields(root);
        T dto = json.bind(root, dtoClass);
        json.blankAbsentProperties(dto, presentFields.keySet());

        UpdatePolicy policy = policyResolver.resolve();
        log.debug("Reading update for {} under policy {}", dtoClass.getSimpleName(), policy.getPolicyName());

        return new PartialUpdate<>(dto, dtoClass, presentFields, policy, nullFieldApplier);
    }
}
