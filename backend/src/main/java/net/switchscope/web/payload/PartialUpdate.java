package net.switchscope.web.payload;

import com.fasterxml.jackson.databind.JsonNode;
import net.switchscope.security.policy.NullFieldApplier;
import net.switchscope.security.policy.UpdatePolicy;
import net.switchscope.security.policy.UpdatePolicyValidator;

import java.util.Map;

/**
 * An update request that has been read and bound, carried from the controller into the service so
 * that the entity can be modified inside the transaction that loaded it.
 * <p>
 * Services already accepted the raw {@code Map<String, JsonNode>} of present fields for the same
 * reason; this replaces that with a value that also knows what to do with them, so that no service
 * has to remember the order of validate-then-apply, and none of them can get it wrong differently.
 *
 * <h2>Why the only public way to build one goes through the reader</h2>
 * {@link #applyNulls} writes nulls into a managed entity, and whether it may is decided by
 * {@link UpdatePolicyValidator} at that moment - it cannot be decided earlier, because a null over
 * a value that is already null changes nothing and so needs no permission. What the reader
 * contributes is the caller's {@link UpdatePolicy} and the fields the body carried; the only
 * constructor that produces a clearing instance is the package-private one the reader calls. An
 * instance that clears fields therefore cannot exist without a policy attached to it, which is a
 * stronger guarantee than a convention that every call site remembers to resolve one, and this
 * project has already been bitten once by a permission check that was written down but never
 * enforced.
 * <p>
 * {@link #valuesOnly} is the deliberate opposite: a DTO that arrived already bound, with no raw
 * body to read presence from. Absent and explicitly-null are indistinguishable in it, so it clears
 * nothing - the behaviour every endpoint had before this class existed.
 *
 * @param <T> the bound DTO type
 */
public final class PartialUpdate<T> {

    private final T dto;
    private final Class<?> dtoClass;
    private final Map<String, JsonNode> presentFields;
    private final UpdatePolicy policy;
    private final NullFieldApplier applier;

    PartialUpdate(T dto, Class<?> dtoClass, Map<String, JsonNode> presentFields,
                  UpdatePolicy policy, NullFieldApplier applier) {
        this.dto = dto;
        this.dtoClass = dtoClass;
        this.presentFields = presentFields;
        this.policy = policy;
        this.applier = applier;
    }

    /**
     * An update built from an already-bound DTO, with no request body behind it. Carries no
     * presence information and therefore clears nothing.
     *
     * @param dto the DTO to apply
     * @param <T> the DTO type
     * @return an update that applies the DTO's values and nothing else
     */
    public static <T> PartialUpdate<T> valuesOnly(T dto) {
        return new PartialUpdate<>(dto, dto == null ? Object.class : dto.getClass(), Map.of(), null, null);
    }

    /**
     * The bound DTO.
     */
    public T dto() {
        return dto;
    }

    /**
     * The DTO class the payload was bound to.
     */
    public Class<?> dtoClass() {
        return dtoClass;
    }

    /**
     * The payload's top-level fields. A name present with a null value is a request to clear.
     */
    public Map<String, JsonNode> presentFields() {
        return presentFields;
    }

    /**
     * Clears every field the payload explicitly set to null.
     * <p>
     * Call it after the mapper and the reference resolver have run: those two apply what the
     * payload <em>says</em>, and IGNORE means neither of them can express what it leaves out.
     *
     * @param entity the managed entity being updated
     */
    public void applyNulls(Object entity) {
        if (applier != null) {
            applier.apply(entity, dtoClass, presentFields, policy);
        }
    }
}
