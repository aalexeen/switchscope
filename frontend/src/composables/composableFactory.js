/**
 * Composable Factory - Generic factory for creating catalog entity composables
 *
 * This factory eliminates code duplication across catalog composables by:
 * 1. Accepting entity configuration from entityRegistry
 * 2. Generating singleton state (shared across all component instances)
 * 3. Dynamically creating CRUD methods with consistent patterns
 * 4. Generating computed properties (basic + custom filters)
 * 5. Maintaining 100% backward compatibility with existing composables
 *
 * Usage:
 * import { createEntityComposable } from '@/composables/composableFactory';
 * import { entities } from '@/composables/entityRegistry';
 *
 * export const useComponentNatures = createEntityComposable(entities.componentNatures);
 */

import { ref, computed } from 'vue';
import api from '@/api';
import { useToast } from 'vue-toastification';

/**
 * Helper function to capitalize first letter
 * @param {string} str - String to capitalize
 * @returns {string} Capitalized string
 */
function capitalize(str) {
  return str.charAt(0).toUpperCase() + str.slice(1);
}

/**
 * Create an entity composable from configuration
 *
 * @param {Object} config - Entity configuration object
 * @param {string} config.entityName - camelCase plural (e.g., 'componentNatures')
 * @param {string} config.entityNameSingular - camelCase singular (e.g., 'componentNature')
 * @param {string} config.singularDisplay - lowercase singular for messages
 * @param {string} config.pluralDisplay - lowercase plural for messages
 * @param {string} config.apiKey - Key in api object
 * @param {string[]} config.searchFields - Array of fields to search
 * @param {Object} config.computedFilters - Optional custom computed properties
 * @returns {Function} Composable function
 */
export function createEntityComposable(config) {
  const {
    entityName,
    entityNameSingular,
    singularDisplay,
    pluralDisplay,
    apiKey,
    searchFields,
    computedFilters = {}
  } = config;

  // Generate method/computed names
  const capitalizedPlural = capitalize(entityName);
  const capitalizedSingular = capitalize(entityNameSingular);

  // Singleton state - created once and shared across all instances
  const entities = ref([]);
  const isLoading = ref(false);
  const error = ref(null);
  const isInitialized = ref(false);

  // Return the composable function
  return function useEntity() {
    const toast = useToast();

    /**
     * Fetch all entities from API
     * @param {boolean} force - Force refresh even if already loaded
     */
    const fetchEntities = async (force = false) => {
      if (isLoading.value || (isInitialized.value && !force)) {
        return;
      }

      isLoading.value = true;
      error.value = null;

      try {
        const response = await api[apiKey].getAll();
        entities.value = response.data;
        isInitialized.value = true;

        if (force) {
          toast.success(`${capitalizedPlural} refreshed successfully`);
        }
      } catch (err) {
        console.error(`Error fetching ${pluralDisplay}:`, err);
        error.value = err.message || `Failed to load ${pluralDisplay}`;
        toast.error(`Failed to load ${pluralDisplay}`);
      } finally {
        isLoading.value = false;
      }
    };

    /**
     * Initialize - fetch only if not already loaded
     */
    const initialize = async () => {
      if (!isInitialized.value) {
        await fetchEntities(false);
      }
    };

    /**
     * Get nested value from object using dot notation
     * @param {Object} obj - Source object
     * @param {string} path - Path like 'componentType.displayName'
     * @returns {*} Value at path or undefined
     */
    const getNestedValue = (obj, path) => {
      if (!path.includes('.')) return obj[path];
      return path.split('.').reduce((current, prop) => current?.[prop], obj);
    };

    /**
     * Search entities by query
     * Supports nested paths in searchFields like 'componentType.displayName'
     * @param {string} query - Search query
     * @returns {Array} Filtered entities
     */
    const searchEntities = (query) => {
      if (!query || query.trim() === '') {
        return entities.value;
      }

      const searchTerm = query.toLowerCase().trim();

      return entities.value.filter(entity => {
        return searchFields.some(field => {
          const value = getNestedValue(entity, field);
          return value && String(value).toLowerCase().includes(searchTerm);
        });
      });
    };

    /**
     * Get entity by ID
     * @param {string} id - Entity UUID
     */
    const getEntityById = async (id) => {
      try {
        const response = await api[apiKey].get(id);
        return response.data;
      } catch (err) {
        console.error(`Error fetching ${singularDisplay}:`, err);
        toast.error(`Failed to load ${singularDisplay} details`);
        throw err;
      }
    };

    /**
     * Create new entity
     * @param {Object} data - Entity data
     */
    const createEntity = async (data) => {
      try {
        const response = await api[apiKey].create(data);
        const newEntity = response.data;
        entities.value.push(newEntity);
        toast.success(`${capitalizedSingular} created successfully`);
        return newEntity;
      } catch (err) {
        console.error(`Error creating ${singularDisplay}:`, err);
        toast.error(`Failed to create ${singularDisplay}`);
        throw err;
      }
    };

    /**
     * Update entity
     * @param {string} id - Entity UUID
     * @param {Object} data - Updated data
     */
    const updateEntity = async (id, data) => {
      console.log(`[Composable] updateEntity called for ${apiKey}, id:`, id);
      try {
        console.log(`[Composable] Calling api.${apiKey}.update()`);
        const response = await api[apiKey].update(id, data);
        console.log(`[Composable] API response:`, response);
        const updated = response.data;
        const index = entities.value.findIndex(e => e.id === id);
        if (index !== -1) {
          entities.value[index] = updated;
        }
        toast.success(`${capitalizedSingular} updated successfully`);
        return updated;
      } catch (err) {
        console.error(`Error updating ${singularDisplay}:`, err);
        toast.error(`Failed to update ${singularDisplay}`);
        throw err;
      }
    };

    /**
     * Delete entity
     * @param {string} id - Entity UUID
     */
    const deleteEntity = async (id) => {
      try {
        await api[apiKey].delete(id);
        entities.value = entities.value.filter(e => e.id !== id);
        toast.success(`${capitalizedSingular} deleted successfully`);
      } catch (err) {
        console.error(`Error deleting ${singularDisplay}:`, err);
        toast.error(`Failed to delete ${singularDisplay}`);
        throw err;
      }
    };

    // Build result object with dynamic property names
    const result = {
      // State
      [entityName]: entities,
      isLoading,
      error,
      isInitialized
    };

    // Add methods with dynamic names
    result[`fetch${capitalizedPlural}`] = fetchEntities;
    result.initialize = initialize;
    result[`search${capitalizedPlural}`] = searchEntities;
    result[`get${capitalizedSingular}ById`] = getEntityById;
    result[`create${capitalizedSingular}`] = createEntity;
    result[`update${capitalizedSingular}`] = updateEntity;
    result[`delete${capitalizedSingular}`] = deleteEntity;

    // Add basic computed properties
    result[`total${capitalizedPlural}`] = computed(() => entities.value.length);
    result[`active${capitalizedPlural}`] = computed(() =>
      entities.value.filter(entity => entity.active !== false)
    );

    // Add custom computed filters from config
    Object.entries(computedFilters).forEach(([name, filterFn]) => {
      result[name] = computed(() => entities.value.filter(filterFn));
    });

    return result;
  };
}
