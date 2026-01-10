<script setup>
defineProps({
  value: {
    type: String,
    default: null
  },
  item: {
    type: Object,
    required: true
  },
  config: {
    type: Object,
    required: true
  }
});

// Get icon class with fallback
const getIconClass = (iconClass) => {
  return iconClass || 'pi pi-box';
};
</script>

<template>
  <div class="flex items-center space-x-2">
    <i
      :class="[getIconClass(item.iconClass), item.colorClass]"
      class="text-xl"
    />
    <span class="font-medium text-gray-900">
      {{ value || config.fallback || '-' }}
    </span>
  </div>
</template>
