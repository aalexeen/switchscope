<script setup>
import { ref } from 'vue';
import * as CellComponents from './cells';
import ExpandedRowContent from './ExpandedRowContent.vue';

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  config: {
    type: Object,
    required: true
  },
  visibleColumns: {
    type: Array,
    required: true
  }
});

defineEmits(['view', 'edit', 'delete']);

const isExpanded = ref(false);

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value;
};

/**
 * Get the appropriate cell component based on column type
 * @param {string} columnType - The type of column (text, code, icon-text, etc.)
 * @returns {Component} - The Vue component to render
 */
const getCellComponent = (columnType) => {
  const componentMap = {
    'text': CellComponents.CellText,
    'code': CellComponents.CellCode,
    'icon-text': CellComponents.CellIconText,
    'status-badge': CellComponents.CellStatusBadge,
    'boolean-icon': CellComponents.CellBooleanIcon,
    'badge': CellComponents.CellBadge,
    'text-badge': CellComponents.CellBadge,
    'text-truncate': CellComponents.CellText,
    'expand': CellComponents.CellExpand,
    'actions': CellComponents.CellActions
  };
  return componentMap[columnType] || CellComponents.CellText;
};

/**
 * Get the value for a column from the item
 * @param {Object} item - The data item
 * @param {string} key - The column key
 * @returns {*} - The value to display
 */
const getColumnValue = (item, key) => {
  return item[key];
};
</script>

<template>
  <tr class="hover:bg-gray-50 transition-colors">
    <td
      v-for="col in visibleColumns"
      :key="col.key"
      :class="[
        'px-4 py-3',
        col.align === 'center' ? 'text-center' : '',
        col.maxWidth || ''
      ]"
    >
      <component
        :is="getCellComponent(col.type)"
        :value="getColumnValue(item, col.key)"
        :item="item"
        :config="col"
        :is-expanded="isExpanded"
        @toggle="toggleExpand"
        @view="$emit('view', item)"
        @edit="$emit('edit', item)"
        @delete="$emit('delete', item)"
      />
    </td>
  </tr>
  <tr v-if="isExpanded" class="border-t-0">
    <td :colspan="visibleColumns.length" class="p-0">
      <ExpandedRowContent :item="item" />
    </td>
  </tr>
</template>

<style scoped>
/* Ensure truncate works properly */
.max-w-xs {
  max-width: 20rem;
}
</style>
