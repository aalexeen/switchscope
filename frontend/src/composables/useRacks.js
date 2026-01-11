/**
 * Composable for Racks Management
 * Provides reactive state and methods for racks operations
 * Uses singleton pattern for shared state across components
 */

import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state (singleton pattern) - outside the composable function
const racks = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);
const lastFetchTime = ref(null);

// Cache duration: 5 minutes
const CACHE_DURATION = 5 * 60 * 1000;

/**
 * Racks Composable
 * @returns {Object} Racks state and methods
 */
export const useRacks = () => {
  const toast = useToast();

  /**
   * Check if cached data is still valid
   */
  const isCacheValid = () => {
    if (!lastFetchTime.value) return false;
    return Date.now() - lastFetchTime.value < CACHE_DURATION;
  };

  /**
   * Fetch all racks from API
   * @param {boolean} forceRefresh - Force refresh even if cache is valid
   * @returns {Promise<Array>} Array of racks
   */
  const fetchRacks = async (forceRefresh = false) => {
    // Return cached data if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && racks.value.length > 0) {
      return racks.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.racks.getAll();
      racks.value = response.data;
      lastFetchTime.value = Date.now();
      isInitialized.value = true;
      return racks.value;
    } catch (err) {
      error.value = err.message || 'Failed to fetch racks';
      console.error('Error fetching racks:', err);
      toast.error('Failed to load racks');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Get a single rack by ID
   * @param {string} id - Rack UUID
   * @returns {Promise<Object>} Rack object
   */
  const getRackById = async (id) => {
    // Try to find in cache first
    const cached = racks.value.find(r => r.id === id);
    if (cached && isCacheValid()) {
      return cached;
    }

    // Fetch from API if not in cache or cache invalid
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.racks.get(id);
      return response.data;
    } catch (err) {
      error.value = err.message || 'Failed to fetch rack';
      console.error('Error fetching rack:', err);
      toast.error('Failed to load rack details');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Create a new rack
   * @param {Object} payload - Rack data
   * @returns {Promise<Object>} Created rack
   */
  const createRack = async (payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.racks.create(payload);
      const newRack = response.data;
      racks.value.push(newRack);
      toast.success('Rack created successfully');
      return newRack;
    } catch (err) {
      error.value = err.message || 'Failed to create rack';
      console.error('Error creating rack:', err);
      toast.error('Failed to create rack');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Update an existing rack
   * @param {string} id - Rack UUID
   * @param {Object} payload - Updated rack data
   * @returns {Promise<Object>} Updated rack
   */
  const updateRack = async (id, payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.racks.update(id, payload);
      const updatedRack = response.data;

      // Update in cache
      const index = racks.value.findIndex(r => r.id === id);
      if (index !== -1) {
        racks.value[index] = updatedRack;
      }

      toast.success('Rack updated successfully');
      return updatedRack;
    } catch (err) {
      error.value = err.message || 'Failed to update rack';
      console.error('Error updating rack:', err);
      toast.error('Failed to update rack');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Delete a rack
   * @param {string} id - Rack UUID
   * @returns {Promise<void>}
   */
  const deleteRack = async (id) => {
    isLoading.value = true;
    error.value = null;

    try {
      await api.racks.delete(id);

      // Remove from cache
      const index = racks.value.findIndex(r => r.id === id);
      if (index !== -1) {
        racks.value.splice(index, 1);
      }

      toast.success('Rack deleted successfully');
    } catch (err) {
      error.value = err.message || 'Failed to delete rack';
      console.error('Error deleting rack:', err);
      toast.error('Failed to delete rack');
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
    racks.value = [];
    isInitialized.value = false;
  };

  /**
   * Initialize composable (fetch data if not already loaded)
   * @returns {Promise<void>}
   */
  const initialize = async () => {
    if (!isInitialized.value || !isCacheValid()) {
      await fetchRacks();
    }
  };

  /**
   * Search racks by query string
   * @param {string} query - Search query
   * @returns {Array} Filtered racks
   */
  const searchRacks = (query) => {
    if (!query || query.trim() === '') {
      return racks.value;
    }

    const lowercaseQuery = query.toLowerCase().trim();

    return racks.value.filter(rack =>
      rack.name?.toLowerCase().includes(lowercaseQuery) ||
      rack.manufacturer?.toLowerCase().includes(lowercaseQuery) ||
      rack.model?.toLowerCase().includes(lowercaseQuery) ||
      rack.serialNumber?.toLowerCase().includes(lowercaseQuery) ||
      rack.description?.toLowerCase().includes(lowercaseQuery) ||
      rack.componentTypeDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      rack.componentStatusDisplayName?.toLowerCase().includes(lowercaseQuery)
    );
  };

  /**
   * Get paginated racks
   * @param {number} page - Page number (1-based)
   * @param {number} pageSize - Items per page
   * @returns {Object} Paginated result with data and metadata
   */
  const getPaginatedRacks = (page = 1, pageSize = 10) => {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedData = racks.value.slice(startIndex, endIndex);

    return {
      data: paginatedData,
      currentPage: page,
      pageSize: pageSize,
      totalItems: racks.value.length,
      totalPages: Math.ceil(racks.value.length / pageSize)
    };
  };

  /**
   * Filter racks by component type
   * @param {string} typeCode - Component type code
   * @returns {Array} Filtered racks
   */
  const filterByType = (typeCode) => {
    if (!typeCode) return racks.value;
    return racks.value.filter(r => r.componentTypeCode === typeCode);
  };

  /**
   * Filter racks by status
   * @param {string} statusCode - Component status code
   * @returns {Array} Filtered racks
   */
  const filterByStatus = (statusCode) => {
    if (!statusCode) return racks.value;
    return racks.value.filter(r => r.componentStatusCode === statusCode);
  };

  /**
   * Filter racks by manufacturer
   * @param {string} manufacturer - Manufacturer name
   * @returns {Array} Filtered racks
   */
  const filterByManufacturer = (manufacturer) => {
    if (!manufacturer) return racks.value;
    return racks.value.filter(r =>
      r.manufacturer?.toLowerCase().includes(manufacturer.toLowerCase())
    );
  };

  /**
   * Filter racks by height (rack units)
   * @param {number} minHeight - Minimum height in U
   * @param {number} maxHeight - Maximum height in U
   * @returns {Array} Filtered racks
   */
  const filterByHeight = (minHeight, maxHeight) => {
    return racks.value.filter(r => {
      const height = r.heightUnits || 0;
      if (minHeight && height < minHeight) return false;
      if (maxHeight && height > maxHeight) return false;
      return true;
    });
  };

  // Computed properties
  const totalRacks = computed(() => racks.value.length);
  const hasRacks = computed(() => racks.value.length > 0);

  return {
    // State
    racks: computed(() => racks.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    isInitialized: computed(() => isInitialized.value),
    totalRacks,
    hasRacks,

    // Methods
    fetchRacks,
    getRackById,
    createRack,
    updateRack,
    deleteRack,
    clearCache,
    initialize,
    searchRacks,
    getPaginatedRacks,
    filterByType,
    filterByStatus,
    filterByManufacturer,
    filterByHeight
  };
};
