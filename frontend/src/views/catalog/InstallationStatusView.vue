<script setup>
import InstallationStatusSearchBar from '@/components/installation/catalog/InstallationStatusSearchBar.vue';
import InstallationStatusListingsTable from '@/components/installation/catalog/InstallationStatusListingsTable.vue';
import { ref, computed } from 'vue';
import { useInstallationStatuses } from '@/composables/useInstallationStatuses';
import { useToast } from 'vue-toastification';

const toast = useToast();

// Get search functionality from composable
const { searchInstallationStatuses, installationStatuses, totalInstallationStatuses } = useInstallationStatuses();

// Reactive state for search
const searchQuery = ref('');

// Computed property for filtered installation statuses
const filteredStatuses = computed(() => {
  return searchInstallationStatuses(searchQuery.value);
});

// Clear search
const clearSearch = () => {
  searchQuery.value = '';
};

// Event handlers
const handleView = (installationStatus) => {
  // TODO: Implement view details modal or navigate to details page
  toast.info(`View details for: ${installationStatus.name}`);
  console.log('View installation status:', installationStatus);
};

const handleEdit = (installationStatus) => {
  // TODO: Implement edit modal or navigate to edit page
  toast.info(`Edit: ${installationStatus.name}`);
  console.log('Edit installation status:', installationStatus);
};

const handleDelete = (installationStatus) => {
  // TODO: Implement delete confirmation dialog
  toast.warning(`Delete action for: ${installationStatus.name} (not implemented yet)`);
  console.log('Delete installation status:', installationStatus);
};
</script>

<template>
  <!-- Search Bar Component -->
  <InstallationStatusSearchBar
    :search-query="searchQuery"
    :found-count="filteredStatuses.length"
    :total-count="totalInstallationStatuses"
    @update:search-query="searchQuery = $event"
    @clear="clearSearch"
  />

  <!-- Installation Status Listings Table -->
  <InstallationStatusListingsTable
    :filtered-statuses="filteredStatuses"
    @view="handleView"
    @edit="handleEdit"
    @delete="handleDelete"
  />
</template>
