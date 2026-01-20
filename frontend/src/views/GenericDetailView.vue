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
                @click="handleBack"
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
                <h1 class="text-2xl font-bold text-gray-900">
                  {{ isEditMode ? `Edit: ${item.name}` : item.name }}
                </h1>
                <p v-if="item.code" class="text-sm text-gray-500 font-mono">{{ item.code }}</p>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <!-- View Mode Buttons -->
              <template v-if="!isEditMode">
                <span
                  v-if="item.active !== undefined"
                  :class="item.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                  class="px-3 py-1 rounded-full text-sm font-medium"
                >
                  {{ item.active ? 'Active' : 'Inactive' }}
                </span>
                <button
                  @click="enterEditMode"
                  class="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded transition-colors"
                >
                  <i class="pi pi-pencil mr-2"></i>
                  Edit
                </button>
              </template>

              <!-- Edit Mode Buttons -->
              <template v-else>
                <button
                  @click="cancelEdit"
                  :disabled="isSaving"
                  class="px-4 py-2 border border-gray-300 text-gray-700 hover:bg-gray-50 rounded transition-colors disabled:opacity-50"
                >
                  <i class="pi pi-times mr-2"></i>
                  Cancel
                </button>
                <button
                  @click="saveChanges"
                  :disabled="isSaving || !hasChanges"
                  class="px-4 py-2 bg-green-500 hover:bg-green-600 text-white rounded transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <i :class="['pi mr-2', isSaving ? 'pi-spinner pi-spin' : 'pi-check']"></i>
                  {{ isSaving ? 'Saving...' : 'Save' }}
                </button>
              </template>
            </div>
          </div>

          <!-- Unsaved Changes Warning -->
          <div
            v-if="isEditMode && hasChanges"
            class="px-6 py-2 bg-yellow-50 border-b border-yellow-200 flex items-center gap-2 text-sm text-yellow-800"
          >
            <i class="pi pi-exclamation-triangle"></i>
            You have unsaved changes
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
              <dt class="text-sm font-medium text-gray-500 mb-1 flex items-center gap-1">
                {{ field.label }}
                <span v-if="field.required && isEditMode" class="text-red-500">*</span>
              </dt>
              <dd class="text-sm text-gray-900">
                <!-- Edit Mode -->
                <EditFieldRenderer
                  v-if="isEditMode"
                  v-model="editForm[field.key]"
                  :field="field"
                  :disabled="isSaving"
                />
                <!-- View Mode -->
                <template v-else>
                  <component
                    v-if="field.component"
                    :is="field.component"
                    :value="getFieldValue(item, field.key)"
                    :item="item"
                  />
                  <span v-else>{{ formatFieldValue(item, field) }}</span>
                </template>
              </dd>
            </div>
          </dl>
        </div>

        <!-- Properties Section (View only - editing properties is complex) -->
        <div v-if="hasProperties && !isEditMode" class="bg-white rounded-lg shadow p-6">
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

        <!-- Workflow / Status Transitions Section (View only) -->
        <div v-if="config.sections?.workflow && hasTransitions && !isEditMode" class="bg-white rounded-lg shadow p-6">
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

        <!-- Containment Rules Section (View only) -->
        <div v-if="config.sections?.containment && hasContainmentRules && !isEditMode" class="bg-white rounded-lg shadow p-6">
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

        <!-- Related Tables Section (View only) -->
        <div
          v-for="relatedSection in config.sections?.relatedTables || []"
          v-if="!isEditMode"
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

        <!-- Audit Information (View only) -->
        <div v-if="(item.createdAt || item.updatedAt) && !isEditMode" class="bg-white rounded-lg shadow p-6">
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
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import GenericListingsTable from '@/components/table/GenericListingsTable.vue';
import RelatedItemsBadges from '@/components/detail/RelatedItemsBadges.vue';
import EditFieldRenderer from '@/components/form/EditFieldRenderer.vue';
import { detailViewRegistry } from '@/configs/details/detailViewRegistry';
import { composableRegistry } from '@/configs/tables/tableRegistry';

const route = useRoute();
const router = useRouter();
const toast = useToast();

const item = ref(null);
const isLoading = ref(false);
const error = ref(null);

// Edit mode state
const isEditMode = ref(false);
const editForm = ref({});
const originalForm = ref({});
const isSaving = ref(false);

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

