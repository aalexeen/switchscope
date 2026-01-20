<template>
  <div class="relative" ref="dropdownRef">
    <!-- Trigger Button -->
    <button
      type="button"
      @click="toggleDropdown"
      :disabled="disabled"
      :class="[
        'w-full flex items-center justify-between px-3 py-2 border rounded-lg text-left transition-colors',
        disabled
          ? 'bg-gray-100 border-gray-200 text-gray-500 cursor-not-allowed'
          : isOpen
            ? 'border-blue-500 ring-2 ring-blue-200 bg-white'
            : 'border-gray-300 hover:border-gray-400 bg-white'
      ]"
    >
      <span :class="selectedLabel ? 'text-gray-900' : 'text-gray-500'">
        {{ selectedLabel || placeholder }}
      </span>
      <i
        :class="[
          'pi text-gray-400 transition-transform duration-200',
          isOpen ? 'pi-chevron-up' : 'pi-chevron-down'
        ]"
      ></i>
    </button>

    <!-- Dropdown Panel -->
    <Transition
      enter-active-class="transition ease-out duration-100"
      enter-from-class="transform opacity-0 scale-95"
      enter-to-class="transform opacity-100 scale-100"
      leave-active-class="transition ease-in duration-75"
      leave-from-class="transform opacity-100 scale-100"
      leave-to-class="transform opacity-0 scale-95"
    >
      <div
        v-if="isOpen"
        class="absolute z-50 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg overflow-hidden"
      >
        <!-- Search Input -->
        <div class="p-2 border-b border-gray-200">
          <div class="relative">
            <i class="pi pi-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm"></i>
            <input
              ref="searchInputRef"
              v-model="searchQuery"
              type="text"
              :placeholder="searchPlaceholder"
              class="w-full pl-9 pr-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
              @keydown.escape="closeDropdown"
              @keydown.enter.prevent="selectHighlighted"
              @keydown.down.prevent="highlightNext"
              @keydown.up.prevent="highlightPrev"
            />
            <button
              v-if="searchQuery"
              type="button"
              @click="clearSearch"
              class="absolute right-2 top-1/2 -translate-y-1/2 p-1 text-gray-400 hover:text-gray-600"
            >
              <i class="pi pi-times text-xs"></i>
            </button>
          </div>
        </div>

        <!-- Options List -->
        <div
          class="max-h-48 overflow-y-auto"
          role="listbox"
        >
          <!-- Loading State -->
          <div v-if="loading" class="px-3 py-4 text-center text-gray-500">
            <i class="pi pi-spinner pi-spin mr-2"></i>
            Loading...
          </div>

          <!-- Empty State -->
          <div v-else-if="filteredOptions.length === 0" class="px-3 py-4 text-center text-gray-500">
            <i class="pi pi-search mr-2"></i>
            {{ searchQuery ? 'No matches found' : 'No options available' }}
          </div>

          <!-- Options -->
          <template v-else>
            <!-- Clear Option (if clearable) -->
            <button
              v-if="clearable && modelValue"
              type="button"
              @click="clearSelection"
              class="w-full px-3 py-2 text-left text-sm text-gray-500 hover:bg-gray-100 flex items-center gap-2 border-b border-gray-100"
            >
              <i class="pi pi-times-circle"></i>
              Clear selection
            </button>

            <!-- Option Items -->
            <button
              v-for="(option, index) in filteredOptions"
              :key="option[valueKey]"
              type="button"
              @click="selectOption(option)"
              @mouseenter="highlightedIndex = index"
              :class="[
                'w-full px-3 py-2 text-left text-sm flex items-center gap-2 transition-colors',
                option[valueKey] === modelValue
                  ? 'bg-blue-50 text-blue-700'
                  : highlightedIndex === index
                    ? 'bg-gray-100 text-gray-900'
                    : 'text-gray-700 hover:bg-gray-50'
              ]"
              role="option"
              :aria-selected="option[valueKey] === modelValue"
            >
              <i
                v-if="option.iconClass"
                :class="[option.iconClass, option.colorClass || 'text-gray-500']"
              ></i>
              <span class="flex-1">{{ option[labelKey] }}</span>
              <i
                v-if="option[valueKey] === modelValue"
                class="pi pi-check text-blue-600"
              ></i>
            </button>
          </template>
        </div>

        <!-- Footer (optional, for showing count) -->
        <div
          v-if="!loading && filteredOptions.length > 0"
          class="px-3 py-1.5 text-xs text-gray-500 bg-gray-50 border-t border-gray-200"
        >
          {{ filteredOptions.length }} of {{ options.length }} items
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue';

