import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state across all instances
const componentStatuses = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);

/**
 * Composable for managing Component Statuses
 * Uses singleton pattern - state is shared across all component instances
 */
export function useComponentStatuses() {
  const toast = useToast();

  /**
   * Fetch all component statuses from API
   * @param {boolean} force - Force refresh even if already loaded
   */
  const fetchComponentStatuses = async (force = false) => {
    if (isLoading.value || (isInitialized.value && !force)) {
      return;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.componentStatuses.getAll();
      componentStatuses.value = response.data;
      isInitialized.value = true;

      if (force) {
        toast.success('Component statuses refreshed successfully');
      }
    } catch (err) {
      console.error('Error fetching component statuses:', err);
      error.value = err.message || 'Failed to load component statuses';
      toast.error('Failed to load component statuses');
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Initialize - fetch only if not already loaded
   */
  const initialize = async () => {
    if (!isInitialized.value) {
      await fetchComponentStatuses(false);
    }
  };

  /**
   * Search component statuses by query
   * Searches in: name, code, displayName, description, lifecyclePhase
   * @param {string} query - Search query
   * @returns {Array} Filtered component statuses
   */
  const searchComponentStatuses = (query) => {
    if (!query || query.trim() === '') {
      return componentStatuses.value;
    }

    const searchTerm = query.toLowerCase().trim();

    return componentStatuses.value.filter(status => {
      return (
        status.name?.toLowerCase().includes(searchTerm) ||
        status.code?.toLowerCase().includes(searchTerm) ||
        status.displayName?.toLowerCase().includes(searchTerm) ||
        status.description?.toLowerCase().includes(searchTerm) ||
        status.lifecyclePhase?.toLowerCase().includes(searchTerm)
      );
    });
  };

  /**
   * Get component status by ID
   * @param {string} id - Component status UUID
   */
  const getComponentStatusById = async (id) => {
    try {
      const response = await api.componentStatuses.get(id);
      return response.data;
    } catch (err) {
      console.error('Error fetching component status:', err);
      toast.error('Failed to load component status details');
      throw err;
    }
  };

  /**
   * Create new component status
   * @param {Object} data - Component status data
   */
  const createComponentStatus = async (data) => {
    try {
      const response = await api.componentStatuses.create(data);
      const newStatus = response.data;
      componentStatuses.value.push(newStatus);
      toast.success('Component status created successfully');
      return newStatus;
    } catch (err) {
      console.error('Error creating component status:', err);
      toast.error('Failed to create component status');
      throw err;
    }
  };

  /**
   * Update component status
   * @param {string} id - Component status UUID
   * @param {Object} data - Updated data
   */
  const updateComponentStatus = async (id, data) => {
    try {
      const response = await api.componentStatuses.update(id, data);
      const updated = response.data;
      const index = componentStatuses.value.findIndex(s => s.id === id);
      if (index !== -1) {
        componentStatuses.value[index] = updated;
      }
      toast.success('Component status updated successfully');
      return updated;
    } catch (err) {
      console.error('Error updating component status:', err);
      toast.error('Failed to update component status');
      throw err;
    }
  };

  /**
   * Delete component status
   * @param {string} id - Component status UUID
   */
  const deleteComponentStatus = async (id) => {
    try {
      await api.componentStatuses.delete(id);
      componentStatuses.value = componentStatuses.value.filter(s => s.id !== id);
      toast.success('Component status deleted successfully');
    } catch (err) {
      console.error('Error deleting component status:', err);
      toast.error('Failed to delete component status');
      throw err;
    }
  };

  // Computed properties
  const totalComponentStatuses = computed(() => componentStatuses.value.length);

  const activeComponentStatuses = computed(() =>
    componentStatuses.value.filter(status => status.active !== false)
  );

  const availableStatuses = computed(() =>
    componentStatuses.value.filter(status => status.available === true)
  );

  const operationalStatuses = computed(() =>
    componentStatuses.value.filter(status => status.operational === true)
  );

  const physicallyPresentStatuses = computed(() =>
    componentStatuses.value.filter(status => status.physicallyPresent === true)
  );

  const inventoryStatuses = computed(() =>
    componentStatuses.value.filter(status => status.inInventory === true)
  );

  const installableStatuses = computed(() =>
    componentStatuses.value.filter(status => status.canAcceptInstallations === true)
  );

  const attentionRequiredStatuses = computed(() =>
    componentStatuses.value.filter(status => status.requiresAttention === true)
  );

  const transitionStatuses = computed(() =>
    componentStatuses.value.filter(status => status.inTransition === true)
  );

  return {
    // State
    componentStatuses,
    isLoading,
    error,
    isInitialized,

    // Computed
    totalComponentStatuses,
    activeComponentStatuses,
    availableStatuses,
    operationalStatuses,
    physicallyPresentStatuses,
    inventoryStatuses,
    installableStatuses,
    attentionRequiredStatuses,
    transitionStatuses,

    // Methods
    fetchComponentStatuses,
    initialize,
    searchComponentStatuses,
    getComponentStatusById,
    createComponentStatus,
    updateComponentStatus,
    deleteComponentStatus
  };
}
