<template>
  <!-- Heading Type -->
  <span v-if="field.type === 'heading'" class="text-xl font-bold text-gray-900">
    {{ formattedValue }}
  </span>

  <!-- Badge Type -->
  <span v-else-if="field.type === 'badge'" :class="getBadgeClass(value)" class="px-3 py-1 rounded-full text-sm font-medium inline-block">
    {{ formattedValue }}
  </span>

  <!-- Boolean Type -->
  <span v-else-if="field.type === 'boolean'" class="flex items-center gap-2">
    <i :class="value ? 'pi-check-circle text-green-500' : 'pi-times-circle text-red-500'" class="pi"></i>
    <span>{{ value ? 'Yes' : 'No' }}</span>
  </span>

  <!-- Summary Type (highlighted) -->
  <span v-else-if="field.type === 'summary'" class="font-medium text-blue-600">
    {{ formattedValue }}
  </span>

  <!-- URL Type -->
  <a v-else-if="field.type === 'url' && value" :href="value" target="_blank" rel="noopener noreferrer" class="text-blue-600 hover:text-blue-800 hover:underline flex items-center gap-1">
    {{ formatUrl(value) }}
    <i class="pi pi-external-link text-xs"></i>
  </a>

  <!-- Currency Type -->
  <span v-else-if="field.type === 'currency'" class="font-medium">
    {{ formatCurrency(value, model.currencyCode) }}
  </span>

  <!-- Date Type -->
  <span v-else-if="field.type === 'date'">
    {{ formatDate(value) }}
  </span>

  <!-- DateTime Type -->
  <span v-else-if="field.type === 'datetime'">
    {{ formatDateTime(value) }}
  </span>

  <!-- Textarea Type -->
  <p v-else-if="field.type === 'textarea'" class="whitespace-pre-wrap text-sm leading-relaxed">
    {{ formattedValue }}
  </p>

  <!-- Array Type -->
  <div v-else-if="field.type === 'array' && Array.isArray(value) && value.length > 0" class="flex flex-wrap gap-2">
    <span
      v-for="(item, index) in value"
      :key="index"
      class="px-2 py-1 bg-blue-100 text-blue-800 rounded text-xs font-medium"
    >
      {{ item }}
    </span>
  </div>

  <!-- Map/Object Type (for deviceSpecs) -->
  <div v-else-if="field.type === 'map' && value && typeof value === 'object' && Object.keys(value).length > 0" class="grid grid-cols-1 gap-2">
    <div
      v-for="([key, val]) in Object.entries(value)"
      :key="key"
      class="flex items-start gap-2 bg-gray-50 rounded px-2 py-1 border border-gray-200"
    >
      <div class="flex-shrink-0">
        <i class="pi pi-tag text-blue-500 text-xs mt-0.5"></i>
      </div>
      <div class="flex-1 min-w-0">
        <div class="text-xs font-medium text-gray-500">
          {{ formatMapKey(key) }}
        </div>
        <div class="text-sm text-gray-900">
          {{ val }}
        </div>
      </div>
    </div>
  </div>

  <!-- Default Text Type -->
  <span v-else>
    {{ formattedValue }}
  </span>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  model: {
    type: Object,
    required: true
  },
  field: {
    type: Object,
    required: true
  }
});

// Get value from model
const value = computed(() => {
  return props.model[props.field.key];
});

// Format value with fallback
const formattedValue = computed(() => {
  const val = value.value;

  if (val === null || val === undefined || val === '') {
    return props.field.fallback || '-';
  }

  // Handle arrays
  if (Array.isArray(val)) {
    if (val.length === 0) {
      return props.field.fallback || '-';
    }
    return val.join(', ');
  }

  // Handle booleans
  if (typeof val === 'boolean') {
    return val ? 'Yes' : 'No';
  }

  return val;
});

// Badge styling
const getBadgeClass = (val) => {
  if (!val) return 'bg-gray-100 text-gray-800';

  const badgeMap = {
    'Active': 'bg-green-100 text-green-800',
    'Discontinued': 'bg-yellow-100 text-yellow-800',
    'End of Life': 'bg-red-100 text-red-800',
    'Inactive': 'bg-gray-100 text-gray-800',
    'Unknown': 'bg-gray-100 text-gray-800'
  };

  return badgeMap[val] || 'bg-blue-100 text-blue-800';
};

// Format URL for display
const formatUrl = (url) => {
  if (!url) return '-';
  try {
    const urlObj = new URL(url);
    return urlObj.hostname + urlObj.pathname;
  } catch {
    return url;
  }
};

// Format currency
const formatCurrency = (amount, currency = 'USD') => {
  if (amount === null || amount === undefined) return '-';

  try {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency || 'USD'
    }).format(amount);
  } catch {
    return `${currency || 'USD'} ${amount}`;
  }
};

// Format date (YYYY-MM-DD)
const formatDate = (dateString) => {
  if (!dateString) return '-';

  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  } catch {
    return dateString;
  }
};

// Format datetime (YYYY-MM-DD HH:MM)
const formatDateTime = (dateString) => {
  if (!dateString) return '-';

  try {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch {
    return dateString;
  }
};

// Format map key (snake_case to Title Case)
const formatMapKey = (key) => {
  return key
    .split('_')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
};
</script>
