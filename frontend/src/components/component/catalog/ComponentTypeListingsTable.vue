<script setup>
import { RouterLink } from 'vue-router';
import ComponentTypeListTable from './ComponentTypeListTable.vue';
import { defineProps, onMounted, computed } from 'vue';
import { useComponentTypes } from '@/composables/useComponentTypes';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Use the Component Types composable
const {
  componentTypes,
  isLoading,
  error,
  fetchComponentTypes,
  initialize,
  totalComponentTypes
} = useComponentTypes();

const props = defineProps({
  limit: Number,
  showButton: {
    type: Boolean,
    default: false
  },
  filteredTypes: {
    type: Array,
    default: null
  }
});

// Use filtered types if provided, otherwise use all types
const displayedTypes = computed(() => {
  const types = props.filteredTypes !== null ? props.filteredTypes : componentTypes.value;
  return props.limit ? types.slice(0, props.limit) : types;
});

onMounted(async () => {
  try {
    // Initialize will only fetch if data is not already loaded
    await initialize();
  } catch (err) {
    console.error('Error loading component types:', err);
  }
});
</script>

<template>
  <section class="bg-blue-50 px-4 py-10">
    <div class="container-xl lg:container m-auto">
      <h2 class="text-3xl font-bold text-indigo-500 mb-6 text-center">
        Browse Component Types - Catalog
      </h2>

      <!-- Statistics -->
      <div class="bg-white rounded-lg shadow p-4 mb-6">
        <div class="flex justify-between items-center">
          <div class="text-sm text-gray-600">
            <span class="font-medium">
              Displaying: {{ displayedTypes.length }} types
            </span>
            <span
              v-if="props.limit"
              class="ml-4"
            >
              (Limit: {{ Math.min(props.limit, displayedTypes.length) }} of {{ totalComponentTypes }})
            </span>
          </div>
          <button
            :disabled="isLoading"
            class="bg-blue-500 hover:bg-blue-600 disabled:bg-gray-400 text-white px-4 py-2 rounded text-sm"
            @click="fetchComponentTypes(true)"
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
          Error loading component types. Please try again.
        </p>
        <button
          class="bg-indigo-500 hover:bg-indigo-600 text-white px-4 py-2 rounded"
          @click="fetchComponentTypes(true)"
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
          Loading component types...
        </p>
      </div>

      <!-- Show component type listing table when done loading -->
      <div
        v-else-if="displayedTypes.length > 0"
        class="bg-white rounded-lg shadow overflow-hidden"
      >
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Name
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Code
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Display Name
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Category
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Type
                </th>
                <th
                  class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider"
                  title="System Type"
                >
                  System
                </th>
                <th
                  class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider"
                  title="Requires Management"
                >
                  Mgmt
                </th>
                <th
                  class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider"
                  title="Can Have IP Address"
                >
                  IP
                </th>
                <th
                  class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider"
                  title="Supports SNMP"
                >
                  SNMP
                </th>
                <th
                  class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider"
                  title="Requires Rack Space"
                >
                  Rack
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
              <ComponentTypeListTable
                v-for="type in displayedTypes"
                :key="type.id"
                :type="type"
                @view="(t) => $emit('view', t)"
                @edit="(t) => $emit('edit', t)"
                @delete="(t) => $emit('delete', t)"
              />
            </tbody>
          </table>
        </div>
      </div>

      <!-- Show message when no types are available -->
      <div
        v-else
        class="bg-white rounded-lg shadow p-8 text-center text-gray-500"
      >
        <p>No component types found.</p>
      </div>
    </div>
  </section>

  <section
    v-if="showButton"
    class="m-auto max-w-lg my-10 px-6"
  >
    <RouterLink
      to="/catalog/component-types"
      class="block bg-black text-white text-center py-4 px-6 rounded-xl hover:bg-gray-700"
    >
      View All Component Types
    </RouterLink>
  </section>
</template>
