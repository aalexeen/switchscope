package net.switchscope.error;

import static net.switchscope.error.ErrorType.MALFORMED_REQUEST;

/**
 * A request body the server could not read: not JSON, not the shape the route takes, or a value
 * that does not bind to the field it is given for.
 * <p>
 * Kept apart from {@link IllegalRequestDataException}, which is 422 and means the opposite - the
 * payload was read and understood, and its contents were refused. Spring answers the same mistake
 * on a typed {@code @RequestBody} with {@code HttpMessageNotReadableException}, which the exception
 * handler maps to this same status, so a route that reads its body as a string and a route that
 * lets Spring bind it do not disagree about what an unreadable body is.
 */
public class MalformedRequestException extends AppException {
    public MalformedRequestException(String msg) {
        super(msg, MALFORMED_REQUEST);
    }
}
