<script setup>
import GenericSearchBar from '@/components/common/GenericSearchBar.vue';
import ComponentModelListingsTable from '@/components/component/catalog/ComponentModelListingsTable.vue';
import { ref, computed } from 'vue';
import { useComponentModels } from '@/composables/useComponentModels';
import { useToast } from 'vue-toastification';

const toast = useToast();

// Get search functionality from composable
const { searchComponentModels, totalComponentModels } = useComponentModels();

// Reactive state for search
const searchQuery = ref('');

// Computed property for filtered component models
const filteredModels = computed(() => {
  return searchComponentModels(searchQuery.value);
});

// Clear search
const clearSearch = () => {
  searchQuery.value = '';
};

// Event handlers
const handleView = (model) => {
  // TODO: Implement view details modal or navigate to details page
  toast.info(`View details for: ${model.modelDesignation || model.name}`);
  console.log('View model:', model);
};

const handleEdit = (model) => {
  // TODO: Implement edit modal or navigate to edit page
  toast.info(`Edit: ${model.modelDesignation || model.name}`);
  console.log('Edit model:', model);
};

const handleDelete = (model) => {
  // TODO: Implement delete confirmation dialog
  toast.warning(`Delete action for: ${model.modelDesignation || model.name} (not implemented yet)`);
  console.log('Delete model:', model);
};
</script>

<template>
  <!-- Search Bar Component -->
  <GenericSearchBar
    entity-name="Component Models"
    :search-query="searchQuery"
    :found-count="filteredModels.length"
    :total-count="totalComponentModels"
    theme="indigo"
    intensity="500"
    :search-fields="['manufacturer', 'model', 'name', 'part number']"
    @update:search-query="searchQuery = $event"
    @clear="clearSearch"
  />

  <!-- Component Model Listings Table -->
  <div>
    <ComponentModelListingsTable
      :filtered-models="filteredModels"
      @view="handleView"
      @edit="handleEdit"
      @delete="handleDelete"
    />
  </div>
</template>
