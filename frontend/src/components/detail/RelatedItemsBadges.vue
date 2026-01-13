<template>
  <div v-if="hasItems" class="space-y-3">
    <div v-if="items && items.length > 0">
      <div class="flex flex-wrap gap-2">
        <span
          v-for="item in sortedItems"
          :key="item"
          class="inline-flex items-center px-3 py-1.5 rounded-lg text-sm font-medium border transition-colors"
          :class="badgeClasses"
        >
          <i v-if="icon" :class="[icon, 'mr-2 text-xs']"></i>
          {{ formatItemName(item) }}
        </span>
      </div>
    </div>
    <div v-else class="text-sm text-gray-500 italic">
      {{ emptyMessage }}
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  items: {
    type: Array,
    default: () => []
  },
  icon: {
    type: String,
    default: null
  },
  theme: {
    type: String,
    default: 'blue' // blue, green, purple, etc.
  },
  emptyMessage: {
    type: String,
    default: 'No items'
  }
});

const hasItems = computed(() => {
  return props.items && props.items.length > 0;
});

const sortedItems = computed(() => {
  if (!props.items) return [];
  return [...props.items].sort();
});

const badgeClasses = computed(() => {
  const themes = {
    blue: 'bg-blue-50 text-blue-700 border-blue-200 hover:bg-blue-100',
    green: 'bg-green-50 text-green-700 border-green-200 hover:bg-green-100',
    purple: 'bg-purple-50 text-purple-700 border-purple-200 hover:bg-purple-100',
    orange: 'bg-orange-50 text-orange-700 border-orange-200 hover:bg-orange-100',
    indigo: 'bg-indigo-50 text-indigo-700 border-indigo-200 hover:bg-indigo-100'
  };
  return themes[props.theme] || themes.blue;
});

const formatItemName = (code) => {
  // Convert NETWORK_SWITCH to Network Switch
  return code
    .split('_')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
    .join(' ');
};
</script>
