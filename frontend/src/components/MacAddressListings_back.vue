<script setup>
import { RouterLink } from 'vue-router';
import MacAddressList from './MacAddressList.vue';
import { reactive, defineProps, onMounted } from 'vue';
import { useApi } from '@/composables/useApi';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

const $api = useApi();

const state = reactive({
    macs: [],
    isLoading: true
});

defineProps({
    limit: Number,
    showButton: {
        type: Boolean,
        default: false
    }
});

onMounted(
    // this.$load(async() => {
    //     const response = await axios.get('/api/macs');
    //     state.macs = response.data;
    // }, false)
    async () => {
        try {
            const response = await $api.blockedmacs.getAll();
            state.macs = response.data;
            //console.log(state.macs)
        } catch {
            console.error('Error fetching macs');
        } finally {
            state.isLoading = false;
        }
    }
);
</script>

<template>
  <section class="bg-blue-50 px-4 py-10">
    <div class="container-xl lg:container m-auto">
      <h2 class="text-3xl font-bold text-green-500 mb-6 text-center">
        Browse Macs
      </h2>
      <!-- Show loading spinner while loading is true -->
      <div
        v-if="state.isLoading"
        class="text-center text-gray-500 py-6"
      >
        <PulseLoader />
      </div>

      <!-- Show mac listing when done loading -->
      <div
        v-else
        class="grid grid-cols-1 md:grid-cols-3 gap-6"
      >
        <MacAddressList
          v-for="mac in state.macs.slice(0, limit || state.macs.length)"
          :key="mac.id"
          :mac="mac"
        />
      </div>
    </div>
  </section>

  <section
    v-if="showButton"
    class="m-auto max-w-lg my-10 px-6"
  >
    <RouterLink
      to="/removemac"
      class="block bg-black text-white text-center py-4 px-6 rounded-xl hover:bg-gray-700"
    >
      View All Macs
    </RouterLink>
  </section>
</template>