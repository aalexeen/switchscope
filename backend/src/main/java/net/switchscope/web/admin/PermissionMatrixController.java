package net.switchscope.web.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.model.security.PermissionEntity;
import net.switchscope.model.security.RoleEntity;
import net.switchscope.repository.security.PermissionRepository;
import net.switchscope.repository.security.RoleRepository;
import net.switchscope.security.permission.EndpointPermission;
import net.switchscope.security.permission.PermissionAuditReport;
import net.switchscope.security.permission.PermissionRegistry;
import net.switchscope.security.permission.PermissionResource;
import net.switchscope.security.permission.RequiresPermission;
import net.switchscope.to.security.PermissionMatrixTo;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

/**
 * Serves the coverage matrix: what may be granted, what is granted, and where code and configuration
 * disagree.
 * <p>
 * Lives under {@code /api/admin/**}, which {@code SecurityConfig} already restricts to ADMIN - a
 * genuinely coarse boundary, which is the one place role checks still belong.
 */
@Slf4j
@RestController
@RequestMapping(value = PermissionMatrixController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PermissionResource("system.permission")
public class PermissionMatrixController {

    static final String REST_URL = "/api/admin/permissions";

    private final PermissionRegistry registry;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @RequiresPermission("read")
    @GetMapping("/matrix")
    @Transactional(readOnly = true)
    public PermissionMatrixTo matrix() {
        log.info("permission matrix requested");
        PermissionAuditReport report = registry.getReport();
        List<RoleEntity> roles = roleRepository.findAllWithPermissions();

        List<PermissionMatrixTo.GrantTo> grants = roles.stream()
                .flatMap(role -> role.getPermissions().stream()
                        .map(p -> new PermissionMatrixTo.GrantTo(role.getCode(), p.getCode())))
                .sorted(Comparator.comparing(PermissionMatrixTo.GrantTo::role)
                        .thenComparing(PermissionMatrixTo.GrantTo::permission))
                .toList();

        return new PermissionMatrixTo(
                registry.getMode().name(),
                roles.stream().map(PermissionMatrixController::toRole).toList(),
                permissionRepository.findAll().stream()
                        .sorted(Comparator.comparing(PermissionEntity::getCode))
                        .map(PermissionMatrixController::toPermission).toList(),
                grants,
                report.endpoints().stream().map(PermissionMatrixController::toEndpoint).toList(),
                List.copyOf(report.missingInDatabase()),
                List.copyOf(report.orphanPermissions()),
                report.unannotatedEndpoints().stream().map(PermissionMatrixController::toEndpoint).toList());
    }

    private static PermissionMatrixTo.RoleTo toRole(RoleEntity role) {
        return new PermissionMatrixTo.RoleTo(role.getCode(), role.getDisplayName(), role.getDescription(),
                role.isSystemRole(), role.isActive(), role.getPermissions().size());
    }

    private static PermissionMatrixTo.PermissionTo toPermission(PermissionEntity permission) {
        return new PermissionMatrixTo.PermissionTo(permission.getCode(), permission.getDomain(),
                permission.getResource(), permission.getAction(), permission.getDisplayName(),
                permission.getDescription(), permission.isActive());
    }

    private static PermissionMatrixTo.EndpointTo toEndpoint(EndpointPermission endpoint) {
        return new PermissionMatrixTo.EndpointTo(endpoint.httpMethod(), endpoint.pattern(),
                endpoint.handler(), endpoint.permissionCode(), endpoint.status().name(), endpoint.note());
    }
}
