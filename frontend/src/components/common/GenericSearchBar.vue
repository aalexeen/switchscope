<script setup>
/**
 * GenericSearchBar - Универсальный компонент поиска с theme system
 * 
 * Заменяет все специфичные SearchBar компоненты единым переиспользуемым решением.
 * 
 * @example
 * <GenericSearchBar
 *   entity-name="Component Natures"
 *   :search-query="searchQuery"
 *   :found-count="filteredNatures.length"
 *   :total-count="totalNatures"
 *   theme="indigo"
 *   :search-fields="['name', 'code', 'display name', 'description']"
 *   @update:search-query="searchQuery = $event"
 *   @clear="searchQuery = ''"
 * />
 */

import { computed } from 'vue';

const props = defineProps({
  /** Название сущности для заголовка (e.g., "Component Natures") */
  entityName: {
    type: String,
    required: true
  },
  /** Текущий поисковый запрос (v-model:searchQuery) */
  searchQuery: {
    type: String,
    required: true
  },
  /** Количество найденных элементов */
  foundCount: {
    type: Number,
    required: true
  },
  /** Общее количество элементов */
  totalCount: {
    type: Number,
    required: true
  },
  /** 
   * Цветовая тема компонента 
   * @values green, amber, teal, blue, indigo, purple, rose, cyan, emerald, orange
   */
  theme: {
    type: String,
    default: 'indigo',
    validator: (value) => [
      'green', 'amber', 'teal', 'blue', 'indigo', 'purple', 
      'rose', 'cyan', 'emerald', 'orange'
    ].includes(value)
  },
  /** 
   * Интенсивность цвета (500, 600, 700) 
   * @default 600
   */
  intensity: {
    type: [String, Number],
    default: '600',
    validator: (value) => ['500', '600', '700', 500, 600, 700].includes(value)
  },
  /** Список полей для placeholder (e.g., ['name', 'code']) */
  searchFields: {
    type: Array,
    default: () => ['name', 'code', 'description']
  },
  /** Суффикс заголовка (e.g., "Catalog", "Table View") */
  titleSuffix: {
    type: String,
    default: 'Catalog'
  }
});

const emit = defineEmits(['update:searchQuery', 'clear']);

// Theme system - все поддерживаемые цветовые схемы
const themes = {
  green: {
    bg: { 500: 'bg-green-500', 600: 'bg-green-600', 700: 'bg-green-700' },
    text: { 500: 'text-green-500', 600: 'text-green-600', 700: 'text-green-700' },
    ring: { 500: 'focus:ring-green-300', 600: 'focus:ring-green-300', 700: 'focus:ring-green-400' }
  },
  amber: {
    bg: { 500: 'bg-amber-500', 600: 'bg-amber-600', 700: 'bg-amber-700' },
    text: { 500: 'text-amber-500', 600: 'text-amber-600', 700: 'text-amber-700' },
    ring: { 500: 'focus:ring-amber-300', 600: 'focus:ring-amber-300', 700: 'focus:ring-amber-400' }
  },
  teal: {
    bg: { 500: 'bg-teal-500', 600: 'bg-teal-600', 700: 'bg-teal-700' },
    text: { 500: 'text-teal-500', 600: 'text-teal-600', 700: 'text-teal-700' },
    ring: { 500: 'focus:ring-teal-300', 600: 'focus:ring-teal-300', 700: 'focus:ring-teal-400' }
  },
  blue: {
    bg: { 500: 'bg-blue-500', 600: 'bg-blue-600', 700: 'bg-blue-700' },
    text: { 500: 'text-blue-500', 600: 'text-blue-600', 700: 'text-blue-700' },
    ring: { 500: 'focus:ring-blue-300', 600: 'focus:ring-blue-300', 700: 'focus:ring-blue-400' }
  },
  indigo: {
    bg: { 500: 'bg-indigo-500', 600: 'bg-indigo-600', 700: 'bg-indigo-700' },
    text: { 500: 'text-indigo-500', 600: 'text-indigo-600', 700: 'text-indigo-700' },
    ring: { 500: 'focus:ring-indigo-300', 600: 'focus:ring-indigo-300', 700: 'focus:ring-indigo-400' }
  },
  purple: {
    bg: { 500: 'bg-purple-500', 600: 'bg-purple-600', 700: 'bg-purple-700' },
    text: { 500: 'text-purple-500', 600: 'text-purple-600', 700: 'text-purple-700' },
    ring: { 500: 'focus:ring-purple-300', 600: 'focus:ring-purple-300', 700: 'focus:ring-purple-400' }
  },
  rose: {
    bg: { 500: 'bg-rose-500', 600: 'bg-rose-600', 700: 'bg-rose-700' },
    text: { 500: 'text-rose-500', 600: 'text-rose-600', 700: 'text-rose-700' },
    ring: { 500: 'focus:ring-rose-300', 600: 'focus:ring-rose-300', 700: 'focus:ring-rose-400' }
  },
  cyan: {
    bg: { 500: 'bg-cyan-500', 600: 'bg-cyan-600', 700: 'bg-cyan-700' },
    text: { 500: 'text-cyan-500', 600: 'text-cyan-600', 700: 'text-cyan-700' },
    ring: { 500: 'focus:ring-cyan-300', 600: 'focus:ring-cyan-300', 700: 'focus:ring-cyan-400' }
  },
  emerald: {
    bg: { 500: 'bg-emerald-500', 600: 'bg-emerald-600', 700: 'bg-emerald-700' },
    text: { 500: 'text-emerald-500', 600: 'text-emerald-600', 700: 'text-emerald-700' },
    ring: { 500: 'focus:ring-emerald-300', 600: 'focus:ring-emerald-300', 700: 'focus:ring-emerald-400' }
  },
  orange: {
    bg: { 500: 'bg-orange-500', 600: 'bg-orange-600', 700: 'bg-orange-700' },
    text: { 500: 'text-orange-500', 600: 'text-orange-600', 700: 'text-orange-700' },
    ring: { 500: 'focus:ring-orange-300', 600: 'focus:ring-orange-300', 700: 'focus:ring-orange-400' }
  }
};

