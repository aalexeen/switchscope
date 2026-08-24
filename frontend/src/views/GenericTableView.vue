<script setup>
/**
 * GenericTableView - Universal Table View for ALL entities
 *
 * This component provides a unified interface for displaying any table:
 * - Catalog tables (Component Natures, Types, Statuses, etc.)
 * - Entity tables (Components, Devices, etc.)
 *
 * Usage:
 * 1. Define table configuration in configs/tables/
 * 2. Register in tableRegistry
 * 3. Add route with meta.tableKey
 *
 * The rows on screen are one page read from the server, not a slice of a list held in the browser.
 * Searching goes with them: a search box that filtered the page in front of it would find only
 * what happened to be on it. The entity composable is still here, for deleting a row and for the
 * toasts that go with it - what it is no longer used for is holding every row of the table.
 */

import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import GenericSearchBar from '@/components/common/GenericSearchBar.vue';
import GenericListingsTable from '@/components/table/GenericListingsTable.vue';
import TablePager from '@/components/table/TablePager.vue';
import { useEntityPage } from '@/composables/useEntityPage';

// Import table registry
import { tableRegistry, composableRegistry } from '@/configs/tables/tableRegistry';

/** How long the view waits after a keystroke before asking the server. */
const SEARCH_DELAY_MS = 300;

/**
 * Helper function to capitalize first letter
 */
const capitalize = (str) => str.charAt(0).toUpperCase() + str.slice(1);

/**
 * Helper function to capitalize all words
 */
const capitalizeWords = (str) => str.split(' ').map(capitalize).join(' ');

/**
 * Helper function to format field names for display
 */
const formatFieldName = (field) => {
  return field.replace(/([A-Z])/g, ' $1').trim().toLowerCase();
};

// Get table configuration from route meta
const route = useRoute();
const router = useRouter();
const tableKey = route.meta.tableKey;

if (!tableKey) {
  throw new Error('Route meta.tableKey is required for GenericTableView');
}

const config = tableRegistry[tableKey];

if (!config) {
  throw new Error(`Table configuration not found for key: ${tableKey}`);
}

// Get the composable for this table
const composableFactory = composableRegistry[tableKey];

if (!composableFactory) {
  throw new Error(`Composable not found for key: ${tableKey}`);
}

const composable = composableFactory();

/**
 * The composable's delete, found rather than spelled out.
 *
 * A composable names it after the entity's singular - deleteComponentCategory - and the singular
 * was being made here by dropping the last letter of the plural, which gives
 * deleteComponentCategorie, deleteComponentStatuse, deleteInstallationStatuse and
 * deleteNetworkSwitche. Four of the twenty tables therefore answered "Delete operation not
 * available for this table" to every delete, and said it in a toast that reads like a missing
 * feature rather than a bug. Each composable exposes exactly one method whose name begins with
 * delete, so that is what is asked for.
 */
const [, deleteFn] = Object.entries(composable)
  .find(([name, value]) => name.startsWith('delete') && typeof value === 'function') ?? [];

/**
 * The page on screen. searchIn names the fields the server searches, which is what the search bar
 * tells the user it searches - the two are the same list.
 */
const listing = useEntityPage(tableKey, {
  size: config.pageSize || 20,
  searchIn: config.searchFields || []
});

const { rows, total, totalPages, page, size, collectionSize, isLoading, error } = listing;

/**
 * What the table component is handed. Built here rather than written as an object literal in the
 * template on purpose: a template expression unwraps a ref, and this component reads
 * {@code data.value} and destructures the object once, so unwrapped values would arrive frozen at
 * whatever they were on the first render - a spinner that never stops or never starts.
 */
const listingData = {
  data: rows,
  isLoading,
  error,
  fetchData: () => listing.load(),
  total
};

// Search state, debounced: a request per keystroke would answer them out of order as often as not
const searchQuery = ref('');
let pendingSearch = null;

watch(searchQuery, (term) => {
  clearTimeout(pendingSearch);
  pendingSearch = setTimeout(() => listing.setSearch(term), SEARCH_DELAY_MS);
});

onBeforeUnmount(() => clearTimeout(pendingSearch));

const clearSearch = () => {
  searchQuery.value = '';
};

// Event handlers with toast notifications
const toast = useToast();

const handleView = (item) => {
  // Navigate to detail view if route exists
  if (config.routes?.view) {
    const viewPath = config.routes.view.replace(':id', item.id);
    router.push(viewPath);
  } else {
    toast.warning('Detail view not available for this table');
  }
};

const handleEdit = (item) => {
  // Navigate to detail view in edit mode
  if (config.routes?.view) {
    const viewPath = config.routes.view.replace(':id', item.id);
    router.push({ path: viewPath, query: { edit: 'true' } });
  } else {
    toast.warning('Edit view not available for this table');
  }
};

const handleDelete = async (item) => {
  // Check if item can be deleted (for system types, etc.)
  if (item.canBeDeleted === false || item.systemType === true || item.systemCategory === true) {
    toast.warning(`Cannot delete system item: ${item.name || item.displayName}`);
    return;
  }

  // Delete function is called after confirmation in CellActions.vue
  try {
    if (deleteFn) {
      await deleteFn(item.id);
      // The page has a hole in it now, and the rows after it have moved up one
      await listing.load();
    } else {
      toast.error('Delete operation not available for this table');
    }
  } catch {
    // Error toast is shown by composable
  }
};

// Formatted search fields for display
const displaySearchFields = computed(() => {
  return (config.searchFields || ['name']).map(formatFieldName);
});

// Get theme configuration
const theme = config.theme || 'indigo';
const themeIntensity = config.themeIntensity || '500';

// Read the first page on mount
onMounted(() => listing.load());
</script>

<template>
  <!-- Generic Search Bar -->
  <GenericSearchBar
    :entity-name="capitalizeWords(config.entityNamePlural || config.entityName)"
    :search-query="searchQuery"
    :found-count="total"
    :total-count="collectionSize"
    :theme="theme"
    :intensity="themeIntensity"
    :search-fields="displaySearchFields"
    @update:search-query="searchQuery = $event"
    @clear="clearSearch"
  />

  <!-- Generic Listings Table -->
  <div>
    <GenericListingsTable
      :config="config"
      :filtered-data="rows"
      :composable-data="listingData"
      @view="handleView"
      @edit="handleEdit"
      @delete="handleDelete"
    />

    <div class="container-xl lg:container m-auto px-4 pb-10 -mt-6">
      <TablePager
        :page="page"
        :size="size"
        :total="total"
        :total-pages="totalPages"
        :disabled="isLoading"
        @update:page="listing.goToPage($event)"
        @update:size="listing.setSize($event)"
      />
    </div>
  </div>
</template>
