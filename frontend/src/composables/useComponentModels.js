import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state across all instances
const componentModels = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);

/**
 * Composable for managing Component Models
 * Uses singleton pattern - state is shared across all component instances
 */
export function useComponentModels() {
  const toast = useToast();

  /**
   * Fetch all component models from API
   * @param {boolean} force - Force refresh even if already loaded
   */
  const fetchComponentModels = async (force = false) => {
    if (isLoading.value || (isInitialized.value && !force)) {
      return;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.componentModels.getAll();
      componentModels.value = response.data;
      isInitialized.value = true;

      if (force) {
        toast.success('Component models refreshed successfully');
      }
    } catch (err) {
      console.error('Error fetching component models:', err);
      error.value = err.message || 'Failed to load component models';
      toast.error('Failed to load component models');
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Initialize - fetch only if not already loaded
   */
  const initialize = async () => {
    if (!isInitialized.value) {
      await fetchComponentModels(false);
    }
  };

  /**
   * Search component models by query
   * Searches in: name, manufacturer, modelNumber, partNumber, description
   * @param {string} query - Search query
   * @returns {Array} Filtered component models
   */
  const searchComponentModels = (query) => {
    if (!query || query.trim() === '') {
      return componentModels.value;
    }

    const searchTerm = query.toLowerCase().trim();

    return componentModels.value.filter(model => {
      return (
        model.name?.toLowerCase().includes(searchTerm) ||
        model.manufacturer?.toLowerCase().includes(searchTerm) ||
        model.modelNumber?.toLowerCase().includes(searchTerm) ||
        model.modelDesignation?.toLowerCase().includes(searchTerm) ||
        model.partNumber?.toLowerCase().includes(searchTerm) ||
        model.sku?.toLowerCase().includes(searchTerm) ||
        model.description?.toLowerCase().includes(searchTerm) ||
        model.componentTypeCode?.toLowerCase().includes(searchTerm) ||
        model.componentTypeDisplayName?.toLowerCase().includes(searchTerm)
      );
    });
  };

  /**
   * Get component model by ID
   * @param {string} id - Component model UUID
   */
  const getComponentModelById = async (id) => {
    try {
      const response = await api.componentModels.get(id);
      return response.data;
    } catch (err) {
      console.error('Error fetching component model:', err);
      toast.error('Failed to load component model details');
      throw err;
    }
  };

  /**
   * Create new component model
   * @param {Object} data - Component model data
   */
  const createComponentModel = async (data) => {
    try {
      const response = await api.componentModels.create(data);
      const newModel = response.data;
      componentModels.value.push(newModel);
      toast.success('Component model created successfully');
      return newModel;
    } catch (err) {
      console.error('Error creating component model:', err);
      toast.error('Failed to create component model');
      throw err;
    }
  };

  /**
   * Update component model
   * @param {string} id - Component model UUID
   * @param {Object} data - Updated data
   */
  const updateComponentModel = async (id, data) => {
    try {
      const response = await api.componentModels.update(id, data);
      const updated = response.data;
      const index = componentModels.value.findIndex(m => m.id === id);
      if (index !== -1) {
        componentModels.value[index] = updated;
      }
      toast.success('Component model updated successfully');
      return updated;
    } catch (err) {
      console.error('Error updating component model:', err);
      toast.error('Failed to update component model');
      throw err;
    }
  };

  /**
   * Delete component model
   * @param {string} id - Component model UUID
   */
  const deleteComponentModel = async (id) => {
    try {
      await api.componentModels.delete(id);
      componentModels.value = componentModels.value.filter(m => m.id !== id);
      toast.success('Component model deleted successfully');
    } catch (err) {
      console.error('Error deleting component model:', err);
      toast.error('Failed to delete component model');
      throw err;
    }
  };

  // Computed properties
  const totalComponentModels = computed(() => componentModels.value.length);

  const activeComponentModels = computed(() =>
    componentModels.value.filter(model => model.active !== false)
  );

  const discontinuedModels = computed(() =>
    componentModels.value.filter(model => model.discontinued === true)
  );

  const endOfLifeModels = computed(() =>
    componentModels.value.filter(model => model.endOfLife === true)
  );

  const verifiedModels = computed(() =>
    componentModels.value.filter(model => model.verified === true)
  );

  return {
    // State
    componentModels,
    isLoading,
    error,
    isInitialized,

    // Computed
    totalComponentModels,
    activeComponentModels,
    discontinuedModels,
    endOfLifeModels,
    verifiedModels,

    // Methods
    fetchComponentModels,
    initialize,
    searchComponentModels,
    getComponentModelById,
    createComponentModel,
    updateComponentModel,
    deleteComponentModel
  };
}
