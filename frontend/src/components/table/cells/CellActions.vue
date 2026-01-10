<script setup>
import { computed } from 'vue';

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  config: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['view', 'edit', 'delete']);

// Determine which actions to show
const showView = computed(() => props.config.actions?.includes('view') ?? true);
const showEdit = computed(() => props.config.actions?.includes('edit') ?? true);
const showDelete = computed(() => props.config.actions?.includes('delete') ?? true);

/**
 * Handle delete action with confirmation
 */
const handleDelete = () => {
  const itemName = props.item.name || props.item.displayName || 'this item';
  if (confirm(`Are you sure you want to delete "${itemName}"?`)) {
    emit('delete', props.item);
  }
};
</script>

<template>
  <div class="flex space-x-2">
    <button
      v-if="showView"
      class="text-blue-600 hover:text-blue-800 transition-colors"
      title="View Details"
      @click="$emit('view', item)"
    >
      <i class="pi pi-eye" />
    </button>
    <button
      v-if="showEdit"
      class="text-green-600 hover:text-green-800 transition-colors"
      title="Edit"
      @click="$emit('edit', item)"
    >
      <i class="pi pi-pencil" />
    </button>
    <button
      v-if="showDelete"
      class="text-red-600 hover:text-red-800 transition-colors"
      title="Delete"
      @click="handleDelete"
    >
      <i class="pi pi-trash" />
    </button>
  </div>
</template>
