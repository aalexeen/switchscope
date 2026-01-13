<template>
  <div class="flex items-center justify-center">
    <button
      @click.stop="toggleExpand"
      class="p-1 rounded hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
      :class="{ 'text-blue-600': isExpanded, 'text-gray-400': !isExpanded }"
      :disabled="!hasExpandableContent"
      :title="hasExpandableContent ? (isExpanded ? 'Collapse' : 'Expand') : 'No properties'"
    >
      <i
        v-if="hasExpandableContent"
        class="pi transition-transform duration-200"
        :class="isExpanded ? 'pi-chevron-down' : 'pi-chevron-right'"
      ></i>
      <i v-else class="pi pi-minus text-gray-300"></i>
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  isExpanded: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['toggle']);

const hasExpandableContent = computed(() => {
  return props.item.properties && Object.keys(props.item.properties).length > 0;
});

const toggleExpand = () => {
  if (hasExpandableContent.value) {
    emit('toggle');
  }
};
</script>
