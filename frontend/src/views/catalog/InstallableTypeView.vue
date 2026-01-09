<script setup>
import InstallableTypeSearchBar from '@/components/installation/catalog/InstallableTypeSearchBar.vue';
import InstallableTypeListingsTable from '@/components/installation/catalog/InstallableTypeListingsTable.vue';
import { ref, computed } from 'vue';
import { useInstallableTypes } from '@/composables/useInstallableTypes';
import { useToast } from 'vue-toastification';

const toast = useToast();

// Get search functionality from composable
const { searchInstallableTypes, installableTypes, totalInstallableTypes } = useInstallableTypes();

// Reactive state for search
const searchQuery = ref('');

// Computed property for filtered installable types
const filteredTypes = computed(() => {
  return searchInstallableTypes(searchQuery.value);
});

// Clear search
const clearSearch = () => {
  searchQuery.value = '';
};

// Event handlers
const handleView = (installableType) => {
  // TODO: Implement view details modal or navigate to details page
  toast.info(`View details for: ${installableType.name}`);
  console.log('View installable type:', installableType);
};

const handleEdit = (installableType) => {
  // TODO: Implement edit modal or navigate to edit page
  toast.info(`Edit: ${installableType.name}`);
  console.log('Edit installable type:', installableType);
};

const handleDelete = (installableType) => {
  // TODO: Implement delete confirmation dialog
  toast.warning(`Delete action for: ${installableType.name} (not implemented yet)`);
  console.log('Delete installable type:', installableType);
};
</script>

<template>
  <!-- Search Bar Component -->
  <InstallableTypeSearchBar
    :search-query="searchQuery"
    :found-count="filteredTypes.length"
    :total-count="totalInstallableTypes"
    @update:search-query="searchQuery = $event"
    @clear="clearSearch"
  />

  <!-- Installable Type Listings Table -->
  <InstallableTypeListingsTable
    :filtered-types="filteredTypes"
    @view="handleView"
    @edit="handleEdit"
    @delete="handleDelete"
  />
</template>
