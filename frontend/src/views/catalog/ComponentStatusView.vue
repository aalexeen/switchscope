<script setup>
import ComponentStatusSearchBar from '@/components/component/catalog/ComponentStatusSearchBar.vue';
import ComponentStatusListingsTable from '@/components/component/catalog/ComponentStatusListingsTable.vue';
import { ref, computed } from 'vue';
import { useComponentStatuses } from '@/composables/useComponentStatuses';
import { useToast } from 'vue-toastification';

const toast = useToast();

// Get search functionality from composable
const { searchComponentStatuses, componentStatuses, totalComponentStatuses } = useComponentStatuses();

// Reactive state for search
const searchQuery = ref('');

// Computed property for filtered component statuses
const filteredStatuses = computed(() => {
  return searchComponentStatuses(searchQuery.value);
});

// Clear search
const clearSearch = () => {
  searchQuery.value = '';
};

// Event handlers
const handleView = (status) => {
  // TODO: Implement view details modal or navigate to details page
  toast.info(`View details for: ${status.name}`);
  console.log('View status:', status);
};

const handleEdit = (status) => {
  // TODO: Implement edit modal or navigate to edit page
  toast.info(`Edit: ${status.name}`);
  console.log('Edit status:', status);
};

const handleDelete = (status) => {
  // TODO: Implement delete confirmation dialog
  toast.warning(`Delete action for: ${status.name} (not implemented yet)`);
  console.log('Delete status:', status);
};
</script>

<template>
  <!-- Search Bar Component -->
  <ComponentStatusSearchBar
    v-model:search-query="searchQuery"
    :found-count="filteredStatuses.length"
    :total-count="totalComponentStatuses"
    @clear="clearSearch"
  />

  <!-- Component Status Listings Table -->
  <div>
    <ComponentStatusListingsTable
      :filtered-statuses="filteredStatuses"
      @view="handleView"
      @edit="handleEdit"
      @delete="handleDelete"
    />
  </div>
</template>
