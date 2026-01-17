<script setup>
import { computed, ref, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import GenericListTable from './GenericListTable.vue';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

/**
 * GenericListingsTable - A configurable table component
 *
 * This component renders a data table based on a configuration object.
 * It supports features like:
 * - Column visibility toggle (show all / show default)
 * - Loading states
 * - Error handling
 * - Empty states
 * - Refresh functionality
 * - Statistics display
 * - Responsive design
 *
 * @emits view - Emitted when view action is triggered on an item
 * @emits edit - Emitted when edit action is triggered on an item
 * @emits delete - Emitted when delete action is triggered on an item
 */

const props = defineProps({
  config: {
    type: Object,
    required: true,
    validator: (value) => {
      // Validate required config properties
      return value.entityName &&
             value.entityNamePlural &&
             value.columns &&
             Array.isArray(value.columns);
    }
  },
  limit: {
    type: Number,
    default: null
  },
  showButton: {
    type: Boolean,
    default: false
  },
  filteredData: {
    type: Array,
    default: null
  },
  composableData: {
    type: Object,
    required: true,
    validator: (value) => {
      // Validate required composable properties
      return value.data !== undefined &&
             value.isLoading !== undefined &&
             value.error !== undefined &&
             typeof value.fetchData === 'function' &&
             value.total !== undefined;
    }
  }
});

defineEmits(['view', 'edit', 'delete']);

// Extract composable data
const { data, isLoading, error, fetchData, total } = props.composableData;

// Toggle for showing all columns vs only visible ones
const showAllColumns = ref(false);
const editEnabled = ref(false);

/**
 * Compute visible columns based on showAllColumns toggle
 */
const visibleColumns = computed(() => {
  if (showAllColumns.value) {
    return props.config.columns;
  }
  return props.config.columns.filter(col => col.visible);
});

/**
 * Compute displayed data (with limit and filtering)
 */
const displayedData = computed(() => {
  const items = props.filteredData !== null ? props.filteredData : data.value;
  return props.limit ? items.slice(0, props.limit) : items;
});

/**
 * Count of hidden columns
 */
const hiddenColumnsCount = computed(() => {
  return props.config.columns.filter(col => !col.visible).length;
});

/**
 * Initialize data on mount
 */
onMounted(async () => {
  try {
    await fetchData();
  } catch {
    // Error handling - composable will show toast notification
  }
});
</script>

<template>
  <section class="bg-blue-50 px-4 py-10">
    <div class="container-xl lg:container m-auto">
      <h2 class="text-3xl font-bold text-indigo-500 mb-6 text-center">
        Browse {{ config.entityNamePlural }} - Catalog
      </h2>

      <!-- Statistics and Controls -->
      <div class="bg-white rounded-lg shadow p-4 mb-6">
        <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:gap-3">
          <button
            class="px-4 py-2 rounded text-sm transition-colors order-1"
            :class="editEnabled
              ? 'bg-red-600 hover:bg-red-700 text-white'
              : 'bg-red-600/20 text-red-600 hover:bg-red-600/30 opacity-60'"
            title="Toggle edit actions"
            :aria-pressed="editEnabled"
            @click="editEnabled = !editEnabled"
          >
            <i class="pi pi-pencil" />
            Edit on/off
          </button>
          <div class="flex space-x-2 order-2 sm:order-3 sm:ml-auto">
            <!-- Toggle Show All Columns Button -->
            <button
              v-if="hiddenColumnsCount > 0"
              class="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded text-sm transition-colors"
              @click="showAllColumns = !showAllColumns"
            >
              <i :class="showAllColumns ? 'pi pi-eye-slash' : 'pi pi-eye'" />
              {{ showAllColumns ? 'Show Less' : `Show All (${hiddenColumnsCount} hidden)` }}
            </button>
            <!-- Refresh Button -->
            <button
              :disabled="isLoading"
              class="bg-blue-500 hover:bg-blue-600 disabled:bg-gray-400 text-white px-4 py-2 rounded text-sm transition-colors"
              @click="fetchData(true)"
            >
              <i class="pi pi-refresh" />
              {{ isLoading ? 'Refreshing...' : 'Refresh' }}
            </button>
          </div>
          <div class="text-sm text-gray-600 whitespace-nowrap order-3 sm:order-2">
            <span class="font-medium">
              Displaying: {{ displayedData.length }} {{ config.entityKeyPlural }}
            </span>
            <span
              v-if="limit"
              class="ml-4"
            >
              (Limit: {{ Math.min(limit, displayedData.length) }} of {{ total }})
            </span>
          </div>
        </div>
      </div>

      <!-- Error State -->
      <div
        v-if="error"
        class="bg-white rounded-lg shadow p-6 text-center text-red-500"
      >
        <i class="pi pi-exclamation-triangle text-4xl mb-4" />
        <p class="mb-4">
          Error loading {{ config.entityNamePlural }}. Please try again.
        </p>
        <button
          class="bg-indigo-500 hover:bg-indigo-600 text-white px-4 py-2 rounded transition-colors"
          @click="fetchData(true)"
        >
          <i class="pi pi-refresh mr-2" />
          Retry
        </button>
      </div>

      <!-- Loading State -->
      <div
        v-else-if="isLoading"
        class="bg-white rounded-lg shadow p-8 text-center text-gray-500"
      >
        <PulseLoader />
        <p class="mt-4">
          Loading {{ config.entityNamePlural }}...
        </p>
      </div>

      <!-- Data Table -->
      <div
        v-else-if="displayedData.length > 0"
        class="bg-white rounded-lg shadow overflow-hidden"
      >
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th
                  v-for="col in visibleColumns"
                  :key="col.key"
                  :class="[
                    'px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider',
                    col.align === 'center' ? 'text-center' : 'text-left'
                  ]"
                  :title="col.headerTitle"
                >
                  {{ col.label }}
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <GenericListTable
                v-for="item in displayedData"
                :key="item.id"
                :item="item"
                :config="config"
                :visible-columns="visibleColumns"
                :edit-enabled="editEnabled"
                @view="(i) => $emit('view', i)"
                @edit="(i) => $emit('edit', i)"
                @delete="(i) => $emit('delete', i)"
              />
            </tbody>
          </table>
        </div>
      </div>

      <!-- Empty State -->
      <div
        v-else
        class="bg-white rounded-lg shadow p-8 text-center text-gray-500"
      >
        <i class="pi pi-inbox text-4xl mb-4" />
        <p>No {{ config.entityNamePlural }} found.</p>
      </div>
    </div>
  </section>

  <!-- View All Button -->
  <section
    v-if="showButton"
    class="m-auto max-w-lg my-10 px-6"
  >
    <RouterLink
      :to="config.routes.list"
      class="block bg-black text-white text-center py-4 px-6 rounded-xl hover:bg-gray-700 transition-colors"
    >
      View All {{ config.entityNamePlural }}
    </RouterLink>
  </section>
</template>
