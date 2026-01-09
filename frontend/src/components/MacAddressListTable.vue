<script setup>
import { defineProps, ref, computed } from 'vue';
import { RouterLink } from 'vue-router';
import { useMacAddresses } from '@/composables/useMacAddresses';

const props = defineProps({
    mac: Object,
});

const { deleteMacAddress } = useMacAddresses();

const showFullBlockTime = ref(false);
const isDeleting = ref(false);

const toggleFullBlockTime = () => {
    showFullBlockTime.value = !showFullBlockTime.value;
};

const truncatedBlockTime = computed(() => {
    let blockTime = props.mac.blockTime;
    if (!showFullBlockTime.value && blockTime && blockTime.length > 20) {
        blockTime = blockTime.substring(0, 20) + '...';
    }
    return blockTime;
});

const handleDelete = async () => {
    if (confirm(`Are you sure you want to delete MAC address ${props.mac.clientMac}?`)) {
        isDeleting.value = true;
        try {
            await deleteMacAddress(props.mac.id);
        } catch (error) {
            console.error('Error deleting MAC address:', error);
            alert('Failed to delete MAC address. Please try again.');
        } finally {
            isDeleting.value = false;
        }
    }
};
</script>

<template>
  <tr class="hover:bg-gray-50 border-b border-gray-200">
    <!-- MAC Address -->
    <td class="px-4 py-3 text-sm font-mono text-gray-900">
      {{ mac.clientMac }}
    </td>
        
    <!-- Reason -->
    <td class="px-4 py-3 text-sm text-gray-900">
      <div class="max-w-xs">
        <div class="font-medium">
          {{ mac.reason }}
        </div>
      </div>
    </td>
        
    <!-- Block Time -->
    <td class="px-4 py-3 text-sm text-gray-900">
      <div class="max-w-xs">
        <div>{{ truncatedBlockTime }}</div>
        <button 
          v-if="mac.blockTime && mac.blockTime.length > 20"
          class="text-green-500 hover:text-green-600 text-xs mt-1" 
          @click="toggleFullBlockTime"
        >
          {{ showFullBlockTime ? 'Less' : 'More' }}
        </button>
      </div>
    </td>
        
    <!-- Remaining Time -->
    <td class="px-4 py-3 text-sm">
      <span class="text-green-600 font-medium">{{ mac.remainingTime }}</span>
    </td>
        
    <!-- Location -->
    <td class="px-4 py-3 text-sm text-orange-700">
      <div class="flex items-center">
        <i class="pi pi-map-marker text-orange-700 mr-1" />
        New York, NY
      </div>
    </td>

    <!-- Location -->
    <td class="px-4 py-3 text-sm text-gray-900">
      <div class="flex items-center">
        <div class="font-medium">
          {{ mac.wlc.id }}
        </div>
      </div>
    </td>
        
    <!-- Actions -->
    <td class="px-4 py-3 text-sm">
      <div class="flex space-x-2">
        <RouterLink
          :to="`/removemac/${mac.id}?from=table`"
          class="bg-green-500 hover:bg-green-600 text-white px-3 py-1 rounded text-xs"
        >
          View Details
        </RouterLink>
        <button
          :disabled="isDeleting"
          class="bg-red-500 hover:bg-red-600 disabled:bg-gray-400 text-white px-3 py-1 rounded text-xs"
          @click="handleDelete"
        >
          {{ isDeleting ? 'Deleting...' : 'Delete' }}
        </button>
      </div>
    </td>
  </tr>
</template>