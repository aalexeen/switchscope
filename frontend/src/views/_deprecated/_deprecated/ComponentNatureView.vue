<script setup>
import GenericSearchBar from '@/components/common/GenericSearchBar.vue';
import ComponentNatureListingsTable from '@/components/component/catalog/ComponentNatureListingsTable.vue';
import { ref, computed } from 'vue';
import { useComponentNatures } from '@/composables/useComponentNatures';
import { useToast } from 'vue-toastification';

const toast = useToast();

// Get search functionality from composable
const { searchComponentNatures, totalComponentNatures } = useComponentNatures();

// Reactive state for search
const searchQuery = ref('');

// Computed property for filtered component natures
const filteredNatures = computed(() => {
  return searchComponentNatures(searchQuery.value);
});

// Clear search
const clearSearch = () => {
  searchQuery.value = '';
};

// Event handlers
const handleView = (nature) => {
  // TODO: Implement view details modal or navigate to details page
  toast.info(`View details for: ${nature.name}`);
  console.log('View nature:', nature);
};

const handleEdit = (nature) => {
  // TODO: Implement edit modal or navigate to edit page
  toast.info(`Edit: ${nature.name}`);
  console.log('Edit nature:', nature);
};

const handleDelete = (nature) => {
  // TODO: Implement delete confirmation dialog
  toast.warning(`Delete action for: ${nature.name} (not implemented yet)`);
  console.log('Delete nature:', nature);
};
</script>

<template>
  <!-- Search Bar Component -->
  <GenericSearchBar
    entity-name="Component Natures"
    :search-query="searchQuery"
    :found-count="filteredNatures.length"
    :total-count="totalComponentNatures"
    theme="indigo"
    intensity="500"
    :search-fields="['name', 'code', 'display name', 'description']"
    @update:search-query="searchQuery = $event"
    @clear="clearSearch"
  />

  <!-- Component Nature Listings Table -->
  <div>
    <ComponentNatureListingsTable
      :filtered-natures="filteredNatures"
      @view="handleView"
      @edit="handleEdit"
      @delete="handleDelete"
    />
  </div>
</template>
