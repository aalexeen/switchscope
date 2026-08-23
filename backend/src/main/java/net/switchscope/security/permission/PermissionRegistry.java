package net.switchscope.security.permission;

import lombok.extern.slf4j.Slf4j;
import net.switchscope.repository.security.PermissionRepository;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Checks on startup that every API operation is a configurable permission, and reports where it is
 * not.
 * <p>
 * Modelled on {@link net.switchscope.service.component.InstallableComponentRegistry}, which scans
 * annotations and reconciles them with catalog rows in both directions. The difference is the
 * source: this one reads {@link RequestMappingHandlerMapping}, not the classpath. That mapping is
 * the ground truth for what the application actually serves - dead controllers never appear in it -
 * and, more importantly, {@link HandlerMethod#getBeanType()} gives the <em>concrete</em> controller.
 * That is what lets {@link net.switchscope.web.AbstractCrudController} declare an action once for
 * nine subclasses and still produce nine distinct permission codes.
 * <p>
 * Three findings, and their weights:
 * <ul>
 *   <li>code asks for a permission the table does not have - the operation cannot be granted to
 *       anyone, so <b>ERROR</b>;</li>
 *   <li>the table has a permission no endpoint asks for - a dead row, <b>WARN</b>;</li>
 *   <li>an endpoint declaring neither {@link RequiresPermission} nor {@link AuthenticatedOnly} -
 *       precisely the hole that lived in this project unnoticed, so <b>ERROR</b>.</li>
 * </ul>
 * In {@link Mode#AUDIT}, the stage-1 default, all three are reported and none of them block: the
 * point of the first pass is to see the whole picture without locking out users of a working
 * system. {@link Mode#ENFORCE} makes the errors fail startup, and belongs with the switch to
 * request-time enforcement.
 */
@Component
@Slf4j
public class PermissionRegistry {

    /**
     * What the registry does about the errors it finds.
     */
    public enum Mode {
        /** Report only. Stage 1: observation without risk. */
        AUDIT,
        /** Fail startup on an unconfigurable or unannotated operation. */
        ENFORCE
    }

    private static final String OWN_PACKAGE = "net.switchscope";

    private final PermissionRepository permissionRepository;
    private final RequestMappingHandlerMapping handlerMapping;
    private final Mode mode;

    private final Map<Method, String> codeByMethod = new HashMap<>();
    private volatile PermissionAuditReport report =
            new PermissionAuditReport(List.of(), emptySorted(), emptySorted(), emptySorted(), List.of());

    public PermissionRegistry(PermissionRepository permissionRepository,
                              @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping,
                              @org.springframework.beans.factory.annotation.Value(
                                      "${switchscope.security.permission.mode:AUDIT}") Mode mode) {
        this.permissionRepository = permissionRepository;
        this.handlerMapping = handlerMapping;
        this.mode = mode;
    }

    /**
     * Runs after the context is up, so the handler mapping is fully populated and the datasource is
     * available. Not {@code @PostConstruct}: the mapping is built from the very beans this would be
     * inspecting.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void audit() {
        List<EndpointPermission> endpoints = scanEndpoints();
        this.report = reconcile(endpoints);
        logReport(this.report);
        if (mode == Mode.ENFORCE && report.hasErrors()) {
            throw new IllegalStateException(
                    "Permission coverage is incomplete: " + report.missingInDatabase().size()
                            + " permission(s) missing from the database, "
                            + report.unannotatedEndpoints().size() + " unannotated endpoint(s)."
                            + " See the report above.");
        }
    }

    /**
     * The permission an endpoint requires, for enforcement built on the same scan the report comes
     * from - so that what is enforced and what is reported cannot drift apart.
     *
     * @param handlerMethod the handler Spring selected
     * @return the required permission code, or {@code null} if the endpoint requires none
     */
    public String getRequiredCode(HandlerMethod handlerMethod) {
        return codeByMethod.get(specificMethod(handlerMethod));
    }

    public PermissionAuditReport getReport() {
        return report;
    }

    public Mode getMode() {
        return mode;
    }

    private List<EndpointPermission> scanEndpoints() {
        codeByMethod.clear();
        List<EndpointPermission> endpoints = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (!handlerMethod.getBeanType().getName().startsWith(OWN_PACKAGE)) {
                continue; // springdoc and friends are not ours to gate
            }
            endpoints.add(describe(entry.getKey(), handlerMethod));
        }
        endpoints.sort(Comparator.comparing(EndpointPermission::pattern)
                .thenComparing(EndpointPermission::httpMethod));
        return endpoints;
    }

    private EndpointPermission describe(RequestMappingInfo info, HandlerMethod handlerMethod) {
        Method method = specificMethod(handlerMethod);
        Class<?> controller = handlerMethod.getBeanType();
        String handler = controller.getSimpleName() + '#' + method.getName();
        String httpMethod = httpMethodsOf(info);
        String pattern = patternOf(info);

        AuthenticatedOnly exempt = findAnnotation(method, controller, AuthenticatedOnly.class);
        if (exempt != null) {
            return new EndpointPermission(httpMethod, pattern, handler, null,
                    EndpointPermission.Status.AUTHENTICATED_ONLY, exempt.reason());
        }

        RequiresPermission required = findAnnotation(method, controller, RequiresPermission.class);
        if (required == null) {
            return new EndpointPermission(httpMethod, pattern, handler, null,
                    EndpointPermission.Status.UNANNOTATED, null);
        }

        PermissionResource resource = AnnotationUtils.findAnnotation(controller, PermissionResource.class);
        String code = PermissionCode.join(resource == null ? null : resource.value(), required.value());
        codeByMethod.put(method, code);
        return new EndpointPermission(httpMethod, pattern, handler, code,
                EndpointPermission.Status.GUARDED, null);
    }

    /**
     * The method as the concrete controller sees it. A subclass may override {@code getAll()}
     * without repeating the annotation, so the lookup falls back to the super-method - and the
     * resource still comes from the concrete class, which is the whole point.
     */
    private static Method specificMethod(HandlerMethod handlerMethod) {
        return AopUtils.getMostSpecificMethod(handlerMethod.getMethod(), handlerMethod.getBeanType());
    }

    private static <A extends java.lang.annotation.Annotation> A findAnnotation(
            Method method, Class<?> controller, Class<A> annotationType) {
        A onMethod = AnnotationUtils.findAnnotation(method, annotationType);
        return onMethod != null ? onMethod : AnnotationUtils.findAnnotation(controller, annotationType);
    }

    private PermissionAuditReport reconcile(List<EndpointPermission> endpoints) {
        SortedSet<String> requiredCodes = endpoints.stream()
                .map(EndpointPermission::permissionCode)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));

        Set<String> dbCodes = Set.copyOf(permissionRepository.findAllCodes());

        SortedSet<String> missing = requiredCodes.stream()
                .filter(code -> !dbCodes.contains(code))
                .collect(Collectors.toCollection(TreeSet::new));
        SortedSet<String> orphans = dbCodes.stream()
                .filter(code -> !requiredCodes.contains(code))
                .collect(Collectors.toCollection(TreeSet::new));
        List<EndpointPermission> unannotated = endpoints.stream()
                .filter(e -> e.status() == EndpointPermission.Status.UNANNOTATED)
                .toList();

        return new PermissionAuditReport(endpoints, requiredCodes, missing, orphans, unannotated);
    }

    private void logReport(PermissionAuditReport report) {
        long guarded = report.endpoints().stream()
                .filter(e -> e.status() == EndpointPermission.Status.GUARDED).count();
        long exempt = report.endpoints().stream()
                .filter(e -> e.status() == EndpointPermission.Status.AUTHENTICATED_ONLY).count();
        log.info("Permission audit [{}]: {} endpoints - {} guarded, {} authenticated-only, {} unannotated;"
                        + " {} distinct permissions required, {} missing in database, {} orphaned rows",
                mode, report.endpoints().size(), guarded, exempt, report.unannotatedEndpoints().size(),
                report.requiredCodes().size(), report.missingInDatabase().size(),
                report.orphanPermissions().size());

        report.missingInDatabase().forEach(code ->
                log.error("Permission '{}' is required by code but missing from the database:"
                        + " the operation cannot be granted to anyone", code));
        report.unannotatedEndpoints().forEach(endpoint ->
                log.error("Endpoint {} declares neither @RequiresPermission nor @AuthenticatedOnly", endpoint));
        report.orphanPermissions().forEach(code ->
                log.warn("Permission '{}' exists in the database but no endpoint requires it", code));

        if (!report.hasErrors()) {
            log.info("Permission audit: every served endpoint is either configurable or explicitly exempt");
        }
    }

    private static String httpMethodsOf(RequestMappingInfo info) {
        Set<org.springframework.web.bind.annotation.RequestMethod> methods =
                info.getMethodsCondition().getMethods();
        return methods.isEmpty() ? "ANY"
                : methods.stream().map(Enum::name).sorted().collect(Collectors.joining(","));
    }

    private static String patternOf(RequestMappingInfo info) {
        if (info.getPathPatternsCondition() != null) {
            return info.getPathPatternsCondition().getPatterns().stream()
                    .map(Object::toString).sorted().collect(Collectors.joining(","));
        }
        if (info.getPatternsCondition() != null) {
            return String.join(",", new TreeSet<>(info.getPatternsCondition().getPatterns()));
        }
        return "";
    }

    private static SortedSet<String> emptySorted() {
        return java.util.Collections.unmodifiableSortedSet(new TreeSet<>());
    }
}
