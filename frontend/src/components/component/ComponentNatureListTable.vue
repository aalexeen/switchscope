<script setup>
import { defineProps } from 'vue';

const props = defineProps({
  nature: {
    type: Object,
    required: true
  }
});

// Helper to format boolean values
const formatBoolean = (value) => value ? 'Yes' : 'No';

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
</script>

<template>
  <tr class="hover:bg-gray-50 transition-colors">
    <!-- Icon & Name -->
    <td class="px-4 py-3">
      <div class="flex items-center space-x-2">
        <i
          :class="[getIconClass(nature.iconClass), nature.colorClass]"
          class="text-xl"
        ></i>
        <span class="font-medium text-gray-900">{{ nature.name }}</span>
      </div>
    </td>

    <!-- Code -->
    <td class="px-4 py-3">
      <code class="bg-gray-100 px-2 py-1 rounded text-sm font-mono text-gray-700">
        {{ nature.code }}
      </code>
    </td>

    <!-- Display Name -->
    <td class="px-4 py-3 text-gray-700">
      {{ nature.displayName || '-' }}
    </td>

    <!-- Status -->
    <td class="px-4 py-3">
      <span
        :class="getStatusBadgeClass(nature.active)"
        class="px-2 py-1 rounded-full text-xs font-medium"
      >
        {{ nature.active !== false ? 'Active' : 'Inactive' }}
      </span>
    </td>

    <!-- Requires Management -->
    <td class="px-4 py-3 text-center">
      <i
        :class="nature.requiresManagement ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      ></i>
    </td>

    <!-- Has IP Address -->
    <td class="px-4 py-3 text-center">
      <i
        :class="nature.canHaveIpAddress ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      ></i>
    </td>

    <!-- Has Firmware -->
    <td class="px-4 py-3 text-center">
      <i
        :class="nature.hasFirmware ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      ></i>
    </td>

    <!-- Processes Traffic -->
    <td class="px-4 py-3 text-center">
      <i
        :class="nature.processesNetworkTraffic ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      ></i>
    </td>

    <!-- SNMP -->
    <td class="px-4 py-3 text-center">
      <i
        :class="nature.supportsSnmpMonitoring ? 'pi pi-check text-green-600' : 'pi pi-times text-gray-400'"
        class="text-lg"
      ></i>
    </td>

    <!-- Power -->
    <td class="px-4 py-3">
      <span class="text-sm text-gray-600 capitalize">
        {{ nature.powerConsumptionCategory || 'none' }}
      </span>
    </td>

    <!-- Description -->
    <td class="px-4 py-3 text-gray-600 text-sm max-w-xs truncate">
      {{ nature.description || '-' }}
    </td>

    <!-- Actions -->
    <td class="px-4 py-3">
      <div class="flex space-x-2">
        <button
          @click="$emit('view', nature)"
          class="text-blue-600 hover:text-blue-800 transition-colors"
          title="View Details">
          <i class="pi pi-eye"></i>
        </button>
        <button
          @click="$emit('edit', nature)"
          class="text-green-600 hover:text-green-800 transition-colors"
          title="Edit">
          <i class="pi pi-pencil"></i>
        </button>
        <button
          @click="$emit('delete', nature)"
          class="text-red-600 hover:text-red-800 transition-colors"
          title="Delete">
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
