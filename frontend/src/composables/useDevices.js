// @/composables/useDevices.js
import { ref, computed } from "vue";
import api from '@/api/index';
import { useToast } from 'vue-toastification';

// Global reactive state - shared across all components
const devices = ref([]);
const isLoading = ref(false);
const lastFetched = ref(null);
const error = ref(null);

// Cache duration in milliseconds (5 minutes)
const CACHE_DURATION = 5 * 60 * 1000;

export const useDevices = () => {
  const toast = useToast();

  // Check if cache is still valid
  const isCacheValid = () => {
    if (!lastFetched.value) return false;
    return Date.now() - lastFetched.value < CACHE_DURATION;
  };

  // Fetch all devices from API
  const fetchDevices = async (forceRefresh = false) => {
    // Use cache if valid and not forcing refresh
    if (!forceRefresh && isCacheValid() && devices.value.length > 0) {
      return devices.value;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.devices.getAll();
      devices.value = response.data;
      lastFetched.value = Date.now();
      return response.data;
    } catch (err) {
      error.value = err;
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  // Get a specific device by ID
  const getDeviceById = async (id) => {
    // First check if we have it in our cached data
    const cachedDevice = devices.value.find(device => device.id === id);
    if (cachedDevice) {
      return cachedDevice;
    }

    // If not cached, fetch from API
    try {
      const response = await api.devices.get(id);

      // Update our cached data with the fetched device
      const existingIndex = devices.value.findIndex(device => device.id === id);
      if (existingIndex !== -1) {
        devices.value[existingIndex] = response.data;
      } else {
        devices.value.push(response.data);
      }

      return response.data;
    } catch (err) {
      error.value = err;
      throw err;
    }
  };

  // Add a new device
  const createDevice = async (deviceData) => {
    try {
      const response = await api.devices.create(deviceData);

      // Add to local cache
      devices.value.unshift(response.data);

      return response.data;
    } catch (err) {
      error.value = err;
      throw err;
    }
  };

  // Update a device
  const updateDevice = async (id, deviceData) => {
    try {
      const response = await api.devices.update(id, deviceData);

      // Update in local cache
      const index = devices.value.findIndex(device => device.id === id);
      if (index !== -1) {
        devices.value[index] = response.data;
      }

      return response.data;
    } catch (err) {
      error.value = err;
      throw err;
    }
  };

  // Delete a device
  const deleteDevice = async (id) => {
    try {
      await api.devices.delete(id);

      // Remove from local cache
      const index = devices.value.findIndex(device => device.id === id);
      if (index !== -1) {
        devices.value.splice(index, 1);
      }

      toast.success('Device deleted successfully');
      return true;
    } catch (err) {
      error.value = err;
      toast.error('Failed to delete device');
      throw err;
    }
  };

  // Clear cache and force refresh on next fetch
  const clearCache = () => {
    devices.value = [];
    lastFetched.value = null;
    error.value = null;
  };

  // Initialize - fetch data if not already loaded
  const initialize = async () => {
    if (devices.value.length === 0) {
      await fetchDevices();
    }
  };

  // Search/filter devices
  const searchDevices = (query) => {
    if (!query) return devices.value;

    const lowercaseQuery = query.toLowerCase();
    return devices.value.filter(device =>
      device.name?.toLowerCase().includes(lowercaseQuery) ||
      device.manufacturer?.toLowerCase().includes(lowercaseQuery) ||
      device.model?.toLowerCase().includes(lowercaseQuery) ||
      device.serialNumber?.toLowerCase().includes(lowercaseQuery) ||
      device.description?.toLowerCase().includes(lowercaseQuery) ||
      device.ipAddress?.toLowerCase().includes(lowercaseQuery) ||
      device.hostname?.toLowerCase().includes(lowercaseQuery) ||
      device.componentTypeDisplayName?.toLowerCase().includes(lowercaseQuery) ||
      device.componentStatusDisplayName?.toLowerCase().includes(lowercaseQuery)
    );
  };

  // Get devices with pagination
  const getPaginatedDevices = (page = 1, limit = 10) => {
    const startIndex = (page - 1) * limit;
    const endIndex = startIndex + limit;
    return devices.value.slice(startIndex, endIndex);
  };

  // Filter by type
  const filterByType = (typeId) => {
    if (!typeId) return devices.value;
    return devices.value.filter(device => device.componentTypeId === typeId);
  };

  // Filter by status
  const filterByStatus = (statusId) => {
    if (!statusId) return devices.value;
    return devices.value.filter(device => device.componentStatusId === statusId);
  };

  return {
    // Reactive computed properties
    devices: computed(() => devices.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    totalDevices: computed(() => devices.value.length),
    lastFetched: computed(() => lastFetched.value),
    isCacheValid: computed(() => isCacheValid()),

    // Methods
    fetchDevices,
    getDeviceById,
    createDevice,
    updateDevice,
    deleteDevice,
    clearCache,
    initialize,
    searchDevices,
    getPaginatedDevices,
    filterByType,
    filterByStatus,

    // Utility methods
    refreshData: () => fetchDevices(true),
  };
};
