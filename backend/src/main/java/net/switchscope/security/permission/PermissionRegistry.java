package net.switchscope.security.permission;

import lombok.extern.slf4j.Slf4j;
import net.switchscope.repository.security.PermissionRepository;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
 * Checks that every API operation is a configurable permission, reports where it is not, and
 * answers what a given handler requires so that enforcement and the report cannot disagree.
 * <p>
 * Modelled on {@link net.switchscope.service.component.InstallableComponentRegistry}, which scans
 * annotations and reconciles them with catalog rows in both directions. The difference is the
 * source: this one reads {@link RequestMappingHandlerMapping}, not the classpath. That mapping is
 * the ground truth for what the application actually serves - dead controllers never appear in it -
 * and, more importantly, {@link HandlerMethod#getBeanType()} gives the <em>concrete</em> controller.
 * That is what lets {@link net.switchscope.web.AbstractCrudController} declare an action once for
 * nine subclasses and still produce nine distinct permission codes.
 * <p>
 * Four findings, and their weights:
 * <ul>
 *   <li>code asks for a permission the table does not have - the operation cannot be granted to
 *       anyone, so <b>ERROR</b>;</li>
 *   <li>the table has a permission no endpoint asks for - a dead row, <b>WARN</b>;</li>
 *   <li>an endpoint declaring neither {@link RequiresPermission} nor {@link AuthenticatedOnly} -
 *       precisely the hole that lived in this project unnoticed, so <b>ERROR</b>;</li>
 *   <li>a guarded endpoint Spring AOP cannot advise - the declaration is there and the check never
 *       runs, so <b>ERROR</b>.</li>
 * </ul>
 *
 * <h2>Why the work happens before the context is "ready"</h2>
 * The scan used to run on {@code ApplicationReadyEvent}. The web server starts earlier, in
 * {@code finishRefresh()} via {@code WebServerStartStopLifecycle}, so there was a window in which
 * requests were served against an empty lookup table. With nothing enforced that window was
 * invisible; with enforcement it is either a burst of wrong 403s or - worse - an open API. Both
 * halves therefore run from {@link SmartInitializingSingleton}, which fires inside
 * {@code finishBeanFactoryInitialization}: after every singleton exists (so the handler mapping is
 * built and the datasource has migrated) and before the port is open.
 */
@Component
@Slf4j
public class PermissionRegistry implements SmartInitializingSingleton {

    /**
     * The package the application's own controllers live in, and the edge of what this mechanism
     * speaks for. Public because the advisor's pointcut has to draw the same line: the scan skips
     * a handler outside it - springdoc's resources are not ours to gate - so a pointcut that did
     * not skip it would hand the manager a handler the scan never recorded. That is
     * {@link EndpointRequirement.Unknown}, and Unknown closes, which is how {@code mode: ENFORCE}
     * turned {@code /v3/api-docs} and the Swagger UI into 403s.
     */
    public static final String OWN_PACKAGE = "net.switchscope";

    private final PermissionRepository permissionRepository;
    /**
     * Resolved lazily rather than injected. The mapping instantiates every controller to find its
     * handler methods, and one of those controllers - {@code PermissionMatrixController} - needs
     * this registry, so constructor injection makes the two beans depend on each other in a circle
     * that only happens to resolve.
     * <p>
     * Qualified by name because actuator contributes a second bean of the same type
     * ({@code controllerEndpointHandlerMapping}); the one wanted is the MVC mapping for the
     * application's own controllers.
     */
    private final ObjectProvider<RequestMappingHandlerMapping> handlerMappingProvider;
    private final PermissionEnforcementProperties properties;

    /**
     * Keyed by the concrete controller as well as the method: nine subclasses inherit
     * {@code create}/{@code update}/{@code delete} from {@link net.switchscope.web.AbstractCrudController}
     * without overriding them, so the {@code Method} alone is the same object for all nine and would
     * collapse nine codes into one, with whichever the scan reached last winning. Published as an
     * immutable snapshot because it is written on startup and read from request threads.
     */
    private volatile Map<HandlerKey, EndpointRequirement> requirementByHandler = Map.of();
    private volatile boolean scanned = false;
    private volatile PermissionAuditReport report = new PermissionAuditReport(
            List.of(), emptySorted(), emptySorted(), emptySorted(), List.of(), List.of());

    public PermissionRegistry(PermissionRepository permissionRepository,
                              @Qualifier("requestMappingHandlerMapping")
                              ObjectProvider<RequestMappingHandlerMapping> handlerMappingProvider,
                              PermissionEnforcementProperties properties) {
        this.permissionRepository = permissionRepository;
        this.handlerMappingProvider = handlerMappingProvider;
        this.properties = properties;
    }

    @Override
    public void afterSingletonsInstantiated() {
        audit();
    }

    /**
     * Scans, reconciles with the table and reports. Public so a test can re-run it; ordinarily
     * called once, before the application serves anything.
     */
    public void audit() {
        List<EndpointPermission> endpoints = scanEndpoints();
        this.report = reconcile(endpoints);
        logReport(this.report);
        if (shouldFailStartup(this.report, properties)) {
            throw new IllegalStateException(
                    "Permission coverage is incomplete where it is enforced: "
                            + report.missingInDatabase().size() + " permission(s) missing from the"
                            + " database, " + report.unannotatedEndpoints().size()
                            + " unannotated endpoint(s), " + report.unproxyableEndpoints().size()
                            + " unproxyable endpoint(s). See the report above.");
        }
    }

    /**
     * Whether a finding is bad enough, <em>here</em>, to refuse to start.
     * <p>
     * Not simply "mode is ENFORCE". A domain promoted through {@code enforce-domains} refuses for
     * real while the global mode is still {@code SHADOW}, so gating the check on the global mode
     * alone would let a catalog permission missing from the table produce blanket 403s behind a
     * clean startup log - the exact outcome the ERROR finding exists to make loud. Conversely, a
     * finding in a domain nobody enforces yet must not stop the application: shadow is for looking,
     * and something to look at is the point.
     * <p>
     * An unannotated endpoint has no permission code and therefore no domain to be promoted by, so
     * it follows the global mode - the same rule enforcement applies to it at request time.
     *
     * @param report     what the scan found
     * @param properties the enforcement switch
     * @return whether startup must fail
     */
    static boolean shouldFailStartup(PermissionAuditReport report,
                                     PermissionEnforcementProperties properties) {
        if (!properties.isEnforcingAnything() || !report.hasErrors()) {
            return false;
        }
        if (properties.getMode() == PermissionMode.ENFORCE) {
            return true;
        }
        return report.missingInDatabase().stream().anyMatch(properties::isEnforced)
                || report.unproxyableEndpoints().stream()
                        .map(EndpointPermission::permissionCode)
                        .anyMatch(properties::isEnforced);
    }

    /**
     * What an endpoint requires, for enforcement built on the same scan the report comes from - so
     * that what is enforced and what is reported cannot drift apart.
     * <p>
     * The caller must pass the method already resolved against the concrete class, exactly as the
     * scan resolves it. That is not a formality: the subclasses of {@code AbstractCrudController}
     * carry <em>bridge methods</em> - {@code RackController} declares both {@code RackTo get(UUID)}
     * and the synthetic {@code BaseTo get(UUID)} - so the raw reflective method reaching an AOP
     * interceptor need not be the one the scan recorded. Both sides go through
     * {@link AopUtils#getMostSpecificMethod} so the two keys are equal by construction.
     *
     * @param targetClass the concrete controller class
     * @param method      the most specific method on it
     * @return what the endpoint requires; never {@code null}
     */
    public EndpointRequirement requirementOf(Class<?> targetClass, Method method) {
        if (!scanned) {
            return EndpointRequirement.Unknown.NOT_SCANNED;
        }
        EndpointRequirement requirement = requirementByHandler.get(new HandlerKey(targetClass, method));
        return requirement != null ? requirement : EndpointRequirement.Unknown.NO_ANNOTATION;
    }

    /**
     * Whether this mechanism speaks for the given controller at all.
     * <p>
     * Only the application's own controllers are scanned, so only they can be enforced; asking the
     * registry about anything else can produce one answer only - {@code Unknown} - and Unknown is
     * a refusal. The pointcut in {@code PermissionEnforcementConfig} therefore asks this first, so
     * that "not scanned" and "not enforced" are the same set by construction rather than by two
     * copies of one string.
     *
     * @param beanType the concrete controller class
     * @return whether the scan covers it
     */
    public static boolean isOwnEndpoint(Class<?> beanType) {
        return beanType.getName().startsWith(OWN_PACKAGE);
    }

    /**
     * Convenience for callers that hold the MVC handler rather than an AOP invocation.
     */
    public EndpointRequirement requirementOf(HandlerMethod handlerMethod) {
        return requirementOf(handlerMethod.getBeanType(), specificMethod(handlerMethod));
    }

    /**
     * How many endpoints the scan can resolve a permission for. Equals the number of guarded
     * endpoints; anything less means distinct endpoints are sharing a key.
     */
    public int getResolvableEndpointCount() {
        return (int) requirementByHandler.values().stream()
                .filter(EndpointRequirement.Permission.class::isInstance).count();
    }

    /**
     * The identity of an endpoint for permission lookup: which controller, and which method on it.
     */
    private record HandlerKey(Class<?> beanType, Method method) {
    }

    public PermissionAuditReport getReport() {
        return report;
    }

    public PermissionMode getMode() {
        return properties.getMode();
    }

    private List<EndpointPermission> scanEndpoints() {
        Map<HandlerKey, EndpointRequirement> requirements = new HashMap<>();
        List<EndpointPermission> endpoints = new ArrayList<>();
        RequestMappingHandlerMapping handlerMapping = handlerMappingProvider.getObject();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (!isOwnEndpoint(handlerMethod.getBeanType())) {
                continue; // springdoc and friends are not ours to gate
            }
            endpoints.add(describe(entry.getKey(), handlerMethod, requirements));
        }
        endpoints.sort(Comparator.comparing(EndpointPermission::pattern)
                .thenComparing(EndpointPermission::httpMethod));
        this.requirementByHandler = Map.copyOf(requirements);
        this.scanned = true;
        return endpoints;
    }

    private EndpointPermission describe(RequestMappingInfo info, HandlerMethod handlerMethod,
                                        Map<HandlerKey, EndpointRequirement> requirements) {
        Method method = specificMethod(handlerMethod);
        Class<?> controller = handlerMethod.getBeanType();
        String handler = controller.getSimpleName() + '#' + method.getName();
        String httpMethod = httpMethodsOf(info);
        String pattern = patternOf(info);

        AuthenticatedOnly exempt = findAnnotation(method, controller, AuthenticatedOnly.class);
        if (exempt != null) {
            requirements.put(new HandlerKey(controller, method), EndpointRequirement.Exempt.INSTANCE);
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
        requirements.put(new HandlerKey(controller, method), new EndpointRequirement.Permission(code));
        return new EndpointPermission(httpMethod, pattern, handler, code,
                EndpointPermission.Status.GUARDED, proxyabilityProblem(controller, method));
    }

    /**
     * Why Spring AOP could not advise this handler, or {@code null} if it can.
     * <p>
     * Enforcement is an {@code AuthorizationManager} published as an advisor, so it reaches a
     * handler only through a proxy. A {@code final} class or a {@code final} or non-public method
     * makes the advice silently inapplicable: the annotation reads as protection and there is none.
     * The registry exists to make that class of silence impossible, so it checks.
     */
    private static String proxyabilityProblem(Class<?> controller, Method method) {
        if (Modifier.isFinal(controller.getModifiers())) {
            return "controller class is final - Spring AOP cannot advise it, so the permission is never checked";
        }
        if (Modifier.isFinal(method.getModifiers())) {
            return "method is final - Spring AOP cannot advise it, so the permission is never checked";
        }
        if (!Modifier.isPublic(method.getModifiers())) {
            return "method is not public - Spring AOP cannot advise it, so the permission is never checked";
        }
        return null;
    }

    /**
     * The method as the concrete controller sees it. A subclass may override {@code getAll()}
     * without repeating the annotation, so the lookup falls back to the super-method - and the
     * resource still comes from the concrete class, which is the whole point. This also resolves
     * bridge methods, which the subclasses of {@code AbstractCrudController} do have.
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
        List<EndpointPermission> unproxyable = endpoints.stream()
                .filter(e -> e.status() == EndpointPermission.Status.GUARDED && e.note() != null)
                .toList();

        return new PermissionAuditReport(endpoints, requiredCodes, missing, orphans, unannotated, unproxyable);
    }

    private void logReport(PermissionAuditReport report) {
        long guarded = report.endpoints().stream()
                .filter(e -> e.status() == EndpointPermission.Status.GUARDED).count();
        long exempt = report.endpoints().stream()
                .filter(e -> e.status() == EndpointPermission.Status.AUTHENTICATED_ONLY).count();
        log.info("Permission audit [mode={}, enforced domains={}]: {} endpoints - {} guarded,"
                        + " {} authenticated-only, {} unannotated, {} unproxyable;"
                        + " {} distinct permissions required, {} missing in database, {} orphaned rows",
                properties.getMode(), properties.getEnforceDomains(), report.endpoints().size(),
                guarded, exempt, report.unannotatedEndpoints().size(), report.unproxyableEndpoints().size(),
                report.requiredCodes().size(), report.missingInDatabase().size(),
                report.orphanPermissions().size());

        report.missingInDatabase().forEach(code ->
                log.error("Permission '{}' is required by code but missing from the database:"
                        + " the operation cannot be granted to anyone", code));
        report.unannotatedEndpoints().forEach(endpoint ->
                log.error("Endpoint {} declares neither @RequiresPermission nor @AuthenticatedOnly", endpoint));
        report.unproxyableEndpoints().forEach(endpoint ->
                log.error("Endpoint {} declares permission '{}' that can never be enforced: {}",
                        endpoint, endpoint.permissionCode(), endpoint.note()));
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
