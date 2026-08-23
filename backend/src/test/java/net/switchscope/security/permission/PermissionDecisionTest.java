package net.switchscope.security.permission;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Every branch of the decision, including the ones the running application cannot reach.
 *
 * <h2>Why the unreachable ones matter most</h2>
 * An endpoint with no annotation is refused at request time - but under {@code ENFORCE} the
 * registry already refuses to start, so that path never executes in this application. The end-to-end
 * tests therefore cannot cover it, and "we are safe because startup would have failed" is a claim
 * about a guard nobody checked either. Both are checked here, in isolation and in milliseconds.
 * The project's whole argument is that an unverifiable claim is how the last gap survived; a
 * fail-closed branch nothing exercises is the same shape.
 */
class PermissionDecisionTest {

    /** A stand-in for any advised handler; only its identity matters to the manager. */
    static class SomeController {
        public void handle() {
        }
    }

    private final PermissionRegistry registry = mock(PermissionRegistry.class);

    private static PermissionEnforcementProperties properties(PermissionMode mode, String... domains) {
        var properties = new PermissionEnforcementProperties();
        properties.setMode(mode);
        properties.setEnforceDomains(Set.of(domains));
        return properties;
    }

    private static Supplier<Authentication> caller(String... authorities) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "someone", "n/a", AuthorityUtils.createAuthorityList(authorities));
        return () -> authentication;
    }

    private AuthorizationDecision decide(PermissionEnforcementProperties properties,
                                         EndpointRequirement requirement,
                                         Supplier<Authentication> caller) throws Exception {
        given(registry.requirementOf(any(), any())).willReturn(requirement);
        Method method = SomeController.class.getMethod("handle");
        MethodInvocation invocation = mock(MethodInvocation.class);
        given(invocation.getThis()).willReturn(new SomeController());
        given(invocation.getMethod()).willReturn(method);
        return new RequiresPermissionAuthorizationManager(registry, properties)
                .check(caller, invocation);
    }

    @Test
    @DisplayName("holding the permission is granted")
    void permissionHeld() throws Exception {
        AuthorizationDecision decision = decide(properties(PermissionMode.ENFORCE),
                new EndpointRequirement.Permission("catalog.component-type:read"),
                caller("catalog.component-type:read"));
        assertThat(decision).isNotNull();
        assertThat(decision.isGranted()).isTrue();
    }

    @Test
    @DisplayName("a role is not a permission")
    void rolesAreNotPermissions() throws Exception {
        AuthorizationDecision decision = decide(properties(PermissionMode.ENFORCE),
                new EndpointRequirement.Permission("catalog.component-type:read"),
                caller("ROLE_ADMIN"));
        assertThat(decision.isGranted())
                .as("hasRole and hasAuthority must not be mixed on the same route")
                .isFalse();
    }

    @Test
    @DisplayName("shadow records the refusal and refuses nothing")
    void shadowDoesNotRefuse() throws Exception {
        assertThat(decide(properties(PermissionMode.SHADOW),
                new EndpointRequirement.Permission("catalog.component-type:read"),
                caller("something.else:read")))
                .as("abstaining, so the invocation proceeds")
                .isNull();
    }

    @Test
    @DisplayName("a promoted domain refuses while the global mode is still shadow")
    void promotedDomainRefuses() throws Exception {
        AuthorizationDecision decision = decide(properties(PermissionMode.SHADOW, "catalog"),
                new EndpointRequirement.Permission("catalog.component-type:read"),
                caller("something.else:read"));
        assertThat(decision.isGranted()).isFalse();
    }

    @Test
    @DisplayName("promoting one domain leaves the others alone")
    void unpromotedDomainStillShadows() throws Exception {
        assertThat(decide(properties(PermissionMode.SHADOW, "catalog"),
                new EndpointRequirement.Permission("component.rack:read"),
                caller("something.else:read")))
                .isNull();
    }

    @Test
    @DisplayName("an explicitly exempt endpoint is left alone - login must never close")
    void exemptAbstains() throws Exception {
        assertThat(decide(properties(PermissionMode.ENFORCE), EndpointRequirement.Exempt.INSTANCE,
                caller()))
                .as("/api/auth/** answers who you are and what you may do, so it cannot itself"
                        + " require a permission; if this ever refused, nobody could log in")
                .isNull();
    }

    @Test
    @DisplayName("a forgotten annotation closes, it does not open")
    void unknownRefusesUnderEnforce() throws Exception {
        for (EndpointRequirement.Unknown unknown : EndpointRequirement.Unknown.values()) {
            AuthorizationDecision decision = decide(properties(PermissionMode.ENFORCE), unknown,
                    caller("catalog.component-type:read", "ROLE_ADMIN"));
            assertThat(decision).as("%s", unknown).isNotNull();
            assertThat(decision.isGranted())
                    .as("%s must refuse whoever asks - treating 'no requirement found' as 'no"
                            + " requirement needed' is how the previous gap stayed open", unknown)
                    .isFalse();
        }
    }

    @Test
    @DisplayName("audit mode says nothing about requests at all")
    void auditModeAbstains() throws Exception {
        assertThat(decide(properties(PermissionMode.AUDIT), EndpointRequirement.Unknown.NO_ANNOTATION,
                caller()))
                .isNull();
    }

    // --- the startup guard -------------------------------------------------------------------

    private static PermissionAuditReport report(SortedSet<String> missing,
                                                List<EndpointPermission> unannotated) {
        return new PermissionAuditReport(List.of(), sorted(), missing, sorted(), unannotated, List.of());
    }

    private static SortedSet<String> sorted(String... codes) {
        return new TreeSet<>(List.of(codes));
    }

    private static EndpointPermission unannotatedEndpoint() {
        return new EndpointPermission("GET", "/api/whatever", "Some#thing", null,
                EndpointPermission.Status.UNANNOTATED, null);
    }

    @Test
    @DisplayName("a clean report never stops startup")
    void cleanReportStarts() {
        assertThat(PermissionRegistry.shouldFailStartup(
                report(sorted(), List.of()), properties(PermissionMode.ENFORCE))).isFalse();
    }

    @Test
    @DisplayName("shadow looks at findings rather than dying of them")
    void shadowToleratesFindings() {
        assertThat(PermissionRegistry.shouldFailStartup(
                report(sorted("catalog.component-type:read"), List.of(unannotatedEndpoint())),
                properties(PermissionMode.SHADOW)))
                .as("having something to look at is the point of shadow")
                .isFalse();
    }

    @Test
    @DisplayName("an ungrantable permission inside a promoted domain stops startup")
    void missingCodeInPromotedDomainStopsStartup() {
        assertThat(PermissionRegistry.shouldFailStartup(
                report(sorted("catalog.component-type:read"), List.of()),
                properties(PermissionMode.SHADOW, "catalog")))
                .as("otherwise the domain refuses everyone for real behind a clean startup log")
                .isTrue();
    }

    @Test
    @DisplayName("the same finding elsewhere does not stop startup")
    void missingCodeOutsidePromotedDomainStarts() {
        assertThat(PermissionRegistry.shouldFailStartup(
                report(sorted("component.rack:read"), List.of()),
                properties(PermissionMode.SHADOW, "catalog"))).isFalse();
    }

    @Test
    @DisplayName("an unannotated endpoint stops startup under full enforcement")
    void unannotatedStopsStartupUnderEnforce() {
        assertThat(PermissionRegistry.shouldFailStartup(
                report(sorted(), List.of(unannotatedEndpoint())),
                properties(PermissionMode.ENFORCE)))
                .as("it has no domain to be promoted by, so only the global mode can catch it")
                .isTrue();
    }

    @Test
    @DisplayName("no findings, no domains, nothing to decide")
    void nothingEnforcedNeverStopsStartup() {
        assertThat(PermissionRegistry.shouldFailStartup(
                report(sorted("catalog.component-type:read"), List.of(unannotatedEndpoint())),
                properties(PermissionMode.AUDIT))).isFalse();
    }

    @Test
    @DisplayName("an empty authority set is not a wildcard")
    void noAuthoritiesIsNotAccess() throws Exception {
        Authentication anonymous = new UsernamePasswordAuthenticationToken(
                "nobody", "n/a", Collections.emptyList());
        AuthorizationDecision decision = decide(properties(PermissionMode.ENFORCE),
                new EndpointRequirement.Permission("catalog.component-type:read"), () -> anonymous);
        assertThat(decision.isGranted()).isFalse();
    }
}
