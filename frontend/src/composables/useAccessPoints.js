/**
 * Composable for Access Points Management
 * Provides reactive state and methods for access points operations
 * Uses singleton pattern for shared state across components
 */

import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state (singleton pattern) - outside the composable function
const accessPoints = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);
const lastFetchTime = ref(null);

// Cache duration: 5 minutes
const CACHE_DURATION = 5 * 60 * 1000;

/**
 * Access Points Composable
 * @returns {Object} Access Points state and methods
 */
export const useAccessPoints = () => {
  const toast = useToast();

  /**
   * Check if cached data is still valid
   */
  const isCacheValid = () => {
    if (!lastFetchTime.value) return false;
    return Date.now() - lastFetchTime.value < CACHE_DURATION;
  };

  /**
   * Fetch all access points from API
   * @param {boolean} forceRefresh - Force refresh even if cache is valid
   * @returns {Promise<Array>} Array of access points
   */
  const fetchAccessPoints = async (forceRefresh = false) => {
    // Return cached data if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && accessPoints.value.length > 0) {
      return accessPoints.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.accessPoints.getAll();
      accessPoints.value = response.data;
      lastFetchTime.value = Date.now();
      isInitialized.value = true;
      return accessPoints.value;
    } catch (err) {
      error.value = err.message || 'Failed to fetch access points';
      console.error('Error fetching access points:', err);
      toast.error('Failed to load access points');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Get a single access point by ID
   * @param {string} id - Access Point UUID
   * @returns {Promise<Object>} Access Point object
   */
  const getAccessPointById = async (id) => {
    // Try to find in cache first
    const cached = accessPoints.value.find(ap => ap.id === id);
    if (cached && isCacheValid()) {
      return cached;
    }

    // Fetch from API if not in cache or cache invalid
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.accessPoints.get(id);
      return response.data;
    } catch (err) {
      error.value = err.message || 'Failed to fetch access point';
      console.error('Error fetching access point:', err);
      toast.error('Failed to load access point details');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Create a new access point
   * @param {Object} payload - Access Point data
   * @returns {Promise<Object>} Created access point
   */
  const createAccessPoint = async (payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.accessPoints.create(payload);
      const newAccessPoint = response.data;
      accessPoints.value.push(newAccessPoint);
      toast.success('Access point created successfully');
      return newAccessPoint;
    } catch (err) {
      error.value = err.message || 'Failed to create access point';
      console.error('Error creating access point:', err);
      toast.error('Failed to create access point');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Update an existing access point
   * @param {string} id - Access Point UUID
   * @param {Object} payload - Updated access point data
   * @returns {Promise<Object>} Updated access point
   */
  const updateAccessPoint = async (id, payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.accessPoints.update(id, payload);
      const updatedAccessPoint = response.data;

      // Update in cache
      const index = accessPoints.value.findIndex(ap => ap.id === id);
      if (index !== -1) {
        accessPoints.value[index] = updatedAccessPoint;
      }

      toast.success('Access point updated successfully');
      return updatedAccessPoint;
    } catch (err) {
      error.value = err.message || 'Failed to update access point';
      console.error('Error updating access point:', err);
      toast.error('Failed to update access point');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Delete an access point
   * @param {string} id - Access Point UUID
   * @returns {Promise<void>}
   */
  const deleteAccessPoint = async (id) => {
    isLoading.value = true;
    error.value = null;

    try {
      await api.accessPoints.delete(id);

      // Remove from cache
      const index = accessPoints.value.findIndex(ap => ap.id === id);
      if (index !== -1) {
        accessPoints.value.splice(index, 1);
      }

      toast.success('Access point deleted successfully');
    } catch (err) {
      error.value = err.message || 'Failed to delete access point';
      console.error('Error deleting access point:', err);
      toast.error('Failed to delete access point');
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
    accessPoints.value = [];
    isInitialized.value = false;
  };

  /**
   * Initialize composable (fetch data if not already loaded)
   * @returns {Promise<void>}
   */
  const initialize = async () => {
    if (!isInitialized.value || !isCacheValid()) {
      await fetchAccessPoints();
    }
  };

  /**
   * Search access points by query string
   * @param {string} query - Search query
   * @returns {Array} Filtered access points
   */
  const searchAccessPoints = (query) => {
    if (!query || query.trim() === '') {
      return accessPoints.value;
    }

    const lowercaseQuery = query.toLowerCase().trim();

    return accessPoints.value.filter(accessPoint =>
      accessPoint.name?.toLowerCase().includes(lowercaseQuery) ||
      accessPoint.ipAddress?.toLowerCase().includes(lowercaseQuery) ||
      accessPoint.hostname?.toLowerCase().includes(lowercaseQuery) ||
      accessPoint.manufacturer?.toLowerCase().includes(lowercaseQuery) ||
      accessPoint.model?.toLowerCase().includes(lowercaseQuery) ||
      accessPoint.serialNumber?.toLowerCase().includes(lowercaseQuery) ||
      accessPoint.description?.toLowerCase().includes(lowercaseQuery) ||
      accessPoint.componentTypeDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      accessPoint.componentStatusDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      accessPoint.managementProtocol?.toLowerCase().includes(lowercaseQuery) ||
      accessPoint.ssids?.toLowerCase().includes(lowercaseQuery)
    );
  };

  /**
   * Get paginated access points
   * @param {number} page - Page number (1-based)
   * @param {number} pageSize - Items per page
   * @returns {Object} Paginated result with data and metadata
   */
  const getPaginatedAccessPoints = (page = 1, pageSize = 10) => {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedData = accessPoints.value.slice(startIndex, endIndex);

    return {
      data: paginatedData,
      currentPage: page,
      pageSize: pageSize,
      totalItems: accessPoints.value.length,
      totalPages: Math.ceil(accessPoints.value.length / pageSize)
    };
  };

  /**
   * Filter access points by component type
   * @param {string} typeCode - Component type code
   * @returns {Array} Filtered access points
   */
  const filterByType = (typeCode) => {
    if (!typeCode) return accessPoints.value;
    return accessPoints.value.filter(ap => ap.componentTypeCode === typeCode);
  };

  /**
   * Filter access points by status
   * @param {string} statusCode - Component status code
   * @returns {Array} Filtered access points
   */
  const filterByStatus = (statusCode) => {
    if (!statusCode) return accessPoints.value;
    return accessPoints.value.filter(ap => ap.componentStatusCode === statusCode);
  };

  /**
   * Filter access points by management protocol
   * @param {string} protocol - Management protocol (SNMP, SSH, HTTP, etc.)
   * @returns {Array} Filtered access points
   */
  const filterByProtocol = (protocol) => {
    if (!protocol) return accessPoints.value;
    return accessPoints.value.filter(ap => ap.managementProtocol === protocol);
  };

  /**
   * Filter access points by wireless band
   * @param {string} band - Wireless band (2.4GHz, 5GHz, 6GHz, dual-band, tri-band)
   * @returns {Array} Filtered access points
   */
  const filterByBand = (band) => {
    if (!band) return accessPoints.value;
    return accessPoints.value.filter(ap =>
      ap.wirelessBands?.toLowerCase().includes(band.toLowerCase())
    );
  };

  // Computed properties
  const totalAccessPoints = computed(() => accessPoints.value.length);
  const hasAccessPoints = computed(() => accessPoints.value.length > 0);

  return {
    // State
    accessPoints: computed(() => accessPoints.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    isInitialized: computed(() => isInitialized.value),
    totalAccessPoints,
    hasAccessPoints,

    // Methods
    fetchAccessPoints,
    getAccessPointById,
    createAccessPoint,
    updateAccessPoint,
    deleteAccessPoint,
    clearCache,
    initialize,
    searchAccessPoints,
    getPaginatedAccessPoints,
    filterByType,
    filterByStatus,
    filterByProtocol,
    filterByBand
  };
};
