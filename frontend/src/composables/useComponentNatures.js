import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state across all instances
const componentNatures = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);

/**
 * Composable for managing Component Natures
 * Uses singleton pattern - state is shared across all component instances
 */
export function useComponentNatures() {
  const toast = useToast();

  /**
   * Fetch all component natures from API
   * @param {boolean} force - Force refresh even if already loaded
   */
  const fetchComponentNatures = async (force = false) => {
    if (isLoading.value || (isInitialized.value && !force)) {
      return;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.componentNatures.getAll();
      componentNatures.value = response.data;
      isInitialized.value = true;

      if (force) {
        toast.success('Component natures refreshed successfully');
      }
    } catch (err) {
      console.error('Error fetching component natures:', err);
      error.value = err.message || 'Failed to load component natures';
      toast.error('Failed to load component natures');
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Initialize - fetch only if not already loaded
   */
  const initialize = async () => {
    if (!isInitialized.value) {
      await fetchComponentNatures(false);
    }
  };

  /**
   * Search component natures by query
   * Searches in: name, code, displayName, description
   * @param {string} query - Search query
   * @returns {Array} Filtered component natures
   */
  const searchComponentNatures = (query) => {
    if (!query || query.trim() === '') {
      return componentNatures.value;
    }

    const searchTerm = query.toLowerCase().trim();

    return componentNatures.value.filter(nature => {
      return (
        nature.name?.toLowerCase().includes(searchTerm) ||
        nature.code?.toLowerCase().includes(searchTerm) ||
        nature.displayName?.toLowerCase().includes(searchTerm) ||
        nature.description?.toLowerCase().includes(searchTerm)
      );
    });
  };

  /**
   * Get component nature by ID
   * @param {string} id - Component nature UUID
   */
  const getComponentNatureById = async (id) => {
    try {
      const response = await api.componentNatures.get(id);
      return response.data;
    } catch (err) {
      console.error('Error fetching component nature:', err);
      toast.error('Failed to load component nature details');
      throw err;
    }
  };

  /**
   * Create new component nature
   * @param {Object} data - Component nature data
   */
  const createComponentNature = async (data) => {
    try {
      const response = await api.componentNatures.create(data);
      const newNature = response.data;
      componentNatures.value.push(newNature);
      toast.success('Component nature created successfully');
      return newNature;
    } catch (err) {
      console.error('Error creating component nature:', err);
      toast.error('Failed to create component nature');
      throw err;
    }
  };

  /**
   * Update component nature
   * @param {string} id - Component nature UUID
   * @param {Object} data - Updated data
   */
  const updateComponentNature = async (id, data) => {
    try {
      const response = await api.componentNatures.update(id, data);
      const updated = response.data;
      const index = componentNatures.value.findIndex(n => n.id === id);
      if (index !== -1) {
        componentNatures.value[index] = updated;
      }
      toast.success('Component nature updated successfully');
      return updated;
    } catch (err) {
      console.error('Error updating component nature:', err);
      toast.error('Failed to update component nature');
      throw err;
    }
  };

  /**
   * Delete component nature
   * @param {string} id - Component nature UUID
   */
  const deleteComponentNature = async (id) => {
    try {
      await api.componentNatures.delete(id);
      componentNatures.value = componentNatures.value.filter(n => n.id !== id);
      toast.success('Component nature deleted successfully');
    } catch (err) {
      console.error('Error deleting component nature:', err);
      toast.error('Failed to delete component nature');
      throw err;
    }
  };

  // Computed properties
  const totalComponentNatures = computed(() => componentNatures.value.length);

  const activeComponentNatures = computed(() =>
    componentNatures.value.filter(nature => nature.active !== false)
  );

  return {
    // State
    componentNatures,
    isLoading,
    error,
    isInitialized,

    // Computed
    totalComponentNatures,
    activeComponentNatures,

    // Methods
    fetchComponentNatures,
    initialize,
    searchComponentNatures,
    getComponentNatureById,
    createComponentNature,
    updateComponentNature,
    deleteComponentNature
  };
}
