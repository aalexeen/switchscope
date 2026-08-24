<script setup>
/**
 * TablePager - moves a table between pages of rows the server sends
 *
 * Shows which rows are on screen out of how many there are, because that is the question a pager
 * exists to answer: without the count, "next" is a guess and the last page is indistinguishable
 * from a page that happens to be short.
 */

import { computed } from 'vue';

const props = defineProps({
  page: { type: Number, required: true },
  size: { type: Number, required: true },
  total: { type: Number, required: true },
  totalPages: { type: Number, required: true },
  disabled: { type: Boolean, default: false },
  sizes: { type: Array, default: () => [10, 20, 50, 100] }
});

const emit = defineEmits(['update:page', 'update:size']);

const first = computed(() => (props.total === 0 ? 0 : props.page * props.size + 1));
const last = computed(() => Math.min(props.total, (props.page + 1) * props.size));
const isFirstPage = computed(() => props.page <= 0);
const isLastPage = computed(() => props.page >= props.totalPages - 1);

/**
 * At most seven page buttons, centred on the current page. A hundred pages of buttons is not
 * navigation.
 */
const numbers = computed(() => {
  const span = 7;
  const start = Math.max(0, Math.min(props.page - Math.floor(span / 2), props.totalPages - span));
  return Array.from({ length: Math.min(span, props.totalPages) }, (unused, i) => start + i);
});

const go = (wanted) => {
  if (!props.disabled && wanted !== props.page) {
    emit('update:page', wanted);
  }
};
</script>

<template>
  <div
    v-if="total > 0"
    class="bg-white rounded-lg shadow px-4 py-3 mt-4 flex flex-col gap-3 sm:flex-row sm:items-center"
  >
    <div class="text-sm text-gray-600 whitespace-nowrap">
      Showing <span class="font-medium">{{ first }}-{{ last }}</span>
      of <span class="font-medium">{{ total }}</span>
    </div>

    <div class="flex items-center gap-1 sm:ml-auto">
      <button
        class="px-3 py-2 rounded text-sm transition-colors bg-gray-100 hover:bg-gray-200 disabled:opacity-40 disabled:hover:bg-gray-100"
        :disabled="disabled || isFirstPage"
        title="First page"
        @click="go(0)"
      >
        <i class="pi pi-angle-double-left" />
      </button>
      <button
        class="px-3 py-2 rounded text-sm transition-colors bg-gray-100 hover:bg-gray-200 disabled:opacity-40 disabled:hover:bg-gray-100"
        :disabled="disabled || isFirstPage"
        title="Previous page"
        @click="go(page - 1)"
      >
        <i class="pi pi-angle-left" />
      </button>

      <button
        v-for="number in numbers"
        :key="number"
        class="px-3 py-2 rounded text-sm transition-colors"
        :class="number === page
          ? 'bg-indigo-500 text-white'
          : 'bg-gray-100 hover:bg-gray-200 text-gray-700'"
        :disabled="disabled"
        @click="go(number)"
      >
        {{ number + 1 }}
      </button>

      <button
        class="px-3 py-2 rounded text-sm transition-colors bg-gray-100 hover:bg-gray-200 disabled:opacity-40 disabled:hover:bg-gray-100"
        :disabled="disabled || isLastPage"
        title="Next page"
        @click="go(page + 1)"
      >
        <i class="pi pi-angle-right" />
      </button>
      <button
        class="px-3 py-2 rounded text-sm transition-colors bg-gray-100 hover:bg-gray-200 disabled:opacity-40 disabled:hover:bg-gray-100"
        :disabled="disabled || isLastPage"
        title="Last page"
        @click="go(totalPages - 1)"
      >
        <i class="pi pi-angle-double-right" />
      </button>
    </div>

    <label class="text-sm text-gray-600 flex items-center gap-2 sm:ml-4">
      Rows
      <select
        class="border border-gray-300 rounded px-2 py-1 text-sm"
        :value="size"
        :disabled="disabled"
        @change="emit('update:size', Number($event.target.value))"
      >
        <option
          v-for="option in sizes"
          :key="option"
          :value="option"
        >
          {{ option }}
        </option>
      </select>
    </label>
  </div>
</template>
