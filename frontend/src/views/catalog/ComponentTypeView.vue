<script setup>
import ComponentTypeSearchBar from '@/components/component/catalog/ComponentTypeSearchBar.vue';
import ComponentTypeListingsTable from '@/components/component/catalog/ComponentTypeListingsTable.vue';
import { ref, computed } from 'vue';
import { useComponentTypes } from '@/composables/useComponentTypes';
import { useToast } from 'vue-toastification';

const toast = useToast();

// Get search functionality from composable
const { searchComponentTypes, componentTypes, totalComponentTypes } = useComponentTypes();

// Reactive state for search
const searchQuery = ref('');

// Computed property for filtered component types
const filteredTypes = computed(() => {
  return searchComponentTypes(searchQuery.value);
});

// Clear search
const clearSearch = () => {
  searchQuery.value = '';
};

// Event handlers
const handleView = (type) => {
  // TODO: Implement view details modal or navigate to details page
  toast.info(`View details for: ${type.name}`);
  console.log('View type:', type);
};

const handleEdit = (type) => {
  // TODO: Implement edit modal or navigate to edit page
  toast.info(`Edit: ${type.name}`);
  console.log('Edit type:', type);
};

const handleDelete = (type) => {
  if (!type.canBeDeleted) {
    toast.warning(`Cannot delete system type: ${type.name}`);
    return;
  }
  // TODO: Implement delete confirmation dialog
  toast.warning(`Delete action for: ${type.name} (not implemented yet)`);
  console.log('Delete type:', type);
};
</script>

<template>
  <!-- Search Bar Component -->
  <ComponentTypeSearchBar
    v-model:search-query="searchQuery"
    :found-count="filteredTypes.length"
    :total-count="totalComponentTypes"
    @clear="clearSearch"
  />

  <!-- Component Type Listings Table -->
  <div>
    <ComponentTypeListingsTable
      :filtered-types="filteredTypes"
      @view="handleView"
      @edit="handleEdit"
      @delete="handleDelete"
    />
  </div>
</template>
