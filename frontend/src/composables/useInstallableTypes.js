import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state across all instances
const installableTypes = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);

/**
 * Composable for managing Installable Types
 * Uses singleton pattern - state is shared across all component instances
 */
export function useInstallableTypes() {
  const toast = useToast();

  /**
   * Fetch all installable types from API
   * @param {boolean} force - Force refresh even if already loaded
   */
  const fetchInstallableTypes = async (force = false) => {
    if (isLoading.value || (isInitialized.value && !force)) {
      return;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.installableTypes.getAll();
      installableTypes.value = response.data;
      isInitialized.value = true;

      if (force) {
        toast.success('Installable types refreshed successfully');
      }
    } catch (err) {
      console.error('Error fetching installable types:', err);
      error.value = err.message || 'Failed to load installable types';
      toast.error('Failed to load installable types');
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Initialize - fetch only if not already loaded
   */
  const initialize = async () => {
    if (!isInitialized.value) {
      await fetchInstallableTypes(false);
    }
  };

  /**
   * Search installable types by query
   * Searches in: name, code, displayName, description, entityClass
   * @param {string} query - Search query
   * @returns {Array} Filtered installable types
   */
  const searchInstallableTypes = (query) => {
    if (!query || query.trim() === '') {
      return installableTypes.value;
    }

    const searchTerm = query.toLowerCase().trim();

    return installableTypes.value.filter(type => {
      return (
        type.name?.toLowerCase().includes(searchTerm) ||
        type.code?.toLowerCase().includes(searchTerm) ||
        type.displayName?.toLowerCase().includes(searchTerm) ||
        type.description?.toLowerCase().includes(searchTerm) ||
        type.entityClass?.toLowerCase().includes(searchTerm)
      );
    });
  };

  /**
   * Get installable type by ID
   * @param {string} id - Installable type UUID
   */
  const getInstallableTypeById = async (id) => {
    try {
      const response = await api.installableTypes.get(id);
      return response.data;
    } catch (err) {
      console.error('Error fetching installable type:', err);
      toast.error('Failed to load installable type details');
      throw err;
    }
  };

  /**
   * Create new installable type
   * @param {Object} data - Installable type data
   */
  const createInstallableType = async (data) => {
    try {
      const response = await api.installableTypes.create(data);
      const newType = response.data;
      installableTypes.value.push(newType);
      toast.success('Installable type created successfully');
      return newType;
    } catch (err) {
      console.error('Error creating installable type:', err);
      toast.error('Failed to create installable type');
      throw err;
    }
  };

  /**
   * Update installable type
   * @param {string} id - Installable type UUID
   * @param {Object} data - Updated data
   */
  const updateInstallableType = async (id, data) => {
    try {
      const response = await api.installableTypes.update(id, data);
      const updated = response.data;
      const index = installableTypes.value.findIndex(t => t.id === id);
      if (index !== -1) {
        installableTypes.value[index] = updated;
      }
      toast.success('Installable type updated successfully');
      return updated;
    } catch (err) {
      console.error('Error updating installable type:', err);
      toast.error('Failed to update installable type');
      throw err;
    }
  };

  /**
   * Delete installable type
   * @param {string} id - Installable type UUID
   */
  const deleteInstallableType = async (id) => {
    try {
      await api.installableTypes.delete(id);
      installableTypes.value = installableTypes.value.filter(t => t.id !== id);
      toast.success('Installable type deleted successfully');
    } catch (err) {
      console.error('Error deleting installable type:', err);
      toast.error('Failed to delete installable type');
      throw err;
    }
  };

  // Computed properties
  const totalInstallableTypes = computed(() => installableTypes.value.length);

  const activeTypes = computed(() =>
    installableTypes.value.filter(type => type.active !== false)
  );

  const deviceTypes = computed(() =>
    installableTypes.value.filter(type => type.deviceType === true)
  );

  const connectivityTypes = computed(() =>
    installableTypes.value.filter(type => type.connectivityType === true)
  );

  const supportTypes = computed(() =>
    installableTypes.value.filter(type => type.supportType === true)
  );

  const rackMountable = computed(() =>
    installableTypes.value.filter(type => type.requiresRackPosition === true)
  );

  const hotSwappableTypes = computed(() =>
    installableTypes.value.filter(type => type.hotSwappable === true)
  );

  const powerManagedTypes = computed(() =>
    installableTypes.value.filter(type => type.supportsPowerManagement === true)
  );

  const environmentalControlTypes = computed(() =>
    installableTypes.value.filter(type => type.requiresEnvironmentalControl === true)
  );

  const highPriorityTypes = computed(() =>
    installableTypes.value.filter(type => type.highPriority === true)
  );

  const lowPriorityTypes = computed(() =>
    installableTypes.value.filter(type => type.lowPriority === true)
  );

  const specialHandlingTypes = computed(() =>
    installableTypes.value.filter(type => type.requiresSpecialHandling === true)
  );

  return {
    // State
    installableTypes,
    isLoading,
    error,
    isInitialized,

    // Computed
    totalInstallableTypes,
    activeTypes,
    deviceTypes,
    connectivityTypes,
    supportTypes,
    rackMountable,
    hotSwappableTypes,
    powerManagedTypes,
    environmentalControlTypes,
    highPriorityTypes,
    lowPriorityTypes,
    specialHandlingTypes,

    // Methods
    fetchInstallableTypes,
    initialize,
    searchInstallableTypes,
    getInstallableTypeById,
    createInstallableType,
    updateInstallableType,
    deleteInstallableType
  };
}
