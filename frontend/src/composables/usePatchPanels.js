/**
 * Composable for Patch Panels Management
 * Provides reactive state and methods for patch panels operations
 * Uses singleton pattern for shared state across components
 */

import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state (singleton pattern) - outside the composable function
const patchPanels = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);
const lastFetchTime = ref(null);

// Cache duration: 5 minutes
const CACHE_DURATION = 5 * 60 * 1000;

/**
 * Patch Panels Composable
 * @returns {Object} Patch Panels state and methods
 */
export const usePatchPanels = () => {
  const toast = useToast();

  /**
   * Check if cached data is still valid
   */
  const isCacheValid = () => {
    if (!lastFetchTime.value) return false;
    return Date.now() - lastFetchTime.value < CACHE_DURATION;
  };

  /**
   * Fetch all patch panels from API
   * @param {boolean} forceRefresh - Force refresh even if cache is valid
   * @returns {Promise<Array>} Array of patch panels
   */
  const fetchPatchPanels = async (forceRefresh = false) => {
    // Return cached data if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && patchPanels.value.length > 0) {
      return patchPanels.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.patchPanels.getAll();
      patchPanels.value = response.data;
      lastFetchTime.value = Date.now();
      isInitialized.value = true;
      return patchPanels.value;
    } catch (err) {
      error.value = err.message || 'Failed to fetch patch panels';
      console.error('Error fetching patch panels:', err);
      toast.error('Failed to load patch panels');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Get a single patch panel by ID
   * @param {string} id - Patch Panel UUID
   * @returns {Promise<Object>} Patch Panel object
   */
  const getPatchPanelById = async (id) => {
    // Try to find in cache first
    const cached = patchPanels.value.find(pp => pp.id === id);
    if (cached && isCacheValid()) {
      return cached;
    }

    // Fetch from API if not in cache or cache invalid
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.patchPanels.get(id);
      return response.data;
    } catch (err) {
      error.value = err.message || 'Failed to fetch patch panel';
      console.error('Error fetching patch panel:', err);
      toast.error('Failed to load patch panel details');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Create a new patch panel
   * @param {Object} payload - Patch Panel data
   * @returns {Promise<Object>} Created patch panel
   */
  const createPatchPanel = async (payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.patchPanels.create(payload);
      const newPatchPanel = response.data;
      patchPanels.value.push(newPatchPanel);
      toast.success('Patch panel created successfully');
      return newPatchPanel;
    } catch (err) {
      error.value = err.message || 'Failed to create patch panel';
      console.error('Error creating patch panel:', err);
      toast.error('Failed to create patch panel');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Update an existing patch panel
   * @param {string} id - Patch Panel UUID
   * @param {Object} payload - Updated patch panel data
   * @returns {Promise<Object>} Updated patch panel
   */
  const updatePatchPanel = async (id, payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.patchPanels.update(id, payload);
      const updatedPatchPanel = response.data;

      // Update in cache
      const index = patchPanels.value.findIndex(pp => pp.id === id);
      if (index !== -1) {
        patchPanels.value[index] = updatedPatchPanel;
      }

      toast.success('Patch panel updated successfully');
      return updatedPatchPanel;
    } catch (err) {
      error.value = err.message || 'Failed to update patch panel';
      console.error('Error updating patch panel:', err);
      toast.error('Failed to update patch panel');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Delete a patch panel
   * @param {string} id - Patch Panel UUID
   * @returns {Promise<void>}
   */
  const deletePatchPanel = async (id) => {
    isLoading.value = true;
    error.value = null;

    try {
      await api.patchPanels.delete(id);

      // Remove from cache
      const index = patchPanels.value.findIndex(pp => pp.id === id);
      if (index !== -1) {
        patchPanels.value.splice(index, 1);
      }

      toast.success('Patch panel deleted successfully');
    } catch (err) {
      error.value = err.message || 'Failed to delete patch panel';
      console.error('Error deleting patch panel:', err);
      toast.error('Failed to delete patch panel');
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
    patchPanels.value = [];
    isInitialized.value = false;
  };

  /**
   * Initialize composable (fetch data if not already loaded)
   * @returns {Promise<void>}
   */
  const initialize = async () => {
    if (!isInitialized.value || !isCacheValid()) {
      await fetchPatchPanels();
    }
  };

  /**
   * Search patch panels by query string
   * @param {string} query - Search query
   * @returns {Array} Filtered patch panels
   */
  const searchPatchPanels = (query) => {
    if (!query || query.trim() === '') {
      return patchPanels.value;
    }

    const lowercaseQuery = query.toLowerCase().trim();

    return patchPanels.value.filter(patchPanel =>
      patchPanel.name?.toLowerCase().includes(lowercaseQuery) ||
      patchPanel.manufacturer?.toLowerCase().includes(lowercaseQuery) ||
      patchPanel.model?.toLowerCase().includes(lowercaseQuery) ||
      patchPanel.serialNumber?.toLowerCase().includes(lowercaseQuery) ||
      patchPanel.description?.toLowerCase().includes(lowercaseQuery) ||
      patchPanel.componentTypeDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      patchPanel.componentStatusDisplayName?.toLowerCase().includes(lowercaseQuery)
    );
  };

  /**
   * Get paginated patch panels
   * @param {number} page - Page number (1-based)
   * @param {number} pageSize - Items per page
   * @returns {Object} Paginated result with data and metadata
   */
  const getPaginatedPatchPanels = (page = 1, pageSize = 10) => {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedData = patchPanels.value.slice(startIndex, endIndex);

    return {
      data: paginatedData,
      currentPage: page,
      pageSize: pageSize,
      totalItems: patchPanels.value.length,
      totalPages: Math.ceil(patchPanels.value.length / pageSize)
    };
  };

  /**
   * Filter patch panels by component type
   * @param {string} typeCode - Component type code
   * @returns {Array} Filtered patch panels
   */
  const filterByType = (typeCode) => {
    if (!typeCode) return patchPanels.value;
    return patchPanels.value.filter(pp => pp.componentTypeCode === typeCode);
  };

  /**
   * Filter patch panels by status
   * @param {string} statusCode - Component status code
   * @returns {Array} Filtered patch panels
   */
  const filterByStatus = (statusCode) => {
    if (!statusCode) return patchPanels.value;
    return patchPanels.value.filter(pp => pp.componentStatusCode === statusCode);
  };

  /**
   * Filter patch panels by manufacturer
   * @param {string} manufacturer - Manufacturer name
   * @returns {Array} Filtered patch panels
   */
  const filterByManufacturer = (manufacturer) => {
    if (!manufacturer) return patchPanels.value;
    return patchPanels.value.filter(pp =>
      pp.manufacturer?.toLowerCase().includes(manufacturer.toLowerCase())
    );
  };

  // Computed properties
  const totalPatchPanels = computed(() => patchPanels.value.length);
  const hasPatchPanels = computed(() => patchPanels.value.length > 0);

  return {
    // State
    patchPanels: computed(() => patchPanels.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    isInitialized: computed(() => isInitialized.value),
    totalPatchPanels,
    hasPatchPanels,

    // Methods
    fetchPatchPanels,
    getPatchPanelById,
    createPatchPanel,
    updatePatchPanel,
    deletePatchPanel,
    clearCache,
    initialize,
    searchPatchPanels,
    getPaginatedPatchPanels,
    filterByType,
    filterByStatus,
    filterByManufacturer
  };
};
