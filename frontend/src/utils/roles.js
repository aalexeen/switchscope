// @/utils/roles.js

// Role constants
export const ROLES = {
  ADMIN: 'ADMIN',
  USER: 'USER',
  MODERATOR: 'MODERATOR', // In case you add more roles later
  SUPER_ADMIN: 'SUPER_ADMIN' // In case you add more roles later
};

// Role hierarchies - higher roles inherit permissions from lower roles
export const ROLE_HIERARCHY = {
  [ROLES.SUPER_ADMIN]: [ROLES.ADMIN, ROLES.MODERATOR, ROLES.USER],
  [ROLES.ADMIN]: [ROLES.MODERATOR, ROLES.USER],
  [ROLES.MODERATOR]: [ROLES.USER],
  [ROLES.USER]: []
};

// Permission definitions
export const PERMISSIONS = {
  // User management permissions
  VIEW_ALL_USERS: 'view_all_users',
  CREATE_USER: 'create_user',
  EDIT_USER: 'edit_user',
  DELETE_USER: 'delete_user',
  
  // MAC address permissions
  VIEW_MAC_ADDRESSES: 'view_mac_addresses',
  CREATE_MAC_ADDRESS: 'create_mac_address',
  DELETE_MAC_ADDRESS: 'delete_mac_address',
  
  // Profile permissions
  VIEW_OWN_PROFILE: 'view_own_profile',
  EDIT_OWN_PROFILE: 'edit_own_profile',
  
  // System permissions
  SYSTEM_ADMIN: 'system_admin'
};

// Role-permission mapping
export const ROLE_PERMISSIONS = {
  [ROLES.USER]: [
    PERMISSIONS.VIEW_MAC_ADDRESSES,
    PERMISSIONS.CREATE_MAC_ADDRESS,
    PERMISSIONS.DELETE_MAC_ADDRESS,
    PERMISSIONS.VIEW_OWN_PROFILE,
    PERMISSIONS.EDIT_OWN_PROFILE
  ],
  [ROLES.MODERATOR]: [
    ...ROLE_PERMISSIONS[ROLES.USER],
    PERMISSIONS.VIEW_ALL_USERS
  ],
  [ROLES.ADMIN]: [
    ...ROLE_PERMISSIONS[ROLES.USER],
    PERMISSIONS.VIEW_ALL_USERS,
    PERMISSIONS.CREATE_USER,
    PERMISSIONS.EDIT_USER,
    PERMISSIONS.DELETE_USER,
    PERMISSIONS.SYSTEM_ADMIN
  ],
  [ROLES.SUPER_ADMIN]: [
    ...ROLE_PERMISSIONS[ROLES.ADMIN]
  ]
};

/**
 * Check if a user has a specific permission based on their roles
 * @param {string[]} userRoles - Array of user roles
 * @param {string} permission - Permission to check
 * @returns {boolean}
 */
export function hasPermission(userRoles, permission) {
  if (!userRoles || !Array.isArray(userRoles)) {
    return false;
  }

  return userRoles.some(role => {
    const rolePermissions = ROLE_PERMISSIONS[role] || [];
    return rolePermissions.includes(permission);
  });
}

/**
 * Check if a user has any of the specified permissions
 * @param {string[]} userRoles - Array of user roles
 * @param {string[]} permissions - Array of permissions to check
 * @returns {boolean}
 */
export function hasAnyPermission(userRoles, permissions) {
  return permissions.some(permission => hasPermission(userRoles, permission));
}

/**
 * Check if a user has all of the specified permissions
 * @param {string[]} userRoles - Array of user roles
 * @param {string[]} permissions - Array of permissions to check
 * @returns {boolean}
 */
export function hasAllPermissions(userRoles, permissions) {
  return permissions.every(permission => hasPermission(userRoles, permission));
}

/**
 * Get all permissions for a user based on their roles
 * @param {string[]} userRoles - Array of user roles
 * @returns {string[]} Array of permissions
 */
export function getUserPermissions(userRoles) {
  if (!userRoles || !Array.isArray(userRoles)) {
    return [];
  }

  const permissions = new Set();
  
  userRoles.forEach(role => {
    const rolePermissions = ROLE_PERMISSIONS[role] || [];
    rolePermissions.forEach(permission => permissions.add(permission));
  });

  return Array.from(permissions);
}

/**
 * Check if a role has access to another role (hierarchy check)
 * @param {string} userRole - User's role
 * @param {string} targetRole - Target role to check access for
 * @returns {boolean}
 */
export function canAccessRole(userRole, targetRole) {
  if (userRole === targetRole) {
    return true;
  }

  const hierarchy = ROLE_HIERARCHY[userRole] || [];
  return hierarchy.includes(targetRole);
}

/**
 * Get role display name for UI
 * @param {string} role - Role constant
 * @returns {string} Display name
 */
export function getRoleDisplayName(role) {
  const displayNames = {
    [ROLES.SUPER_ADMIN]: 'Super Administrator',
    [ROLES.ADMIN]: 'Administrator',
    [ROLES.MODERATOR]: 'Moderator',
    [ROLES.USER]: 'User'
  };

  return displayNames[role] || role;
}

/**
 * Get role color class for UI styling
 * @param {string} role - Role constant
 * @returns {string} CSS class string
 */
export function getRoleColorClass(role) {
  const colorClasses = {
    [ROLES.SUPER_ADMIN]: 'bg-purple-100 text-purple-800',
    [ROLES.ADMIN]: 'bg-red-100 text-red-800',
    [ROLES.MODERATOR]: 'bg-yellow-100 text-yellow-800',
    [ROLES.USER]: 'bg-blue-100 text-blue-800'
  };

  return colorClasses[role] || 'bg-gray-100 text-gray-800';
}