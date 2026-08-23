package net.switchscope.security.permission;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an endpoint that deliberately needs no configurable permission - being authenticated is the
 * whole requirement.
 * <p>
 * This exists so that "no permission" and "someone forgot the annotation" are different states. The
 * registry treats an unannotated endpoint as a hole and reports it; without an explicit opt-out the
 * self-service endpoints would have to be either wrongly gated or silently tolerated, and a
 * tolerated exception is how the previous gap survived.
 * <p>
 * Self-service is the real case: {@code /api/auth/check} has to answer before the caller knows what
 * they may do, and a user reading or deleting their own profile is not exercising a grantable
 * permission.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AuthenticatedOnly {

    /**
     * Why this endpoint is outside the permission model. Required: an unexplained exemption is
     * indistinguishable from an oversight.
     */
    String reason();
}
