<script setup>
import { defineProps } from 'vue';

defineProps({
  type: {
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
  return iconClass || 'pi pi-box';
};

// Get badge class for component type category
const getTypeCategoryBadge = (type) => {
  if (type.housingComponent) return 'bg-purple-100 text-purple-800';
  if (type.connectivityComponent) return 'bg-blue-100 text-blue-800';
  if (type.supportComponent) return 'bg-orange-100 text-orange-800';
  if (type.moduleComponent) return 'bg-teal-100 text-teal-800';
  return 'bg-gray-100 text-gray-800';
};

// Get type category label
const getTypeCategoryLabel = (type) => {
  if (type.housingComponent) return 'Housing';
  if (type.connectivityComponent) return 'Connectivity';
  if (type.supportComponent) return 'Support';
  if (type.moduleComponent) return 'Module';
  return 'Other';
};
</script>

<template>
  <tr class="hover:bg-gray-50 transition-colors">
    <!-- Icon & Name -->
    <td class="px-4 py-3">
      <div class="flex items-center space-x-2">
        <i
          :class="[getIconClass(type.iconClass), type.colorClass]"
          class="text-xl"
        />
        <span class="font-medium text-gray-900">{{ type.name }}</span>
      </div>
    </td>

    <!-- Code -->
    <td class="px-4 py-3">
      <code class="bg-gray-100 px-2 py-1 rounded text-sm font-mono text-gray-700">
        {{ type.code }}
      </code>
    </td>

    <!-- Display Name -->
    <td class="px-4 py-3 text-gray-700">
      {{ type.displayName || '-' }}
    </td>

    <!-- Category -->
    <td class="px-4 py-3">
      <div class="text-sm">
        <div class="font-medium text-gray-900">
          {{ type.categoryDisplayName || type.categoryCode || '-' }}
        </div>
        <code class="text-xs text-gray-500">{{ type.categoryCode }}</code>
      </div>
    </td>

    <!-- Status -->
    <td class="px-4 py-3">
      <span
        :class="getStatusBadgeClass(type.active)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ type.active !== false ? 'Active' : 'Inactive' }}
      </span>
    </td>

    <!-- Type Category -->
    <td class="px-4 py-3">
      <span
        :class="getTypeCategoryBadge(type)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ getTypeCategoryLabel(type) }}
      </span>
    </td>

    <!-- System Type -->
    <td class="px-4 py-3 text-center">
      <i
        :class="type.systemType ? 'pi pi-lock text-yellow-600' : 'pi pi-unlock text-gray-400'"
        class="text-lg"
        :title="type.systemType ? 'System type (protected)' : 'User type'"
      />
    </td>

    <!-- Requires Management -->
    <td class="px-4 py-3 text-center">
      <i
        :class="type.requiresManagement ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Has IP Address -->
    <td class="px-4 py-3 text-center">
      <i
        :class="type.canHaveIpAddress ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- SNMP -->
    <td class="px-4 py-3 text-center">
      <i
        :class="type.supportsSnmp ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Rack Space -->
    <td class="px-4 py-3 text-center">
      <div
        v-if="type.requiresRackSpace"
        class="text-sm"
      >
        <i class="pi pi-check text-green-600 text-lg" />
        <div
          v-if="type.typicalRackUnits"
          class="text-xs text-gray-600"
        >
          {{ type.typicalRackUnits }}U
        </div>
      </div>
      <i
        v-else
        class="pi pi-times text-gray-400 text-lg"
      />
    </td>

    <!-- Description -->
    <td class="px-4 py-3 text-gray-600 text-sm max-w-xs truncate">
      {{ type.description || '-' }}
    </td>

    <!-- Actions -->
    <td class="px-4 py-3">
      <div class="flex space-x-2">
        <button
          class="text-blue-600 hover:text-blue-800 transition-colors"
          title="View Details"
          @click="$emit('view', type)"
        >
          <i class="pi pi-eye" />
        </button>
        <button
          class="text-green-600 hover:text-green-800 transition-colors"
          title="Edit"
          @click="$emit('edit', type)"
        >
          <i class="pi pi-pencil" />
        </button>
        <button
          class="text-red-600 hover:text-red-800 transition-colors"
          title="Delete"
          :disabled="!type.canBeDeleted"
          :class="{ 'opacity-50 cursor-not-allowed': !type.canBeDeleted }"
          @click="$emit('delete', type)"
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
