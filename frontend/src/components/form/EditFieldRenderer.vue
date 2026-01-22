<template>
  <div class="edit-field">
    <!-- Text Input -->
    <input
      v-if="editType === 'input' || editType === 'text'"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      type="text"
      :placeholder="field.placeholder || `Enter ${field.label}...`"
      :disabled="disabled"
      :required="field.required"
      :maxlength="field.validation?.maxLength"
      :class="inputClasses"
    />

    <!-- Number Input -->
    <input
      v-else-if="editType === 'number'"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value ? Number($event.target.value) : null)"
      type="number"
      :placeholder="field.placeholder || `Enter ${field.label}...`"
      :disabled="disabled"
      :required="field.required"
      :min="field.validation?.min"
      :max="field.validation?.max"
      :step="field.validation?.step || 1"
      :class="inputClasses"
    />

    <!-- Date Input -->
    <input
      v-else-if="editType === 'date'"
      :value="dateInputValue"
      @input="$emit('update:modelValue', $event.target.value ? `${$event.target.value}T00:00:00` : null)"
      type="date"
      :placeholder="field.placeholder || `Select ${field.label}...`"
      :disabled="disabled"
      :required="field.required"
      :class="inputClasses"
    />

    <!-- Textarea -->
    <textarea
      v-else-if="editType === 'textarea'"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      :placeholder="field.placeholder || `Enter ${field.label}...`"
      :disabled="disabled"
      :required="field.required"
      :maxlength="field.validation?.maxLength"
      :rows="field.rows || 3"
      :class="[inputClasses, 'resize-none']"
    ></textarea>

    <!-- Checkbox / Boolean Toggle -->
    <label
      v-else-if="editType === 'checkbox' || editType === 'boolean'"
      class="inline-flex items-center gap-3 cursor-pointer"
    >
      <button
        type="button"
        role="switch"
        :aria-checked="modelValue"
        @click="!disabled && $emit('update:modelValue', !modelValue)"
        :disabled="disabled"
        :class="[
          'relative inline-flex h-6 w-11 flex-shrink-0 rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2',
          modelValue ? 'bg-blue-600' : 'bg-gray-200',
          disabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'
        ]"
      >
        <span
          :class="[
            'pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
            modelValue ? 'translate-x-5' : 'translate-x-0'
          ]"
        ></span>
      </button>
      <span :class="['text-sm', modelValue ? 'text-gray-900' : 'text-gray-500']">
        {{ modelValue ? 'Yes' : 'No' }}
      </span>
    </label>

    <!-- Simple Select -->
    <select
      v-else-if="editType === 'select'"
      :value="modelValue"
      @change="$emit('update:modelValue', $event.target.value)"
      :disabled="disabled"
      :required="field.required"
      :class="inputClasses"
    >
      <option value="" disabled>{{ field.placeholder || 'Select...' }}</option>
      <option
        v-for="option in field.options"
        :key="option.value"
        :value="option.value"
      >
        {{ option.label }}
      </option>
    </select>

    <!-- Searchable Select (for FK relations) -->
    <SearchableDropdown
      v-else-if="editType === 'searchable-select'"
      :model-value="modelValue"
      @update:model-value="$emit('update:modelValue', $event)"
      :options="relationOptions"
      :value-key="field.relation?.valueKey || 'id'"
      :label-key="field.relation?.labelKey || 'displayName'"
      :search-fields="field.relation?.searchFields || ['name', 'displayName', 'code']"
      :placeholder="field.placeholder || `Select ${field.label}...`"
      :disabled="disabled"
      :loading="isLoadingRelation"
      :clearable="!field.required"
    />

    <!-- Color Class Picker (specialized for Tailwind color classes) -->
    <div v-else-if="editType === 'color-class'" class="space-y-2">
      <input
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
        type="text"
        :placeholder="field.placeholder || 'e.g., text-blue-600'"
        :disabled="disabled"
        :class="inputClasses"
      />
      <div class="flex flex-wrap gap-2">
        <button
          v-for="color in colorPresets"
          :key="color.class"
          type="button"
          @click="!disabled && $emit('update:modelValue', color.class)"
          :class="[
            'w-6 h-6 rounded border-2 transition-all',
            modelValue === color.class ? 'border-gray-900 ring-2 ring-offset-1 ring-gray-400' : 'border-gray-300 hover:border-gray-400',
            color.bg
          ]"
          :title="color.class"
        ></button>
      </div>
    </div>

    <!-- Icon Class Picker (specialized for PrimeIcons) -->
    <div v-else-if="editType === 'icon-class'" class="space-y-2">
      <div class="relative">
        <i v-if="modelValue" :class="['pi absolute left-3 top-1/2 -translate-y-1/2 text-gray-600', modelValue]"></i>
        <input
          :value="modelValue"
          @input="$emit('update:modelValue', $event.target.value)"
          type="text"
          :placeholder="field.placeholder || 'e.g., pi-folder'"
          :disabled="disabled"
          :class="[inputClasses, modelValue ? 'pl-9' : '']"
        />
      </div>
      <div class="flex flex-wrap gap-2">
        <button
          v-for="icon in iconPresets"
          :key="icon"
          type="button"
          @click="!disabled && $emit('update:modelValue', icon)"
          :class="[
            'w-8 h-8 rounded border flex items-center justify-center transition-all',
            modelValue === icon ? 'border-blue-500 bg-blue-50 text-blue-600' : 'border-gray-300 hover:border-gray-400 text-gray-600'
          ]"
          :title="icon"
        >
          <i :class="['pi', icon]"></i>
        </button>
      </div>
    </div>

    <!-- Read-only (for non-editable fields like code) -->
    <div v-else-if="editType === 'readonly'" class="text-gray-900 bg-gray-50 px-3 py-2 rounded-lg border border-gray-200">
      {{ modelValue || '-' }}
    </div>

    <!-- Fallback: regular input -->
    <input
      v-else
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      type="text"
      :placeholder="field.placeholder || `Enter ${field.label}...`"
      :disabled="disabled"
      :class="inputClasses"
    />
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue';
import SearchableDropdown from './SearchableDropdown.vue';
import { composableRegistry } from '@/configs/tables/tableRegistry';

