<script setup>
/**
 * GenericTableView - Universal Table View for ALL entities
 *
 * This component provides a unified interface for displaying any table:
 * - Catalog tables (Component Natures, Types, Statuses, etc.)
 * - Entity tables (Components, Devices, etc.)
 *
 * Usage:
 * 1. Define table configuration in configs/tables/
 * 2. Register in tableRegistry
 * 3. Add route with meta.tableKey
 */

import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useToast } from 'vue-toastification';
import GenericSearchBar from '@/components/common/GenericSearchBar.vue';
import GenericListingsTable from '@/components/table/GenericListingsTable.vue';

// Import table registry
import { tableRegistry, composableRegistry } from '@/configs/tables/tableRegistry';

/**
 * Helper function to capitalize first letter
 */
const capitalize = (str) => str.charAt(0).toUpperCase() + str.slice(1);

/**
 * Helper function to capitalize all words
 */
const capitalizeWords = (str) => str.split(' ').map(capitalize).join(' ');

/**
 * Helper function to convert camelCase to PascalCase
 */
const toPascalCase = (str) => str.charAt(0).toUpperCase() + str.slice(1);

/**
 * Helper function to format field names for display
 */
const formatFieldName = (field) => {
  return field.replace(/([A-Z])/g, ' $1').trim().toLowerCase();
};

// Get table configuration from route meta
const route = useRoute();
const tableKey = route.meta.tableKey;

if (!tableKey) {
  throw new Error('Route meta.tableKey is required for GenericTableView');
}

const config = tableRegistry[tableKey];

if (!config) {
  throw new Error(`Table configuration not found for key: ${tableKey}`);
}

// Get the composable for this table
const composableFactory = composableRegistry[tableKey];

if (!composableFactory) {
  throw new Error(`Composable not found for key: ${tableKey}`);
}

const composable = composableFactory();

// For composables, we need to use the tableKey (e.g., 'componentNatures')
// because that's how they're registered and how methods are named
const dataKey = tableKey; // e.g., 'componentNatures'
const entityNamePascal = toPascalCase(dataKey); // e.g., 'ComponentNatures'
const searchFunctionName = `search${entityNamePascal}`;
const totalCountName = `total${entityNamePascal}`;
const fetchFunctionName = `fetch${entityNamePascal}`;

// For delete, we need singular form (e.g., 'ComponentNature' not 'ComponentNatures')
// Remove trailing 's' if present for singular form
const entityNameSingular = entityNamePascal.endsWith('s')
  ? entityNamePascal.slice(0, -1)
  : entityNamePascal;
const deleteFunctionName = `delete${entityNameSingular}`;

const data = composable[dataKey];
const searchFn = composable[searchFunctionName];
const totalCount = composable[totalCountName];
const fetchData = composable[fetchFunctionName];
const deleteFn = composable[deleteFunctionName];
const isLoading = composable.isLoading;
const error = composable.error;

// Search state
const searchQuery = ref('');

// Filtered items using search function from composable
const filteredItems = computed(() => {
  return searchFn(searchQuery.value);
});

// Clear search
const clearSearch = () => {
  searchQuery.value = '';
};

// Event handlers with toast notifications
const toast = useToast();

const handleView = (item) => {
  // TODO: Implement view details modal or navigate to details page
  toast.info(`View details for: ${item.name}`);
};

const handleEdit = (item) => {
  // TODO: Implement edit modal or navigate to edit page
  toast.info(`Edit: ${item.name}`);
};

const handleDelete = async (item) => {
  // Check if item can be deleted (for system types, etc.)
  if (item.canBeDeleted === false || item.systemType === true || item.systemCategory === true) {
    toast.warning(`Cannot delete system item: ${item.name || item.displayName}`);
    return;
  }

  // Delete function is called after confirmation in CellActions.vue
  try {
    if (deleteFn) {
      await deleteFn(item.id);
      // Success toast is shown by composable
    } else {
      toast.error('Delete operation not available for this table');
    }
  } catch {
    // Error toast is shown by composable
  }
};

// Formatted search fields for display
const displaySearchFields = computed(() => {
  return (config.searchFields || ['name']).map(formatFieldName);
});

// Get theme configuration
const theme = config.theme || 'indigo';
const themeIntensity = config.themeIntensity || '500';

// Initialize data on mount
onMounted(async () => {
  try {
    // Try to use initialize if available (for singleton composables)
    if (composable.initialize) {
      await composable.initialize();
    } else if (fetchData) {
      await fetchData();
    }
  } catch {
    // Error handling - composable will show toast notification
  }
});
</script>

<template>
  <!-- Generic Search Bar -->
  <GenericSearchBar
    :entity-name="capitalizeWords(config.entityNamePlural || config.entityName)"
    :search-query="searchQuery"
    :found-count="filteredItems.length"
    :total-count="totalCount"
    :theme="theme"
    :intensity="themeIntensity"
    :search-fields="displaySearchFields"
    @update:search-query="searchQuery = $event"
    @clear="clearSearch"
  />

  <!-- Generic Listings Table -->
  <div>
    <GenericListingsTable
      :config="config"
      :filtered-data="filteredItems"
      :composable-data="{
        data: data,
        isLoading: isLoading,
        error: error,
        fetchData: fetchData,
        total: totalCount
      }"
      @view="handleView"
      @edit="handleEdit"
      @delete="handleDelete"
    />
  </div>
</template>
