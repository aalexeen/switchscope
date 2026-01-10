<script setup>
import { RouterLink } from 'vue-router';
import ComponentModelListTable from './ComponentModelListTable.vue';
import { defineProps, onMounted, computed } from 'vue';
import { useComponentModels } from '@/composables/useComponentModels';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Use the Component Models composable
const {
  componentModels,
  isLoading,
  error,
  fetchComponentModels,
  initialize,
  totalComponentModels
} = useComponentModels();

defineEmits(['view', 'edit', 'delete']);

const props = defineProps({
  limit: {
    type: Number,
    default: null
  },
  showButton: {
    type: Boolean,
    default: false
  },
  filteredModels: {
    type: Array,
    default: null
  }
});

// Use filtered models if provided, otherwise use all models
const displayedModels = computed(() => {
  const models = props.filteredModels !== null ? props.filteredModels : componentModels.value;
  return props.limit ? models.slice(0, props.limit) : models;
});

onMounted(async () => {
  try {
    // Initialize will only fetch if data is not already loaded
    await initialize();
  } catch (err) {
    console.error('Error loading component models:', err);
  }
});
</script>

<template>
  <section class="bg-blue-50 px-4 py-10">
    <div class="container-xl lg:container m-auto">
      <h2 class="text-3xl font-bold text-indigo-500 mb-6 text-center">
        Browse Component Models - Catalog
      </h2>

      <!-- Statistics -->
      <div class="bg-white rounded-lg shadow p-4 mb-6">
        <div class="flex justify-between items-center">
          <div class="text-sm text-gray-600">
            <span class="font-medium">
              Displaying: {{ displayedModels.length }} models
            </span>
            <span
              v-if="props.limit"
              class="ml-4"
            >
              (Limit: {{ Math.min(props.limit, displayedModels.length) }} of {{ totalComponentModels }})
            </span>
          </div>
          <button
            :disabled="isLoading"
            class="bg-blue-500 hover:bg-blue-600 disabled:bg-gray-400 text-white px-4 py-2 rounded text-sm"
            @click="fetchComponentModels(true)"
          >
            {{ isLoading ? 'Refreshing...' : 'Refresh' }}
          </button>
        </div>
      </div>

      <!-- Show error message if there's an error -->
      <div
        v-if="error"
        class="bg-white rounded-lg shadow p-6 text-center text-red-500"
      >
        <p class="mb-4">
          Error loading component models. Please try again.
        </p>
        <button
          class="bg-indigo-500 hover:bg-indigo-600 text-white px-4 py-2 rounded"
          @click="fetchComponentModels(true)"
        >
          Retry
        </button>
      </div>

      <!-- Show loading spinner while loading -->
      <div
        v-else-if="isLoading"
        class="bg-white rounded-lg shadow p-8 text-center text-gray-500"
      >
        <PulseLoader />
        <p class="mt-4">
          Loading component models...
        </p>
      </div>

      <!-- Show component model listing table when done loading -->
      <div
        v-else-if="displayedModels.length > 0"
        class="bg-white rounded-lg shadow overflow-hidden"
      >
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Manufacturer
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Model Number
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Component Type
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Part Number
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  SKU
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Release Date
                </th>
                <th
                  class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider"
                  title="Verified"
                >
                  Verified
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Description
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <ComponentModelListTable
                v-for="model in displayedModels"
                :key="model.id"
                :model="model"
                @view="(m) => $emit('view', m)"
                @edit="(m) => $emit('edit', m)"
                @delete="(m) => $emit('delete', m)"
              />
            </tbody>
          </table>
        </div>
      </div>

      <!-- Show message when no models are available -->
      <div
        v-else
        class="bg-white rounded-lg shadow p-8 text-center text-gray-500"
      >
        <p>No component models found.</p>
      </div>
    </div>
  </section>

  <section
    v-if="showButton"
    class="m-auto max-w-lg my-10 px-6"
  >
    <RouterLink
      to="/catalog/component-models"
      class="block bg-black text-white text-center py-4 px-6 rounded-xl hover:bg-gray-700"
    >
      View All Component Models
    </RouterLink>
  </section>
</template>
