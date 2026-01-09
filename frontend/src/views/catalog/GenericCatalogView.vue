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

// Import all ListingsTable components (from their respective domain directories)
import ComponentNatureListingsTable from '@/components/component/catalog/ComponentNatureListingsTable.vue';
import ComponentCategoryListingsTable from '@/components/component/catalog/ComponentCategoryListingsTable.vue';
import ComponentTypeListingsTable from '@/components/component/catalog/ComponentTypeListingsTable.vue';
import ComponentStatusListingsTable from '@/components/component/catalog/ComponentStatusListingsTable.vue';
import ComponentModelListingsTable from '@/components/component/catalog/ComponentModelListingsTable.vue';
import LocationTypeListingsTable from '@/components/location/catalog/LocationTypeListingsTable.vue';
import InstallationStatusListingsTable from '@/components/installation/catalog/InstallationStatusListingsTable.vue';
import InstallableTypeListingsTable from '@/components/installation/catalog/InstallableTypeListingsTable.vue';

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

// Component lookup object
const componentLookup = {
  componentNatures: ComponentNatureListingsTable,
  componentCategories: ComponentCategoryListingsTable,
  componentTypes: ComponentTypeListingsTable,
  componentStatuses: ComponentStatusListingsTable,
  componentModels: ComponentModelListingsTable,
  locationTypes: LocationTypeListingsTable,
  installationStatuses: InstallationStatusListingsTable,
  installableTypes: InstallableTypeListingsTable
};

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

// Dynamic component for ListingsTable
const ListingsTable = computed(() => {
  return componentLookup[entityKey];
});

// Dynamic props for ListingsTable
// Each ListingsTable expects different prop names (e.g., filteredNatures, filteredTypes)
const listingsProps = computed(() => {
  const propName = `filtered${capitalize(config.entityName)}`;
  return {
    [propName]: filteredItems.value
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