// Computed классы для текущей темы
const currentTheme = computed(() => {
  const themeConfig = themes[props.theme] || themes.indigo;
  const int = String(props.intensity);
  return {
    bg: themeConfig.bg[int] || themeConfig.bg['600'],
    text: themeConfig.text[int] || themeConfig.text['600'],
    ring: themeConfig.ring[int] || themeConfig.ring['600']
  };
});

// Динамический placeholder из searchFields
const placeholderText = computed(() => {
  if (props.searchFields.length === 0) {
    return 'Search...';
  }
  return `Search by ${props.searchFields.join(', ')}...`;
});

// Заголовок с entity name и suffix
const headerTitle = computed(() => {
  return props.titleSuffix 
    ? `${props.entityName} - ${props.titleSuffix}` 
    : props.entityName;
});

const updateSearch = (event) => {
  emit('update:searchQuery', event.target.value);
};

const clearSearch = () => {
  emit('clear');
};
</script>

<template>
  <!-- Header with Search -->
  <section :class="[currentTheme.bg, 'text-white py-4']">
    <div class="container mx-auto px-4">
      <div class="flex justify-between items-center mb-4">
        <h1 class="text-2xl font-bold">
          {{ headerTitle }}
        </h1>
        <!-- Slot для дополнительных элементов в заголовке (кнопки, etc.) -->
        <slot name="header-actions" />
      </div>

      <!-- Search Section -->
      <div class="flex flex-col sm:flex-row gap-4 items-start sm:items-center">
        <div class="flex-1 max-w-md">
          <div class="relative">
            <input
              :value="searchQuery"
              type="text"
              :placeholder="placeholderText"
              :class="[
                'w-full px-4 py-2 pl-10 text-gray-900 rounded-lg border-0 focus:ring-2 focus:outline-none',
                currentTheme.ring
              ]"
              @input="updateSearch"
            >
            <i class="pi pi-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
          </div>
        </div>

        <div class="flex items-center space-x-4 text-sm">
          <div class="flex items-center space-x-2">
            <span>Found: {{ foundCount }}</span>
            <span>|</span>
            <span>Total: {{ totalCount }}</span>
          </div>

          <button
            v-if="searchQuery"
            :class="[
              'bg-white px-3 py-1 rounded text-sm hover:bg-gray-100 transition-colors',
              currentTheme.text
            ]"
            @click="clearSearch"
          >
            <i class="pi pi-times mr-1" />
            Clear
          </button>
          
          <!-- Slot для дополнительных кнопок рядом с Clear -->
          <slot name="extra-actions" />
        </div>
      </div>
      
      <!-- Slot для контента под поиском -->
      <slot name="below-search" />
    </div>
  </section>
</template>

<style scoped>
/* Component-specific styles if needed */
</style>
