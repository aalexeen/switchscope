<script setup>
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useToast } from 'vue-toastification';
import GenericSearchBar from '@/components/common/GenericSearchBar.vue';
import { entities } from '@/composables/entityRegistry';

// Import all composables
import { useComponentNatures } from '@/composables/useComponentNatures';
import { useComponentCategories } from '@/composables/useComponentCategories';
import { useComponentTypes } from '@/composables/useComponentTypes';
import { useComponentStatuses } from '@/composables/useComponentStatuses';
import { useComponentModels } from '@/composables/useComponentModels';
import { useLocationTypes } from '@/composables/useLocationTypes';
import { useInstallationStatuses } from '@/composables/useInstallationStatuses';
import { useInstallableTypes } from '@/composables/useInstallableTypes';

// Import Generic Table Components (NEW!)
import GenericListingsTable from '@/components/table/GenericListingsTable.vue';

// Import table configurations
import componentNaturesConfig from '@/configs/tables/componentNatures.config.js';
import componentCategoriesConfig from '@/configs/tables/componentCategories.config.js';
import componentTypesConfig from '@/configs/tables/componentTypes.config.js';
import componentStatusesConfig from '@/configs/tables/componentStatuses.config.js';
import componentModelsConfig from '@/configs/tables/componentModels.config.js';
import locationTypesConfig from '@/configs/tables/locationTypes.config.js';
import installationStatusesConfig from '@/configs/tables/installationStatuses.config.js';
import installableTypesConfig from '@/configs/tables/installableTypes.config.js';

// Old ListingsTable components - NO LONGER NEEDED!
// All entities have been migrated to GenericListingsTable + config files

/**
 * Helper function to capitalize first letter
 */
const capitalize = (str) => str.charAt(0).toUpperCase() + str.slice(1);

/**
 * Helper function to capitalize all words
 * "component natures" → "Component Natures"
 */
const capitalizeWords = (str) => str.split(' ').map(capitalize).join(' ');

/**
 * Helper function to format field names for display
 * "displayName" → "display name"
 */
const formatFieldName = (field) => {
  return field.replace(/([A-Z])/g, ' $1').trim().toLowerCase();
};

// Composable lookup object
const composableLookup = {
  componentNatures: useComponentNatures,
  componentCategories: useComponentCategories,
  componentTypes: useComponentTypes,
  componentStatuses: useComponentStatuses,
  componentModels: useComponentModels,
  locationTypes: useLocationTypes,
  installationStatuses: useInstallationStatuses,
  installableTypes: useInstallableTypes
};

// Table configuration lookup (NEW!)
const tableConfigLookup = {
  componentNatures: componentNaturesConfig,
  componentCategories: componentCategoriesConfig,
  componentTypes: componentTypesConfig,
  componentStatuses: componentStatusesConfig,
  componentModels: componentModelsConfig,
  locationTypes: locationTypesConfig,
  installationStatuses: installationStatusesConfig,
  installableTypes: installableTypesConfig
};

// Component lookup object - NO LONGER NEEDED!
// All entities now use GenericListingsTable with config files
// const componentLookup = { ... };

// Get entity configuration from route meta
const route = useRoute();
const entityKey = route.meta.entityKey;
const config = entities[entityKey];

// Get the composable for this entity
const composable = composableLookup[entityKey]();

// Dynamically get search function and total count
const searchFunctionName = `search${capitalize(config.entityName)}`;
const totalCountName = `total${capitalize(config.entityName)}`;
const searchFn = composable[searchFunctionName];
const totalCount = composable[totalCountName];

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
  console.log('View item:', item);
};

const handleEdit = (item) => {
  // TODO: Implement edit modal or navigate to edit page
  toast.info(`Edit: ${item.name}`);
  console.log('Edit item:', item);
};

const handleDelete = (item) => {
  // Check if item can be deleted (for system types, etc.)
  if (item.canBeDeleted === false || item.systemType === true || item.systemCategory === true) {
    toast.warning(`Cannot delete system item: ${item.name}`);
    return;
  }
  // TODO: Implement delete confirmation dialog
  toast.warning(`Delete action for: ${item.name} (not implemented yet)`);
  console.log('Delete item:', item);
};

// All entities use Generic Table now!
const ListingsTable = GenericListingsTable;

// Dynamic props for GenericListingsTable
const listingsProps = computed(() => {
  return {
    config: tableConfigLookup[entityKey],
    filteredData: filteredItems.value,
    composableData: {
      data: composable[config.entityName],
      isLoading: composable.isLoading,
      error: composable.error,
      fetchData: composable[`fetch${capitalize(config.entityName)}`],
      total: composable[totalCountName]
    }
  };
});

// Formatted search fields for display
const displaySearchFields = computed(() => {
  return config.searchFields.map(formatFieldName);
});
</script>

<template>
  <!-- Generic Search Bar -->
  <GenericSearchBar
    :entity-name="capitalizeWords(config.pluralDisplay)"
    :search-query="searchQuery"
    :found-count="filteredItems.length"
    :total-count="totalCount"
    :theme="config.theme"
    :intensity="config.themeIntensity"
    :search-fields="displaySearchFields"
    @update:search-query="searchQuery = $event"
    @clear="clearSearch"
  />

  <!-- Dynamic Listings Table Component -->
  <div>
    <component
      :is="ListingsTable"
      v-bind="listingsProps"
      @view="handleView"
      @edit="handleEdit"
      @delete="handleDelete"
    />
  </div>
</template>
