<template>
  <div class="min-h-screen bg-blue-50 py-8">
    <div class="max-w-5xl mx-auto px-4">
      <!-- Loading State -->
      <div v-if="isLoading" class="bg-white rounded-lg shadow p-8 text-center">
        <pulse-loader :loading="true" color="#3b82f6" size="12px"></pulse-loader>
        <p class="text-gray-500 mt-4">Loading details...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bg-white rounded-lg shadow p-8 text-center text-red-500">
        <i class="pi pi-exclamation-triangle text-4xl mb-4"></i>
        <p class="text-lg font-semibold mb-2">Error loading details</p>
        <p class="text-sm text-gray-600 mb-4">{{ error }}</p>
        <button
          @click="loadItem"
          class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded transition-colors"
        >
          Try Again
        </button>
      </div>

      <!-- Detail View -->
      <div v-else-if="item" class="space-y-6">
        <!-- Header Section -->
        <div class="bg-white rounded-lg shadow overflow-hidden">
          <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
            <div class="flex items-center gap-3">
              <button
                @click="goBack"
                class="p-2 hover:bg-gray-100 rounded transition-colors"
                title="Go back"
              >
                <i class="pi pi-arrow-left text-gray-600"></i>
              </button>
              <i
                v-if="item.iconClass"
                :class="[item.iconClass, item.colorClass || 'text-gray-600']"
                class="text-2xl"
              ></i>
              <div>
                <h1 class="text-2xl font-bold text-gray-900">{{ item.name }}</h1>
                <p v-if="item.code" class="text-sm text-gray-500 font-mono">{{ item.code }}</p>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <span
                v-if="item.active !== undefined"
                :class="item.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                class="px-3 py-1 rounded-full text-sm font-medium"
              >
                {{ item.active ? 'Active' : 'Inactive' }}
              </span>
              <button
                @click="handleEdit"
                class="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded transition-colors"
              >
                <i class="pi pi-pencil mr-2"></i>
                Edit
              </button>
            </div>
          </div>
        </div>

        <!-- Basic Information Section -->
        <div v-if="config.sections?.basicInfo" class="bg-white rounded-lg shadow p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Basic Information</h2>
          <dl class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div
              v-for="field in config.sections.basicInfo.fields"
              :key="field.key"
              class="border-b border-gray-100 pb-3"
            >
              <dt class="text-sm font-medium text-gray-500 mb-1">{{ field.label }}</dt>
              <dd class="text-sm text-gray-900">
                <component
                  v-if="field.component"
                  :is="field.component"
                  :value="getFieldValue(item, field.key)"
                  :item="item"
                />
                <span v-else>{{ formatFieldValue(item, field) }}</span>
              </dd>
            </div>
          </dl>
        </div>

        <!-- Properties Section -->
        <div v-if="hasProperties" class="bg-white rounded-lg shadow p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Properties</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
            <div
              v-for="[key, value] in sortedProperties"
              :key="key"
              class="flex items-start gap-2 bg-gray-50 rounded-lg px-3 py-2 border border-gray-200"
            >
              <div class="flex-shrink-0">
                <i class="pi pi-tag text-blue-500 text-xs mt-0.5"></i>
              </div>
              <div class="flex-1 min-w-0">
                <div class="text-xs font-medium text-gray-500 mb-0.5">
                  {{ formatPropertyKey(key) }}
                </div>
                <div class="text-sm text-gray-900 break-words">
                  {{ formatPropertyValue(value) }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Workflow / Status Transitions Section -->
        <div v-if="config.sections?.workflow && hasTransitions" class="bg-white rounded-lg shadow p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">
            <i class="pi pi-arrow-right-arrow-left mr-2 text-purple-500"></i>
            {{ config.sections.workflow.title || 'Workflow Transitions' }}
          </h2>

          <div class="space-y-4">
            <!-- Next Possible Statuses -->
            <div v-if="item.nextPossibleStatusCodes && item.nextPossibleStatusCodes.length > 0">
              <h3 class="text-sm font-medium text-gray-700 mb-2">Can transition to:</h3>
              <RelatedItemsBadges
                :items="item.nextPossibleStatusCodes"
                icon="pi-arrow-right"
                theme="purple"
                empty-message="No transitions available"
              />
            </div>
            <div v-else>
              <p class="text-sm text-gray-500 italic">No transition rules defined</p>
            </div>
          </div>
        </div>

        <!-- Containment Rules Section -->
        <div v-if="config.sections?.containment && hasContainmentRules" class="bg-white rounded-lg shadow p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">
            <i class="pi pi-sitemap mr-2 text-blue-500"></i>
            {{ config.sections.containment.title || 'Containment Rules' }}
          </h2>

          <div class="space-y-4">
            <!-- Allowed Child Types -->
            <div v-if="containmentChildItems.length > 0">
              <h3 class="text-sm font-medium text-gray-700 mb-2">
                {{ containmentLabels.child }}
              </h3>
              <RelatedItemsBadges
                :items="containmentChildItems"
                icon="pi-tag"
                theme="blue"
                empty-message="No allowed child types"
              />
            </div>

            <!-- Allowed Child Categories -->
            <div v-if="containmentCategoryItems.length > 0">
              <h3 class="text-sm font-medium text-gray-700 mb-2">
                {{ containmentLabels.category }}
              </h3>
              <RelatedItemsBadges
                :items="containmentCategoryItems"
                icon="pi-folder"
                theme="green"
                empty-message="No allowed child categories"
              />
            </div>

            <!-- Allowed Parent Types -->
            <div v-if="containmentParentItems.length > 0">
              <h3 class="text-sm font-medium text-gray-700 mb-2">
                {{ containmentLabels.parent }}
              </h3>
              <RelatedItemsBadges
                :items="containmentParentItems"
                icon="pi-sitemap"
                theme="indigo"
                empty-message="No allowed parent types"
              />
            </div>
          </div>
        </div>

        <!-- Related Tables Section -->
        <div
          v-for="relatedSection in config.sections?.relatedTables || []"
          :key="relatedSection.key"
          class="bg-white rounded-lg shadow p-6"
        >
          <h2 class="text-lg font-semibold text-gray-900 mb-4">{{ relatedSection.title }}</h2>
          <GenericListingsTable
            v-if="relatedSection.tableConfig"
            :config="relatedSection.tableConfig"
            :filtered-data="getRelatedData(relatedSection)"
            :show-button="false"
          />
          <p v-else class="text-sm text-gray-500 italic">No related data available</p>
        </div>

        <!-- Audit Information -->
        <div v-if="item.createdAt || item.updatedAt" class="bg-white rounded-lg shadow p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Audit Information</h2>
          <dl class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div v-if="item.createdAt" class="border-b border-gray-100 pb-3">
              <dt class="text-sm font-medium text-gray-500 mb-1">Created At</dt>
              <dd class="text-sm text-gray-900">{{ formatDate(item.createdAt) }}</dd>
            </div>
            <div v-if="item.updatedAt" class="border-b border-gray-100 pb-3">
              <dt class="text-sm font-medium text-gray-500 mb-1">Updated At</dt>
              <dd class="text-sm text-gray-900">{{ formatDate(item.updatedAt) }}</dd>
            </div>
          </dl>
        </div>
      </div>

      <!-- Not Found State -->
      <div v-else class="bg-white rounded-lg shadow p-8 text-center text-gray-500">
        <i class="pi pi-search text-4xl mb-4"></i>
        <p class="text-lg font-semibold mb-2">Item not found</p>
        <button
          @click="goBack"
          class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded transition-colors"
        >
          Go Back
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import GenericListingsTable from '@/components/table/GenericListingsTable.vue';
import RelatedItemsBadges from '@/components/detail/RelatedItemsBadges.vue';
import { detailViewRegistry } from '@/configs/details/detailViewRegistry';
import { composableRegistry } from '@/configs/tables/tableRegistry';

const route = useRoute();
const router = useRouter();

const item = ref(null);
const isLoading = ref(false);
const error = ref(null);

// Get config from route meta
const config = computed(() => {
  const configKey = route.meta?.detailKey;
  if (!configKey) {
    console.error('No detailKey found in route meta');
    return {};
  }
  return detailViewRegistry[configKey] || {};
});

// Get composable from composableRegistry
const composable = computed(() => {
  const tableKey = config.value.tableKey;
  if (!tableKey) {
    console.error('No tableKey found in config');
    return null;
  }

  const composableFactory = composableRegistry[tableKey];
  if (!composableFactory) {
    console.error(`Composable not found for tableKey: ${tableKey}`);
    return null;
  }

  return composableFactory();
});

const allItems = computed(() => {
  const comp = composable.value;
  const tableKey = config.value.tableKey;
  if (!comp || !tableKey) return [];
  const items = comp[tableKey];
  return items?.value || items || [];
});

const relatedItemMap = computed(() => {
  const map = new Map();
  allItems.value.forEach((entry) => {
    if (!entry?.id) return;
    map.set(entry.id, {
      code: entry.code || entry.displayName || entry.name || entry.id,
      label: entry.displayName || entry.name || entry.code || entry.id
    });
  });
  return map;
});

const containmentConfig = computed(() => config.value.sections?.containment || {});
const containmentLabels = computed(() => ({
  child: containmentConfig.value.childLabel || 'Can contain these component types:',
  category: containmentConfig.value.categoryLabel || 'Can contain these categories:',
  parent: containmentConfig.value.parentLabel || 'Can be child of these types:'
}));

const getContainmentItems = (key) => {
  if (!key) return [];
  const values = item.value?.[key];
  if (!Array.isArray(values)) return [];

  if (key.endsWith('Ids')) {
    return values
      .map((id) => relatedItemMap.value.get(id)?.code || id)
      .filter(Boolean);
  }

  return values;
};

const containmentChildItems = computed(() => {
  const key = containmentConfig.value.childKey || 'allowedChildTypeCodes';
  return getContainmentItems(key);
});

const containmentCategoryItems = computed(() => {
  const key = containmentConfig.value.categoryKey || 'allowedChildCategoryCodes';
  return getContainmentItems(key);
});

const containmentParentItems = computed(() => {
  const key = containmentConfig.value.parentKey;
  return getContainmentItems(key);
});

const hasProperties = computed(() => {
  return item.value?.properties && Object.keys(item.value.properties).length > 0;
});

const sortedProperties = computed(() => {
  if (!hasProperties.value) return [];
  return Object.entries(item.value.properties).sort(([a], [b]) => a.localeCompare(b));
});

const hasTransitions = computed(() => {
  return (
    item.value?.nextPossibleStatusCodes &&
    Array.isArray(item.value.nextPossibleStatusCodes)
  );
});

const hasContainmentRules = computed(() => {
  return (
    containmentChildItems.value.length > 0 ||
    containmentCategoryItems.value.length > 0 ||
    containmentParentItems.value.length > 0
  );
});

const loadItem = async () => {
  const id = route.params.id;
  if (!id) {
    error.value = 'No ID provided';
    return;
  }

  isLoading.value = true;
  error.value = null;

  try {
    const comp = composable.value;
    const tableKey = config.value.tableKey;

    if (!comp || !tableKey) {
      throw new Error('Composable not available');
    }

    // Get the data key (e.g., 'componentCategories')
    const dataKey = tableKey;

    // Build fetch function name (e.g., 'fetchComponentCategories')
    const fetchFnName = `fetch${tableKey.charAt(0).toUpperCase() + tableKey.slice(1)}`;

    const fetchFn = comp[fetchFnName];

    if (!fetchFn) {
      throw new Error(`Fetch method ${fetchFnName} not found in composable`);
    }

    await fetchFn();

    // Get items from composable (e.g., comp.componentCategories)
    const items = comp[dataKey];
    const itemsList = items?.value || items;

    item.value = Array.isArray(itemsList)
      ? itemsList.find(i => i.id === id)
      : null;

    if (!item.value) {
      error.value = 'Item not found';
    }
  } catch (err) {
    console.error('Error loading item:', err);
    error.value = err.message || 'Failed to load item';
  } finally {
    isLoading.value = false;
  }
};

const goBack = () => {
  router.back();
};

const handleEdit = () => {
  // TODO: Navigate to edit page
  console.log('Edit:', item.value);
};

const getFieldValue = (item, key) => {
  return item[key];
};

const formatFieldValue = (item, field) => {
  const value = item[field.key];

  if (value === null || value === undefined) {
    return field.fallback || '-';
  }

  if (field.type === 'boolean') {
    return value ? 'Yes' : 'No';
  }

  if (field.format === 'date') {
    return formatDate(value);
  }

  return value;
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const formatPropertyKey = (key) => {
  return key
    .split('_')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
};

const formatPropertyValue = (value) => {
  if (value === 'true') return '✓ Yes';
  if (value === 'false') return '✗ No';
  return value;
};

const getRelatedData = (relatedSection) => {
  // TODO: Implement related data fetching based on section config
  return [];
};

onMounted(() => {
  loadItem();
});
</script>
