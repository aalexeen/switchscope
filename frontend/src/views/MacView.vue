<script setup>
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import BackButtonMacs from '../components/BackButtonMacs.vue';
import { reactive, onMounted, computed, ref } from 'vue';
import { useRoute, RouterLink, useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import { useMacAddresses } from '@/composables/useMacAddresses';

const route = useRoute();
const router = useRouter();
const toast = useToast();

// Use the same composable as MacAddressListTable.vue
const { getMacById, deleteMacAddress } = useMacAddresses();

const macId = route.params.id;
const isDeleting = ref(false);

const state = reactive({
  mac: {},
  isLoading: true
});

// Determine which view to go back to after deletion
const backRoute = computed(() => {
  const fromView = route.query.from;
  
  if (fromView === 'table') {
    return '/removemac-table';
  } else {
    return '/removemac';
  }
});

const deleteMac = async () => {
  // Use the same confirmation pattern as MacAddressListTable.vue
  if (confirm(`Are you sure you want to delete MAC address ${state.mac.clientMac}?`)) {
    isDeleting.value = true;
    try {
      // Use the same deletion method as MacAddressListTable.vue
      await deleteMacAddress(parseInt(macId));
      toast.success('MAC address deleted successfully');
      // Route back to the view the user came from
      router.push(backRoute.value);
    } catch (error) {
      console.error('Error deleting MAC address:', error);
      // Use alert for consistency with MacAddressListTable.vue, but keep toast for better UX in this view
      toast.error('Failed to delete MAC address. Please try again.');
    } finally {
      isDeleting.value = false;
    }
  }
};

onMounted(async () => {
  try {
    // Use the same method as the composable for consistency
    const macData = await getMacById(macId);
    state.mac = macData;
  } catch (error) {
    console.error('Error fetching MAC address:', error);
    toast.error('Error loading MAC address details');
  } finally {
    state.isLoading = false;
  }
});
</script>

<template>
  <BackButtonMacs />
  <section v-if="!state.isLoading" class="bg-green-50">
    <div class="container m-auto py-10 px-6">
      <div class="grid grid-cols-1 md:grid-cols-70/30 w-full gap-6">
        <main>
          <div
            class="bg-white p-6 rounded-lg shadow-md text-center md:text-left">
            <div class="text-gray-500 mb-4">Full Mac address</div>
            <h1 class="text-3xl font-bold mb-4">{{ state.mac.clientMac }}</h1>
            <!-- <div
              class="text-gray-500 mb-4 flex align-middle justify-center md:justify-start">
              <i
                class="pi pi-map-marker text-lx text-orange-700 mr-2"></i>
              <p class="text-orange-700">Boston, MA</p>
            </div> -->
          </div>

          <div class="bg-white p-6 rounded-lg shadow-md mt-6">
            <h3 class="text-green-800 text-lg font-bold mb-6">
              Reason
            </h3>

            <p class="mb-4">
              {{ state.mac.reason }}
            </p>

            <h3 class="text-green-800 text-lg font-bold mb-2">Block Time</h3>

            <p class="mb-4">{{ state.mac.blockTime }}</p>

            <h3 class="text-green-800 text-lg font-bold mb-2">Remaining Time</h3>
            <p class="mb-4">{{ state.mac.remainingTime }}</p>
          </div>
        </main>

        <!-- Sidebar -->
        <aside>
          <!-- Company Info -->
          <div class="bg-white p-6 rounded-lg shadow-md">
            <!-- <h3 class="text-xl font-bold mb-6">WLC details</h3>

            <h2 class="text-2xl">{{ state.mac.wlc }}</h2> -->

            <!-- <p class="my-2">
              NewTek Solutions is a leading technology company specializing in
              web development and digital solutions. We pride ourselves on
              delivering high-quality products and services to our clients
              while fostering a collaborative and innovative work environment.
            </p> 

            <hr class="my-4" />

            <h3 class="text-xl">Contact Email:</h3>

            <p class="my-2 bg-green-100 p-2 font-bold">
              contact@newteksolutions.com
            </p>

            <h3 class="text-xl">Contact Phone:</h3>

            <p class="my-2 bg-green-100 p-2 font-bold">555-555-5555</p>
            -->
          </div>

          <!-- Manage -->
          <div class="bg-white p-6 rounded-lg shadow-md mt-6">
            <h3 class="text-xl font-bold mb-6">Manage MAC</h3>
            <!--
            <RouterLink
              :to="`/macs/edit/${state.mac.id}`"
              class="bg-green-500 hover:bg-green-600 text-white text-center font-bold py-2 px-4 rounded-full w-full focus:outline-none focus:shadow-outline mt-4 block">Edit MAC
            </RouterLink>
            -->
            <button 
              @click="deleteMac"
              :disabled="isDeleting"
              class="bg-red-500 hover:bg-red-600 disabled:bg-gray-400 text-white font-bold py-2 px-4 rounded-full w-full focus:outline-none focus:shadow-outline mt-4 block">
              {{ isDeleting ? 'Deleting...' : 'Delete MAC' }}
            </button>
          </div>
        </aside>
      </div>
    </div>
  </section>

  <!-- Show loading spinner while loading is true -->
  <div v-else class="text-center text-gray-500 py-6">
    <PulseLoader />
  </div>

</template>