const props = defineProps({
  modelValue: {
    type: [String, Number, null],
    default: null
  },
  options: {
    type: Array,
    default: () => []
  },
  valueKey: {
    type: String,
    default: 'id'
  },
  labelKey: {
    type: String,
    default: 'name'
  },
  searchFields: {
    type: Array,
    default: () => ['name']
  },
  placeholder: {
    type: String,
    default: 'Select an option...'
  },
  searchPlaceholder: {
    type: String,
    default: 'Search...'
  },
  disabled: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  },
  clearable: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['update:modelValue', 'change']);

const dropdownRef = ref(null);
const searchInputRef = ref(null);
const isOpen = ref(false);
const searchQuery = ref('');
const highlightedIndex = ref(-1);

// Find selected label
const selectedLabel = computed(() => {
  if (!props.modelValue) return null;
  const selected = props.options.find(opt => opt[props.valueKey] === props.modelValue);
  return selected ? selected[props.labelKey] : null;
});

// Filter options based on search query
const filteredOptions = computed(() => {
  if (!searchQuery.value.trim()) {
    return props.options;
  }

  const query = searchQuery.value.toLowerCase().trim();
  return props.options.filter(option => {
    return props.searchFields.some(field => {
      const value = option[field];
      return value && String(value).toLowerCase().includes(query);
    });
  });
});

// Toggle dropdown
const toggleDropdown = () => {
  if (props.disabled) return;
  isOpen.value = !isOpen.value;
};

// Open dropdown
const openDropdown = () => {
  if (props.disabled) return;
  isOpen.value = true;
};

// Close dropdown
const closeDropdown = () => {
  isOpen.value = false;
  searchQuery.value = '';
  highlightedIndex.value = -1;
};

// Clear search
const clearSearch = () => {
  searchQuery.value = '';
  searchInputRef.value?.focus();
};

// Select option
const selectOption = (option) => {
  emit('update:modelValue', option[props.valueKey]);
  emit('change', option);
  closeDropdown();
};

// Clear selection
const clearSelection = () => {
  emit('update:modelValue', null);
  emit('change', null);
  closeDropdown();
};

// Keyboard navigation
const highlightNext = () => {
  if (highlightedIndex.value < filteredOptions.value.length - 1) {
    highlightedIndex.value++;
  }
};

const highlightPrev = () => {
  if (highlightedIndex.value > 0) {
    highlightedIndex.value--;
  }
};

const selectHighlighted = () => {
  if (highlightedIndex.value >= 0 && highlightedIndex.value < filteredOptions.value.length) {
    selectOption(filteredOptions.value[highlightedIndex.value]);
  }
};

// Focus search input when dropdown opens
watch(isOpen, async (newValue) => {
  if (newValue) {
    await nextTick();
    searchInputRef.value?.focus();

    // Highlight current selection
    if (props.modelValue) {
      const index = filteredOptions.value.findIndex(
        opt => opt[props.valueKey] === props.modelValue
      );
      if (index >= 0) {
        highlightedIndex.value = index;
      }
    }
  }
});

// Reset highlight when search changes
watch(searchQuery, () => {
  highlightedIndex.value = filteredOptions.value.length > 0 ? 0 : -1;
});

// Click outside handler
const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    closeDropdown();
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>
