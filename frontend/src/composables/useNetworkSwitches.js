/**
 * Composable for Network Switches Management
 * Provides reactive state and methods for network switches operations
 * Uses singleton pattern for shared state across components
 */

import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state (singleton pattern) - outside the composable function
const networkSwitches = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);
const lastFetchTime = ref(null);

// Cache duration: 5 minutes
const CACHE_DURATION = 5 * 60 * 1000;

/**
 * Network Switches Composable
 * @returns {Object} Network switches state and methods
 */
export const useNetworkSwitches = () => {
  const toast = useToast();

  /**
   * Check if cached data is still valid
   */
  const isCacheValid = () => {
    if (!lastFetchTime.value) return false;
    return Date.now() - lastFetchTime.value < CACHE_DURATION;
  };

  /**
   * Fetch all network switches from API
   * @param {boolean} forceRefresh - Force refresh even if cache is valid
   * @returns {Promise<Array>} Array of network switches
   */
  const fetchNetworkSwitches = async (forceRefresh = false) => {
    // Return cached data if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && networkSwitches.value.length > 0) {
      return networkSwitches.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.networkSwitches.getAll();
      networkSwitches.value = response.data;
      lastFetchTime.value = Date.now();
      isInitialized.value = true;
      return networkSwitches.value;
    } catch (err) {
      error.value = err.message || 'Failed to fetch network switches';
      console.error('Error fetching network switches:', err);
      toast.error('Failed to load network switches');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Get a single network switch by ID
   * @param {string} id - Network switch UUID
   * @returns {Promise<Object>} Network switch object
   */
  const getNetworkSwitchById = async (id) => {
    // Try to find in cache first
    const cached = networkSwitches.value.find(ns => ns.id === id);
    if (cached && isCacheValid()) {
      return cached;
    }

    // Fetch from API if not in cache or cache invalid
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.networkSwitches.get(id);
      return response.data;
    } catch (err) {
      error.value = err.message || 'Failed to fetch network switch';
      console.error('Error fetching network switch:', err);
      toast.error('Failed to load network switch details');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Create a new network switch
   * @param {Object} payload - Network switch data
   * @returns {Promise<Object>} Created network switch
   */
  const createNetworkSwitch = async (payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.networkSwitches.create(payload);
      const newSwitch = response.data;
      networkSwitches.value.push(newSwitch);
      toast.success('Network switch created successfully');
      return newSwitch;
    } catch (err) {
      error.value = err.message || 'Failed to create network switch';
      console.error('Error creating network switch:', err);
      toast.error('Failed to create network switch');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Update an existing network switch
   * @param {string} id - Network switch UUID
   * @param {Object} payload - Updated network switch data
   * @returns {Promise<Object>} Updated network switch
   */
  const updateNetworkSwitch = async (id, payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.networkSwitches.update(id, payload);
      const updatedSwitch = response.data;

      // Update in cache
      const index = networkSwitches.value.findIndex(ns => ns.id === id);
      if (index !== -1) {
        networkSwitches.value[index] = updatedSwitch;
      }

      toast.success('Network switch updated successfully');
      return updatedSwitch;
    } catch (err) {
      error.value = err.message || 'Failed to update network switch';
      console.error('Error updating network switch:', err);
      toast.error('Failed to update network switch');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Delete a network switch
   * @param {string} id - Network switch UUID
   * @returns {Promise<void>}
   */
  const deleteNetworkSwitch = async (id) => {
    isLoading.value = true;
    error.value = null;

    try {
      await api.networkSwitches.delete(id);

      // Remove from cache
      const index = networkSwitches.value.findIndex(ns => ns.id === id);
      if (index !== -1) {
        networkSwitches.value.splice(index, 1);
      }

      toast.success('Network switch deleted successfully');
    } catch (err) {
      error.value = err.message || 'Failed to delete network switch';
      console.error('Error deleting network switch:', err);
      toast.error('Failed to delete network switch');
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
    networkSwitches.value = [];
    isInitialized.value = false;
  };

  /**
   * Initialize composable (fetch data if not already loaded)
   * @returns {Promise<void>}
   */
  const initialize = async () => {
    if (!isInitialized.value || !isCacheValid()) {
      await fetchNetworkSwitches();
    }
  };

  /**
   * Search network switches by query string
   * @param {string} query - Search query
   * @returns {Array} Filtered network switches
   */
  const searchNetworkSwitches = (query) => {
    if (!query || query.trim() === '') {
      return networkSwitches.value;
    }

    const lowercaseQuery = query.toLowerCase().trim();

    return networkSwitches.value.filter(networkSwitch =>
      networkSwitch.name?.toLowerCase().includes(lowercaseQuery) ||
      networkSwitch.ipAddress?.toLowerCase().includes(lowercaseQuery) ||
      networkSwitch.hostname?.toLowerCase().includes(lowercaseQuery) ||
      networkSwitch.manufacturer?.toLowerCase().includes(lowercaseQuery) ||
      networkSwitch.model?.toLowerCase().includes(lowercaseQuery) ||
      networkSwitch.serialNumber?.toLowerCase().includes(lowercaseQuery) ||
      networkSwitch.description?.toLowerCase().includes(lowercaseQuery) ||
      networkSwitch.componentTypeDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      networkSwitch.componentStatusDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      networkSwitch.managementProtocol?.toLowerCase().includes(lowercaseQuery)
    );
  };

  /**
   * Get paginated network switches
   * @param {number} page - Page number (1-based)
   * @param {number} pageSize - Items per page
   * @returns {Object} Paginated result with data and metadata
   */
  const getPaginatedNetworkSwitches = (page = 1, pageSize = 10) => {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedData = networkSwitches.value.slice(startIndex, endIndex);

    return {
      data: paginatedData,
      currentPage: page,
      pageSize: pageSize,
      totalItems: networkSwitches.value.length,
      totalPages: Math.ceil(networkSwitches.value.length / pageSize)
    };
  };

  /**
   * Filter network switches by component type
   * @param {string} typeCode - Component type code
   * @returns {Array} Filtered network switches
   */
  const filterByType = (typeCode) => {
    if (!typeCode) return networkSwitches.value;
    return networkSwitches.value.filter(ns => ns.componentTypeCode === typeCode);
  };

  /**
   * Filter network switches by status
   * @param {string} statusCode - Component status code
   * @returns {Array} Filtered network switches
   */
  const filterByStatus = (statusCode) => {
    if (!statusCode) return networkSwitches.value;
    return networkSwitches.value.filter(ns => ns.componentStatusCode === statusCode);
  };

  /**
   * Filter network switches by management protocol
   * @param {string} protocol - Management protocol (SNMP, SSH, TELNET, etc.)
   * @returns {Array} Filtered network switches
   */
  const filterByProtocol = (protocol) => {
    if (!protocol) return networkSwitches.value;
    return networkSwitches.value.filter(ns => ns.managementProtocol === protocol);
  };

  // Computed properties
  const totalNetworkSwitches = computed(() => networkSwitches.value.length);
  const hasNetworkSwitches = computed(() => networkSwitches.value.length > 0);

  return {
    // State
    networkSwitches: computed(() => networkSwitches.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    isInitialized: computed(() => isInitialized.value),
    totalNetworkSwitches,
    hasNetworkSwitches,

    // Methods
    fetchNetworkSwitches,
    getNetworkSwitchById,
    createNetworkSwitch,
    updateNetworkSwitch,
    deleteNetworkSwitch,
    clearCache,
    initialize,
    searchNetworkSwitches,
    getPaginatedNetworkSwitches,
    filterByType,
    filterByStatus,
    filterByProtocol
  };
};
