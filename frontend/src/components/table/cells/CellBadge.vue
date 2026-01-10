<script setup>
import { computed } from 'vue';

const props = defineProps({
  value: {
    type: [String, Number],
    default: null
  },
  config: {
    type: Object,
    required: true
  }
});

// Apply text transformation if specified
const transformedValue = computed(() => {
  if (!props.value) return props.config.fallback || '-';

  const val = String(props.value);

  switch (props.config.transform) {
    case 'capitalize':
      return val.charAt(0).toUpperCase() + val.slice(1).toLowerCase();
    case 'uppercase':
      return val.toUpperCase();
    case 'lowercase':
      return val.toLowerCase();
    default:
      return val;
  }
});

// Get badge color (can be customized via config.badgeColor)
const badgeClass = computed(() => {
  // If value is falsy and there's a fallback, use default color for fallback
  if (!props.value && props.config.fallback) {
    return 'bg-gray-100 text-gray-600';
  }
  return props.config.badgeColor || 'bg-blue-100 text-blue-800';
});
</script>

<template>
  <span
    :class="badgeClass"
    class="px-2 py-1 rounded text-xs font-medium"
  >
    {{ transformedValue }}
  </span>
</template>
