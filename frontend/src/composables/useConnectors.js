/**
 * Composable for Connectors Management
 * Provides reactive state and methods for connectors operations
 * Uses singleton pattern for shared state across components
 */

import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state (singleton pattern) - outside the composable function
const connectors = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);
const lastFetchTime = ref(null);

// Cache duration: 5 minutes
const CACHE_DURATION = 5 * 60 * 1000;

/**
 * Connectors Composable
 * @returns {Object} Connectors state and methods
 */
export const useConnectors = () => {
  const toast = useToast();

  /**
   * Check if cached data is still valid
   */
  const isCacheValid = () => {
    if (!lastFetchTime.value) return false;
    return Date.now() - lastFetchTime.value < CACHE_DURATION;
  };

  /**
   * Fetch all connectors from API
   * @param {boolean} forceRefresh - Force refresh even if cache is valid
   * @returns {Promise<Array>} Array of connectors
   */
  const fetchConnectors = async (forceRefresh = false) => {
    // Return cached data if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && connectors.value.length > 0) {
      return connectors.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.connectors.getAll();
      connectors.value = response.data;
      lastFetchTime.value = Date.now();
      isInitialized.value = true;
      return connectors.value;
    } catch (err) {
      error.value = err.message || 'Failed to fetch connectors';
      console.error('Error fetching connectors:', err);
      toast.error('Failed to load connectors');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Get a single connector by ID
   * @param {string} id - Connector UUID
   * @returns {Promise<Object>} Connector object
   */
  const getConnectorById = async (id) => {
    // Try to find in cache first
    const cached = connectors.value.find(c => c.id === id);
    if (cached && isCacheValid()) {
      return cached;
    }

    // Fetch from API if not in cache or cache invalid
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.connectors.get(id);
      return response.data;
    } catch (err) {
      error.value = err.message || 'Failed to fetch connector';
      console.error('Error fetching connector:', err);
      toast.error('Failed to load connector details');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Create a new connector
   * @param {Object} payload - Connector data
   * @returns {Promise<Object>} Created connector
   */
  const createConnector = async (payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.connectors.create(payload);
      const newConnector = response.data;
      connectors.value.push(newConnector);
      toast.success('Connector created successfully');
      return newConnector;
    } catch (err) {
      error.value = err.message || 'Failed to create connector';
      console.error('Error creating connector:', err);
      toast.error('Failed to create connector');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Update an existing connector
   * @param {string} id - Connector UUID
   * @param {Object} payload - Updated connector data
   * @returns {Promise<Object>} Updated connector
   */
  const updateConnector = async (id, payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.connectors.update(id, payload);
      const updatedConnector = response.data;

      // Update in cache
      const index = connectors.value.findIndex(c => c.id === id);
      if (index !== -1) {
        connectors.value[index] = updatedConnector;
      }

      toast.success('Connector updated successfully');
      return updatedConnector;
    } catch (err) {
      error.value = err.message || 'Failed to update connector';
      console.error('Error updating connector:', err);
      toast.error('Failed to update connector');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Delete a connector
   * @param {string} id - Connector UUID
   * @returns {Promise<void>}
   */
  const deleteConnector = async (id) => {
    isLoading.value = true;
    error.value = null;

    try {
      await api.connectors.delete(id);

      // Remove from cache
      const index = connectors.value.findIndex(c => c.id === id);
      if (index !== -1) {
        connectors.value.splice(index, 1);
      }

      toast.success('Connector deleted successfully');
    } catch (err) {
      error.value = err.message || 'Failed to delete connector';
      console.error('Error deleting connector:', err);
      toast.error('Failed to delete connector');
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
    connectors.value = [];
    isInitialized.value = false;
  };

  /**
   * Initialize composable (fetch data if not already loaded)
   * @returns {Promise<void>}
   */
  const initialize = async () => {
    if (!isInitialized.value || !isCacheValid()) {
      await fetchConnectors();
    }
  };

  /**
   * Search connectors by query string
   * @param {string} query - Search query
   * @returns {Array} Filtered connectors
   */
  const searchConnectors = (query) => {
    if (!query || query.trim() === '') {
      return connectors.value;
    }

    const lowercaseQuery = query.toLowerCase().trim();

    return connectors.value.filter(connector =>
      connector.name?.toLowerCase().includes(lowercaseQuery) ||
      connector.connectorType?.toLowerCase().includes(lowercaseQuery) ||
      connector.gender?.toLowerCase().includes(lowercaseQuery) ||
      connector.mountingType?.toLowerCase().includes(lowercaseQuery) ||
      connector.description?.toLowerCase().includes(lowercaseQuery) ||
      connector.componentTypeDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      connector.componentStatusDisplayName?.toLowerCase().includes(lowercaseQuery)
    );
  };

  /**
   * Get paginated connectors
   * @param {number} page - Page number (1-based)
   * @param {number} pageSize - Items per page
   * @returns {Object} Paginated result with data and metadata
   */
  const getPaginatedConnectors = (page = 1, pageSize = 10) => {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedData = connectors.value.slice(startIndex, endIndex);

    return {
      data: paginatedData,
      currentPage: page,
      pageSize: pageSize,
      totalItems: connectors.value.length,
      totalPages: Math.ceil(connectors.value.length / pageSize)
    };
  };

  /**
   * Filter connectors by component type
   * @param {string} typeCode - Component type code
   * @returns {Array} Filtered connectors
   */
  const filterByType = (typeCode) => {
    if (!typeCode) return connectors.value;
    return connectors.value.filter(c => c.componentTypeCode === typeCode);
  };

  /**
   * Filter connectors by status
   * @param {string} statusCode - Component status code
   * @returns {Array} Filtered connectors
   */
  const filterByStatus = (statusCode) => {
    if (!statusCode) return connectors.value;
    return connectors.value.filter(c => c.componentStatusCode === statusCode);
  };

  /**
   * Filter connectors by connector type
   * @param {string} connectorType - Connector type (RJ45, LC, SC, ST, FC, etc.)
   * @returns {Array} Filtered connectors
   */
  const filterByConnectorType = (connectorType) => {
    if (!connectorType) return connectors.value;
    return connectors.value.filter(c =>
      c.connectorType?.toLowerCase().includes(connectorType.toLowerCase())
    );
  };

  /**
   * Filter connectors by gender
   * @param {string} gender - Gender (Male, Female, Hermaphroditic)
   * @returns {Array} Filtered connectors
   */
  const filterByGender = (gender) => {
    if (!gender) return connectors.value;
    return connectors.value.filter(c =>
      c.gender?.toLowerCase() === gender.toLowerCase()
    );
  };

  /**
   * Filter connectors by mounting type
   * @param {string} mountingType - Mounting type (Panel, Inline, etc.)
   * @returns {Array} Filtered connectors
   */
  const filterByMountingType = (mountingType) => {
    if (!mountingType) return connectors.value;
    return connectors.value.filter(c =>
      c.mountingType?.toLowerCase().includes(mountingType.toLowerCase())
    );
  };

  // Computed properties
  const totalConnectors = computed(() => connectors.value.length);
  const hasConnectors = computed(() => connectors.value.length > 0);

  return {
    // State
    connectors: computed(() => connectors.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    isInitialized: computed(() => isInitialized.value),
    totalConnectors,
    hasConnectors,

    // Methods
    fetchConnectors,
    getConnectorById,
    createConnector,
    updateConnector,
    deleteConnector,
    clearCache,
    initialize,
    searchConnectors,
    getPaginatedConnectors,
    filterByType,
    filterByStatus,
    filterByConnectorType,
    filterByGender,
    filterByMountingType
  };
};
