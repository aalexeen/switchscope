package net.switchscope.security.permission;

import java.util.List;
import java.util.SortedSet;

/**
 * What the startup scan found: every endpoint, every permission code the code asks for, and how
 * that set differs from the {@code permissions} table in both directions.
 * <p>
 * A grant table is worthless if nobody can check that it covers every operation - which is exactly
 * how the {@code @PreAuthorize} gap survived: the annotations were there, the mechanism to verify
 * them was not. This report is that mechanism; {@code GET /api/admin/permissions/matrix} serves it
 * so the check can be diffed between environments and run in CI.
 *
 * @param endpoints            every endpoint served under the application's own controllers
 * @param requiredCodes        permission codes the code asks for
 * @param missingInDatabase    asked for in code, absent from the table - the operation cannot be
 *                             granted at all, so this is an error
 * @param orphanPermissions    in the table, asked for by no endpoint - a dead configuration row
 *                             that misleads whoever reads the admin UI
 * @param unannotatedEndpoints endpoints declaring neither a permission nor an exemption
 * @param unproxyableEndpoints guarded endpoints Spring AOP cannot advise - a {@code final} class,
 *                             or a {@code final} or non-public method. The annotation is there and
 *                             the check silently never runs; without this finding nothing would
 *                             notice, which is the same shape of failure the registry exists for
 */
public record PermissionAuditReport(List<EndpointPermission> endpoints,
                                    SortedSet<String> requiredCodes,
                                    SortedSet<String> missingInDatabase,
                                    SortedSet<String> orphanPermissions,
                                    List<EndpointPermission> unannotatedEndpoints,
                                    List<EndpointPermission> unproxyableEndpoints) {

    /**
     * Whether anything found here is an error rather than a warning: an operation that cannot be
     * configured, an endpoint that says nothing about what it needs, or one whose declaration
     * cannot be acted on.
     */
    public boolean hasErrors() {
        return !missingInDatabase.isEmpty() || !unannotatedEndpoints.isEmpty()
                || !unproxyableEndpoints.isEmpty();
    }
}
