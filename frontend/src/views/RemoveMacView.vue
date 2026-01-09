<script setup>
import MacAddressListings from '@/components/MacAddressListings.vue';
import MacAddressListingsTable from '@/components/MacAddressListingsTable.vue';
import { ref, computed, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useMacAddresses } from '@/composables/useMacAddresses';

const route = useRoute();
const router = useRouter();

// Get search functionality from composable
const { searchMacs, totalMacs } = useMacAddresses();

// Reactive state for current view type and search
const viewType = ref('card'); // 'card' or 'table'
const searchQuery = ref('');

// Computed property for filtered MAC addresses
const filteredMacs = computed(() => {
  return searchMacs(searchQuery.value);
});

// Computed properties for view switching
const isCardView = computed(() => viewType.value === 'card');
const isTableView = computed(() => viewType.value === 'table');

// Set view type based on current route
const setViewFromRoute = () => {
  if (route.path === '/removemac-table') {
    viewType.value = 'table';
    localStorage.setItem('lastMacView', 'table'); // Store preference
  } else {
    viewType.value = 'card';
    localStorage.setItem('lastMacView', 'card'); // Store preference
  }
};

// Switch view and update URL
const switchToCard = () => {
  viewType.value = 'card';
  router.push('/removemac');
};

const switchToTable = () => {
  viewType.value = 'table';
  router.push('/removemac-table');
};

// Clear search
const clearSearch = () => {
  searchQuery.value = '';
};

// Watch route changes to update view type
watch(() => route.path, () => {
  setViewFromRoute();
});

// Initialize view type on mount
onMounted(() => {
  setViewFromRoute();
});
</script>

<template>
  <!-- Header with View Switch and Search -->
  <section class="bg-green-500 text-white py-4">
    <div class="container mx-auto px-4">
      <div class="flex justify-between items-center mb-4">
        <h1 class="text-2xl font-bold">
          MAC Addresses - {{ isCardView ? 'Card' : 'Table' }} View
        </h1>
        <div class="flex space-x-2">
          <button
            :class="[
              isTableView 
                ? 'bg-green-700 text-white' 
                : 'bg-white text-green-500 hover:bg-gray-100',
              'px-4 py-2 rounded transition-colors'
            ]"
            @click="switchToTable"
          >
            <i class="pi pi-list mr-2" />
            Table View
          </button>
          <button
            :class="[
              isCardView 
                ? 'bg-green-700 text-white' 
                : 'bg-white text-green-500 hover:bg-gray-100',
              'px-4 py-2 rounded transition-colors'
            ]"
            @click="switchToCard"
          >
            <i class="pi pi-th-large mr-2" />
            Card View
          </button>
        </div>
      </div>
      
      <!-- Search Section -->
      <div class="flex flex-col sm:flex-row gap-4 items-start sm:items-center">
        <div class="flex-1 max-w-md">
          <div class="relative">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search by MAC address or reason..."
              class="w-full px-4 py-2 pl-10 text-gray-900 rounded-lg border-0 focus:ring-2 focus:ring-green-300 focus:outline-none"
            >
            <i class="pi pi-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
          </div>
        </div>
        
        <div class="flex items-center space-x-4 text-sm">
          <div class="flex items-center space-x-2">
            <span>Found: {{ filteredMacs.length }}</span>
            <span>|</span>
            <span>Total: {{ totalMacs }}</span>
          </div>
          
          <button
            v-if="searchQuery"
            class="bg-white text-green-500 px-3 py-1 rounded text-sm hover:bg-gray-100 transition-colors"
            @click="clearSearch"
          >
            <i class="pi pi-times mr-1" />
            Clear
          </button>
        </div>
      </div>
    </div>
  </section>

  <!-- Dynamic View Content -->
  <div>
    <!-- Card View -->
    <MacAddressListings 
      v-if="isCardView" 
      :filtered-macs="filteredMacs"
      :search-query="searchQuery" 
    />
    
    <!-- Table View -->
    <MacAddressListingsTable 
      v-if="isTableView"
      :filtered-macs="filteredMacs"
      :search-query="searchQuery"
    />
  </div>
</template>