package net.switchscope.web.payload;

import lombok.RequiredArgsConstructor;
import net.switchscope.error.PayloadValidationException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.Set;

/**
 * Bean validation for bodies that are read as raw JSON instead of being bound by Spring.
 * <p>
 * A typed {@code @Valid @RequestBody} gets this for free, which is why nobody noticed it was
 * missing: since the update endpoints started reading the body as a string - the only way to tell
 * an absent field from one explicitly set to {@code null} - {@code @Valid} was gone from every PUT
 * in the project, and the only remaining check was the entity's own constraints at flush time. A
 * constraint that lives on the DTO and not on the entity ({@code @NoHtml} on
 * {@code InstallationTo.installedBy}, {@code @Size} on {@code AccessPointTo.controllerIp}) was
 * therefore checked on create and not on update.
 *
 * <h2>Why an update validates only the fields the body carried</h2>
 * Validating the whole DTO would fail every partial request: the absent fields have been blanked
 * back to {@code null} on purpose, so {@code @NotBlank name} would reject a body that only wanted
 * to change the description.
 *
 * <h2>Why a field sent as null is skipped</h2>
 * Whether a field may be cleared is not a bean-validation question in this project - it is the
 * field-access layer's, and its answer is a 403 rather than a 422. {@code LocationTo.typeId} is
 * both {@code @NotNull} and {@code @FieldAccess(REQUIRED)}: validating the null here would answer
 * "unprocessable" to a request that the policy has a better answer for, and would move a documented
 * refusal from one status to another. So nulls stay with the policy, and this validates the values
 * the caller actually sent.
 * <p>
 * Failures are reported as {@link PayloadValidationException}, which carries the same
 * {@code invalid_params} map that a bound {@code @Valid} body produces, so a client sees one shape
 * of validation error however the route read its body.
 */
@Component
@RequiredArgsConstructor
public class PayloadValidator {

    /**
     * The Spring adapter rather than the plain {@code jakarta.validation.Validator}, for its
     * {@code validateValue(Class, String, Object, Errors)} - the per-property check the partial
     * updates need - and because it reports into a {@code BindingResult}, which is what the error
     * handler already knows how to turn into {@code invalid_params}.
     */
    private final SpringValidatorAdapter validator;

    /**
     * Validates a whole DTO, as a create does.
     *
     * @param dto the bound DTO
     * @throws PayloadValidationException if any constraint is violated
     */
    public void validateWhole(Object dto) {
        BeanPropertyBindingResult errors = errorsFor(dto);
        validator.validate(dto, errors);
        failIfInvalid(errors);
    }

    /**
     * Validates the fields an update body carried, skipping the ones it sent as null.
     *
     * @param dto           the bound DTO, with absent properties already blanked
     * @param presentFields the names the payload carried
     * @throws PayloadValidationException if any constraint is violated
     */
    public void validatePresent(Object dto, Set<String> presentFields) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(dto);
        BeanPropertyBindingResult errors = errorsFor(dto);
        for (String name : presentFields) {
            // A payload name that is not a property of the DTO is not this class's business:
            // binding has already had its say about it, and validateValue would throw on it.
            if (!wrapper.isReadableProperty(name)) {
                continue;
            }
            Object value = wrapper.getPropertyValue(name);
            if (value == null) {
                continue;
            }
            validator.validateValue(dto.getClass(), name, value, errors);
        }
        failIfInvalid(errors);
    }

    private static BeanPropertyBindingResult errorsFor(Object dto) {
        return new BeanPropertyBindingResult(dto, dto.getClass().getSimpleName());
    }

    private static void failIfInvalid(BeanPropertyBindingResult errors) {
        if (errors.hasErrors()) {
            throw new PayloadValidationException(errors);
        }
    }
}
