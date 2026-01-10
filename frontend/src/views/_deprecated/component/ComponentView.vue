<script setup>
import GenericSearchBar from '@/components/common/GenericSearchBar.vue';
import GenericListingsTable from '@/components/table/GenericListingsTable.vue';
import componentsConfig from '@/configs/tables/components.config.js';
import { ref, computed } from 'vue';
import { useComponents } from '@/composables/useComponents';

// Get composable data
const composable = useComponents();
const {
  components,
  searchComponents,
  totalComponents,
  isLoading,
  error,
  fetchComponents
} = composable;

// Reactive state for search
const searchQuery = ref('');

// Computed property for filtered components
const filteredComponents = computed(() => {
  return searchComponents(searchQuery.value);
});

// Clear search
const clearSearch = () => {
  searchQuery.value = '';
};
</script>

<template>
  <!-- Search Bar Component -->
  <GenericSearchBar
    entity-name="Components"
    :search-query="searchQuery"
    :found-count="filteredComponents.length"
    :total-count="totalComponents"
    theme="green"
    intensity="500"
    title-suffix="Table View"
    :search-fields="['name', 'manufacturer', 'model', 'serial number']"
    @update:search-query="searchQuery = $event"
    @clear="clearSearch"
  />

  <!-- Generic Listings Table -->
  <div>
    <GenericListingsTable
      :config="componentsConfig"
      :filtered-data="filteredComponents"
      :composable-data="{
        data: components,
        isLoading: isLoading,
        error: error,
        fetchData: fetchComponents,
        total: totalComponents
      }"
    />
  </div>
</template>
