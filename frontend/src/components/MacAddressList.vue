<script setup>
import { defineProps, ref, computed } from 'vue';
import { RouterLink } from 'vue-router';

const props = defineProps({
    mac: Object,
});

const showFullBlockTime = ref(false);

const toggleFullBlockTime = () => {
    showFullBlockTime.value = !showFullBlockTime.value;
}

const truncatedBlockTime = computed(() => {
    let blockTime = props.mac.blockTime;
    if (!showFullBlockTime.value) {
        blockTime = blockTime.substring(0, 8) + '...';
    }
    return blockTime;
});
</script>

<template>
  <div class="bg-white rounded-xl shadow-md relative">
    <div class="p-4">
      <div class="mb-6">
        <div class="text-gray-600 my-2">
          {{ mac.clientMac }}
        </div>
        <h3 class="text-xl font-bold">
          {{ mac.reason }}
        </h3>
      </div>

      <div class="mb-5">
        <div>
          {{ truncatedBlockTime }}
        </div>
        <button
          class="text-green-500 hover:text-green-600 mb-5"
          @click="toggleFullBlockTime"
        >
          {{ showFullBlockTime ? 'Less' : 'More' }}
        </button>
      </div>


      <h3 class="text-green-500 mb-2">
        {{ mac.remainingTime }}
      </h3>

      <div class="text-gray-600 my-2">
        WLC ID: <span class="font-medium">{{ mac.wlc.id }}</span>
      </div>

      <div class="border border-gray-100 mb-5" />

      <div class="flex flex-col lg:flex-row justify-between mb-4">
        <div class="text-orange-700 mb-3">
          <i class="pi pi-map-marker text-orange-700" />
          New York, NY
        </div>

        <!-- v-bind with query parameter for card view -->
        <RouterLink
          :to="`/removemac/${mac.id}?from=card`"
          class="h-[36px] bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg text-center text-sm"
        >
          Get More
        </RouterLink>
      </div>
    </div>
  </div>
</template>