package net.switchscope.security.permission;

/**
 * What an endpoint requires, as a value that cannot be misread.
 * <p>
 * The scan used to answer this with a {@code String} that was {@code null} in three unrelated
 * cases: the endpoint is deliberately exempt, the annotation was forgotten, and the scan has not
 * run yet. Enforcement must treat those three differently - the first opens, the other two close -
 * and a {@code null} cannot carry that distinction. A tolerated ambiguity in exactly this place is
 * how the previous authorization gap survived, so the distinction is made by the type rather than
 * by whoever remembers to check.
 */
public sealed interface EndpointRequirement {

    /**
     * The endpoint is configurable: the caller needs this permission code as an authority.
     *
     * @param code the {@code <domain>.<resource>:<action>} code
     */
    record Permission(String code) implements EndpointRequirement {
    }

    /**
     * The endpoint declares {@link AuthenticatedOnly} - outside the permission model on purpose.
     * Enforcement abstains; the filter chain has already required authentication.
     */
    enum Exempt implements EndpointRequirement {
        INSTANCE
    }

    /**
     * Nothing is known about the endpoint. Both cases must close, never open.
     */
    enum Unknown implements EndpointRequirement {
        /** The endpoint declares neither annotation - the hole the registry exists to find. */
        NO_ANNOTATION,
        /**
         * The scan has not run. Should be unreachable now that scanning happens before the web
         * server accepts connections, but the enforcement path must not depend on that being true.
         */
        NOT_SCANNED
    }
}
