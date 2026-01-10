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

// Get icon class based on boolean value
const iconClass = computed(() => {
  return props.value
    ? 'pi pi-check text-green-600'
    : 'pi pi-times text-gray-400';
});
</script>

<template>
  <i
    :class="iconClass"
    class="text-lg"
  />
</template>