// Check if form has changes
const hasChanges = computed(() => {
  if (!isEditMode.value) return false;

  const fields = config.value.sections?.basicInfo?.fields || [];
  for (const field of fields) {
    if (field.editable === false) continue;

    const originalValue = originalForm.value[field.key];
    const currentValue = editForm.value[field.key];

    // Handle null/undefined comparison
    const orig = originalValue ?? null;
    const curr = currentValue ?? null;

    if (orig !== curr) {
      return true;
    }
  }
  return false;
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

    // Check for edit mode from query param
    if (route.query.edit === 'true' && item.value) {
      enterEditMode();
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

// Handle back button with unsaved changes check
const handleBack = () => {
  if (isEditMode.value && hasChanges.value) {
    if (confirm('You have unsaved changes. Are you sure you want to leave?')) {
      exitEditMode();
      goBack();
    }
  } else {
    if (isEditMode.value) {
      exitEditMode();
    } else {
      goBack();
    }
  }
};

// Enter edit mode
const enterEditMode = () => {
  if (!item.value) return;

  // Initialize edit form with current values
  const fields = config.value.sections?.basicInfo?.fields || [];
  const formData = {};

  fields.forEach(field => {
    formData[field.key] = item.value[field.key] ?? null;
  });

  editForm.value = { ...formData };
  originalForm.value = { ...formData };
  isEditMode.value = true;

  // Update URL with edit query param
  router.replace({
    ...route,
    query: { ...route.query, edit: 'true' }
  });
};

// Exit edit mode
const exitEditMode = () => {
  isEditMode.value = false;
  editForm.value = {};
  originalForm.value = {};

  // Remove edit query param from URL
  const query = { ...route.query };
  delete query.edit;
  router.replace({ ...route, query });
};

// Cancel edit
const cancelEdit = () => {
  if (hasChanges.value) {
    if (confirm('Are you sure you want to discard your changes?')) {
      exitEditMode();
    }
  } else {
    exitEditMode();
  }
};

// Save changes
const saveChanges = async () => {
  if (!hasChanges.value || isSaving.value) return;

  const comp = composable.value;
  const tableKey = config.value.tableKey;

  if (!comp || !tableKey) {
    toast.error('Unable to save: composable not available');
    return;
  }

  // Build update function name (e.g., 'updateComponentCategory')
  // Convert plural to singular for update function
  const singularKey = tableKey.endsWith('ies')
    ? tableKey.slice(0, -3) + 'y'
    : tableKey.endsWith('es')
      ? tableKey.slice(0, -2)
      : tableKey.endsWith('s')
        ? tableKey.slice(0, -1)
        : tableKey;

  const updateFnName = `update${singularKey.charAt(0).toUpperCase() + singularKey.slice(1)}`;
  const updateFn = comp[updateFnName];

  if (!updateFn) {
    toast.error(`Update method ${updateFnName} not found`);
    return;
  }

  isSaving.value = true;

  try {
    // Build payload - merge original item with changed fields
    const payload = {
      ...item.value,
      ...editForm.value
    };

    // Remove read-only fields that shouldn't be sent to API
    delete payload.createdAt;
    delete payload.updatedAt;

    await updateFn(item.value.id, payload);

    // Update local item with new values
    Object.assign(item.value, editForm.value);

    toast.success('Changes saved successfully');
    exitEditMode();

    // Refresh data
    await loadItem();
  } catch (err) {
    console.error('Error saving changes:', err);
    toast.error(err.response?.data?.message || err.message || 'Failed to save changes');
  } finally {
    isSaving.value = false;
  }
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
  if (value === 'true') return 'Yes';
  if (value === 'false') return 'No';
  return value;
};

const getRelatedData = (relatedSection) => {
  // TODO: Implement related data fetching based on section config
  return [];
};

// Watch for route changes (e.g., navigating to different item)
watch(() => route.params.id, (newId, oldId) => {
  if (newId !== oldId) {
    exitEditMode();
    loadItem();
  }
});

// Handle browser back/forward with edit mode
watch(() => route.query.edit, (newEdit) => {
  if (newEdit === 'true' && !isEditMode.value && item.value) {
    enterEditMode();
  } else if (newEdit !== 'true' && isEditMode.value) {
    isEditMode.value = false;
    editForm.value = {};
    originalForm.value = {};
  }
});

onMounted(() => {
  loadItem();
});
</script>
