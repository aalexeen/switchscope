/**
 * Composable for Ports Management
 * Provides reactive state and methods for ports operations
 * Uses singleton pattern for shared state across components
 */

import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state (singleton pattern) - outside the composable function
const ports = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);
const lastFetchTime = ref(null);

// Cache duration: 5 minutes
const CACHE_DURATION = 5 * 60 * 1000;

/**
 * Ports Composable
 * @returns {Object} Ports state and methods
 */
export const usePorts = () => {
  const toast = useToast();

  /**
   * Check if cached data is still valid
   */
  const isCacheValid = () => {
    if (!lastFetchTime.value) return false;
    return Date.now() - lastFetchTime.value < CACHE_DURATION;
  };

  /**
   * Fetch all ports from API
   * @param {boolean} forceRefresh - Force refresh even if cache is valid
   * @returns {Promise<Array>} Array of ports
   */
  const fetchPorts = async (forceRefresh = false) => {
    // Return cached data if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && ports.value.length > 0) {
      return ports.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.ports.getAll();
      ports.value = response.data;
      lastFetchTime.value = Date.now();
      isInitialized.value = true;
      return ports.value;
    } catch (err) {
      error.value = err.message || 'Failed to fetch ports';
      console.error('Error fetching ports:', err);
      toast.error('Failed to load ports');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Get a single port by ID
   * @param {string} id - Port UUID
   * @returns {Promise<Object>} Port object
   */
  const getPortById = async (id) => {
    // Try to find in cache first
    const cached = ports.value.find(p => p.id === id);
    if (cached && isCacheValid()) {
      return cached;
    }

    // Fetch from API if not in cache or cache invalid
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.ports.get(id);
      return response.data;
    } catch (err) {
      error.value = err.message || 'Failed to fetch port';
      console.error('Error fetching port:', err);
      toast.error('Failed to load port details');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Create a new port
   * @param {Object} payload - Port data
   * @returns {Promise<Object>} Created port
   */
  const createPort = async (payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.ports.create(payload);
      const newPort = response.data;
      ports.value.push(newPort);
      toast.success('Port created successfully');
      return newPort;
    } catch (err) {
      error.value = err.message || 'Failed to create port';
      console.error('Error creating port:', err);
      toast.error('Failed to create port');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Update an existing port
   * @param {string} id - Port UUID
   * @param {Object} payload - Updated port data
   * @returns {Promise<Object>} Updated port
   */
  const updatePort = async (id, payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.ports.update(id, payload);
      const updatedPort = response.data;

      // Update in cache
      const index = ports.value.findIndex(p => p.id === id);
      if (index !== -1) {
        ports.value[index] = updatedPort;
      }

      toast.success('Port updated successfully');
      return updatedPort;
    } catch (err) {
      error.value = err.message || 'Failed to update port';
      console.error('Error updating port:', err);
      toast.error('Failed to update port');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Delete a port
   * @param {string} id - Port UUID
   * @returns {Promise<void>}
   */
  const deletePort = async (id) => {
    isLoading.value = true;
    error.value = null;

    try {
      await api.ports.delete(id);

      // Remove from cache
      const index = ports.value.findIndex(p => p.id === id);
      if (index !== -1) {
        ports.value.splice(index, 1);
      }

      toast.success('Port deleted successfully');
    } catch (err) {
      error.value = err.message || 'Failed to delete port';
      console.error('Error deleting port:', err);
      toast.error('Failed to delete port');
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
    ports.value = [];
    isInitialized.value = false;
  };

  /**
   * Initialize composable (fetch data if not already loaded)
   * @returns {Promise<void>}
   */
  const initialize = async () => {
    if (!isInitialized.value || !isCacheValid()) {
      await fetchPorts();
    }
  };

  /**
   * Search ports by query string
   * @param {string} query - Search query
   * @returns {Array} Filtered ports
   */
  const searchPorts = (query) => {
    if (!query || query.trim() === '') {
      return ports.value;
    }

    const lowercaseQuery = query.toLowerCase().trim();

    return ports.value.filter(port =>
      port.name?.toLowerCase().includes(lowercaseQuery) ||
      port.portNumber?.toString().toLowerCase().includes(lowercaseQuery) ||
      port.portType?.toLowerCase().includes(lowercaseQuery) ||
      port.speed?.toString().toLowerCase().includes(lowercaseQuery) ||
      port.deviceName?.toLowerCase().includes(lowercaseQuery) ||
      port.macAddress?.toLowerCase().includes(lowercaseQuery) ||
      port.status?.toLowerCase().includes(lowercaseQuery) ||
      port.description?.toLowerCase().includes(lowercaseQuery)
    );
  };

  /**
   * Get paginated ports
   * @param {number} page - Page number (1-based)
   * @param {number} pageSize - Items per page
   * @returns {Object} Paginated result with data and metadata
   */
  const getPaginatedPorts = (page = 1, pageSize = 10) => {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedData = ports.value.slice(startIndex, endIndex);

    return {
      data: paginatedData,
      currentPage: page,
      pageSize: pageSize,
      totalItems: ports.value.length,
      totalPages: Math.ceil(ports.value.length / pageSize)
    };
  };

  /**
   * Filter ports by device
   * @param {string} deviceId - Device UUID
   * @returns {Array} Filtered ports
   */
  const filterByDevice = (deviceId) => {
    if (!deviceId) return ports.value;
    return ports.value.filter(p => p.deviceId === deviceId);
  };

  /**
   * Filter ports by port type
   * @param {string} portType - Port type (ETHERNET, FIBER, etc.)
   * @returns {Array} Filtered ports
   */
  const filterByType = (portType) => {
    if (!portType) return ports.value;
    return ports.value.filter(p => p.portType === portType);
  };

  /**
   * Filter ports by status
   * @param {string} status - Port status
   * @returns {Array} Filtered ports
   */
  const filterByStatus = (status) => {
    if (!status) return ports.value;
    return ports.value.filter(p => p.status === status);
  };

  /**
   * Filter ports by speed
   * @param {number} speed - Port speed (e.g., 1000, 10000)
   * @returns {Array} Filtered ports
   */
  const filterBySpeed = (speed) => {
    if (!speed) return ports.value;
    return ports.value.filter(p => p.speed === speed);
  };

  /**
   * Filter ports by enabled status
   * @param {boolean} enabled - Enabled status
   * @returns {Array} Filtered ports
   */
  const filterByEnabled = (enabled) => {
    if (enabled === null || enabled === undefined) return ports.value;
    return ports.value.filter(p => p.enabled === enabled);
  };

  // Computed properties
  const totalPorts = computed(() => ports.value.length);
  const hasPorts = computed(() => ports.value.length > 0);

  return {
    // State
    ports: computed(() => ports.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    isInitialized: computed(() => isInitialized.value),
    totalPorts,
    hasPorts,

    // Methods
    fetchPorts,
    getPortById,
    createPort,
    updatePort,
    deletePort,
    clearCache,
    initialize,
    searchPorts,
    getPaginatedPorts,
    filterByDevice,
    filterByType,
    filterByStatus,
    filterBySpeed,
    filterByEnabled
  };
};
