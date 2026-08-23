package net.switchscope.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.model.security.PermissionEntity;
import net.switchscope.model.security.RoleEntity;
import net.switchscope.repository.security.RoleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The grant table, read once and kept for a minute: role code to the permission codes it carries.
 *
 * <h2>Why this and not a cache of user authorities</h2>
 * Authentication is stateless HTTP Basic, so the user is loaded on every single request. Assembling
 * authorities from {@code roles} joined to {@code role_permissions} would turn one lookup into
 * three, per request. The obvious fix is to cache the assembled authorities per user; this caches
 * the half that is actually shared instead, and it is the better half to hold:
 * <ul>
 *   <li>it is two roles by eighty-one permissions - one structure for the whole application, not
 *       one entry per user;</li>
 *   <li>it contains no user data and no password hash, unlike a cached {@code UserDetails}, which
 *       {@code CachingUserDetailsService} can only do with {@code eraseCredentials(false)};</li>
 *   <li>a user's own roles stay fresh: they are read on every request anyway, because
 *       {@code user_role} is already an eager {@code @ElementCollection}. So no extra table is read
 *       per request at all;</li>
 *   <li>it invalidates as one thing, which matters because it is exactly the table an administrator
 *       edits.</li>
 * </ul>
 *
 * <h2>The TTL is part of the requirement, not a tuning knob</h2>
 * The whole point of the model is that a row in {@code role_permissions} is the configuration - an
 * {@code UPDATE}, not a release. A cache without expiry would quietly retract that promise, so the
 * TTL is short (see {@code spring.cache.caffeine.spec}) and {@link #invalidate()} exists for
 * whatever eventually writes to these tables to call.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RolePermissionCatalog {

    public static final String CACHE = "rolePermissions";

    private final RoleRepository roleRepository;

    /**
     * Every active role's active permissions. Inactive rows are left out on purpose: deactivating a
     * permission is another way to change policy without a release, and it would be surprising if
     * it changed the admin UI but not the answer.
     *
     * @return role code to the set of permission codes granted to it
     */
    @Cacheable(CACHE)
    @Transactional(readOnly = true)
    public Map<String, Set<String>> byRole() {
        Map<String, Set<String>> grants = roleRepository.findAllWithPermissions().stream()
                .filter(RoleEntity::isActive)
                .collect(Collectors.toMap(RoleEntity::getCode,
                        role -> role.getPermissions().stream()
                                .filter(PermissionEntity::isActive)
                                .map(PermissionEntity::getCode)
                                .collect(Collectors.toUnmodifiableSet())));
        log.info("Loaded grants for {} role(s): {}", grants.size(),
                grants.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size())));
        return Map.copyOf(grants);
    }

    /**
     * Drops the cached grants. Nothing writes to {@code role_permissions} through the API yet, so
     * today the TTL is what makes an {@code UPDATE} take effect; this is the hook for the admin
     * endpoint that will.
     */
    @CacheEvict(value = CACHE, allEntries = true)
    public void invalidate() {
        log.info("Role grants invalidated");
    }
}
