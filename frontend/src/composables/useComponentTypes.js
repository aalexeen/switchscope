import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state across all instances
const componentTypes = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);

/**
 * Composable for managing Component Types
 * Uses singleton pattern - state is shared across all component instances
 */
export function useComponentTypes() {
  const toast = useToast();

  /**
   * Fetch all component types from API
   * @param {boolean} force - Force refresh even if already loaded
   */
  const fetchComponentTypes = async (force = false) => {
    if (isLoading.value || (isInitialized.value && !force)) {
      return;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.componentTypes.getAll();
      componentTypes.value = response.data;
      isInitialized.value = true;

      if (force) {
        toast.success('Component types refreshed successfully');
      }
    } catch (err) {
      console.error('Error fetching component types:', err);
      error.value = err.message || 'Failed to load component types';
      toast.error('Failed to load component types');
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Initialize - fetch only if not already loaded
   */
  const initialize = async () => {
    if (!isInitialized.value) {
      await fetchComponentTypes(false);
    }
  };

  /**
   * Search component types by query
   * Searches in: name, code, displayName, description, categoryCode, categoryDisplayName
   * @param {string} query - Search query
   * @returns {Array} Filtered component types
   */
  const searchComponentTypes = (query) => {
    if (!query || query.trim() === '') {
      return componentTypes.value;
    }

    const searchTerm = query.toLowerCase().trim();

    return componentTypes.value.filter(type => {
      return (
        type.name?.toLowerCase().includes(searchTerm) ||
        type.code?.toLowerCase().includes(searchTerm) ||
        type.displayName?.toLowerCase().includes(searchTerm) ||
        type.description?.toLowerCase().includes(searchTerm) ||
        type.categoryCode?.toLowerCase().includes(searchTerm) ||
        type.categoryDisplayName?.toLowerCase().includes(searchTerm)
      );
    });
  };

  /**
   * Get component type by ID
   * @param {string} id - Component type UUID
   */
  const getComponentTypeById = async (id) => {
    try {
      const response = await api.componentTypes.get(id);
      return response.data;
    } catch (err) {
      console.error('Error fetching component type:', err);
      toast.error('Failed to load component type details');
      throw err;
    }
  };

  /**
   * Create new component type
   * @param {Object} data - Component type data
   */
  const createComponentType = async (data) => {
    try {
      const response = await api.componentTypes.create(data);
      const newType = response.data;
      componentTypes.value.push(newType);
      toast.success('Component type created successfully');
      return newType;
    } catch (err) {
      console.error('Error creating component type:', err);
      toast.error('Failed to create component type');
      throw err;
    }
  };

  /**
   * Update component type
   * @param {string} id - Component type UUID
   * @param {Object} data - Updated data
   */
  const updateComponentType = async (id, data) => {
    try {
      const response = await api.componentTypes.update(id, data);
      const updated = response.data;
      const index = componentTypes.value.findIndex(t => t.id === id);
      if (index !== -1) {
        componentTypes.value[index] = updated;
      }
      toast.success('Component type updated successfully');
      return updated;
    } catch (err) {
      console.error('Error updating component type:', err);
      toast.error('Failed to update component type');
      throw err;
    }
  };

  /**
   * Delete component type
   * @param {string} id - Component type UUID
   */
  const deleteComponentType = async (id) => {
    try {
      await api.componentTypes.delete(id);
      componentTypes.value = componentTypes.value.filter(t => t.id !== id);
      toast.success('Component type deleted successfully');
    } catch (err) {
      console.error('Error deleting component type:', err);
      toast.error('Failed to delete component type');
      throw err;
    }
  };

  // Computed properties
  const totalComponentTypes = computed(() => componentTypes.value.length);

  const activeComponentTypes = computed(() =>
    componentTypes.value.filter(type => type.active !== false)
  );

  const systemTypes = computed(() =>
    componentTypes.value.filter(type => type.systemType === true)
  );

  const userTypes = computed(() =>
    componentTypes.value.filter(type => type.systemType !== true)
  );

  const housingTypes = computed(() =>
    componentTypes.value.filter(type => type.housingComponent === true)
  );

  const connectivityTypes = computed(() =>
    componentTypes.value.filter(type => type.connectivityComponent === true)
  );

  const supportTypes = computed(() =>
    componentTypes.value.filter(type => type.supportComponent === true)
  );

  const moduleTypes = computed(() =>
    componentTypes.value.filter(type => type.moduleComponent === true)
  );

  const networkingTypes = computed(() =>
    componentTypes.value.filter(type => type.networkingEquipment === true)
  );

  return {
    // State
    componentTypes,
    isLoading,
    error,
    isInitialized,

    // Computed
    totalComponentTypes,
    activeComponentTypes,
    systemTypes,
    userTypes,
    housingTypes,
    connectivityTypes,
    supportTypes,
    moduleTypes,
    networkingTypes,

    // Methods
    fetchComponentTypes,
    initialize,
    searchComponentTypes,
    getComponentTypeById,
    createComponentType,
    updateComponentType,
    deleteComponentType
  };
}
