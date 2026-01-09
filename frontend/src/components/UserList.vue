<script setup>
import { defineProps, ref, computed } from 'vue';
import { RouterLink } from 'vue-router';

const props = defineProps({
  user: {
    type: Object,
    default: () => ({})
  },
});

const showFullDescrition = ref(false);

const toggleFullDescription = () => {
    showFullDescrition.value = !showFullDescrition.value;
}

const trunkatedDescription = computed(() => {
    let description = props.user.description;
    if (!showFullDescrition.value) {
        description = description.substring(0, 8) + '...';
    }
    return description;
});
</script>

<template>
  <div class="bg-white rounded-xl shadow-md relative">
    <div class="p-4">
      <div class="mb-6">
        <div class="text-gray-600 my-2">
          {{ user.nickname }}
        </div>
        <h3 class="text-xl font-bold">
          {{ user.first_name }}
        </h3>
      </div>

      <div class="mb-5">
        <div>
          {{ trunkatedDescription }}
        </div>
        <button
          class="text-green-500 hover:text-green-600 mb-5"
          @click="toggleFullDescription"
        >
          {{ showFullDescrition ? 'Less' : 'More' }}
        </button>
      </div>

      <h3 class="text-green-500 mb-2">
        {{ user.contactEmail }}
      </h3>

      <div class="border border-gray-100 mb-5" />

      <div class="flex flex-col lg:flex-row justify-between mb-4">
        <div class="text-orange-700 mb-3">
          <i class="pi pi-map-marker text-orange-700" />
          {{ user.subdivision }}
        </div>
        <!-- v-bind -->
        <RouterLink
          :to="'/users/allusers/' + user.id"
          class="h-[36px] bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg text-center text-sm"
        >
          Get More
        </RouterLink>
      </div>
    </div>
  </div>
</template>