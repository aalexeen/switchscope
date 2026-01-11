/**
 * Composable for Routers Management
 * Provides reactive state and methods for routers operations
 * Uses singleton pattern for shared state across components
 */

import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state (singleton pattern) - outside the composable function
const routers = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);
const lastFetchTime = ref(null);

// Cache duration: 5 minutes
const CACHE_DURATION = 5 * 60 * 1000;

/**
 * Routers Composable
 * @returns {Object} Routers state and methods
 */
export const useRouters = () => {
  const toast = useToast();

  /**
   * Check if cached data is still valid
   */
  const isCacheValid = () => {
    if (!lastFetchTime.value) return false;
    return Date.now() - lastFetchTime.value < CACHE_DURATION;
  };

  /**
   * Fetch all routers from API
   * @param {boolean} forceRefresh - Force refresh even if cache is valid
   * @returns {Promise<Array>} Array of routers
   */
  const fetchRouters = async (forceRefresh = false) => {
    // Return cached data if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && routers.value.length > 0) {
      return routers.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.routers.getAll();
      routers.value = response.data;
      lastFetchTime.value = Date.now();
      isInitialized.value = true;
      return routers.value;
    } catch (err) {
      error.value = err.message || 'Failed to fetch routers';
      console.error('Error fetching routers:', err);
      toast.error('Failed to load routers');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Get a single router by ID
   * @param {string} id - Router UUID
   * @returns {Promise<Object>} Router object
   */
  const getRouterById = async (id) => {
    // Try to find in cache first
    const cached = routers.value.find(r => r.id === id);
    if (cached && isCacheValid()) {
      return cached;
    }

    // Fetch from API if not in cache or cache invalid
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.routers.get(id);
      return response.data;
    } catch (err) {
      error.value = err.message || 'Failed to fetch router';
      console.error('Error fetching router:', err);
      toast.error('Failed to load router details');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Create a new router
   * @param {Object} payload - Router data
   * @returns {Promise<Object>} Created router
   */
  const createRouter = async (payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.routers.create(payload);
      const newRouter = response.data;
      routers.value.push(newRouter);
      toast.success('Router created successfully');
      return newRouter;
    } catch (err) {
      error.value = err.message || 'Failed to create router';
      console.error('Error creating router:', err);
      toast.error('Failed to create router');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Update an existing router
   * @param {string} id - Router UUID
   * @param {Object} payload - Updated router data
   * @returns {Promise<Object>} Updated router
   */
  const updateRouter = async (id, payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.routers.update(id, payload);
      const updatedRouter = response.data;

      // Update in cache
      const index = routers.value.findIndex(r => r.id === id);
      if (index !== -1) {
        routers.value[index] = updatedRouter;
      }

      toast.success('Router updated successfully');
      return updatedRouter;
    } catch (err) {
      error.value = err.message || 'Failed to update router';
      console.error('Error updating router:', err);
      toast.error('Failed to update router');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Delete a router
   * @param {string} id - Router UUID
   * @returns {Promise<void>}
   */
  const deleteRouter = async (id) => {
    isLoading.value = true;
    error.value = null;

    try {
      await api.routers.delete(id);

      // Remove from cache
      const index = routers.value.findIndex(r => r.id === id);
      if (index !== -1) {
        routers.value.splice(index, 1);
      }

      toast.success('Router deleted successfully');
    } catch (err) {
      error.value = err.message || 'Failed to delete router';
      console.error('Error deleting router:', err);
      toast.error('Failed to delete router');
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
    routers.value = [];
    isInitialized.value = false;
  };

  /**
   * Initialize composable (fetch data if not already loaded)
   * @returns {Promise<void>}
   */
  const initialize = async () => {
    if (!isInitialized.value || !isCacheValid()) {
      await fetchRouters();
    }
  };

  /**
   * Search routers by query string
   * @param {string} query - Search query
   * @returns {Array} Filtered routers
   */
  const searchRouters = (query) => {
    if (!query || query.trim() === '') {
      return routers.value;
    }

    const lowercaseQuery = query.toLowerCase().trim();

    return routers.value.filter(router =>
      router.name?.toLowerCase().includes(lowercaseQuery) ||
      router.ipAddress?.toLowerCase().includes(lowercaseQuery) ||
      router.hostname?.toLowerCase().includes(lowercaseQuery) ||
      router.manufacturer?.toLowerCase().includes(lowercaseQuery) ||
      router.model?.toLowerCase().includes(lowercaseQuery) ||
      router.serialNumber?.toLowerCase().includes(lowercaseQuery) ||
      router.description?.toLowerCase().includes(lowercaseQuery) ||
      router.componentTypeDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      router.componentStatusDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      router.managementProtocol?.toLowerCase().includes(lowercaseQuery) ||
      router.routingProtocols?.toLowerCase().includes(lowercaseQuery)
    );
  };

  /**
   * Get paginated routers
   * @param {number} page - Page number (1-based)
   * @param {number} pageSize - Items per page
   * @returns {Object} Paginated result with data and metadata
   */
  const getPaginatedRouters = (page = 1, pageSize = 10) => {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedData = routers.value.slice(startIndex, endIndex);

    return {
      data: paginatedData,
      currentPage: page,
      pageSize: pageSize,
      totalItems: routers.value.length,
      totalPages: Math.ceil(routers.value.length / pageSize)
    };
  };

  /**
   * Filter routers by component type
   * @param {string} typeCode - Component type code
   * @returns {Array} Filtered routers
   */
  const filterByType = (typeCode) => {
    if (!typeCode) return routers.value;
    return routers.value.filter(r => r.componentTypeCode === typeCode);
  };

  /**
   * Filter routers by status
   * @param {string} statusCode - Component status code
   * @returns {Array} Filtered routers
   */
  const filterByStatus = (statusCode) => {
    if (!statusCode) return routers.value;
    return routers.value.filter(r => r.componentStatusCode === statusCode);
  };

  /**
   * Filter routers by management protocol
   * @param {string} protocol - Management protocol (SNMP, SSH, TELNET, etc.)
   * @returns {Array} Filtered routers
   */
  const filterByProtocol = (protocol) => {
    if (!protocol) return routers.value;
    return routers.value.filter(r => r.managementProtocol === protocol);
  };

  /**
   * Filter routers by routing protocol
   * @param {string} routingProtocol - Routing protocol (OSPF, BGP, EIGRP, etc.)
   * @returns {Array} Filtered routers
   */
  const filterByRoutingProtocol = (routingProtocol) => {
    if (!routingProtocol) return routers.value;
    return routers.value.filter(r =>
      r.routingProtocols?.toLowerCase().includes(routingProtocol.toLowerCase())
    );
  };

  // Computed properties
  const totalRouters = computed(() => routers.value.length);
  const hasRouters = computed(() => routers.value.length > 0);

  return {
    // State
    routers: computed(() => routers.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    isInitialized: computed(() => isInitialized.value),
    totalRouters,
    hasRouters,

    // Methods
    fetchRouters,
    getRouterById,
    createRouter,
    updateRouter,
    deleteRouter,
    clearCache,
    initialize,
    searchRouters,
    getPaginatedRouters,
    filterByType,
    filterByStatus,
    filterByProtocol,
    filterByRoutingProtocol
  };
};
