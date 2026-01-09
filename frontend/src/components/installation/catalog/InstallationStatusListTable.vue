<script setup>
import { defineProps } from 'vue';

defineProps({
  installationStatus: {
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
  return iconClass || 'pi pi-flag';
};

// Get status category badge
const getStatusCategoryBadge = (category) => {
  const categories = {
    'initial': 'bg-blue-100 text-blue-800',
    'in-progress': 'bg-purple-100 text-purple-800',
    'completed': 'bg-green-100 text-green-800',
    'error': 'bg-red-100 text-red-800',
    'maintenance': 'bg-yellow-100 text-yellow-800',
    'removed': 'bg-gray-100 text-gray-800'
  };
  return categories[category?.toLowerCase()] || 'bg-gray-100 text-gray-800';
};

// Get urgency badge
const getUrgencyBadge = (level) => {
  if (level >= 9) return 'bg-red-100 text-red-800';
  if (level >= 6) return 'bg-orange-100 text-orange-800';
  if (level >= 3) return 'bg-yellow-100 text-yellow-800';
  return 'bg-green-100 text-green-800';
};

// Get urgency label
const getUrgencyLabel = (level) => {
  if (level >= 9) return 'Critical';
  if (level >= 6) return 'High';
  if (level >= 3) return 'Medium';
  return 'Low';
};
</script>

<template>
  <tr class="hover:bg-gray-50 transition-colors">
    <!-- Icon & Name -->
    <td class="px-4 py-3">
      <div class="flex items-center space-x-2">
        <i
          :class="getIconClass(installationStatus.iconClass)"
          class="text-xl text-blue-600"
        />
        <span class="font-medium text-gray-900">{{ installationStatus.name }}</span>
      </div>
    </td>

    <!-- Code -->
    <td class="px-4 py-3">
      <code class="bg-gray-100 px-2 py-1 rounded text-sm font-mono text-gray-700">
        {{ installationStatus.code }}
      </code>
    </td>

    <!-- Display Name -->
    <td class="px-4 py-3 text-gray-700">
      {{ installationStatus.displayName || '-' }}
    </td>

    <!-- Status -->
    <td class="px-4 py-3">
      <span
        :class="getStatusBadgeClass(installationStatus.active)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ installationStatus.active !== false ? 'Active' : 'Inactive' }}
      </span>
    </td>

    <!-- Status Category -->
    <td class="px-4 py-3">
      <span
        v-if="installationStatus.statusCategory"
        :class="getStatusCategoryBadge(installationStatus.statusCategory)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ installationStatus.statusCategory.replace('-', ' ') }}
      </span>
      <span
        v-else
        class="text-gray-400 text-sm"
      >-</span>
    </td>

    <!-- Urgency Level -->
    <td class="px-4 py-3 text-center">
      <span
        v-if="installationStatus.urgencyLevel"
        :class="getUrgencyBadge(installationStatus.urgencyLevel)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ getUrgencyLabel(installationStatus.urgencyLevel) }}
      </span>
      <span
        v-else
        class="text-gray-400 text-sm"
      >-</span>
    </td>

    <!-- Physically Present -->
    <td class="px-4 py-3 text-center">
      <i
        :class="installationStatus.physicallyPresent ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Operational -->
    <td class="px-4 py-3 text-center">
      <i
        :class="installationStatus.operational ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Requires Attention -->
    <td class="px-4 py-3 text-center">
      <i
        :class="installationStatus.requiresAttention ? 'pi pi-exclamation-triangle text-orange-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
        :title="installationStatus.requiresAttention ? 'Requires attention' : 'No attention required'"
      />
    </td>

    <!-- Final Status -->
    <td class="px-4 py-3 text-center">
      <i
        :class="installationStatus.finalStatus ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Description -->
    <td class="px-4 py-3 text-gray-600 text-sm max-w-xs truncate">
      {{ installationStatus.description || '-' }}
    </td>

    <!-- Actions -->
    <td class="px-4 py-3">
      <div class="flex space-x-2">
        <button
          class="text-blue-600 hover:text-blue-800 transition-colors"
          title="View Details"
          @click="$emit('view', installationStatus)"
        >
          <i class="pi pi-eye" />
        </button>
        <button
          class="text-green-600 hover:text-green-800 transition-colors"
          title="Edit"
          @click="$emit('edit', installationStatus)"
        >
          <i class="pi pi-pencil" />
        </button>
        <button
          class="text-red-600 hover:text-red-800 transition-colors"
          title="Delete"
          @click="$emit('delete', installationStatus)"
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
