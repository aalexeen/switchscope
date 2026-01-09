import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

// Shared state across all instances
const componentCategories = ref([]);
const isLoading = ref(false);
const error = ref(null);
const isInitialized = ref(false);

/**
 * Composable for managing Component Categories
 * Uses singleton pattern - state is shared across all component instances
 */
export function useComponentCategories() {
  const toast = useToast();

  /**
   * Fetch all component categories from API
   * @param {boolean} force - Force refresh even if already loaded
   */
  const fetchComponentCategories = async (force = false) => {
    if (isLoading.value || (isInitialized.value && !force)) {
      return;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await api.componentCategories.getAll();
      componentCategories.value = response.data;
      isInitialized.value = true;

      if (force) {
        toast.success('Component categories refreshed successfully');
      }
    } catch (err) {
      console.error('Error fetching component categories:', err);
      error.value = err.message || 'Failed to load component categories';
      toast.error('Failed to load component categories');
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Initialize - fetch only if not already loaded
   */
  const initialize = async () => {
    if (!isInitialized.value) {
      await fetchComponentCategories(false);
    }
  };

  /**
   * Search component categories by query
   * Searches in: name, code, displayName, description
   * @param {string} query - Search query
   * @returns {Array} Filtered component categories
   */
  const searchComponentCategories = (query) => {
    if (!query || query.trim() === '') {
      return componentCategories.value;
    }

    const searchTerm = query.toLowerCase().trim();

    return componentCategories.value.filter(category => {
      return (
        category.name?.toLowerCase().includes(searchTerm) ||
        category.code?.toLowerCase().includes(searchTerm) ||
        category.displayName?.toLowerCase().includes(searchTerm) ||
        category.description?.toLowerCase().includes(searchTerm)
      );
    });
  };

  /**
   * Get component category by ID
   * @param {string} id - Component category UUID
   */
  const getComponentCategoryById = async (id) => {
    try {
      const response = await api.componentCategories.get(id);
      return response.data;
    } catch (err) {
      console.error('Error fetching component category:', err);
      toast.error('Failed to load component category details');
      throw err;
    }
  };

  /**
   * Create new component category
   * @param {Object} data - Component category data
   */
  const createComponentCategory = async (data) => {
    try {
      const response = await api.componentCategories.create(data);
      const newCategory = response.data;
      componentCategories.value.push(newCategory);
      toast.success('Component category created successfully');
      return newCategory;
    } catch (err) {
      console.error('Error creating component category:', err);
      toast.error('Failed to create component category');
      throw err;
    }
  };

  /**
   * Update component category
   * @param {string} id - Component category UUID
   * @param {Object} data - Updated data
   */
  const updateComponentCategory = async (id, data) => {
    try {
      const response = await api.componentCategories.update(id, data);
      const updated = response.data;
      const index = componentCategories.value.findIndex(c => c.id === id);
      if (index !== -1) {
        componentCategories.value[index] = updated;
      }
      toast.success('Component category updated successfully');
      return updated;
    } catch (err) {
      console.error('Error updating component category:', err);
      toast.error('Failed to update component category');
      throw err;
    }
  };

  /**
   * Delete component category
   * @param {string} id - Component category UUID
   */
  const deleteComponentCategory = async (id) => {
    try {
      await api.componentCategories.delete(id);
      componentCategories.value = componentCategories.value.filter(c => c.id !== id);
      toast.success('Component category deleted successfully');
    } catch (err) {
      console.error('Error deleting component category:', err);
      toast.error('Failed to delete component category');
      throw err;
    }
  };

  // Computed properties
  const totalComponentCategories = computed(() => componentCategories.value.length);

  const activeComponentCategories = computed(() =>
    componentCategories.value.filter(category => category.active !== false)
  );

  const systemCategories = computed(() =>
    componentCategories.value.filter(category => category.systemCategory === true)
  );

  const userCategories = computed(() =>
    componentCategories.value.filter(category => category.systemCategory !== true)
  );

  const infrastructureCategories = computed(() =>
    componentCategories.value.filter(category => category.infrastructure === true)
  );

  return {
    // State
    componentCategories,
    isLoading,
    error,
    isInitialized,

    // Computed
    totalComponentCategories,
    activeComponentCategories,
    systemCategories,
    userCategories,
    infrastructureCategories,

    // Methods
    fetchComponentCategories,
    initialize,
    searchComponentCategories,
    getComponentCategoryById,
    createComponentCategory,
    updateComponentCategory,
    deleteComponentCategory
  };
}
