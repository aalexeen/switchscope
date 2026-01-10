<script setup>
import { computed } from 'vue';

const props = defineProps({
  value: {
    type: Boolean,
    default: false
  },
  config: {
    type: Object,
    required: true
  }
});

// Get badge color based on active status
const badgeClass = computed(() => {
  return props.value !== false
    ? 'bg-green-100 text-green-800'
    : 'bg-gray-100 text-gray-800';
});

const badgeText = computed(() => {
  return props.value !== false ? 'Active' : 'Inactive';
});
</script>

<template>
  <span
    :class="badgeClass"
    class="px-2 py-1 rounded-full text-xs font-medium"
  >
    {{ badgeText }}
  </span>
</template>
