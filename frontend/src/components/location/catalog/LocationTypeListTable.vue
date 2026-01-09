<script setup>
import { defineProps } from 'vue';

defineProps({
  locationType: {
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
  return iconClass || 'pi pi-map-marker';
};

// Get badge class for hierarchy level
const getHierarchyBadgeClass = (level) => {
  if (level === 1) return 'bg-purple-100 text-purple-800';
  if (level === 2) return 'bg-blue-100 text-blue-800';
  if (level === 3) return 'bg-teal-100 text-teal-800';
  if (level >= 4) return 'bg-gray-100 text-gray-800';
  return 'bg-gray-100 text-gray-800';
};

// Get location category badge
const getCategoryBadgeClass = (locationType) => {
  if (locationType.buildingLike) return 'bg-indigo-100 text-indigo-800';
  if (locationType.roomLike) return 'bg-cyan-100 text-cyan-800';
  if (locationType.rackLike) return 'bg-orange-100 text-orange-800';
  if (locationType.datacenterLike) return 'bg-red-100 text-red-800';
  return 'bg-gray-100 text-gray-800';
};

const getCategoryLabel = (locationType) => {
  if (locationType.buildingLike) return 'Building';
  if (locationType.roomLike) return 'Room';
  if (locationType.rackLike) return 'Rack';
  if (locationType.datacenterLike) return 'Datacenter';
  return 'Other';
};
</script>

<template>
  <tr class="hover:bg-gray-50 transition-colors">
    <!-- Icon & Name -->
    <td class="px-4 py-3">
      <div class="flex items-center space-x-2">
        <i
          :class="getIconClass(locationType.iconClass)"
          class="text-xl text-blue-600"
        />
        <span class="font-medium text-gray-900">{{ locationType.name }}</span>
      </div>
    </td>

    <!-- Code -->
    <td class="px-4 py-3">
      <code class="bg-gray-100 px-2 py-1 rounded text-sm font-mono text-gray-700">
        {{ locationType.code }}
      </code>
    </td>

    <!-- Display Name -->
    <td class="px-4 py-3 text-gray-700">
      {{ locationType.displayName || '-' }}
    </td>

    <!-- Status -->
    <td class="px-4 py-3">
      <span
        :class="getStatusBadgeClass(locationType.active)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ locationType.active !== false ? 'Active' : 'Inactive' }}
      </span>
    </td>

    <!-- Hierarchy Level -->
    <td class="px-4 py-3 text-center">
      <span
        v-if="locationType.hierarchyLevel"
        :class="getHierarchyBadgeClass(locationType.hierarchyLevel)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        Level {{ locationType.hierarchyLevel }}
      </span>
      <span
        v-else
        class="text-gray-400 text-sm"
      >-</span>
    </td>

    <!-- Category -->
    <td class="px-4 py-3">
      <span
        :class="getCategoryBadgeClass(locationType)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ getCategoryLabel(locationType) }}
      </span>
    </td>

    <!-- Can Have Children -->
    <td class="px-4 py-3 text-center">
      <i
        :class="locationType.canHaveChildren ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Can Hold Equipment -->
    <td class="px-4 py-3 text-center">
      <i
        :class="locationType.canHoldEquipment ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Physical Location -->
    <td class="px-4 py-3 text-center">
      <i
        :class="locationType.physicalLocation ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Requires Address -->
    <td class="px-4 py-3 text-center">
      <i
        :class="locationType.requiresAddress ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      />
    </td>

    <!-- Secure Location -->
    <td class="px-4 py-3 text-center">
      <i
        :class="locationType.secureLocation ? 'pi pi-lock text-yellow-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
        :title="locationType.secureLocation ? 'Secure location' : 'Not secure'"
      />
    </td>

    <!-- Rack Units -->
    <td class="px-4 py-3 text-center">
      <span
        v-if="locationType.defaultRackUnits"
        class="text-sm text-gray-700"
      >
        {{ locationType.defaultRackUnits }}U
      </span>
      <span
        v-else
        class="text-gray-400 text-sm"
      >-</span>
    </td>

    <!-- Description -->
    <td class="px-4 py-3 text-gray-600 text-sm max-w-xs truncate">
      {{ locationType.description || '-' }}
    </td>

    <!-- Actions -->
    <td class="px-4 py-3">
      <div class="flex space-x-2">
        <button
          class="text-blue-600 hover:text-blue-800 transition-colors"
          title="View Details"
          @click="$emit('view', locationType)"
        >
          <i class="pi pi-eye" />
        </button>
        <button
          class="text-green-600 hover:text-green-800 transition-colors"
          title="Edit"
          @click="$emit('edit', locationType)"
        >
          <i class="pi pi-pencil" />
        </button>
        <button
          class="text-red-600 hover:text-red-800 transition-colors"
          title="Delete"
          @click="$emit('delete', locationType)"
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
