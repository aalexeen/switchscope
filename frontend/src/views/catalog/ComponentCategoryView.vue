<script setup>
import ComponentCategorySearchBar from '@/components/component/catalog/ComponentCategorySearchBar.vue';
import ComponentCategoryListingsTable from '@/components/component/catalog/ComponentCategoryListingsTable.vue';
import { ref, computed } from 'vue';
import { useComponentCategories } from '@/composables/useComponentCategories';
import { useToast } from 'vue-toastification';

const toast = useToast();

// Get search functionality from composable
const { searchComponentCategories, componentCategories, totalComponentCategories } = useComponentCategories();

// Reactive state for search
const searchQuery = ref('');

// Computed property for filtered component categories
const filteredCategories = computed(() => {
  return searchComponentCategories(searchQuery.value);
});

// Clear search
const clearSearch = () => {
  searchQuery.value = '';
};

// Event handlers
const handleView = (category) => {
  // TODO: Implement view details modal or navigate to details page
  toast.info(`View details for: ${category.name}`);
  console.log('View category:', category);
};

const handleEdit = (category) => {
  // TODO: Implement edit modal or navigate to edit page
  toast.info(`Edit: ${category.name}`);
  console.log('Edit category:', category);
};

const handleDelete = (category) => {
  if (!category.canBeDeleted) {
    toast.warning(`Cannot delete system category: ${category.name}`);
    return;
  }
  // TODO: Implement delete confirmation dialog
  toast.warning(`Delete action for: ${category.name} (not implemented yet)`);
  console.log('Delete category:', category);
};
</script>

<template>
  <!-- Search Bar Component -->
  <ComponentCategorySearchBar
    v-model:search-query="searchQuery"
    :found-count="filteredCategories.length"
    :total-count="totalComponentCategories"
    @clear="clearSearch"
  />

  <!-- Component Category Listings Table -->
  <div>
    <ComponentCategoryListingsTable
      :filtered-categories="filteredCategories"
      @view="handleView"
      @edit="handleEdit"
      @delete="handleDelete"
    />
  </div>
</template>
