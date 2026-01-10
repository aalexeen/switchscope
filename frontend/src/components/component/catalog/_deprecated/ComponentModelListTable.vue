<script setup>
import { defineProps } from 'vue';

defineEmits(['view', 'edit', 'delete']);

defineProps({
  model: {
    type: Object,
    required: true
  }
});

// Get badge color based on lifecycle status
const getStatusBadgeClass = (model) => {
  if (model.endOfLife) {
    return 'bg-red-100 text-red-800';
  }
  if (model.discontinued) {
    return 'bg-orange-100 text-orange-800';
  }
  if (model.active) {
    return 'bg-green-100 text-green-800';
  }
  return 'bg-gray-100 text-gray-800';
};

// Format the lifecycle status
const getLifecycleStatus = (model) => {
  if (model.lifecycleStatus) {
    return model.lifecycleStatus;
  }
  if (model.endOfLife) return 'End of Life';
  if (model.discontinued) return 'Discontinued';
  if (!model.active) return 'Inactive';
  return 'Active';
};

// Format date
const formatDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
};
</script>

<template>
  <tr class="hover:bg-gray-50 transition-colors">
    <!-- Manufacturer -->
    <td class="px-4 py-3">
      <span class="font-medium text-gray-900">{{ model.manufacturer }}</span>
    </td>

    <!-- Model Number / Designation -->
    <td class="px-4 py-3">
      <div class="flex flex-col">
        <code class="bg-gray-100 px-2 py-1 rounded text-sm font-mono text-gray-700">
          {{ model.modelNumber }}
        </code>
        <span
          v-if="model.name && model.name !== model.modelDesignation"
          class="text-xs text-gray-500 mt-1"
        >
          {{ model.name }}
        </span>
      </div>
    </td>

    <!-- Component Type -->
    <td class="px-4 py-3">
      <div class="flex flex-col">
        <span class="text-sm text-gray-900">
          {{ model.componentTypeDisplayName || model.componentTypeCode || '-' }}
        </span>
        <span
          v-if="model.categoryName"
          class="text-xs text-gray-500"
        >
          {{ model.categoryName }}
        </span>
      </div>
    </td>

    <!-- Part Number -->
    <td class="px-4 py-3 text-sm text-gray-600">
      {{ model.partNumber || '-' }}
    </td>

    <!-- SKU -->
    <td class="px-4 py-3 text-sm text-gray-600">
      {{ model.sku || '-' }}
    </td>

    <!-- Status -->
    <td class="px-4 py-3">
      <span
        :class="getStatusBadgeClass(model)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ getLifecycleStatus(model) }}
      </span>
    </td>

    <!-- Release Date -->
    <td class="px-4 py-3 text-sm text-gray-600">
      {{ formatDate(model.releaseDate) }}
    </td>

    <!-- Verified -->
    <td class="px-4 py-3 text-center">
      <i
        :class="model.verified ? 'pi pi-check-circle text-green-600' : 'pi pi-circle text-gray-400'"
        class="text-lg"
        :title="model.verified ? 'Verified' : 'Not Verified'"
      />
    </td>

    <!-- Description (truncated) -->
    <td class="px-4 py-3 text-gray-600 text-sm max-w-xs truncate">
      {{ model.description || '-' }}
    </td>

    <!-- Actions -->
    <td class="px-4 py-3">
      <div class="flex space-x-2">
        <button
          class="text-blue-600 hover:text-blue-800 transition-colors"
          title="View Details"
          @click="$emit('view', model)"
        >
          <i class="pi pi-eye" />
        </button>
        <button
          class="text-green-600 hover:text-green-800 transition-colors"
          title="Edit"
          @click="$emit('edit', model)"
        >
          <i class="pi pi-pencil" />
        </button>
        <button
          class="text-red-600 hover:text-red-800 transition-colors"
          title="Delete"
          @click="$emit('delete', model)"
        >
          <i class="pi pi-trash" />
        </button>
      </div>
    </td>
  </tr>
</template>

<style scoped>
/* Ensure truncate works properly */
.max-w-xs {
  max-width: 20rem;
}
</style>