const props = defineProps({
  modelValue: {
    type: [String, Number, Boolean, null],
    default: null
  },
  field: {
    type: Object,
    required: true
  },
  disabled: {
    type: Boolean,
    default: false
  }
});

defineEmits(['update:modelValue']);

// Determine edit type from field config
const editType = computed(() => {
  if (props.field.editable === false) return 'readonly';
  return props.field.editType || 'input';
});

// Common input classes
const inputClasses = computed(() => [
  'w-full px-3 py-2 border rounded-lg text-sm transition-colors',
  'focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500',
  props.disabled
    ? 'bg-gray-100 border-gray-200 text-gray-500 cursor-not-allowed'
    : 'bg-white border-gray-300 hover:border-gray-400 text-gray-900'
]);

const dateInputValue = computed(() => {
  if (!props.modelValue) return '';
  if (typeof props.modelValue === 'string') {
    return props.modelValue.substring(0, 10);
  }
  try {
    const date = new Date(props.modelValue);
    if (Number.isNaN(date.getTime())) return '';
    return date.toISOString().substring(0, 10);
  } catch {
    return '';
  }
});

// Color presets for color-class picker
const colorPresets = [
  { class: 'text-gray-600', bg: 'bg-gray-600' },
  { class: 'text-red-600', bg: 'bg-red-600' },
  { class: 'text-orange-600', bg: 'bg-orange-600' },
  { class: 'text-yellow-600', bg: 'bg-yellow-600' },
  { class: 'text-green-600', bg: 'bg-green-600' },
  { class: 'text-teal-600', bg: 'bg-teal-600' },
  { class: 'text-blue-600', bg: 'bg-blue-600' },
  { class: 'text-indigo-600', bg: 'bg-indigo-600' },
  { class: 'text-purple-600', bg: 'bg-purple-600' },
  { class: 'text-pink-600', bg: 'bg-pink-600' }
];

// Icon presets for icon-class picker
const iconPresets = [
  'pi-folder', 'pi-box', 'pi-server', 'pi-desktop', 'pi-wifi',
  'pi-sitemap', 'pi-link', 'pi-building', 'pi-cog', 'pi-database',
  'pi-tag', 'pi-flag', 'pi-star', 'pi-bolt', 'pi-shield'
];

// Relation data loading
const relationOptions = ref([]);
const isLoadingRelation = ref(false);

// Load relation options for searchable-select
const loadRelationOptions = async () => {
  if (editType.value !== 'searchable-select' || !props.field.relation) return;

  const { composable: composableName, dataKey } = props.field.relation;
  if (!composableName || !dataKey) return;

  // Get composable factory from registry
  const composableFactory = composableRegistry[dataKey];
  if (!composableFactory) {
    console.error(`Composable not found for dataKey: ${dataKey}`);
    return;
  }

  isLoadingRelation.value = true;
  try {
    const composable = composableFactory();

    // Build fetch function name (e.g., 'fetchComponentCategories')
    const fetchFnName = `fetch${dataKey.charAt(0).toUpperCase() + dataKey.slice(1)}`;
    const fetchFn = composable[fetchFnName];

    if (fetchFn) {
      await fetchFn();
    }

    // Get data from composable
    const data = composable[dataKey];
    relationOptions.value = data?.value || data || [];
  } catch (err) {
    console.error('Failed to load relation options:', err);
    relationOptions.value = [];
  } finally {
    isLoadingRelation.value = false;
  }
};

// Load relation data on mount
onMounted(() => {
  if (editType.value === 'searchable-select') {
    loadRelationOptions();
  }
});

// Reload if field config changes
watch(() => props.field.relation, () => {
  if (editType.value === 'searchable-select') {
    loadRelationOptions();
  }
}, { deep: true });
</script>
