// @/composables/usePermissions.js
import { computed } from 'vue';
import { useAuth } from '@/composables/useAuth';

/**
 * What this session may do, as the server said it.
 *
 * The list arrives with the identity - `/api/auth/login` and `/api/auth/check` answer with
 * `permissions` alongside `roles` - and lives in the same reactive `user` the rest of the app
 * reads, so a re-check updates every gate at once.
 *
 * **This is a convenience, not a boundary.** Hiding a button the caller has no permission for
 * saves them a 403; showing one they should not see costs nothing but their time, because the
 * server decides again on every request and does not consult this at all.
 *
 * That is why an unknown list opens rather than closes - the opposite of the backend's rule for a
 * missing annotation, and for the same reason. There, "we do not know" had to mean "no", because
 * nothing else was going to refuse. Here it means "show it", because something else already will:
 * a client that stored its identity before this feature existed has no `permissions` key at all,
 * and a blank application with no buttons is a worse answer than the 403 it was already getting.
 * The distinction is between the field being absent and the list being empty - absent is unknown,
 * empty is a user who genuinely holds nothing, which closes everything.
 */
export const usePermissions = () => {
  const { user } = useAuth();

  const granted = computed(() => {
    const permissions = user.value?.permissions;
    return Array.isArray(permissions) ? new Set(permissions) : null;
  });

  /** False while the identity predates permissions, or while nobody is signed in. */
  const isKnown = computed(() => granted.value !== null);

  /**
   * @param {string} code - a permission code, built from `configs/permissions.js`
   * @returns {boolean} whether to show what the code guards
   */
  const can = (code) => (granted.value === null ? true : granted.value.has(code));

  const canAny = (codes) => codes.some((code) => can(code));

  const canAll = (codes) => codes.every((code) => can(code));

  return {
    permissions: computed(() => (granted.value === null ? [] : [...granted.value])),
    isKnown,
    can,
    canAny,
    canAll
  };
};
