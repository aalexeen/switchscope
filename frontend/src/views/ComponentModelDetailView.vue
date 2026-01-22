<template>
  <div class="min-h-screen bg-blue-50 py-8">
    <div class="max-w-5xl mx-auto px-4">
      <!-- Loading State -->
      <div v-if="isLoading" class="bg-white rounded-lg shadow p-8 text-center">
        <pulse-loader :loading="true" color="#3b82f6" size="12px"></pulse-loader>
        <p class="text-gray-500 mt-4">Loading model details...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bg-white rounded-lg shadow p-8 text-center text-red-500">
        <i class="pi pi-exclamation-triangle text-4xl mb-4"></i>
        <p class="text-lg font-semibold mb-2">Error loading model details</p>
        <p class="text-sm text-gray-600 mb-4">{{ error }}</p>
        <button
          @click="loadModel"
          class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded transition-colors"
        >
          Try Again
        </button>
      </div>

      <!-- Detail View -->
      <div v-else-if="model" class="space-y-6">
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
              <i class="pi pi-box text-indigo-600 text-2xl"></i>
              <div>
                <h1 class="text-2xl font-bold text-gray-900">
                  {{ isEditMode ? `Edit: ${model.modelDesignation || model.name}` : (model.modelDesignation || model.name) }}
                </h1>
                <p v-if="model.manufacturer && model.modelNumber" class="text-sm text-gray-500">
                  {{ model.manufacturer }} - {{ model.modelNumber }}
                </p>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <span
                v-if="model.lifecycleStatus && !isEditMode"
                :class="getLifecycleStatusClass(model.lifecycleStatus)"
                class="px-3 py-1 rounded-full text-sm font-medium"
              >
                {{ model.lifecycleStatus }}
              </span>

              <!-- View Mode Buttons -->
              <template v-if="!isEditMode">
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
                  class="px-4 py-2 bg-gray-500 hover:bg-gray-600 text-white rounded transition-colors disabled:opacity-50"
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
            class="px-6 py-2 bg-yellow-50 border-b border-yellow-100 text-yellow-700 text-sm flex items-center gap-2"
          >
            <i class="pi pi-exclamation-triangle"></i>
            You have unsaved changes
          </div>
        </div>

        <!-- Sections (Sorted by Priority) -->
        <template v-for="(section, key) in sortedSections" :key="key">
          <!-- Check condition if present -->
          <div
            v-if="!section.condition || section.condition(model)"
            class="bg-white rounded-lg shadow overflow-hidden"
          >
            <!-- Section Header -->
            <div
              class="px-6 py-4 border-b border-gray-200 flex items-center justify-between cursor-pointer hover:bg-gray-50 transition-colors"
              @click="section.collapsible && toggleSection(key)"
            >
              <div>
                <h2 class="text-lg font-semibold text-gray-900">
                  {{ section.title }}
                </h2>
                <p v-if="section.description" class="mt-1 text-xs text-gray-500">
                  {{ section.description }}
                </p>
              </div>
              <i
                v-if="section.collapsible"
                :class="collapsedSections[key] ? 'pi-chevron-down' : 'pi-chevron-up'"
                class="pi text-gray-400"
              ></i>
            </div>

            <!-- Section Content -->
            <div v-show="!collapsedSections[key]" class="px-6 py-4">
              <dl class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div
                  v-for="field in section.fields"
                  :key="field.key"
                  :class="field.type === 'heading' ? 'md:col-span-2' : ''"
                  class="border-b border-gray-100 pb-3 last:border-b-0"
                >
                  <dt
                    v-if="field.type !== 'heading'"
                    class="text-sm font-medium text-gray-500 mb-1"
                  >
                    {{ field.label }}
                    <span v-if="field.required && isEditMode" class="text-red-500">*</span>
                  </dt>
                  <dd :class="field.type === 'heading' ? 'text-xl font-bold' : 'text-sm text-gray-900'">
                    <!-- Edit Mode -->
                    <EditFieldRenderer
                      v-if="isEditMode && field.editType && field.editable !== false"
                      v-model="editForm[field.key]"
                      :field="field"
                    />
                    <!-- View Mode -->
                    <FieldRenderer v-else :model="model" :field="field" />
                  </dd>
                </div>
              </dl>
            </div>
          </div>
        </template>
      </div>

      <!-- Not Found State -->
      <div v-else class="bg-white rounded-lg shadow p-8 text-center text-gray-500">
        <i class="pi pi-search text-4xl mb-4"></i>
        <p class="text-lg font-semibold mb-2">Model not found</p>
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
import { ref, computed, onMounted, reactive, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import FieldRenderer from '@/components/detail/FieldRenderer.vue';
import EditFieldRenderer from '@/components/form/EditFieldRenderer.vue';

// Import model configs
import switchModelConfig from '@/configs/details/componentModels/switchModel.detail';
import routerModelConfig from '@/configs/details/componentModels/routerModel.detail';
import accessPointModelConfig from '@/configs/details/componentModels/accessPointModel.detail';
import rackModelConfig from '@/configs/details/componentModels/rackModel.detail';

// Import composable
import { useComponentModels } from '@/composables/useComponentModels';

const route = useRoute();
const router = useRouter();
const toast = useToast();

const model = ref(null);
const isLoading = ref(false);
const error = ref(null);
const collapsedSections = reactive({});

// Edit mode state
const isEditMode = ref(false);
const editForm = ref({});
const originalForm = ref({});
const isSaving = ref(false);

// Map discriminator types to configs
const configMap = {
  'SWITCH_MODEL': switchModelConfig,
  'ROUTER_MODEL': routerModelConfig,
  'ACCESS_POINT_MODEL': accessPointModelConfig,
  'RACK_MODEL': rackModelConfig
};

const configAliases = {
  'SWITCH': 'SWITCH_MODEL',
  'ROUTER': 'ROUTER_MODEL',
  'ACCESS_POINT': 'ACCESS_POINT_MODEL',
  'ACCESSPOINT': 'ACCESS_POINT_MODEL',
  'RACK': 'RACK_MODEL'
};

const resolveConfigKey = (rawType) => {
  if (!rawType) return null;
  const normalized = String(rawType).toUpperCase();
  if (configMap[normalized]) return normalized;
  return configAliases[normalized] || null;
};

// Get configuration based on model type
const config = computed(() => {
  if (!model.value) return switchModelConfig; // Default

  // Try to detect discriminator type from various possible fields
  const rawType =
    model.value.discriminatorType ||
    model.value.modelClass ||
    model.value.modelType ||
    model.value.componentTypeCode ||
    'SWITCH_MODEL'; // Default

  const resolvedType = resolveConfigKey(rawType) || 'SWITCH_MODEL';
  return configMap[resolvedType] || switchModelConfig;
});

// Get composable
const { componentModels, fetchComponentModels, updateComponentModel } = useComponentModels();

// Sort sections by priority
const sortedSections = computed(() => {
  if (!config.value?.sections) return {};

  const sections = config.value.sections;
  const sorted = Object.entries(sections)
    .sort(([, a], [, b]) => (a.priority || 99) - (b.priority || 99))
    .reduce((acc, [key, value]) => {
      acc[key] = value;
      return acc;
    }, {});

  return sorted;
});

// Check if form has changes
const hasChanges = computed(() => {
  if (!isEditMode.value) return false;

  for (const key of Object.keys(editForm.value)) {
    const currentValue = editForm.value[key];
    const originalValue = originalForm.value[key];

    // Handle null/undefined comparison
    if (currentValue === originalValue) continue;
    if (currentValue == null && originalValue == null) continue;
    if (currentValue === '' && originalValue == null) continue;
    if (currentValue == null && originalValue === '') continue;

    // Values are different
    return true;
  }
  return false;
});

const loadModel = async () => {
  const id = route.params.id;
  if (!id) {
    error.value = 'No ID provided';
    return;
  }

  isLoading.value = true;
  error.value = null;

  try {
    await fetchComponentModels();

    const models = componentModels.value;
    model.value = Array.isArray(models)
      ? models.find(m => m.id === id)
      : null;

    if (!model.value) {
      error.value = 'Model not found';
    }
  } catch (err) {
    console.error('Error loading model:', err);
    error.value = err.message || 'Failed to load model';
  } finally {
    isLoading.value = false;
  }
};

const goBack = () => {
  if (isEditMode.value && hasChanges.value) {
    if (!confirm('You have unsaved changes. Are you sure you want to leave?')) {
      return;
    }
  }
  router.push({ name: 'component-models' });
};

// Enter edit mode
const enterEditMode = () => {
  if (!model.value) return;

  // Collect all editable fields from all sections
  const formData = {};
  const sections = config.value.sections || {};

  Object.values(sections).forEach(section => {
    if (section.fields) {
      section.fields.forEach(field => {
        if (field.editType && field.editable !== false) {
          formData[field.key] = model.value[field.key] ?? null;
        }
      });
    }
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
  console.log('saveChanges called, hasChanges:', hasChanges.value, 'isSaving:', isSaving.value);
  if (!hasChanges.value || isSaving.value) {
    console.log('saveChanges skipped - no changes or already saving');
    return;
  }

  isSaving.value = true;

  try {
    // Build payload - merge original model with changed fields
    const payload = {
      ...model.value,
      ...editForm.value
    };
    console.log('Payload to send:', JSON.stringify(payload, null, 2));

    // Remove read-only fields that shouldn't be sent to API
    delete payload.createdAt;
    delete payload.updatedAt;
    delete payload.createdDate;
    delete payload.updatedDate;
    delete payload.modelDesignation;
    delete payload.lifecycleStatus;
    delete payload.currentlySupported;
    delete payload.availableForPurchase;
    delete payload.operatingTemperatureRange;
    delete payload.humidityRange;
    delete payload.categoryName;
    delete payload.componentTypeCode;
    delete payload.componentTypeDisplayName;

    console.log('Calling updateComponentModel with id:', model.value.id);
    const result = await updateComponentModel(model.value.id, payload);
    console.log('updateComponentModel result:', result);

    // Update local model with new values
    Object.assign(model.value, editForm.value);

    toast.success('Model updated successfully');
    exitEditMode();

    // Refresh data
    await loadModel();
  } catch (err) {
    console.error('Error saving changes:', err);
    toast.error(err.response?.data?.message || err.message || 'Failed to save changes');
  } finally {
    isSaving.value = false;
  }
};

const toggleSection = (key) => {
  collapsedSections[key] = !collapsedSections[key];
};

const getLifecycleStatusClass = (status) => {
  const statusMap = {
    'Active': 'bg-green-100 text-green-800',
    'Discontinued': 'bg-yellow-100 text-yellow-800',
    'End of Life': 'bg-red-100 text-red-800',
    'Inactive': 'bg-gray-100 text-gray-800'
  };
  return statusMap[status] || 'bg-gray-100 text-gray-800';
};

// Watch for edit query param
watch(() => route.query.edit, (newEdit) => {
  if (newEdit === 'true' && !isEditMode.value && model.value) {
    enterEditMode();
  } else if (newEdit !== 'true' && isEditMode.value) {
    isEditMode.value = false;
    editForm.value = {};
    originalForm.value = {};
  }
});

onMounted(() => {
  loadModel();
});
</script>
