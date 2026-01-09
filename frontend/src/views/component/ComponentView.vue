<script setup>
import GenericSearchBar from '@/components/common/GenericSearchBar.vue';
import ComponentListingsTable from '@/components/component/ComponentListingsTable.vue';
import { ref, computed } from 'vue';
import { useComponents } from '@/composables/useComponents';

// Get search functionality from composable
const { searchComponents, totalComponents } = useComponents();

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

  <!-- Component Listings Table -->
  <div>
    <ComponentListingsTable
      :filtered-components="filteredComponents"
    />
  </div>
</template>
