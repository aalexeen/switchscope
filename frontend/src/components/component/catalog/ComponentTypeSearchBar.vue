<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  searchQuery: {
    type: String,
    required: true
  },
  foundCount: {
    type: Number,
    required: true
  },
  totalCount: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['update:searchQuery', 'clear']);

const updateSearch = (event) => {
  emit('update:searchQuery', event.target.value);
};

const clearSearch = () => {
  emit('clear');
};
</script>

<template>
  <!-- Header with Search -->
  <section class="bg-blue-600 text-white py-4">
    <div class="container mx-auto px-4">
      <div class="flex justify-between items-center mb-4">
        <h1 class="text-2xl font-bold">
          Component Types - Catalog
        </h1>
      </div>

      <!-- Search Section -->
      <div class="flex flex-col sm:flex-row gap-4 items-start sm:items-center">
        <div class="flex-1 max-w-md">
          <div class="relative">
            <input
              :value="searchQuery"
              @input="updateSearch"
              type="text"
              placeholder="Search by name, code, display name, category, description..."
              class="w-full px-4 py-2 pl-10 text-gray-900 rounded-lg border-0 focus:ring-2 focus:ring-blue-300 focus:outline-none">
            <i class="pi pi-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
          </div>
        </div>

        <div class="flex items-center space-x-4 text-sm">
          <div class="flex items-center space-x-2">
            <span>Found: {{ foundCount }}</span>
            <span>|</span>
            <span>Total: {{ totalCount }}</span>
          </div>

          <button
            v-if="searchQuery"
            @click="clearSearch"
            class="bg-white text-blue-600 px-3 py-1 rounded text-sm hover:bg-gray-100 transition-colors">
            <i class="pi pi-times mr-1"></i>
            Clear
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* Component-specific styles if needed */
</style>
