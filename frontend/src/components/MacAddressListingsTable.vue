<script setup>
import { RouterLink } from 'vue-router';
import MacAddressListTable from './MacAddressListTable.vue';
import { defineProps, onMounted, computed } from 'vue';
import { useMacAddresses } from '@/composables/useMacAddresses';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Use the MAC addresses composable
const { 
  macAddresses, 
  isLoading, 
  error, 
  fetchMacAddresses,
  initialize,
  totalMacs
} = useMacAddresses();

const props = defineProps({
    limit: Number,
    showButton: {
        type: Boolean,
        default: false
    },
    filteredMacs: {
        type: Array,
        default: null
    },
    searchQuery: {
        type: String,
        default: ''
    }
});

// Use filtered MACs if provided, otherwise use all MACs
const displayedMacs = computed(() => {
  const macs = props.filteredMacs !== null ? props.filteredMacs : macAddresses.value;
  return props.limit ? macs.slice(0, props.limit) : macs;
});

// Show search info if search is active
const hasActiveSearch = computed(() => {
  return props.searchQuery && props.searchQuery.trim() !== '';
});

onMounted(async () => {
    try {
        // Initialize will only fetch if data is not already loaded
        await initialize();
    } catch (err) {
        console.error('Error loading MAC addresses:', err);
    }
});
</script>

<template>
  <section class="bg-blue-50 px-4 py-10">
    <div class="container-xl lg:container m-auto">
      <h2 class="text-3xl font-bold text-green-500 mb-6 text-center">
        Browse Macs - Table View
        <span
          v-if="hasActiveSearch"
          class="text-lg text-gray-600 block mt-2"
        >
          Search results for: "{{ searchQuery }}"
        </span>
      </h2>
            
      <!-- Statistics -->
      <div class="bg-white rounded-lg shadow p-4 mb-6">
        <div class="flex justify-between items-center">
          <div class="text-sm text-gray-600">
            <span class="font-medium">
              <template v-if="hasActiveSearch">
                Found: {{ displayedMacs.length }} of {{ totalMacs }} total
              </template>
              <template v-else>
                Total MAC Addresses: {{ totalMacs }}
              </template>
            </span>
            <span
              v-if="props.limit"
              class="ml-4"
            >Showing: {{ Math.min(props.limit, displayedMacs.length) }}</span>
          </div>
          <button 
            :disabled="isLoading"
            class="bg-blue-500 hover:bg-blue-600 disabled:bg-gray-400 text-white px-4 py-2 rounded text-sm"
            @click="fetchMacAddresses(true)"
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
          Error loading MAC addresses. Please try again.
        </p>
        <button 
          class="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded" 
          @click="fetchMacAddresses(true)"
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
          Loading MAC addresses...
        </p>
      </div>

      <!-- Show mac listing table when done loading -->
      <div
        v-else-if="displayedMacs.length > 0"
        class="bg-white rounded-lg shadow overflow-hidden"
      >
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  MAC Address
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Reason
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Block Time
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Remaining Time
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Location
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  WLC
                </th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <MacAddressListTable 
                v-for="mac in displayedMacs" 
                :key="mac.id" 
                :mac="mac" 
              />
            </tbody>
          </table>
        </div>
      </div>

      <!-- Show message when no MACs are available -->
      <div
        v-else
        class="bg-white rounded-lg shadow p-8 text-center text-gray-500"
      >
        <p v-if="hasActiveSearch">
          No MAC addresses found matching your search: "{{ searchQuery }}"
        </p>
        <p v-else>
          No MAC addresses found.
        </p>
      </div>
    </div>
  </section>

  <section
    v-if="showButton"
    class="m-auto max-w-lg my-10 px-6"
  >
    <RouterLink
      to="/removemac-table"
      class="block bg-black text-white text-center py-4 px-6 rounded-xl hover:bg-gray-700"
    >
      View All Macs
    </RouterLink>
  </section>
</template>