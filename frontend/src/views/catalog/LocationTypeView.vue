<script setup>
import LocationTypeSearchBar from '@/components/location/catalog/LocationTypeSearchBar.vue';
import LocationTypeListingsTable from '@/components/location/catalog/LocationTypeListingsTable.vue';
import { ref, computed } from 'vue';
import { useLocationTypes } from '@/composables/useLocationTypes';
import { useToast } from 'vue-toastification';

const toast = useToast();

// Get search functionality from composable
const { searchLocationTypes, locationTypes, totalLocationTypes } = useLocationTypes();

// Reactive state for search
const searchQuery = ref('');

// Computed property for filtered location types
const filteredTypes = computed(() => {
  return searchLocationTypes(searchQuery.value);
});

// Clear search
const clearSearch = () => {
  searchQuery.value = '';
};

// Event handlers
const handleView = (locationType) => {
  // TODO: Implement view details modal or navigate to details page
  toast.info(`View details for: ${locationType.name}`);
  console.log('View location type:', locationType);
};

const handleEdit = (locationType) => {
  // TODO: Implement edit modal or navigate to edit page
  toast.info(`Edit: ${locationType.name}`);
  console.log('Edit location type:', locationType);
};

const handleDelete = (locationType) => {
  // TODO: Implement delete confirmation dialog
  toast.warning(`Delete action for: ${locationType.name} (not implemented yet)`);
  console.log('Delete location type:', locationType);
};
</script>

<template>
  <!-- Search Bar Component -->
  <LocationTypeSearchBar
    v-model:search-query="searchQuery"
    :found-count="filteredTypes.length"
    :total-count="totalLocationTypes"
    @clear="clearSearch"
  />

  <!-- Location Type Listings Table -->
  <div>
    <LocationTypeListingsTable
      :filtered-types="filteredTypes"
      @view="handleView"
      @edit="handleEdit"
      @delete="handleDelete"
    />
  </div>
</template>
