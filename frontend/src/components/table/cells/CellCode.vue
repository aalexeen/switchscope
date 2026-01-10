<script setup>
defineProps({
  value: {
    type: [String, Number],
    default: null
  },
  config: {
    type: Object,
    required: true
  }
});
</script>

<template>
  <code class="bg-gray-100 px-2 py-1 rounded text-sm font-mono text-gray-700">
    {{ value || config.fallback || '-' }}
  </code>
</template>
