<script setup>
import { reactive } from 'vue';
import { useToast } from 'vue-toastification';
import { useRouter, useRoute } from 'vue-router';
import { useAuth } from '@/composables/useAuth';

const router = useRouter();
const route = useRoute();
const toast = useToast();

// Use the auth composable
const { login } = useAuth();

const form = reactive({
  email: '',
  password: '',
  remember: false
});

const handleSubmit = async () => {
  try {
    // Use auth service for proper Basic Auth handling
    const credentials = {
      email: form.email, // Use email instead of username
      password: form.password,
      remember: form.remember
    };

    const response = await login(credentials);

    toast.success('Login Successful');

    // Redirect to the page the user was trying to access, or home if none specified
    const redirectPath = route.query.redirect || '/removemac-table';
    router.push(redirectPath);

  } catch (error) {
    console.error('Login error', error);

    // Enhanced error handling
    if (error.response?.status === 401) {
      toast.error('Invalid username or password.');
    } else if (error.response?.status === 403) {
      toast.error('Access denied. Please contact administrator.');
    } else {
      toast.error('Login Failed. Please check your credentials.');
    }
  }
};
</script>

<template>
  <section class="bg-green-50">
    <div class="container m-auto max-w-md py-24">
      <div
        class="bg-white px-6 py-8 mb-4 shadow-md rounded-md border m-4 md:m-0">
        <form @submit.prevent="handleSubmit">
          <h2 class="text-3xl text-center font-semibold mb-6">Login</h2>

          <div class="mb-4">
            <label
              for="email"
              class="block text-gray-700 font-bold mb-2">Email Address</label>
            <input
              type="email"
              v-model="form.email"
              id="email"
              name="email"
              class="border rounded w-full py-2 px-3"
              placeholder="Enter your email"
              required />
          </div>

          <div class="mb-6">
            <label
              for="password"
              class="block text-gray-700 font-bold mb-2">Password</label>
            <input
              type="password"
              v-model="form.password"
              id="password"
              name="password"
              class="border rounded w-full py-2 px-3"
              placeholder="Enter your password"
              required />
          </div>

          <div class="mb-6">
            <label class="flex items-center">
              <input
                type="checkbox"
                v-model="form.remember"
                class="mr-2" />
              <span class="text-gray-700">Remember me</span>
            </label>
          </div>

          <div>
            <button
              class="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded-full w-full focus:outline-none focus:shadow-outline"
              type="submit">
              Sign In
            </button>
          </div>

          <div class="mt-4 text-center">
            <p class="text-gray-600">
              Don't have an account?
              <router-link to="/register" class="text-green-500 hover:text-green-600">Create Account</router-link>
            </p>
          </div>
        </form>
      </div>
    </div>
  </section>
</template>