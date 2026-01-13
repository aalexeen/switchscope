<template>
  <div class="bg-white px-4 py-3 border-t border-gray-200">
    <div v-if="hasProperties" class="max-w-4xl">
      <h4 class="text-sm font-semibold text-gray-700 mb-3">
        Properties
      </h4>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
        <div
          v-for="[key, value] in sortedProperties"
          :key="key"
          class="flex items-start gap-2 bg-gray-50 rounded-lg px-3 py-2 border border-gray-200"
        >
          <div class="flex-shrink-0">
            <i class="pi pi-tag text-blue-500 text-xs mt-0.5"></i>
          </div>
          <div class="flex-1 min-w-0">
            <div class="text-xs font-medium text-gray-500 mb-0.5">
              {{ formatPropertyKey(key) }}
            </div>
            <div class="text-sm text-gray-900 break-words">
              {{ formatPropertyValue(value) }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="text-sm text-gray-500 italic">
      No properties available
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
});

const hasProperties = computed(() => {
  return props.item.properties && Object.keys(props.item.properties).length > 0;
});

const sortedProperties = computed(() => {
  if (!hasProperties.value) return [];
  return Object.entries(props.item.properties).sort(([a], [b]) => a.localeCompare(b));
});

const formatPropertyKey = (key) => {
  // Convert snake_case to Title Case
  return key
    .split('_')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
};

const formatPropertyValue = (value) => {
  // Format boolean values
  if (value === 'true') return '✓ Yes';
  if (value === 'false') return '✗ No';

  // Return value as is for other types
  return value;
};
</script>
