<script setup>
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import BackButtonUsers from '../components/BackButtonUsers.vue';
import { reactive, onMounted } from 'vue';
import { useRoute, RouterLink, useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const toast = useToast();

const userId = route.params.id;

const state = reactive({
  user: {},
  isLoading: true
});

const deleteUser = async () => {
  try {
    const confirm = window.confirm('Are you sure you want to delete this user?');
    if (confirm) {
      await axios.delete(`/api/users/${userId}`);
      toast.success('User Deleted Successfully');
      router.push('/users/allusers');
    }
  } catch (error) {
    console.error('Error deleting user', error);
    toast.error('Job Not Deleted');
  }
};

onMounted(async () => {
  try {
    const response = await axios.get(`/api/users/${userId}`);
    state.user = response.data;
  } catch {
    console.error('Fetching user data error');
  } finally {
    state.isLoading = false;
  }
});
</script>

<template>
  <BackButtonUsers />
  <section
    v-if="!state.isLoading"
    class="bg-green-50"
  >
    <div class="container m-auto py-10 px-6">
      <div class="grid grid-cols-1 md:grid-cols-70/30 w-full gap-6">
        <main>
          <div
            class="bg-white p-6 rounded-lg shadow-md text-center md:text-left"
          >
            <div class="text-gray-500 mb-4">
              {{ state.user.nickname }}
            </div>
            <h1 class="text-3xl font-bold mb-4">
              {{ state.user.first_name }}
            </h1>
            <!-- <div
              class="text-gray-500 mb-4 flex align-middle justify-center md:justify-start">
              <i
                class="pi pi-map-marker text-lx text-orange-700 mr-2"></i>
              <p class="text-orange-700">Boston, MA</p>
            </div> -->
          </div>

          <div class="bg-white p-6 rounded-lg shadow-md mt-6">
            <h3 class="text-green-800 text-lg font-bold mb-6">
              Last Name
            </h3>

            <p class="mb-4">
              {{ state.user.last_name }}
            </p>

            <h3 class="text-green-800 text-lg font-bold mb-2">
              Subdivision
            </h3>

            <p class="mb-4">
              {{ state.user.subdivision }}
            </p>

            <h3 class="text-green-800 text-lg font-bold mb-2">
              Team
            </h3>
            <p class="mb-4">
              {{ state.user.team }}
            </p>
          </div>
        </main>

        <!-- Sidebar -->
        <aside>
          <!-- Company Info -->
          <div class="bg-white p-6 rounded-lg shadow-md">
            <h3 class="text-xl font-bold mb-6">
              User Details
            </h3>

            <h2 class="text-2xl">
              {{ state.user.description }}
            </h2>

            <!-- <p class="my-2">
              NewTek Solutions is a leading technology company specializing in
              web development and digital solutions. We pride ourselves on
              delivering high-quality products and services to our clients
              while fostering a collaborative and innovative work environment.
            </p> -->

            <hr class="my-4">

            <h3 class="text-xl">
              Contact Email:
            </h3>

            <p class="my-2 bg-green-100 p-2 font-bold">
              {{ state.user.contactEmail }}
            </p>

            <h3 class="text-xl">
              Contact Phone:
            </h3>

            <p class="my-2 bg-green-100 p-2 font-bold">
              {{ state.user.contactPhone }}
            </p>
          </div>

          <!-- Manage -->
          <div class="bg-white p-6 rounded-lg shadow-md mt-6">
            <h3 class="text-xl font-bold mb-6">
              Manage User
            </h3>
            <RouterLink
              :to="`/users/edit/${state.user.id}`"
              class="bg-green-500 hover:bg-green-600 text-white text-center font-bold py-2 px-4 rounded-full w-full focus:outline-none focus:shadow-outline mt-4 block"
            >
              Edit User
            </RouterLink>
            <button
              class="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded-full w-full focus:outline-none focus:shadow-outline mt-4 block"
              @click="deleteUser"
            >
              Delete User
            </button>
          </div>
        </aside>
      </div>
    </div>
  </section>

  <!-- Show loading spinner while loading is true -->
  <div
    v-else
    class="text-center text-gray-500 py-6"
  >
    <PulseLoader />
  </div>
</template>