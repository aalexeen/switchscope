package net.switchscope.security.permission;

/**
 * The {@code <domain>.<resource>:<action>} permission code, split into its parts.
 * <p>
 * The parts are stored as columns on {@link net.switchscope.model.security.PermissionEntity} for
 * grouping and for the coverage matrix, so there has to be exactly one place that decides how a
 * code splits. This is it.
 *
 * @param domain   the leading segment before the first dot, or the whole resource when it has none
 * @param resource the {@code <domain>.<resource>} prefix, verbatim
 * @param action   the part after the colon
 */
public record PermissionCode(String domain, String resource, String action) {

    public static final char ACTION_SEPARATOR = ':';

    /**
     * Joins a resource and an action, or passes a value through if it is already a full code.
     *
     * @param resource the controller's {@link PermissionResource}, may be null when {@code action}
     *                 is already a full code
     * @param action   an action, or a full code containing {@value #ACTION_SEPARATOR}
     * @return the permission code
     * @throws IllegalStateException if a bare action is given with no resource to qualify it
     */
    public static String join(String resource, String action) {
        if (action.indexOf(ACTION_SEPARATOR) >= 0) {
            return action;
        }
        if (resource == null || resource.isBlank()) {
            throw new IllegalStateException("Action '" + action
                    + "' has no resource to qualify it: annotate the controller with @PermissionResource"
                    + " or spell the code out in full");
        }
        return resource + ACTION_SEPARATOR + action;
    }

    /**
     * Splits a code into the parts stored as columns.
     *
     * @param code a {@code <domain>.<resource>:<action>} code
     * @return its parts
     * @throws IllegalArgumentException if the code carries no action
     */
    public static PermissionCode parse(String code) {
        int colon = code.indexOf(ACTION_SEPARATOR);
        if (colon <= 0 || colon == code.length() - 1) {
            throw new IllegalArgumentException("Permission code '" + code
                    + "' is not <domain>.<resource>:<action>");
        }
        String resource = code.substring(0, colon);
        String action = code.substring(colon + 1);
        int dot = resource.indexOf('.');
        String domain = dot > 0 ? resource.substring(0, dot) : resource;
        return new PermissionCode(domain, resource, action);
    }
}
