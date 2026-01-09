<script setup>
import { defineProps } from 'vue';

defineEmits(['view', 'edit', 'delete']);

defineProps({
  status: {
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
  return iconClass || 'pi pi-circle';
};

// Get badge class for lifecycle phase
const getLifecycleBadgeClass = (phase) => {
  const phases = {
    'planning': 'bg-blue-100 text-blue-800',
    'procurement': 'bg-purple-100 text-purple-800',
    'deployment': 'bg-indigo-100 text-indigo-800',
    'operational': 'bg-green-100 text-green-800',
    'maintenance': 'bg-yellow-100 text-yellow-800',
    'decommission': 'bg-red-100 text-red-800',
    'disposal': 'bg-gray-100 text-gray-800'
  };
  return phases[phase?.toLowerCase()] || 'bg-gray-100 text-gray-800';
};
</script>

<template>
  <tr class="hover:bg-gray-50 transition-colors">
    <!-- Icon & Name -->
    <td class="px-4 py-3">
      <div class="flex items-center space-x-2">
        <i
          :class="[getIconClass(status.iconClass), status.colorClass]"
          class="text-xl"
        />
        <span class="font-medium text-gray-900">{{ status.name }}</span>
      </div>
    </td>

    <!-- Code -->
    <td class="px-4 py-3">
      <code class="bg-gray-100 px-2 py-1 rounded text-sm font-mono text-gray-700">
        {{ status.code }}
      </code>
    </td>

    <!-- Display Name -->
    <td class="px-4 py-3 text-gray-700">
      {{ status.displayName || '-' }}
    </td>

    <!-- Status -->
    <td class="px-4 py-3">
      <span
        :class="getStatusBadgeClass(status.active)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ status.active !== false ? 'Active' : 'Inactive' }}
      </span>
    </td>

    <!-- Lifecycle Phase -->
    <td class="px-4 py-3">
      <span
        v-if="status.lifecyclePhase"
        :class="getLifecycleBadgeClass(status.lifecyclePhase)"
        class="px-2 py-1 rounded-full text-xs font-medium capitalize"
      >
        {{ status.lifecyclePhase }}
      </span>
      <span
        v-else
        class="text-gray-400 text-sm"
      >-</span>
    </td>

    <!-- Available -->
    <td class="px-4 py-3 text-center">
      <i
        :class="status.available ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Operational -->
    <td class="px-4 py-3 text-center">
      <i
        :class="status.operational ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Physically Present -->
    <td class="px-4 py-3 text-center">
      <i
        :class="status.physicallyPresent ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- In Inventory -->
    <td class="px-4 py-3 text-center">
      <i
        :class="status.inInventory ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Can Accept Installations -->
    <td class="px-4 py-3 text-center">
      <i
        :class="status.canAcceptInstallations ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Requires Attention -->
    <td class="px-4 py-3 text-center">
      <i
        :class="status.requiresAttention ? 'pi pi-exclamation-triangle text-orange-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
        :title="status.requiresAttention ? 'Requires attention' : 'No attention required'"
      />
    </td>

    <!-- In Transition -->
    <td class="px-4 py-3 text-center">
      <i
        :class="status.inTransition ? 'pi pi-sync text-blue-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Description -->
    <td class="px-4 py-3 text-gray-600 text-sm max-w-xs truncate">
      {{ status.description || '-' }}
    </td>

    <!-- Actions -->
    <td class="px-4 py-3">
      <div class="flex space-x-2">
        <button
          class="text-blue-600 hover:text-blue-800 transition-colors"
          title="View Details"
          @click="$emit('view', status)"
        >
          <i class="pi pi-eye" />
        </button>
        <button
          class="text-green-600 hover:text-green-800 transition-colors"
          title="Edit"
          @click="$emit('edit', status)"
        >
          <i class="pi pi-pencil" />
        </button>
        <button
          class="text-red-600 hover:text-red-800 transition-colors"
          title="Delete"
          @click="$emit('delete', status)"
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
