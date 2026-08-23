package net.switchscope.security.permission;

/**
 * How far along the audit-to-enforcement path the application is running.
 * <p>
 * Two modes are not enough. {@link #AUDIT} is a startup check: it verifies that every operation
 * <em>can</em> be granted, but it cannot see who actually calls what, because no request has
 * happened yet. Going straight from there to {@link #ENFORCE} makes production traffic the first
 * test of the grant table - the failure mode every authorization-migration guide warns about.
 * {@link #SHADOW} is the missing step: the real decision, on every real request, recorded and not
 * acted on.
 */
public enum PermissionMode {

    /** Startup report only. Requests are not evaluated at all. */
    AUDIT,

    /**
     * Evaluate every request, log what would be refused, refuse nothing. The safe way to find out
     * what the grant table gets wrong.
     */
    SHADOW,

    /** Refuse. Also fails startup on an unconfigurable, unannotated or unproxyable operation. */
    ENFORCE
}
