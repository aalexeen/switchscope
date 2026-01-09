<script setup>
import { defineProps } from 'vue';

const props = defineProps({
  category: {
    type: Object,
    required: true
  }
});

// Get badge color based on activity status
const getStatusBadgeClass = (active) => {
  return active !== false
    ? 'bg-green-100 text-green-800'
    : 'bg-gray-100 text-gray-800';
};

// Get icon class with fallback
const getIconClass = (iconClass) => {
  return iconClass || 'pi pi-folder';
};

// Get badge class for category type
const getCategoryTypeBadge = (category) => {
  if (category.housingCategory) return 'bg-purple-100 text-purple-800';
  if (category.connectivityCategory) return 'bg-blue-100 text-blue-800';
  if (category.supportCategory) return 'bg-orange-100 text-orange-800';
  if (category.moduleCategory) return 'bg-teal-100 text-teal-800';
  return 'bg-gray-100 text-gray-800';
};

// Get category type label
const getCategoryTypeLabel = (category) => {
  if (category.housingCategory) return 'Housing';
  if (category.connectivityCategory) return 'Connectivity';
  if (category.supportCategory) return 'Support';
  if (category.moduleCategory) return 'Module';
  return 'Other';
};
</script>

<template>
  <tr class="hover:bg-gray-50 transition-colors">
    <!-- Icon & Name -->
    <td class="px-4 py-3">
      <div class="flex items-center space-x-2">
        <i
          :class="[getIconClass(category.iconClass), category.colorClass]"
          class="text-xl"
        ></i>
        <span class="font-medium text-gray-900">{{ category.name }}</span>
      </div>
    </td>

    <!-- Code -->
    <td class="px-4 py-3">
      <code class="bg-gray-100 px-2 py-1 rounded text-sm font-mono text-gray-700">
        {{ category.code }}
      </code>
    </td>

    <!-- Display Name -->
    <td class="px-4 py-3 text-gray-700">
      {{ category.displayName || '-' }}
    </td>

    <!-- Status -->
    <td class="px-4 py-3">
      <span
        :class="getStatusBadgeClass(category.active)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ category.active !== false ? 'Active' : 'Inactive' }}
      </span>
    </td>

    <!-- Category Type -->
    <td class="px-4 py-3">
      <span
        :class="getCategoryTypeBadge(category)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ getCategoryTypeLabel(category) }}
      </span>
    </td>

    <!-- System Category -->
    <td class="px-4 py-3 text-center">
      <i
        :class="category.systemCategory ? 'pi pi-lock text-yellow-600' : 'pi pi-unlock text-gray-400'"
        class="text-lg"
        :title="category.systemCategory ? 'System category (protected)' : 'User category'"
      ></i>
    </td>

    <!-- Infrastructure -->
    <td class="px-4 py-3 text-center">
      <i
        :class="category.infrastructure ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      ></i>
    </td>

    <!-- Component Types Count -->
    <td class="px-4 py-3 text-center">
      <span class="text-sm text-gray-700 font-medium">
        {{ category.componentTypeCount || 0 }}
      </span>
    </td>

    <!-- Description -->
    <td class="px-4 py-3 text-gray-600 text-sm max-w-xs truncate">
      {{ category.description || '-' }}
    </td>

    <!-- Actions -->
    <td class="px-4 py-3">
      <div class="flex space-x-2">
        <button
          @click="$emit('view', category)"
          class="text-blue-600 hover:text-blue-800 transition-colors"
          title="View Details">
          <i class="pi pi-eye"></i>
        </button>
        <button
          @click="$emit('edit', category)"
          class="text-green-600 hover:text-green-800 transition-colors"
          title="Edit">
          <i class="pi pi-pencil"></i>
        </button>
        <button
          @click="$emit('delete', category)"
          class="text-red-600 hover:text-red-800 transition-colors"
          title="Delete"
          :disabled="!category.canBeDeleted"
          :class="{ 'opacity-50 cursor-not-allowed': !category.canBeDeleted }">
          <i class="pi pi-trash"></i>
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
