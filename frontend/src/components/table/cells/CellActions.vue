<script setup>
import { computed } from 'vue';
import { usePermissions } from '@/composables/usePermissions';
import { update, remove } from '@/configs/permissions';

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
  },
  /**
   * The permission resource of the table this row belongs to, e.g. `component.rack`. Comes from
   * the table config, not the column config in `config` - the actions belong to the entity, not to
   * the cell. Absent means "no table said", and an ungated button is what this component did
   * before permissions existed: the server refuses either way.
   */
  permissionResource: {
    type: String,
    default: null
  }
});

const emit = defineEmits(['view', 'edit', 'delete']);

const { can } = usePermissions();

// Determine which actions to show
const showView = computed(() => props.config.actions?.includes('view') ?? true);
const showEdit = computed(() =>
  (props.config.actions?.includes('edit') ?? true) && allowed(update)
);
const showDelete = computed(() =>
  (props.config.actions?.includes('delete') ?? true) && allowed(remove)
);
const editDisabled = computed(() => !props.editEnabled);

/**
 * Whether the caller holds the permission an action needs.
 *
 * Viewing is not asked about: reaching this page at all took the read permission, and the guard in
 * the router already asked.
 *
 * @param {Function} action - a code builder from `configs/permissions.js`
 */
function allowed(action) {
  return props.permissionResource === null || can(action(props.permissionResource));
}

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
