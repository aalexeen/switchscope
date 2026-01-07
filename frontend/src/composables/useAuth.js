// @/composables/useAuth.js
import { ref, computed } from "vue";
import authService from "@/services/auth"; // Your existing auth.js

// Global reactive state - shared across all components
const isLoggedIn = ref(authService.isAuthenticated());
const user = ref(authService.getUser());

export const useAuth = () => {
  // Login wrapper that updates reactive state
  const login = async (credentials) => {
    try {
      const result = await authService.login(credentials);

      // Update reactive state after successful login
      isLoggedIn.value = true;
      user.value = authService.getUser();

      return result;
    } catch (error) {
      // Login failed, ensure state is cleared
      isLoggedIn.value = false;
      user.value = null;
      throw error;
    }
  };

  // Register wrapper that calls the registration API
  const register = async (userData) => {
    try {
      // Use authService register method (which calls the API internally)
      const result = await authService.register(userData);

      // Note: We don't automatically log the user in after registration
      // They need to log in separately after registration

      return result;
    } catch (error) {
      // Registration failed
      throw error;
    }
  };

  // Logout wrapper that updates reactive state
  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.warn("Server logout failed:", error);
    } finally {
      // Always update reactive state
      isLoggedIn.value = false;
      user.value = null;
    }
  };

  // Check auth wrapper that updates reactive state
  const checkAuth = async () => {
    try {
      const status = await authService.checkAuth();

      // Update reactive state based on server response
      isLoggedIn.value = status.isAuthenticated;
      user.value = status.user;

      return status;
    } catch (error) {
      // Auth check failed, clear state
      isLoggedIn.value = false;
      user.value = null;
      return { isAuthenticated: false, user: null };
    }
  };

  // Initialize auth wrapper
  const initializeAuth = async () => {
    try {
      const result = await authService.initializeAuth();

      // Update reactive state
      isLoggedIn.value = result;
      user.value = result ? authService.getUser() : null;

      return result;
    } catch (error) {
      isLoggedIn.value = false;
      user.value = null;
      return false;
    }
  };

  // Get profile wrapper
  const getProfile = async () => {
    try {
      const profile = await authService.getProfile();

      // Update user data
      user.value = profile;

      return profile;
    } catch (error) {
      console.error("Failed to get profile:", error);
      throw error;
    }
  };

  // Verify auth wrapper
  const verifyAuth = async () => {
    try {
      const result = await authService.verifyAuth();

      // Update state based on verification
      if (!result) {
        isLoggedIn.value = false;
        user.value = null;
      }

      return result;
    } catch (error) {
      isLoggedIn.value = false;
      user.value = null;
      return false;
    }
  };

  // Utility function to refresh state from localStorage
  const refreshState = () => {
    isLoggedIn.value = authService.isAuthenticated();
    user.value = authService.getUser();
  };

  // Role-based utility functions
  const hasRole = (role) => {
    return authService.hasRole(role);
  };

  const hasAnyRole = (roleArray) => {
    return authService.hasAnyRole(roleArray);
  };

  const hasAllRoles = (roleArray) => {
    return authService.hasAllRoles(roleArray);
  };

  const isAdmin = () => {
    return authService.isAdmin();
  };

  const isUser = () => {
    return authService.isUser();
  };

  return {
    // Reactive computed properties
    isLoggedIn: computed(() => isLoggedIn.value),
    user: computed(() => user.value),
    username: computed(() => user.value?.name || user.value?.email || "User"),
    userRoles: computed(() => user.value?.roles || []),
    isAdmin: computed(() => authService.isAdmin()),
    isUser: computed(() => authService.isUser()),

    // Methods that wrap your existing authService
    login,
    register,
    logout,
    checkAuth,
    initializeAuth,
    getProfile,
    verifyAuth,
    refreshState,

    // Role-based utility functions
    hasRole,
    hasAnyRole,
    hasAllRoles,
    
    // Direct access to your original service for any edge cases
    authService,
  };
};