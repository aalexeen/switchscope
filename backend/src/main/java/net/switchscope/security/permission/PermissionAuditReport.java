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
 * @param endpoints          every endpoint served under the application's own controllers
 * @param requiredCodes      permission codes the code asks for
 * @param missingInDatabase  asked for in code, absent from the table - the operation cannot be
 *                           granted at all, so this is an error
 * @param orphanPermissions  in the table, asked for by no endpoint - a dead configuration row that
 *                           misleads whoever reads the admin UI
 * @param unannotatedEndpoints endpoints declaring neither a permission nor an exemption
 */
public record PermissionAuditReport(List<EndpointPermission> endpoints,
                                    SortedSet<String> requiredCodes,
                                    SortedSet<String> missingInDatabase,
                                    SortedSet<String> orphanPermissions,
                                    List<EndpointPermission> unannotatedEndpoints) {

    /**
     * Whether anything found here is an error rather than a warning: an operation that cannot be
     * configured, or an endpoint that says nothing about what it needs.
     */
    public boolean hasErrors() {
        return !missingInDatabase.isEmpty() || !unannotatedEndpoints.isEmpty();
    }
}
