<script setup>
import { RouterLink, useRoute } from 'vue-router';
import { computed } from 'vue';

const route = useRoute();

// Determine which view to go back to based on query parameter
const backRoute = computed(() => {
  const fromView = route.query.from;
  
  if (fromView === 'table') {
    return '/removemac-table';
  } else {
    // Default to card view if no 'from' parameter or if from='card'
    return '/removemac';
  }
});

// Determine the appropriate label
const backLabel = computed(() => {
  const fromView = route.query.from;
  
  if (fromView === 'table') {
    return 'Back to MAC Table View';
  } else {
    return 'Back to MAC Card View';
  }
});
</script>

<template>
  <section>
    <div class="container m-auto py-6 px-6">
      <RouterLink
        :to="backRoute"
        class="text-green-500 hover:text-green-600 flex items-center">
        <i class="pi pi-arrow-circle-left mr-3"></i> {{ backLabel }}
      </RouterLink>
    </div>
  </section>
</template>