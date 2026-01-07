<script setup>
import ComponentListingsTable from '@/components/component/ComponentListingsTable.vue';
import { ref, computed, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useComponents } from '@/composables/useComponents';

const route = useRoute();
const router = useRouter();

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
  <!-- Header with Search -->
  <section class="bg-green-500 text-white py-4">
    <div class="container mx-auto px-4">
      <div class="flex justify-between items-center mb-4">
        <h1 class="text-2xl font-bold">
          Components - Table View
        </h1>
      </div>

      <!-- Search Section -->
      <div class="flex flex-col sm:flex-row gap-4 items-start sm:items-center">
        <div class="flex-1 max-w-md">
          <div class="relative">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search by name, manufacturer, model, serial number..."
              class="w-full px-4 py-2 pl-10 text-gray-900 rounded-lg border-0 focus:ring-2 focus:ring-green-300 focus:outline-none">
            <i class="pi pi-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
          </div>
        </div>

        <div class="flex items-center space-x-4 text-sm">
          <div class="flex items-center space-x-2">
            <span>Found: {{ filteredComponents.length }}</span>
            <span>|</span>
            <span>Total: {{ totalComponents }}</span>
          </div>

          <button
            v-if="searchQuery"
            @click="clearSearch"
            class="bg-white text-green-500 px-3 py-1 rounded text-sm hover:bg-gray-100 transition-colors">
            <i class="pi pi-times mr-1"></i>
            Clear
          </button>
        </div>
      </div>
    </div>
  </section>

  <!-- Component Listings Table -->
  <div>
    <ComponentListingsTable
      :filtered-components="filteredComponents"
      :search-query="searchQuery"
    />
  </div>
</template>
