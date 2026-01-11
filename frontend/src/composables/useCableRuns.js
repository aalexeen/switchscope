/**
 * Composable for Cable Runs Management
 * Provides reactive state and methods for cable runs operations
 * Uses singleton pattern for shared state across components
 */

import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state (singleton pattern) - outside the composable function
const cableRuns = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);
const lastFetchTime = ref(null);

// Cache duration: 5 minutes
const CACHE_DURATION = 5 * 60 * 1000;

/**
 * Cable Runs Composable
 * @returns {Object} Cable Runs state and methods
 */
export const useCableRuns = () => {
  const toast = useToast();

  /**
   * Check if cached data is still valid
   */
  const isCacheValid = () => {
    if (!lastFetchTime.value) return false;
    return Date.now() - lastFetchTime.value < CACHE_DURATION;
  };

  /**
   * Fetch all cable runs from API
   * @param {boolean} forceRefresh - Force refresh even if cache is valid
   * @returns {Promise<Array>} Array of cable runs
   */
  const fetchCableRuns = async (forceRefresh = false) => {
    // Return cached data if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && cableRuns.value.length > 0) {
      return cableRuns.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.cableRuns.getAll();
      cableRuns.value = response.data;
      lastFetchTime.value = Date.now();
      isInitialized.value = true;
      return cableRuns.value;
    } catch (err) {
      error.value = err.message || 'Failed to fetch cable runs';
      console.error('Error fetching cable runs:', err);
      toast.error('Failed to load cable runs');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Get a single cable run by ID
   * @param {string} id - Cable Run UUID
   * @returns {Promise<Object>} Cable Run object
   */
  const getCableRunById = async (id) => {
    // Try to find in cache first
    const cached = cableRuns.value.find(cr => cr.id === id);
    if (cached && isCacheValid()) {
      return cached;
    }

    // Fetch from API if not in cache or cache invalid
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.cableRuns.get(id);
      return response.data;
    } catch (err) {
      error.value = err.message || 'Failed to fetch cable run';
      console.error('Error fetching cable run:', err);
      toast.error('Failed to load cable run details');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Create a new cable run
   * @param {Object} payload - Cable Run data
   * @returns {Promise<Object>} Created cable run
   */
  const createCableRun = async (payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.cableRuns.create(payload);
      const newCableRun = response.data;
      cableRuns.value.push(newCableRun);
      toast.success('Cable run created successfully');
      return newCableRun;
    } catch (err) {
      error.value = err.message || 'Failed to create cable run';
      console.error('Error creating cable run:', err);
      toast.error('Failed to create cable run');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Update an existing cable run
   * @param {string} id - Cable Run UUID
   * @param {Object} payload - Updated cable run data
   * @returns {Promise<Object>} Updated cable run
   */
  const updateCableRun = async (id, payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.cableRuns.update(id, payload);
      const updatedCableRun = response.data;

      // Update in cache
      const index = cableRuns.value.findIndex(cr => cr.id === id);
      if (index !== -1) {
        cableRuns.value[index] = updatedCableRun;
      }

      toast.success('Cable run updated successfully');
      return updatedCableRun;
    } catch (err) {
      error.value = err.message || 'Failed to update cable run';
      console.error('Error updating cable run:', err);
      toast.error('Failed to update cable run');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Delete a cable run
   * @param {string} id - Cable Run UUID
   * @returns {Promise<void>}
   */
  const deleteCableRun = async (id) => {
    isLoading.value = true;
    error.value = null;

    try {
      await api.cableRuns.delete(id);

      // Remove from cache
      const index = cableRuns.value.findIndex(cr => cr.id === id);
      if (index !== -1) {
        cableRuns.value.splice(index, 1);
      }

      toast.success('Cable run deleted successfully');
    } catch (err) {
      error.value = err.message || 'Failed to delete cable run';
      console.error('Error deleting cable run:', err);
      toast.error('Failed to delete cable run');
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
    cableRuns.value = [];
    isInitialized.value = false;
  };

  /**
   * Initialize composable (fetch data if not already loaded)
   * @returns {Promise<void>}
   */
  const initialize = async () => {
    if (!isInitialized.value || !isCacheValid()) {
      await fetchCableRuns();
    }
  };

  /**
   * Search cable runs by query string
   * @param {string} query - Search query
   * @returns {Array} Filtered cable runs
   */
  const searchCableRuns = (query) => {
    if (!query || query.trim() === '') {
      return cableRuns.value;
    }

    const lowercaseQuery = query.toLowerCase().trim();

    return cableRuns.value.filter(cableRun =>
      cableRun.name?.toLowerCase().includes(lowercaseQuery) ||
      cableRun.cableType?.toLowerCase().includes(lowercaseQuery) ||
      cableRun.cableCategory?.toLowerCase().includes(lowercaseQuery) ||
      cableRun.length?.toString().includes(lowercaseQuery) ||
      cableRun.color?.toLowerCase().includes(lowercaseQuery) ||
      cableRun.description?.toLowerCase().includes(lowercaseQuery) ||
      cableRun.componentTypeDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      cableRun.componentStatusDisplayName?.toLowerCase().includes(lowercaseQuery)
    );
  };

  /**
   * Get paginated cable runs
   * @param {number} page - Page number (1-based)
   * @param {number} pageSize - Items per page
   * @returns {Object} Paginated result with data and metadata
   */
  const getPaginatedCableRuns = (page = 1, pageSize = 10) => {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedData = cableRuns.value.slice(startIndex, endIndex);

    return {
      data: paginatedData,
      currentPage: page,
      pageSize: pageSize,
      totalItems: cableRuns.value.length,
      totalPages: Math.ceil(cableRuns.value.length / pageSize)
    };
  };

  /**
   * Filter cable runs by component type
   * @param {string} typeCode - Component type code
   * @returns {Array} Filtered cable runs
   */
  const filterByType = (typeCode) => {
    if (!typeCode) return cableRuns.value;
    return cableRuns.value.filter(cr => cr.componentTypeCode === typeCode);
  };

  /**
   * Filter cable runs by status
   * @param {string} statusCode - Component status code
   * @returns {Array} Filtered cable runs
   */
  const filterByStatus = (statusCode) => {
    if (!statusCode) return cableRuns.value;
    return cableRuns.value.filter(cr => cr.componentStatusCode === statusCode);
  };

  /**
   * Filter cable runs by cable type
   * @param {string} cableType - Cable type (Ethernet, Fiber, Coaxial, etc.)
   * @returns {Array} Filtered cable runs
   */
  const filterByCableType = (cableType) => {
    if (!cableType) return cableRuns.value;
    return cableRuns.value.filter(cr =>
      cr.cableType?.toLowerCase().includes(cableType.toLowerCase())
    );
  };

  /**
   * Filter cable runs by cable category
   * @param {string} category - Cable category (Cat5e, Cat6, Cat6a, OM3, OM4, etc.)
   * @returns {Array} Filtered cable runs
   */
  const filterByCategory = (category) => {
    if (!category) return cableRuns.value;
    return cableRuns.value.filter(cr =>
      cr.cableCategory?.toLowerCase().includes(category.toLowerCase())
    );
  };

  // Computed properties
  const totalCableRuns = computed(() => cableRuns.value.length);
  const hasCableRuns = computed(() => cableRuns.value.length > 0);

  return {
    // State
    cableRuns: computed(() => cableRuns.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    isInitialized: computed(() => isInitialized.value),
    totalCableRuns,
    hasCableRuns,

    // Methods
    fetchCableRuns,
    getCableRunById,
    createCableRun,
    updateCableRun,
    deleteCableRun,
    clearCache,
    initialize,
    searchCableRuns,
    getPaginatedCableRuns,
    filterByType,
    filterByStatus,
    filterByCableType,
    filterByCategory
  };
};
