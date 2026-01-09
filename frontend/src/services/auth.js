import api from '@/api/index'; // Direct import of the API instance
import axios from 'axios';

const authService = {
  // Check if user is authenticated (local storage check)
  isAuthenticated() {
    return !!localStorage.getItem("isAuthenticated");
  },

  // Get stored user data
  getUser() {
    const userData = localStorage.getItem("user");
    return userData ? JSON.parse(userData) : null;
  },

  // Get user roles
  getUserRoles() {
    const user = this.getUser();
    return user?.roles || [];
  },

  // Check if user has a specific role
  hasRole(role) {
    const roles = this.getUserRoles();
    return roles.includes(role);
  },

  // Check if user has any of the specified roles
  hasAnyRole(roleArray) {
    const userRoles = this.getUserRoles();
    return roleArray.some(role => userRoles.includes(role));
  },

  // Check if user has all of the specified roles
  hasAllRoles(roleArray) {
    const userRoles = this.getUserRoles();
    return roleArray.every(role => userRoles.includes(role));
  },

  // Check if user is admin
  isAdmin() {
    return this.hasRole('ADMIN');
  },

  // Check if user is regular user
  isUser() {
    return this.hasRole('USER');
  },

  // Get stored credentials for Basic Auth
  getCredentials() {
    const email = localStorage.getItem("email");
    const password = localStorage.getItem("password");
    return email && password ? { email, password } : null;
  },

  // Set Basic Auth header
  setBasicAuthHeader(email, password) {
    const credentials = btoa(`${email}:${password}`);
    axios.defaults.headers.common["Authorization"] = `Basic ${credentials}`;
  },

  // Remove Basic Auth header
  removeAuthHeader() {
    delete axios.defaults.headers.common["Authorization"];
  },

  // Register user
  async register(userData) {
    // Call the registration endpoint using API
    return await api.authentication.signUp(userData);
  },

  // Login user with Basic Auth
  async login(credentials) {
    try {
      // Create Basic Auth header for this specific request
      const authHeader = btoa(`${credentials.email}:${credentials.password}`);

      // Call the login endpoint using API with payload and custom headers
      const response = await api.authentication.logIn({}, {
        headers: {
          'Authorization': `Basic ${authHeader}`,
          'Content-Type': 'application/json'
        }
      });

      // Store authentication data using the response from LoginResponseTo
      localStorage.setItem("isAuthenticated", "true");
      localStorage.setItem("user", JSON.stringify(response.data));
      localStorage.setItem("email", credentials.email);
      localStorage.setItem("password", credentials.password);

      // Set default Basic Auth header for all future requests
      this.setBasicAuthHeader(credentials.email, credentials.password);

      return response;
    } catch (error) {
      // Clean up on failed login
      this.logout();
      throw error;
    }
  },

  // Check authentication with server
  async checkAuth() {
    try {
      const credentials = this.getCredentials();
      if (!credentials) {
        return { isAuthenticated: false, user: null };
      }

      // Set auth header
      this.setBasicAuthHeader(credentials.email, credentials.password);

      // Call the check endpoint
      const response = await api.authentication.checkAuth();
      
      // Update stored user data with fresh data from server (including roles)
      localStorage.setItem("user", JSON.stringify(response.data));
      localStorage.setItem("isAuthenticated", "true");

      return { 
        isAuthenticated: true, 
        user: response.data 
      };
    } catch {
      // Server says user is not authenticated
      this.logout();
      return { 
        isAuthenticated: false, 
        user: null 
      };
    }
  },

  // Get user profile from server
  async getProfile() {
    try {
      const credentials = this.getCredentials();
      if (!credentials) {
        throw new Error('No credentials available');
      }

      // Set auth header
      this.setBasicAuthHeader(credentials.email, credentials.password);

      // Call the profile endpoint
      const response = await api.authentication.getProfile();
      
      // Update stored user data (including roles)
      localStorage.setItem("user", JSON.stringify(response.data));

      return response.data;
    } catch (error) {
      console.error('Failed to get profile:', error);
      throw error;
    }
  },

  // Initialize auth on app start
  async initializeAuth() {
    const credentials = this.getCredentials();
    if (credentials && this.isAuthenticated()) {
      try {
        // Verify credentials with server
        const authStatus = await this.checkAuth();
        if (!authStatus.isAuthenticated) {
          // Server rejected credentials, clear local storage
          this.logout();
          return false;
        }
        return true;
      } catch {
        // Network error or server unavailable, keep local state but don't set headers
        console.warn('Could not verify authentication with server');
        return this.isAuthenticated();
      }
    }
    return false;
  },

  // Logout user
  async logout() {
    try {
      const credentials = this.getCredentials();
      if (credentials) {
        // Set auth header for logout request
        this.setBasicAuthHeader(credentials.email, credentials.password);
        
        // Call server logout endpoint
        await api.authentication.logOut();
      }
    } catch {
      // Logout can fail but we still want to clear local data
      console.warn('Server logout failed');
    } finally {
      // Always clear local storage and headers
      localStorage.removeItem("isAuthenticated");
      localStorage.removeItem("user");
      localStorage.removeItem("email");
      localStorage.removeItem("password");

      // Remove Authorization header
      this.removeAuthHeader();
    }
  },

  // Check if credentials are valid (basic validation)
  validateCredentials(email, password) {
    return email && password && email.trim() !== "" && password.trim() !== "";
  },

  // Get auth header for manual requests
  getAuthHeader() {
    const credentials = this.getCredentials();
    if (credentials) {
      return `Basic ${btoa(`${credentials.email}:${credentials.password}`)}`;
    }
    return null;
  },

  // Verify current authentication status with server
  async verifyAuth() {
    if (!this.isAuthenticated()) {
      return false;
    }

    try {
      const authStatus = await this.checkAuth();
      return authStatus.isAuthenticated;
    } catch {
      return false;
    }
  },

  // Get fresh user data from server
  async refreshUserData() {
    try {
      const profile = await this.getProfile();
      return profile;
    } catch (error) {
      console.error('Failed to refresh user data:', error);
      return this.getUser(); // Return cached data as fallback
    }
  }
};

export default authService;