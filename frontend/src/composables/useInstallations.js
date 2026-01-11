/**
 * Composable for Installations Management
 * Provides reactive state and methods for installations operations
 * Uses singleton pattern for shared state across components
 */

import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state (singleton pattern) - outside the composable function
const installations = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);
const lastFetchTime = ref(null);

// Cache duration: 5 minutes
const CACHE_DURATION = 5 * 60 * 1000;

/**
 * Installations Composable
 * @returns {Object} Installations state and methods
 */
export const useInstallations = () => {
  const toast = useToast();

  /**
   * Check if cached data is still valid
   */
  const isCacheValid = () => {
    if (!lastFetchTime.value) return false;
    return Date.now() - lastFetchTime.value < CACHE_DURATION;
  };

  /**
   * Fetch all installations from API
   * @param {boolean} forceRefresh - Force refresh even if cache is valid
   * @returns {Promise<Array>} Array of installations
   */
  const fetchInstallations = async (forceRefresh = false) => {
    // Return cached data if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && installations.value.length > 0) {
      return installations.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.installations.getAll();
      installations.value = response.data;
      lastFetchTime.value = Date.now();
      isInitialized.value = true;
      return installations.value;
    } catch (err) {
      error.value = err.message || 'Failed to fetch installations';
      console.error('Error fetching installations:', err);
      toast.error('Failed to load installations');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Get a single installation by ID
   * @param {string} id - Installation UUID
   * @returns {Promise<Object>} Installation object
   */
  const getInstallationById = async (id) => {
    // Try to find in cache first
    const cached = installations.value.find(i => i.id === id);
    if (cached && isCacheValid()) {
      return cached;
    }

    // Fetch from API if not in cache or cache invalid
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.installations.get(id);
      return response.data;
    } catch (err) {
      error.value = err.message || 'Failed to fetch installation';
      console.error('Error fetching installation:', err);
      toast.error('Failed to load installation details');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Create a new installation
   * @param {Object} payload - Installation data
   * @returns {Promise<Object>} Created installation
   */
  const createInstallation = async (payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.installations.create(payload);
      const newInstallation = response.data;
      installations.value.push(newInstallation);
      toast.success('Installation created successfully');
      return newInstallation;
    } catch (err) {
      error.value = err.message || 'Failed to create installation';
      console.error('Error creating installation:', err);
      toast.error('Failed to create installation');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Update an existing installation
   * @param {string} id - Installation UUID
   * @param {Object} payload - Updated installation data
   * @returns {Promise<Object>} Updated installation
   */
  const updateInstallation = async (id, payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.installations.update(id, payload);
      const updatedInstallation = response.data;

      // Update in cache
      const index = installations.value.findIndex(i => i.id === id);
      if (index !== -1) {
        installations.value[index] = updatedInstallation;
      }

      toast.success('Installation updated successfully');
      return updatedInstallation;
    } catch (err) {
      error.value = err.message || 'Failed to update installation';
      console.error('Error updating installation:', err);
      toast.error('Failed to update installation');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Delete an installation
   * @param {string} id - Installation UUID
   * @returns {Promise<void>}
   */
  const deleteInstallation = async (id) => {
    isLoading.value = true;
    error.value = null;

    try {
      await api.installations.delete(id);

      // Remove from cache
      const index = installations.value.findIndex(i => i.id === id);
      if (index !== -1) {
        installations.value.splice(index, 1);
      }

      toast.success('Installation deleted successfully');
    } catch (err) {
      error.value = err.message || 'Failed to delete installation';
      console.error('Error deleting installation:', err);
      toast.error('Failed to delete installation');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Clear cache and force fresh fetch on next request
   */
  const clearCache = () => {
    lastFetchTime.value = null;
    installations.value = [];
    isInitialized.value = false;
  };

  /**
   * Initialize composable (fetch data if not already loaded)
   * @returns {Promise<void>}
   */
  const initialize = async () => {
    if (!isInitialized.value || !isCacheValid()) {
      await fetchInstallations();
    }
  };

  /**
   * Search installations by query string
   * @param {string} query - Search query
   * @returns {Array} Filtered installations
   */
  const searchInstallations = (query) => {
    if (!query || query.trim() === '') {
      return installations.value;
    }

    const lowercaseQuery = query.toLowerCase().trim();

    return installations.value.filter(installation =>
      installation.componentName?.toLowerCase().includes(lowercaseQuery) ||
      installation.locationName?.toLowerCase().includes(lowercaseQuery) ||
      installation.installableTypeName?.toLowerCase().includes(lowercaseQuery) ||
      installation.installationStatusName?.toLowerCase().includes(lowercaseQuery) ||
      installation.installedBy?.toLowerCase().includes(lowercaseQuery) ||
      installation.notes?.toLowerCase().includes(lowercaseQuery)
    );
  };

  /**
   * Get paginated installations
   * @param {number} page - Page number (1-based)
   * @param {number} pageSize - Items per page
   * @returns {Object} Paginated result with data and metadata
   */
  const getPaginatedInstallations = (page = 1, pageSize = 10) => {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedData = installations.value.slice(startIndex, endIndex);

    return {
      data: paginatedData,
      currentPage: page,
      pageSize: pageSize,
      totalItems: installations.value.length,
      totalPages: Math.ceil(installations.value.length / pageSize)
    };
  };

  /**
   * Filter installations by component
   * @param {string} componentId - Component UUID
   * @returns {Array} Filtered installations
   */
  const filterByComponent = (componentId) => {
    if (!componentId) return installations.value;
    return installations.value.filter(i => i.componentId === componentId);
  };

  /**
   * Filter installations by location
   * @param {string} locationId - Location UUID
   * @returns {Array} Filtered installations
   */
  const filterByLocation = (locationId) => {
    if (!locationId) return installations.value;
    return installations.value.filter(i => i.locationId === locationId);
  };

  /**
   * Filter installations by installable type
   * @param {string} typeCode - Installable type code
   * @returns {Array} Filtered installations
   */
  const filterByInstallableType = (typeCode) => {
    if (!typeCode) return installations.value;
    return installations.value.filter(i => i.installableTypeCode === typeCode);
  };

  /**
   * Filter installations by status
   * @param {string} statusCode - Installation status code
   * @returns {Array} Filtered installations
   */
  const filterByStatus = (statusCode) => {
    if (!statusCode) return installations.value;
    return installations.value.filter(i => i.installationStatusCode === statusCode);
  };

  /**
   * Filter installations by date range
   * @param {Date} startDate - Start date
   * @param {Date} endDate - End date
   * @returns {Array} Filtered installations
   */
  const filterByDateRange = (startDate, endDate) => {
    return installations.value.filter(i => {
      const installDate = i.installDate ? new Date(i.installDate) : null;
      if (!installDate) return false;
      if (startDate && installDate < startDate) return false;
      if (endDate && installDate > endDate) return false;
      return true;
    });
  };

  // Computed properties
  const totalInstallations = computed(() => installations.value.length);
  const hasInstallations = computed(() => installations.value.length > 0);

  return {
    // State
    installations: computed(() => installations.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    isInitialized: computed(() => isInitialized.value),
    totalInstallations,
    hasInstallations,

    // Methods
    fetchInstallations,
    getInstallationById,
    createInstallation,
    updateInstallation,
    deleteInstallation,
    clearCache,
    initialize,
    searchInstallations,
    getPaginatedInstallations,
    filterByComponent,
    filterByLocation,
    filterByInstallableType,
    filterByStatus,
    filterByDateRange
  };
};
