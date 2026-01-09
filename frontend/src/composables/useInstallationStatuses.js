import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state across all instances
const installationStatuses = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);

/**
 * Composable for managing Installation Statuses
 * Uses singleton pattern - state is shared across all component instances
 */
export function useInstallationStatuses() {
  const toast = useToast();

  /**
   * Fetch all installation statuses from API
   * @param {boolean} force - Force refresh even if already loaded
   */
  const fetchInstallationStatuses = async (force = false) => {
    if (isLoading.value || (isInitialized.value && !force)) {
      return;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.installationStatuses.getAll();
      installationStatuses.value = response.data;
      isInitialized.value = true;

      if (force) {
        toast.success('Installation statuses refreshed successfully');
      }
    } catch (err) {
      console.error('Error fetching installation statuses:', err);
      error.value = err.message || 'Failed to load installation statuses';
      toast.error('Failed to load installation statuses');
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Initialize - fetch only if not already loaded
   */
  const initialize = async () => {
    if (!isInitialized.value) {
      await fetchInstallationStatuses(false);
    }
  };

  /**
   * Search installation statuses by query
   * Searches in: name, code, displayName, description, statusCategory, colorCategory
   * @param {string} query - Search query
   * @returns {Array} Filtered installation statuses
   */
  const searchInstallationStatuses = (query) => {
    if (!query || query.trim() === '') {
      return installationStatuses.value;
    }

    const searchTerm = query.toLowerCase().trim();

    return installationStatuses.value.filter(status => {
      return (
        status.name?.toLowerCase().includes(searchTerm) ||
        status.code?.toLowerCase().includes(searchTerm) ||
        status.displayName?.toLowerCase().includes(searchTerm) ||
        status.description?.toLowerCase().includes(searchTerm) ||
        status.statusCategory?.toLowerCase().includes(searchTerm) ||
        status.colorCategory?.toLowerCase().includes(searchTerm)
      );
    });
  };

  /**
   * Get installation status by ID
   * @param {string} id - Installation status UUID
   */
  const getInstallationStatusById = async (id) => {
    try {
      const response = await api.installationStatuses.get(id);
      return response.data;
    } catch (err) {
      console.error('Error fetching installation status:', err);
      toast.error('Failed to load installation status details');
      throw err;
    }
  };

  /**
   * Create new installation status
   * @param {Object} data - Installation status data
   */
  const createInstallationStatus = async (data) => {
    try {
      const response = await api.installationStatuses.create(data);
      const newStatus = response.data;
      installationStatuses.value.push(newStatus);
      toast.success('Installation status created successfully');
      return newStatus;
    } catch (err) {
      console.error('Error creating installation status:', err);
      toast.error('Failed to create installation status');
      throw err;
    }
  };

  /**
   * Update installation status
   * @param {string} id - Installation status UUID
   * @param {Object} data - Updated data
   */
  const updateInstallationStatus = async (id, data) => {
    try {
      const response = await api.installationStatuses.update(id, data);
      const updated = response.data;
      const index = installationStatuses.value.findIndex(s => s.id === id);
      if (index !== -1) {
        installationStatuses.value[index] = updated;
      }
      toast.success('Installation status updated successfully');
      return updated;
    } catch (err) {
      console.error('Error updating installation status:', err);
      toast.error('Failed to update installation status');
      throw err;
    }
  };

  /**
   * Delete installation status
   * @param {string} id - Installation status UUID
   */
  const deleteInstallationStatus = async (id) => {
    try {
      await api.installationStatuses.delete(id);
      installationStatuses.value = installationStatuses.value.filter(s => s.id !== id);
      toast.success('Installation status deleted successfully');
    } catch (err) {
      console.error('Error deleting installation status:', err);
      toast.error('Failed to delete installation status');
      throw err;
    }
  };

  // Computed properties
  const totalInstallationStatuses = computed(() => installationStatuses.value.length);

  const activeStatuses = computed(() =>
    installationStatuses.value.filter(status => status.active !== false)
  );

  const physicallyPresentStatuses = computed(() =>
    installationStatuses.value.filter(status => status.physicallyPresent === true)
  );

  const operationalStatuses = computed(() =>
    installationStatuses.value.filter(status => status.operational === true)
  );

  const attentionRequiredStatuses = computed(() =>
    installationStatuses.value.filter(status => status.requiresAttention === true)
  );

  const finalStatuses = computed(() =>
    installationStatuses.value.filter(status => status.finalStatus === true)
  );

  const errorStatuses = computed(() =>
    installationStatuses.value.filter(status => status.errorStatus === true)
  );

  const autoTransitionStatuses = computed(() =>
    installationStatuses.value.filter(status => status.hasAutoTransition === true)
  );

  const progressStatuses = computed(() =>
    installationStatuses.value.filter(status => status.progressStatus === true)
  );

  const successStatuses = computed(() =>
    installationStatuses.value.filter(status => status.successStatus === true)
  );

  const warningStatuses = computed(() =>
    installationStatuses.value.filter(status => status.warningStatus === true)
  );

  const terminalStatuses = computed(() =>
    installationStatuses.value.filter(status => status.terminalStatus === true)
  );

  return {
    // State
    installationStatuses,
    isLoading,
    error,
    isInitialized,

    // Computed
    totalInstallationStatuses,
    activeStatuses,
    physicallyPresentStatuses,
    operationalStatuses,
    attentionRequiredStatuses,
    finalStatuses,
    errorStatuses,
    autoTransitionStatuses,
    progressStatuses,
    successStatuses,
    warningStatuses,
    terminalStatuses,

    // Methods
    fetchInstallationStatuses,
    initialize,
    searchInstallationStatuses,
    getInstallationStatusById,
    createInstallationStatus,
    updateInstallationStatus,
    deleteInstallationStatus
  };
}
