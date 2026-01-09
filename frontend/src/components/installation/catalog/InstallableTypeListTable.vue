<script setup>
defineEmits(['view', 'edit', 'delete']);

defineProps({
  installableType: {
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

// Get type category badge styling
const getTypeCategoryBadge = (type) => {
  if (type.deviceType) return 'bg-blue-100 text-blue-800';
  if (type.connectivityType) return 'bg-purple-100 text-purple-800';
  if (type.supportType) return 'bg-orange-100 text-orange-800';
  return 'bg-gray-100 text-gray-800';
};

// Get type category label
const getTypeCategoryLabel = (type) => {
  if (type.deviceType) return 'Device';
  if (type.connectivityType) return 'Connectivity';
  if (type.supportType) return 'Support';
  return 'Other';
};

// Get priority badge styling
const getPriorityBadge = (priority) => {
  const badges = {
    HIGH: 'bg-red-100 text-red-800',
    MEDIUM: 'bg-yellow-100 text-yellow-800',
    LOW: 'bg-green-100 text-green-800'
  };
  return badges[priority] || 'bg-gray-100 text-gray-800';
};

// Get priority label
const getPriorityLabel = (priority) => {
  const labels = {
    HIGH: 'High',
    MEDIUM: 'Medium',
    LOW: 'Low'
  };
  return labels[priority] || priority;
};
</script>

<template>
  <tr class="hover:bg-gray-50 transition-colors">
    <!-- Icon & Name -->
    <td class="px-4 py-3">
      <div class="flex items-center space-x-2">
        <i class="pi pi-box text-xl text-blue-600" />
        <span class="font-medium text-gray-900">{{ installableType.name }}</span>
      </div>
    </td>

    <!-- Code -->
    <td class="px-4 py-3">
      <code class="bg-gray-100 px-2 py-1 rounded text-sm font-mono text-gray-700">
        {{ installableType.code }}
      </code>
    </td>

    <!-- Display Name -->
    <td class="px-4 py-3 text-gray-700">
      {{ installableType.displayName || '-' }}
    </td>

    <!-- Status -->
    <td class="px-4 py-3">
      <span
        :class="getStatusBadgeClass(installableType.active)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ installableType.active !== false ? 'Active' : 'Inactive' }}
      </span>
    </td>

    <!-- Type Category -->
    <td class="px-4 py-3">
      <span
        :class="getTypeCategoryBadge(installableType)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ getTypeCategoryLabel(installableType) }}
      </span>
    </td>

    <!-- Installation Priority -->
    <td class="px-4 py-3 text-center">
      <span
        v-if="installableType.installationPriority"
        :class="getPriorityBadge(installableType.installationPriority)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ getPriorityLabel(installableType.installationPriority) }}
      </span>
      <span
        v-else
        class="text-gray-400 text-sm"
      >-</span>
    </td>

    <!-- Rack Position -->
    <td class="px-4 py-3 text-center">
      <i
        :class="installableType.requiresRackPosition ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Hot Swappable -->
    <td class="px-4 py-3 text-center">
      <i
        :class="installableType.hotSwappable ? 'pi pi-bolt text-yellow-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
        :title="installableType.hotSwappable ? 'Hot swappable' : 'Not hot swappable'"
      />
    </td>

    <!-- Power Management -->
    <td class="px-4 py-3 text-center">
      <i
        :class="installableType.supportsPowerManagement ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Environmental Control -->
    <td class="px-4 py-3 text-center">
      <i
        :class="installableType.requiresEnvironmentalControl ? 'pi pi-sun text-orange-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
        :title="installableType.requiresEnvironmentalControl ? 'Requires environmental control' : 'No special requirements'"
      />
    </td>

    <!-- Description -->
    <td class="px-4 py-3 text-gray-600 text-sm max-w-xs truncate">
      {{ installableType.description || '-' }}
    </td>

    <!-- Actions -->
    <td class="px-4 py-3">
      <div class="flex space-x-2">
        <button
          class="text-blue-600 hover:text-blue-800 transition-colors"
          title="View Details"
          @click="$emit('view', installableType)"
        >
          <i class="pi pi-eye" />
        </button>
        <button
          class="text-green-600 hover:text-green-800 transition-colors"
          title="Edit"
          @click="$emit('edit', installableType)"
        >
          <i class="pi pi-pencil" />
        </button>
        <button
          class="text-red-600 hover:text-red-800 transition-colors"
          title="Delete"
          @click="$emit('delete', installableType)"
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
