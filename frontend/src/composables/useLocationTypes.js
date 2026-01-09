import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state across all instances
const locationTypes = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);

/**
 * Composable for managing Location Types
 * Uses singleton pattern - state is shared across all component instances
 */
export function useLocationTypes() {
  const toast = useToast();

  /**
   * Fetch all location types from API
   * @param {boolean} force - Force refresh even if already loaded
   */
  const fetchLocationTypes = async (force = false) => {
    if (isLoading.value || (isInitialized.value && !force)) {
      return;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.locationTypes.getAll();
      locationTypes.value = response.data;
      isInitialized.value = true;

      if (force) {
        toast.success('Location types refreshed successfully');
      }
    } catch (err) {
      console.error('Error fetching location types:', err);
      error.value = err.message || 'Failed to load location types';
      toast.error('Failed to load location types');
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Initialize - fetch only if not already loaded
   */
  const initialize = async () => {
    if (!isInitialized.value) {
      await fetchLocationTypes(false);
    }
  };

  /**
   * Search location types by query
   * Searches in: name, code, displayName, description, locationCategory
   * @param {string} query - Search query
   * @returns {Array} Filtered location types
   */
  const searchLocationTypes = (query) => {
    if (!query || query.trim() === '') {
      return locationTypes.value;
    }

    const searchTerm = query.toLowerCase().trim();

    return locationTypes.value.filter(type => {
      return (
        type.name?.toLowerCase().includes(searchTerm) ||
        type.code?.toLowerCase().includes(searchTerm) ||
        type.displayName?.toLowerCase().includes(searchTerm) ||
        type.description?.toLowerCase().includes(searchTerm) ||
        type.locationCategory?.toLowerCase().includes(searchTerm)
      );
    });
  };

  /**
   * Get location type by ID
   * @param {string} id - Location type UUID
   */
  const getLocationTypeById = async (id) => {
    try {
      const response = await api.locationTypes.get(id);
      return response.data;
    } catch (err) {
      console.error('Error fetching location type:', err);
      toast.error('Failed to load location type details');
      throw err;
    }
  };

  /**
   * Create new location type
   * @param {Object} data - Location type data
   */
  const createLocationType = async (data) => {
    try {
      const response = await api.locationTypes.create(data);
      const newType = response.data;
      locationTypes.value.push(newType);
      toast.success('Location type created successfully');
      return newType;
    } catch (err) {
      console.error('Error creating location type:', err);
      toast.error('Failed to create location type');
      throw err;
    }
  };

  /**
   * Update location type
   * @param {string} id - Location type UUID
   * @param {Object} data - Updated data
   */
  const updateLocationType = async (id, data) => {
    try {
      const response = await api.locationTypes.update(id, data);
      const updated = response.data;
      const index = locationTypes.value.findIndex(t => t.id === id);
      if (index !== -1) {
        locationTypes.value[index] = updated;
      }
      toast.success('Location type updated successfully');
      return updated;
    } catch (err) {
      console.error('Error updating location type:', err);
      toast.error('Failed to update location type');
      throw err;
    }
  };

  /**
   * Delete location type
   * @param {string} id - Location type UUID
   */
  const deleteLocationType = async (id) => {
    try {
      await api.locationTypes.delete(id);
      locationTypes.value = locationTypes.value.filter(t => t.id !== id);
      toast.success('Location type deleted successfully');
    } catch (err) {
      console.error('Error deleting location type:', err);
      toast.error('Failed to delete location type');
      throw err;
    }
  };

  // Computed properties
  const totalLocationTypes = computed(() => locationTypes.value.length);

  const activeLocationTypes = computed(() =>
    locationTypes.value.filter(type => type.active !== false)
  );

  const topLevelTypes = computed(() =>
    locationTypes.value.filter(type => type.topLevel === true)
  );

  const middleLevelTypes = computed(() =>
    locationTypes.value.filter(type => type.middleLevel === true)
  );

  const bottomLevelTypes = computed(() =>
    locationTypes.value.filter(type => type.bottomLevel === true)
  );

  const physicalTypes = computed(() =>
    locationTypes.value.filter(type => type.physicalLocation === true)
  );

  const virtualTypes = computed(() =>
    locationTypes.value.filter(type => type.virtual === true)
  );

  const buildingLikeTypes = computed(() =>
    locationTypes.value.filter(type => type.buildingLike === true)
  );

  const roomLikeTypes = computed(() =>
    locationTypes.value.filter(type => type.roomLike === true)
  );

  const rackLikeTypes = computed(() =>
    locationTypes.value.filter(type => type.rackLike === true)
  );

  const datacenterLikeTypes = computed(() =>
    locationTypes.value.filter(type => type.datacenterLike === true)
  );

  const secureTypes = computed(() =>
    locationTypes.value.filter(type => type.secureLocation === true)
  );

  return {
    // State
    locationTypes,
    isLoading,
    error,
    isInitialized,

    // Computed
    totalLocationTypes,
    activeLocationTypes,
    topLevelTypes,
    middleLevelTypes,
    bottomLevelTypes,
    physicalTypes,
    virtualTypes,
    buildingLikeTypes,
    roomLikeTypes,
    rackLikeTypes,
    datacenterLikeTypes,
    secureTypes,

    // Methods
    fetchLocationTypes,
    initialize,
    searchLocationTypes,
    getLocationTypeById,
    createLocationType,
    updateLocationType,
    deleteLocationType
  };
}
