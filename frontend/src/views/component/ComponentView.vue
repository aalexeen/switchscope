<script setup>
import ComponentSearchBar from '@/components/component/ComponentSearchBar.vue';
import ComponentListingsTable from '@/components/component/ComponentListingsTable.vue';
import { ref, computed } from 'vue';
import { useComponents } from '@/composables/useComponents';

// Get search functionality from composable
const { searchComponents, components, totalComponents } = useComponents();

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
  <ComponentSearchBar
    v-model:search-query="searchQuery"
    :found-count="filteredComponents.length"
    :total-count="totalComponents"
    @clear="clearSearch"
  />

  <!-- Component Listings Table -->
  <div>
    <ComponentListingsTable
      :filtered-components="filteredComponents"
    />
  </div>
</template>
