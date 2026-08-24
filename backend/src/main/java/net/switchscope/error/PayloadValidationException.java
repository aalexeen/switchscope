package net.switchscope.error;

import lombok.Getter;
import org.springframework.validation.BindingResult;

import static net.switchscope.error.ErrorType.BAD_REQUEST;

/**
 * Bean validation failed on a body that Spring did not bind, so no {@code BindException} was
 * thrown for it.
 * <p>
 * Carries the {@link BindingResult} rather than a flattened message: the error handler turns it
 * into the same {@code invalid_params} map that a bound {@code @Valid @RequestBody} produces, so
 * the routes that read their body as raw JSON report validation failures in the shape clients
 * already receive from the ones that do not.
 * <p>
 * Unchecked, unlike {@code BindException}, because it is thrown from
 * {@code PartialUpdateReader.read} - a method twenty controllers call, none of which could add a
 * checked exception to its signature without saying so in its own.
 */
public class PayloadValidationException extends AppException {

    @Getter
    private final transient BindingResult bindingResult;

    public PayloadValidationException(BindingResult bindingResult) {
        super("Request body failed validation", BAD_REQUEST);
        this.bindingResult = bindingResult;
    }
}
