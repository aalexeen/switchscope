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
                  {{ model.modelDesignation || model.name }}
                </h1>
                <p v-if="model.manufacturer && model.modelNumber" class="text-sm text-gray-500">
                  {{ model.manufacturer }} • {{ model.modelNumber }}
                </p>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <span
                v-if="model.lifecycleStatus"
                :class="getLifecycleStatusClass(model.lifecycleStatus)"
                class="px-3 py-1 rounded-full text-sm font-medium"
              >
                {{ model.lifecycleStatus }}
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
              <h2 class="text-lg font-semibold text-gray-900">
                {{ section.title }}
              </h2>
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
                  </dt>
                  <dd :class="field.type === 'heading' ? 'text-xl font-bold' : 'text-sm text-gray-900'">
                    <FieldRenderer :model="model" :field="field" />
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
import { ref, computed, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import FieldRenderer from '@/components/detail/FieldRenderer.vue';

// Import model configs
import switchModelConfig from '@/configs/details/componentModels/switchModel.detail';
import routerModelConfig from '@/configs/details/componentModels/routerModel.detail';
import accessPointModelConfig from '@/configs/details/componentModels/accessPointModel.detail';
import rackModelConfig from '@/configs/details/componentModels/rackModel.detail';

// Import composable
import { useComponentModels } from '@/composables/useComponentModels';

const route = useRoute();
const router = useRouter();

const model = ref(null);
const isLoading = ref(false);
const error = ref(null);
const collapsedSections = reactive({});

// Map discriminator types to configs
const configMap = {
  'SWITCH_MODEL': switchModelConfig,
  'ROUTER_MODEL': routerModelConfig,
  'ACCESS_POINT_MODEL': accessPointModelConfig,
  'RACK_MODEL': rackModelConfig
};

// Get configuration based on model type
const config = computed(() => {
  if (!model.value) return switchModelConfig; // Default

  // Try to detect discriminator type from various possible fields
  const discriminatorType =
    model.value.discriminatorType ||
    model.value.modelClass ||
    model.value.modelType ||
    'SWITCH_MODEL'; // Default

  return configMap[discriminatorType] || switchModelConfig;
});

// Get composable
const { componentModels, fetchComponentModels } = useComponentModels();

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
  router.push({ name: 'component-models' });
};

const handleEdit = () => {
  // TODO: Navigate to edit page
  console.log('Edit:', model.value);
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

onMounted(() => {
  loadModel();
});
</script>
