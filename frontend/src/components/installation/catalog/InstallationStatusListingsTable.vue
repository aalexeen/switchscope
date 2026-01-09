<script setup>
import { RouterLink } from 'vue-router';
import InstallationStatusListTable from './InstallationStatusListTable.vue';
import { defineProps, onMounted, computed } from 'vue';
import { useInstallationStatuses } from '@/composables/useInstallationStatuses';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Use the Installation Statuses composable
const {
  installationStatuses,
  isLoading,
  error,
  fetchInstallationStatuses,
  initialize,
  totalInstallationStatuses
} = useInstallationStatuses();

const props = defineProps({
  limit: Number,
  showButton: {
    type: Boolean,
    default: false
  },
  filteredStatuses: {
    type: Array,
    default: null
  }
});

// Use filtered statuses if provided, otherwise use all statuses
const displayedStatuses = computed(() => {
  const statuses = props.filteredStatuses !== null ? props.filteredStatuses : installationStatuses.value;
  return props.limit ? statuses.slice(0, props.limit) : statuses;
});

onMounted(async () => {
  try {
    // Initialize will only fetch if data is not already loaded
    await initialize();
  } catch (err) {
    console.error('Error loading installation statuses:', err);
  }
});
</script>

<template>
  <section class="bg-blue-50 px-4 py-10">
    <div class="container-xl lg:container m-auto">
      <h2 class="text-3xl font-bold text-indigo-500 mb-6 text-center">
        Browse Installation Statuses - Catalog
      </h2>

      <!-- Statistics -->
      <div class="bg-white rounded-lg shadow p-4 mb-6">
        <div class="flex justify-between items-center">
          <div class="text-sm text-gray-600">
            <span class="font-medium">
              Displaying: {{ displayedStatuses.length }} statuses
            </span>
            <span v-if="props.limit" class="ml-4">
              (Limit: {{ Math.min(props.limit, displayedStatuses.length) }} of {{ totalInstallationStatuses }})
            </span>
          </div>
          <button
            @click="fetchInstallationStatuses(true)"
            :disabled="isLoading"
            class="bg-blue-500 hover:bg-blue-600 disabled:bg-gray-400 text-white px-4 py-2 rounded text-sm">
            {{ isLoading ? 'Refreshing...' : 'Refresh' }}
          </button>
        </div>
      </div>

      <!-- Show error message if there's an error -->
      <div v-if="error" class="bg-white rounded-lg shadow p-6 text-center text-red-500">
        <p class="mb-4">Error loading installation statuses. Please try again.</p>
        <button
          @click="fetchInstallationStatuses(true)"
          class="bg-indigo-500 hover:bg-indigo-600 text-white px-4 py-2 rounded">
          Retry
        </button>
      </div>

      <!-- Show loading spinner while loading -->
      <div v-else-if="isLoading" class="bg-white rounded-lg shadow p-8 text-center text-gray-500">
        <PulseLoader />
        <p class="mt-4">Loading installation statuses...</p>
      </div>

      <!-- Show installation status listing table when done loading -->
      <div v-else-if="displayedStatuses.length > 0" class="bg-white rounded-lg shadow overflow-hidden">
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
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Category
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Color
                </th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Urgency
                </th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider" title="Physically Present">
                  Physical
                </th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider" title="Operational">
                  Oper
                </th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider" title="Requires Attention">
                  Attn
                </th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider" title="Final Status">
                  Final
                </th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider" title="Auto Transition Minutes">
                  Auto
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
              <InstallationStatusListTable
                v-for="installationStatus in displayedStatuses"
                :key="installationStatus.id"
                :installation-status="installationStatus"
                @view="(s) => $emit('view', s)"
                @edit="(s) => $emit('edit', s)"
                @delete="(s) => $emit('delete', s)"
              />
            </tbody>
          </table>
        </div>
      </div>

      <!-- Show message when no statuses are available -->
      <div v-else class="bg-white rounded-lg shadow p-8 text-center text-gray-500">
        <p>No installation statuses found.</p>
      </div>
    </div>
  </section>

  <section v-if="showButton" class="m-auto max-w-lg my-10 px-6">
    <RouterLink
      to="/catalog/installation-statuses"
      class="block bg-black text-white text-center py-4 px-6 rounded-xl hover:bg-gray-700">
      View All Installation Statuses
    </RouterLink>
  </section>
</template>
