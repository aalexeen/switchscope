package net.switchscope.security.permission;

/**
 * One endpoint Spring actually serves, and what it declares about permissions.
 *
 * @param httpMethod     the HTTP methods the mapping answers, or {@code ANY}
 * @param pattern        the URL pattern
 * @param handler        {@code ControllerSimpleName#method}, naming the concrete controller
 * @param permissionCode the required permission, or {@code null} when the endpoint is exempt or
 *                       unannotated
 * @param status          how the endpoint is classified
 * @param note           the exemption reason for {@link Status#AUTHENTICATED_ONLY}, else {@code null}
 */
public record EndpointPermission(String httpMethod,
                                 String pattern,
                                 String handler,
                                 String permissionCode,
                                 Status status,
                                 String note) {

    public enum Status {
        /** Declares a permission via {@link RequiresPermission}. */
        GUARDED,
        /** Deliberately outside the permission model via {@link AuthenticatedOnly}. */
        AUTHENTICATED_ONLY,
        /** Declares nothing - a hole, and the reason the registry exists. */
        UNANNOTATED
    }

    @Override
    public String toString() {
        return httpMethod + " " + pattern + " (" + handler + ")";
    }
}
