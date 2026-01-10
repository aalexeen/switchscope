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

// Apply truncate if specified
const isTruncate = computed(() => props.config.type === 'text-truncate');
</script>

<template>
  <span
    :class="[
      'text-gray-700',
      isTruncate ? 'text-sm truncate block' : '',
      config.maxWidth || ''
    ]"
    :title="isTruncate ? String(value || config.fallback || '-') : undefined"
  >
    {{ transformedValue }}
  </span>
</template>
