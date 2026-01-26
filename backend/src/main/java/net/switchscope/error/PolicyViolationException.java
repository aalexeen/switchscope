package net.switchscope.error;

import lombok.Getter;

/**
 * Exception thrown when a user attempts to perform an operation
 * that violates the update policy for a field.
 */
@Getter
public class PolicyViolationException extends AppException {

    private final String fieldName;
    private final String policyName;

    public PolicyViolationException(String fieldName, String policyName, String message) {
        super(message, ErrorType.FORBIDDEN);
        this.fieldName = fieldName;
        this.policyName = policyName;
    }

    /**
     * Creates an exception for attempting to nullify a required field.
     */
    public static PolicyViolationException requiredField(String fieldName) {
        return new PolicyViolationException(
                fieldName,
                "REQUIRED",
                String.format("Field '%s' is required and cannot be set to null", fieldName)
        );
    }

    /**
     * Creates an exception for attempting to nullify a field without proper permissions.
     */
    public static PolicyViolationException insufficientPermissions(String fieldName, String policyName) {
        return new PolicyViolationException(
                fieldName,
                policyName,
                String.format("Insufficient permissions to set field '%s' to null. Required role: ADMIN", fieldName)
        );
    }
}
