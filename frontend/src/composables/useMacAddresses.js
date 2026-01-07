// @/composables/useMacAddresses.js
import { ref, computed } from "vue";
import api from '@/api/index';

// Global reactive state - shared across all components
const macAddresses = ref([]);
const isLoading = ref(false);
const lastFetched = ref(null);
const error = ref(null);

// Cache duration in milliseconds (5 minutes)
const CACHE_DURATION = 5 * 60 * 1000;

export const useMacAddresses = () => {
  
  // Check if cache is still valid
  const isCacheValid = () => {
    if (!lastFetched.value) return false;
    return Date.now() - lastFetched.value < CACHE_DURATION;
  };

  // Fetch all MAC addresses from API
  const fetchMacAddresses = async (forceRefresh = false) => {
    // Use cache if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && macAddresses.value.length > 0) {
      return macAddresses.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.blockedmacs.getAll();
      macAddresses.value = response.data;
      lastFetched.value = Date.now();
      return response.data;
    } catch (err) {
      error.value = err;
      console.error('Error fetching MAC addresses:', err);
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  // Get a specific MAC address by ID
  const getMacById = async (id) => {
    // First check if we have it in our cached data
    const cachedMac = macAddresses.value.find(mac => mac.id === parseInt(id));
    if (cachedMac) {
      return cachedMac;
    }

    // If not cached, fetch from API
    try {
      const response = await api.blockedmacs.get(id);
      
      // Update our cached data with the fetched MAC
      const existingIndex = macAddresses.value.findIndex(mac => mac.id === parseInt(id));
      if (existingIndex !== -1) {
        macAddresses.value[existingIndex] = response.data;
      } else {
        macAddresses.value.push(response.data);
      }
      
      return response.data;
    } catch (err) {
      error.value = err;
      console.error('Error fetching MAC address:', err);
      throw err;
    }
  };

  // Add a new MAC address
  const createMacAddress = async (macData) => {
    try {
      const response = await api.blockedmacs.create(macData);
      
      // Add to local cache
      macAddresses.value.unshift(response.data);
      
      return response.data;
    } catch (err) {
      error.value = err;
      console.error('Error creating MAC address:', err);
      throw err;
    }
  };

  // Delete a MAC address
  const deleteMacAddress = async (id) => {
    try {
      await api.blockedmacs.delete(id);
      
      // Remove from local cache
      const index = macAddresses.value.findIndex(mac => mac.id === parseInt(id));
      if (index !== -1) {
        macAddresses.value.splice(index, 1);
      }
      
      return true;
    } catch (err) {
      error.value = err;
      console.error('Error deleting MAC address:', err);
      throw err;
    }
  };

  // Clear cache and force refresh on next fetch
  const clearCache = () => {
    macAddresses.value = [];
    lastFetched.value = null;
    error.value = null;
  };

  // Initialize - fetch data if not already loaded
  const initialize = async () => {
    if (macAddresses.value.length === 0) {
      await fetchMacAddresses();
    }
  };

  // Search/filter MAC addresses
  const searchMacs = (query) => {
    if (!query) return macAddresses.value;
    
    const lowercaseQuery = query.toLowerCase();
    return macAddresses.value.filter(mac => 
      mac.clientMac?.toLowerCase().includes(lowercaseQuery) ||
      mac.reason?.toLowerCase().includes(lowercaseQuery)
    );
  };

  // Get MAC addresses with pagination
  const getPaginatedMacs = (page = 1, limit = 10) => {
    const startIndex = (page - 1) * limit;
    const endIndex = startIndex + limit;
    return macAddresses.value.slice(startIndex, endIndex);
  };

  return {
    // Reactive computed properties
    macAddresses: computed(() => macAddresses.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    totalMacs: computed(() => macAddresses.value.length),
    lastFetched: computed(() => lastFetched.value),
    isCacheValid: computed(() => isCacheValid()),

    // Methods
    fetchMacAddresses,
    getMacById,
    createMacAddress,
    deleteMacAddress,
    clearCache,
    initialize,
    searchMacs,
    getPaginatedMacs,

    // Utility methods
    refreshData: () => fetchMacAddresses(true),
  };
};