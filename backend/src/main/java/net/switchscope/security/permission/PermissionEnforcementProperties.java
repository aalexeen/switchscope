package net.switchscope.security.permission;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Where the enforcement switch lives: {@code switchscope.security.permission}.
 * <p>
 * Two knobs rather than one. {@link #getMode()} is how far the whole application has moved along
 * the audit-to-enforcement path; {@link #getEnforceDomains()} promotes named domains ahead of it,
 * so {@code catalog} can be refused for real while everything else is still only observed. The
 * same property is the rollback: take the domain out of the list and it drops back to shadow, with
 * no rebuild.
 */
@ConfigurationProperties(prefix = "switchscope.security.permission")
public class PermissionEnforcementProperties {

    private PermissionMode mode = PermissionMode.AUDIT;

    private Set<String> enforceDomains = new LinkedHashSet<>();

    public PermissionMode getMode() {
        return mode;
    }

    public void setMode(PermissionMode mode) {
        this.mode = mode;
    }

    public Set<String> getEnforceDomains() {
        return enforceDomains;
    }

    public void setEnforceDomains(Set<String> enforceDomains) {
        this.enforceDomains = enforceDomains == null ? new LinkedHashSet<>() : new LinkedHashSet<>(enforceDomains);
    }

    /**
     * The mode that applies to one permission code: a domain named in {@link #getEnforceDomains()}
     * is enforced whatever the global mode says, so a domain can be switched on without moving the
     * rest of the application.
     *
     * @param code the permission code the endpoint requires
     * @return the mode to apply to it
     */
    public PermissionMode modeFor(String code) {
        return enforceDomains.contains(PermissionCode.parse(code).domain()) ? PermissionMode.ENFORCE : mode;
    }

    /**
     * Whether this particular code is refused for real, globally or by promotion.
     */
    public boolean isEnforced(String code) {
        return code != null && modeFor(code) == PermissionMode.ENFORCE;
    }

    /**
     * Whether anything is refused for real. An endpoint with no permission code has no domain to be
     * promoted by, so it follows the global mode alone.
     */
    public boolean isEnforcingAnything() {
        return mode == PermissionMode.ENFORCE || !enforceDomains.isEmpty();
    }
}
