package net.switchscope.to.security;

import java.util.List;

/**
 * The whole authorization configuration and its coverage, in one machine-readable document.
 * <p>
 * Serves the point of the concept: a grant table nobody can verify is how the previous gap went
 * unnoticed. This can be diffed between environments and asserted on in CI - {@code missingInDatabase}
 * and {@code unannotatedEndpoints} empty is the property worth failing a build over.
 *
 * @param mode                 the registry mode the application is running in
 * @param roles                every role, with its grants
 * @param permissions          every configurable operation
 * @param grants               role code to permission code, flattened for easy diffing
 * @param endpoints            every endpoint served, with the permission it declares
 * @param missingInDatabase    required by code, absent from the table - ungrantable operations
 * @param orphanPermissions    in the table, required by nothing - dead configuration
 * @param unannotatedEndpoints endpoints declaring neither a permission nor an exemption
 * @param unproxyableEndpoints endpoints whose declared permission Spring AOP cannot enforce
 */
public record PermissionMatrixTo(String mode,
                                 List<RoleTo> roles,
                                 List<PermissionTo> permissions,
                                 List<GrantTo> grants,
                                 List<EndpointTo> endpoints,
                                 List<String> missingInDatabase,
                                 List<String> orphanPermissions,
                                 List<EndpointTo> unannotatedEndpoints,
                                 List<EndpointTo> unproxyableEndpoints) {

    public record RoleTo(String code, String displayName, String description, boolean systemRole,
                         boolean active, int permissionCount) {
    }

    public record PermissionTo(String code, String domain, String resource, String action,
                               String displayName, String description, boolean active) {
    }

    public record GrantTo(String role, String permission) {
    }

    public record EndpointTo(String httpMethod, String pattern, String handler, String permission,
                             String status, String note) {
    }
}
