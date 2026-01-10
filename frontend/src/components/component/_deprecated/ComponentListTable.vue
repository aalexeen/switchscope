<script setup>
import { defineProps, ref, computed } from 'vue';
import { RouterLink } from 'vue-router';
import { useComponents } from '@/composables/useComponents';

const props = defineProps({
  component: {
    type: Object,
    default: () => ({})
  },
});

const { deleteComponent } = useComponents();

const showFullDescription = ref(false);
const isDeleting = ref(false);

const toggleFullDescription = () => {
    showFullDescription.value = !showFullDescription.value;
};

const truncatedDescription = computed(() => {
    let description = props.component.description;
    if (!showFullDescription.value && description && description.length > 50) {
        description = description.substring(0, 50) + '...';
    }
    return description || 'No description';
});

const handleDelete = async () => {
    if (confirm(`Are you sure you want to delete component ${props.component.name}?`)) {
        isDeleting.value = true;
        try {
            await deleteComponent(props.component.id);
        } catch (error) {
            console.error('Error deleting component:', error);
            alert('Failed to delete component. Please try again.');
        } finally {
            isDeleting.value = false;
        }
    }
};
</script>

<template>
  <tr class="hover:bg-gray-50 border-b border-gray-200">
    <!-- Name -->
    <td class="px-4 py-3 text-sm font-medium text-gray-900">
      {{ component.name }}
    </td>

    <!-- Manufacturer -->
    <td class="px-4 py-3 text-sm text-gray-900">
      {{ component.manufacturer || 'N/A' }}
    </td>

    <!-- Model -->
    <td class="px-4 py-3 text-sm text-gray-900">
      {{ component.model || 'N/A' }}
    </td>

    <!-- Serial Number -->
    <td class="px-4 py-3 text-sm font-mono text-gray-900">
      {{ component.serialNumber || 'N/A' }}
    </td>

    <!-- Component Type -->
    <td class="px-4 py-3 text-sm">
      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
        {{ component.componentTypeDisplayName || component.componentTypeCode || 'Unknown' }}
      </span>
    </td>

    <!-- Status -->
    <td class="px-4 py-3 text-sm">
      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
        {{ component.componentStatusDisplayName || component.componentStatusCode || 'Unknown' }}
      </span>
    </td>

    <!-- Description -->
    <td class="px-4 py-3 text-sm text-gray-900">
      <div class="max-w-xs">
        <div>{{ truncatedDescription }}</div>
        <button
          v-if="component.description && component.description.length > 50"
          class="text-green-500 hover:text-green-600 text-xs mt-1"
          @click="toggleFullDescription"
        >
          {{ showFullDescription ? 'Less' : 'More' }}
        </button>
      </div>
    </td>

    <!-- Actions -->
    <td class="px-4 py-3 text-sm">
      <div class="flex space-x-2">
        <RouterLink
          :to="`/components/${component.id}?from=table`"
          class="bg-green-500 hover:bg-green-600 text-white px-3 py-1 rounded text-xs"
        >
          View Details
        </RouterLink>
        <button
          :disabled="isDeleting"
          class="bg-red-500 hover:bg-red-600 disabled:bg-gray-400 text-white px-3 py-1 rounded text-xs"
          @click="handleDelete"
        >
          {{ isDeleting ? 'Deleting...' : 'Delete' }}
        </button>
      </div>
    </td>
  </tr>
</template>
