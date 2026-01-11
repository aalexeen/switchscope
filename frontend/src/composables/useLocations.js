/**
 * Composable for Locations Management
 * Provides reactive state and methods for locations operations
 * Uses singleton pattern for shared state across components
 */

import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state (singleton pattern) - outside the composable function
const locations = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);
const lastFetchTime = ref(null);

// Cache duration: 5 minutes
const CACHE_DURATION = 5 * 60 * 1000;

/**
 * Locations Composable
 * @returns {Object} Locations state and methods
 */
export const useLocations = () => {
  const toast = useToast();

  /**
   * Check if cached data is still valid
   */
  const isCacheValid = () => {
    if (!lastFetchTime.value) return false;
    return Date.now() - lastFetchTime.value < CACHE_DURATION;
  };

  /**
   * Fetch all locations from API
   * @param {boolean} forceRefresh - Force refresh even if cache is valid
   * @returns {Promise<Array>} Array of locations
   */
  const fetchLocations = async (forceRefresh = false) => {
    // Return cached data if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && locations.value.length > 0) {
      return locations.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.locations.getAll();
      locations.value = response.data;
      lastFetchTime.value = Date.now();
      isInitialized.value = true;
      return locations.value;
    } catch (err) {
      error.value = err.message || 'Failed to fetch locations';
      console.error('Error fetching locations:', err);
      toast.error('Failed to load locations');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Get a single location by ID
   * @param {string} id - Location UUID
   * @returns {Promise<Object>} Location object
   */
  const getLocationById = async (id) => {
    // Try to find in cache first
    const cached = locations.value.find(l => l.id === id);
    if (cached && isCacheValid()) {
      return cached;
    }

    // Fetch from API if not in cache or cache invalid
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.locations.get(id);
      return response.data;
    } catch (err) {
      error.value = err.message || 'Failed to fetch location';
      console.error('Error fetching location:', err);
      toast.error('Failed to load location details');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Create a new location
   * @param {Object} payload - Location data
   * @returns {Promise<Object>} Created location
   */
  const createLocation = async (payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.locations.create(payload);
      const newLocation = response.data;
      locations.value.push(newLocation);
      toast.success('Location created successfully');
      return newLocation;
    } catch (err) {
      error.value = err.message || 'Failed to create location';
      console.error('Error creating location:', err);
      toast.error('Failed to create location');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Update an existing location
   * @param {string} id - Location UUID
   * @param {Object} payload - Updated location data
   * @returns {Promise<Object>} Updated location
   */
  const updateLocation = async (id, payload) => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.locations.update(id, payload);
      const updatedLocation = response.data;

      // Update in cache
      const index = locations.value.findIndex(l => l.id === id);
      if (index !== -1) {
        locations.value[index] = updatedLocation;
      }

      toast.success('Location updated successfully');
      return updatedLocation;
    } catch (err) {
      error.value = err.message || 'Failed to update location';
      console.error('Error updating location:', err);
      toast.error('Failed to update location');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Delete a location
   * @param {string} id - Location UUID
   * @returns {Promise<void>}
   */
  const deleteLocation = async (id) => {
    isLoading.value = true;
    error.value = null;

    try {
      await api.locations.delete(id);

      // Remove from cache
      const index = locations.value.findIndex(l => l.id === id);
      if (index !== -1) {
        locations.value.splice(index, 1);
      }

      toast.success('Location deleted successfully');
    } catch (err) {
      error.value = err.message || 'Failed to delete location';
      console.error('Error deleting location:', err);
      toast.error('Failed to delete location');
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
    locations.value = [];
    isInitialized.value = false;
  };

  /**
   * Initialize composable (fetch data if not already loaded)
   * @returns {Promise<void>}
   */
  const initialize = async () => {
    if (!isInitialized.value || !isCacheValid()) {
      await fetchLocations();
    }
  };

  /**
   * Search locations by query string
   * @param {string} query - Search query
   * @returns {Array} Filtered locations
   */
  const searchLocations = (query) => {
    if (!query || query.trim() === '') {
      return locations.value;
    }

    const lowercaseQuery = query.toLowerCase().trim();

    return locations.value.filter(location =>
      location.name?.toLowerCase().includes(lowercaseQuery) ||
      location.typeDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      location.address?.toLowerCase().includes(lowercaseQuery) ||
      location.parentLocationName?.toLowerCase().includes(lowercaseQuery) ||
      location.fullPath?.toLowerCase().includes(lowercaseQuery) ||
      location.description?.toLowerCase().includes(lowercaseQuery)
    );
  };

  /**
   * Get paginated locations
   * @param {number} page - Page number (1-based)
   * @param {number} pageSize - Items per page
   * @returns {Object} Paginated result with data and metadata
   */
  const getPaginatedLocations = (page = 1, pageSize = 10) => {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedData = locations.value.slice(startIndex, endIndex);

    return {
      data: paginatedData,
      currentPage: page,
      pageSize: pageSize,
      totalItems: locations.value.length,
      totalPages: Math.ceil(locations.value.length / pageSize)
    };
  };

  /**
   * Filter locations by type
   * @param {string} typeCode - Location type code
   * @returns {Array} Filtered locations
   */
  const filterByType = (typeCode) => {
    if (!typeCode) return locations.value;
    return locations.value.filter(l => l.locationTypeCode === typeCode);
  };

  /**
   * Filter locations by parent
   * @param {string} parentId - Parent location UUID
   * @returns {Array} Filtered locations
   */
  const filterByParent = (parentId) => {
    if (!parentId) return locations.value;
    return locations.value.filter(l => l.parentLocationId === parentId);
  };

  /**
   * Get root locations (no parent)
   * @returns {Array} Root locations
   */
  const getRootLocations = () => {
    return locations.value.filter(l => !l.parentLocationId);
  };

  /**
   * Get child locations of a parent
   * @param {string} parentId - Parent location UUID
   * @returns {Array} Child locations
   */
  const getChildLocations = (parentId) => {
    return locations.value.filter(l => l.parentLocationId === parentId);
  };

  /**
   * Filter locations by country
   * @param {string} country - Country name
   * @returns {Array} Filtered locations
   */
  const filterByCountry = (country) => {
    if (!country) return locations.value;
    return locations.value.filter(l =>
      l.country?.toLowerCase().includes(country.toLowerCase())
    );
  };

  /**
   * Filter locations by city
   * @param {string} city - City name
   * @returns {Array} Filtered locations
   */
  const filterByCity = (city) => {
    if (!city) return locations.value;
    return locations.value.filter(l =>
      l.city?.toLowerCase().includes(city.toLowerCase())
    );
  };

  // Computed properties
  const totalLocations = computed(() => locations.value.length);
  const hasLocations = computed(() => locations.value.length > 0);

  return {
    // State
    locations: computed(() => locations.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    isInitialized: computed(() => isInitialized.value),
    totalLocations,
    hasLocations,

    // Methods
    fetchLocations,
    getLocationById,
    createLocation,
    updateLocation,
    deleteLocation,
    clearCache,
    initialize,
    searchLocations,
    getPaginatedLocations,
    filterByType,
    filterByParent,
    getRootLocations,
    getChildLocations,
    filterByCountry,
    filterByCity
  };
};
