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
  },
  editEnabled: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['view', 'edit', 'delete']);

// Determine which actions to show
const showView = computed(() => props.config.actions?.includes('view') ?? true);
const showEdit = computed(() => props.config.actions?.includes('edit') ?? true);
const showDelete = computed(() => props.config.actions?.includes('delete') ?? true);
const editDisabled = computed(() => !props.editEnabled);

/**
 * Handle delete action with confirmation
 */
const handleDelete = () => {
  const itemName = props.item.name || props.item.displayName || 'this item';
  if (confirm(`Are you sure you want to delete "${itemName}"?`)) {
    emit('delete', props.item);
  }
};

const handleEdit = () => {
  if (editDisabled.value) {
    return;
  }
  emit('edit', props.item);
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
      class="text-green-600 transition-colors"
      :class="editDisabled ? 'opacity-30 cursor-not-allowed' : 'hover:text-green-800'"
      :title="editDisabled ? 'Edit (disabled)' : 'Edit'"
      @click="handleEdit"
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
