// @/composables/useComponents.js
import { ref, computed } from "vue";
import api from '@/api/index';

// Global reactive state - shared across all components
const components = ref([]);
const isLoading = ref(false);
const lastFetched = ref(null);
const error = ref(null);

// Cache duration in milliseconds (5 minutes)
const CACHE_DURATION = 5 * 60 * 1000;

export const useComponents = () => {

  // Check if cache is still valid
  const isCacheValid = () => {
    if (!lastFetched.value) return false;
    return Date.now() - lastFetched.value < CACHE_DURATION;
  };

  // Fetch all components from API
  const fetchComponents = async (forceRefresh = false) => {
    // Use cache if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && components.value.length > 0) {
      return components.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.components.getAll();
      components.value = response.data;
      lastFetched.value = Date.now();
      return response.data;
    } catch (err) {
      error.value = err;
      console.error('Error fetching components:', err);
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  // Get a specific component by ID
  const getComponentById = async (id) => {
    // First check if we have it in our cached data
    const cachedComponent = components.value.find(comp => comp.id === id);
    if (cachedComponent) {
      return cachedComponent;
    }

    // If not cached, fetch from API
    try {
      const response = await api.components.get(id);

      // Update our cached data with the fetched component
      const existingIndex = components.value.findIndex(comp => comp.id === id);
      if (existingIndex !== -1) {
        components.value[existingIndex] = response.data;
      } else {
        components.value.push(response.data);
      }

      return response.data;
    } catch (err) {
      error.value = err;
      console.error('Error fetching component:', err);
      throw err;
    }
  };

  // Add a new component
  const createComponent = async (componentData) => {
    try {
      const response = await api.components.create(componentData);

      // Add to local cache
      components.value.unshift(response.data);

      return response.data;
    } catch (err) {
      error.value = err;
      console.error('Error creating component:', err);
      throw err;
    }
  };

  // Update a component
  const updateComponent = async (id, componentData) => {
    try {
      const response = await api.components.update(id, componentData);

      // Update in local cache
      const index = components.value.findIndex(comp => comp.id === id);
      if (index !== -1) {
        components.value[index] = response.data;
      }

      return response.data;
    } catch (err) {
      error.value = err;
      console.error('Error updating component:', err);
      throw err;
    }
  };

  // Delete a component
  const deleteComponent = async (id) => {
    try {
      await api.components.delete(id);

      // Remove from local cache
      const index = components.value.findIndex(comp => comp.id === id);
      if (index !== -1) {
        components.value.splice(index, 1);
      }

      return true;
    } catch (err) {
      error.value = err;
      console.error('Error deleting component:', err);
      throw err;
    }
  };

  // Clear cache and force refresh on next fetch
  const clearCache = () => {
    components.value = [];
    lastFetched.value = null;
    error.value = null;
  };

  // Initialize - fetch data if not already loaded
  const initialize = async () => {
    if (components.value.length === 0) {
      await fetchComponents();
    }
  };

  // Search/filter components
  const searchComponents = (query) => {
    if (!query) return components.value;

    const lowercaseQuery = query.toLowerCase();
    return components.value.filter(comp =>
      comp.name?.toLowerCase().includes(lowercaseQuery) ||
      comp.manufacturer?.toLowerCase().includes(lowercaseQuery) ||
      comp.model?.toLowerCase().includes(lowercaseQuery) ||
      comp.serialNumber?.toLowerCase().includes(lowercaseQuery) ||
      comp.description?.toLowerCase().includes(lowercaseQuery) ||
      comp.componentTypeDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      comp.componentStatusDisplayName?.toLowerCase().includes(lowercaseQuery)
    );
  };

  // Get components with pagination
  const getPaginatedComponents = (page = 1, limit = 10) => {
    const startIndex = (page - 1) * limit;
    const endIndex = startIndex + limit;
    return components.value.slice(startIndex, endIndex);
  };

  // Filter by type
  const filterByType = (typeId) => {
    if (!typeId) return components.value;
    return components.value.filter(comp => comp.componentTypeId === typeId);
  };

  // Filter by status
  const filterByStatus = (statusId) => {
    if (!statusId) return components.value;
    return components.value.filter(comp => comp.componentStatusId === statusId);
  };

  return {
    // Reactive computed properties
    components: computed(() => components.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    totalComponents: computed(() => components.value.length),
    lastFetched: computed(() => lastFetched.value),
    isCacheValid: computed(() => isCacheValid()),

    // Methods
    fetchComponents,
    getComponentById,
    createComponent,
    updateComponent,
    deleteComponent,
    clearCache,
    initialize,
    searchComponents,
    getPaginatedComponents,
    filterByType,
    filterByStatus,

    // Utility methods
    refreshData: () => fetchComponents(true),
  };
};
