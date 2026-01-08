<script setup>
import { RouterLink } from 'vue-router';
import ComponentNatureListTable from './ComponentNatureListTable.vue';
import { defineProps, onMounted, computed } from 'vue';
import { useComponentNatures } from '@/composables/useComponentNatures';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Use the Component Natures composable
const {
  componentNatures,
  isLoading,
  error,
  fetchComponentNatures,
  initialize,
  totalComponentNatures
} = useComponentNatures();

const props = defineProps({
  limit: Number,
  showButton: {
    type: Boolean,
    default: false
  },
  filteredNatures: {
    type: Array,
    default: null
  }
});

// Use filtered natures if provided, otherwise use all natures
const displayedNatures = computed(() => {
  const natures = props.filteredNatures !== null ? props.filteredNatures : componentNatures.value;
  return props.limit ? natures.slice(0, props.limit) : natures;
});

onMounted(async () => {
  try {
    // Initialize will only fetch if data is not already loaded
    await initialize();
  } catch (err) {
    console.error('Error loading component natures:', err);
  }
});
</script>

<template>
  <section class="bg-blue-50 px-4 py-10">
    <div class="container-xl lg:container m-auto">
      <h2 class="text-3xl font-bold text-indigo-500 mb-6 text-center">
        Browse Component Natures - Catalog
      </h2>

      <!-- Statistics -->
      <div class="bg-white rounded-lg shadow p-4 mb-6">
        <div class="flex justify-between items-center">
          <div class="text-sm text-gray-600">
            <span class="font-medium">
              Displaying: {{ displayedNatures.length }} natures
            </span>
            <span v-if="props.limit" class="ml-4">
              (Limit: {{ Math.min(props.limit, displayedNatures.length) }} of {{ totalComponentNatures }})
            </span>
          </div>
          <button
            @click="fetchComponentNatures(true)"
            :disabled="isLoading"
            class="bg-blue-500 hover:bg-blue-600 disabled:bg-gray-400 text-white px-4 py-2 rounded text-sm">
            {{ isLoading ? 'Refreshing...' : 'Refresh' }}
          </button>
        </div>
      </div>

      <!-- Show error message if there's an error -->
      <div v-if="error" class="bg-white rounded-lg shadow p-6 text-center text-red-500">
        <p class="mb-4">Error loading component natures. Please try again.</p>
        <button
          @click="fetchComponentNatures(true)"
          class="bg-indigo-500 hover:bg-indigo-600 text-white px-4 py-2 rounded">
          Retry
        </button>
      </div>

      <!-- Show loading spinner while loading -->
      <div v-else-if="isLoading" class="bg-white rounded-lg shadow p-8 text-center text-gray-500">
        <PulseLoader />
        <p class="mt-4">Loading component natures...</p>
      </div>

      <!-- Show component nature listing table when done loading -->
      <div v-else-if="displayedNatures.length > 0" class="bg-white rounded-lg shadow overflow-hidden">
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
                  Status
                </th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider" title="Requires Management">
                  Mgmt
                </th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider" title="Can Have IP Address">
                  IP
                </th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider" title="Has Firmware">
                  FW
                </th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider" title="Processes Network Traffic">
                  Traffic
                </th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider" title="Supports SNMP Monitoring">
                  SNMP
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Power
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
              <ComponentNatureListTable
                v-for="nature in displayedNatures"
                :key="nature.id"
                :nature="nature"
                @view="(n) => $emit('view', n)"
                @edit="(n) => $emit('edit', n)"
                @delete="(n) => $emit('delete', n)"
              />
            </tbody>
          </table>
        </div>
      </div>

      <!-- Show message when no natures are available -->
      <div v-else class="bg-white rounded-lg shadow p-8 text-center text-gray-500">
        <p>No component natures found.</p>
      </div>
    </div>
  </section>

  <section v-if="showButton" class="m-auto max-w-lg my-10 px-6">
    <RouterLink
      to="/catalog/component-natures"
      class="block bg-black text-white text-center py-4 px-6 rounded-xl hover:bg-gray-700">
      View All Component Natures
    </RouterLink>
  </section>
</template>
