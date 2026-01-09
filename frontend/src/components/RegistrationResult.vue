<script setup>
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';

// Props
const props = defineProps({
  type: {
    type: String,
    required: true,
    validator: (value) => ['success', 'error'].includes(value)
  },
  result: {
    type: Object,
    required: true
  }
});

// Emits
const emit = defineEmits(['retry', 'reset']);

const router = useRouter();
const route = useRoute();

// Computed properties
const isSuccess = computed(() => props.type === 'success');
const isError = computed(() => props.type === 'error');

// Handle navigation to login
const goToLogin = () => {
  // Check if there's a redirect query parameter that should be preserved
  const redirectTo = route.query.redirect;
  
  if (redirectTo) {
    router.push({ 
      name: 'login', 
      query: { redirect: redirectTo } 
    });
  } else {
    router.push({ name: 'login' });
  }
};

// Handle retry registration
const handleRetry = () => {
  emit('retry');
};
</script>

<template>
  <div class="bg-white px-6 py-8 mb-4 shadow-md rounded-md border m-4 md:m-0">
    <!-- Success State -->
    <div
      v-if="isSuccess"
      class="text-center"
    >
      <!-- Success Icon -->
      <div class="mx-auto mb-4 w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
        <svg
          class="w-8 h-8 text-green-500"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M5 13l4 4L19 7"
          />
        </svg>
      </div>

      <!-- Success Title -->
      <h2 class="text-3xl font-semibold text-green-600 mb-4">
        Account Created Successfully!
      </h2>

      <!-- Success Message -->
      <p class="text-gray-600 mb-6">
        {{ result.message || 'Your account has been created successfully.' }}
      </p>
      
      <!-- Additional login prompt -->
      <div
        v-if="result.showLoginPrompt"
        class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6"
      >
        <p class="text-sm text-blue-700">
          <strong>Next Step:</strong> Please log in with your new credentials to access your account.
          <span
            v-if="result.redirectAfterLogin && result.redirectAfterLogin !== '/'"
            class="block mt-1"
          >
            After logging in, you'll be redirected to your requested page.
          </span>
        </p>
      </div>

      <!-- User Info (if available) -->
      <div
        v-if="result.user"
        class="bg-gray-50 rounded-lg p-4 mb-6"
      >
        <p class="text-sm text-gray-600 mb-2">
          Account Details:
        </p>
        <div class="text-left">
          <p><strong>Name:</strong> {{ result.user.name }}</p>
          <p><strong>Email:</strong> {{ result.user.email }}</p>
          <p
            v-if="result.user.id"
            class="text-xs text-gray-500 mt-2"
          >
            User ID: {{ result.user.id }}
          </p>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="space-y-3">
        <button
          class="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-6 rounded-full w-full focus:outline-none focus:shadow-outline transition-colors"
          @click="goToLogin"
        >
          Continue to Login
        </button>
      </div>
    </div>

    <!-- Error State -->
    <div
      v-if="isError"
      class="text-center"
    >
      <!-- Error Icon -->
      <div class="mx-auto mb-4 w-16 h-16 bg-red-100 rounded-full flex items-center justify-center">
        <svg
          class="w-8 h-8 text-red-500"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M6 18L18 6M6 6l12 12"
          />
        </svg>
      </div>

      <!-- Error Title -->
      <h2 class="text-3xl font-semibold text-red-600 mb-4">
        {{ result.title || 'Registration Failed' }}
      </h2>

      <!-- Error Message -->
      <p class="text-gray-700 mb-4 font-medium">
        {{ result.message || 'An error occurred during registration.' }}
      </p>

      <!-- Error Details -->
      <div
        v-if="result.details"
        class="bg-red-50 border border-red-200 rounded-lg p-4 mb-6"
      >
        <p class="text-sm text-red-700">
          {{ result.details }}
        </p>
      </div>

      <!-- Login suggestion for certain errors -->
      <div
        v-if="result.canSwitchToLogin && result.isEmailAlreadyExists"
        class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6"
      >
        <p class="text-sm text-blue-700">
          <strong>Suggestion:</strong> If you already have an account with this email, you can log in instead.
          <span
            v-if="result.preserveRedirect"
            class="block mt-1"
          >
            Your original destination will be preserved after login.
          </span>
        </p>
      </div>

      <!-- Action Buttons -->
      <div class="space-y-3">
        <!-- For email already exists error - prioritize login -->
        <template v-if="result.isEmailAlreadyExists">
          <button
            class="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-6 rounded-full w-full focus:outline-none focus:shadow-outline transition-colors"
            @click="goToLogin"
          >
            Go to Login
          </button>
          
          <button
            class="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-6 rounded-full w-full focus:outline-none focus:shadow-outline transition-colors"
            @click="handleRetry"
          >
            Try Different Email
          </button>
        </template>
        
        <!-- For other errors - standard retry options -->
        <template v-else>
          <button
            v-if="result.canRetry !== false"
            class="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-6 rounded-full w-full focus:outline-none focus:shadow-outline transition-colors"
            @click="handleRetry"
          >
            Try Again
          </button>
          
          <button
            class="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-6 rounded-full w-full focus:outline-none focus:shadow-outline transition-colors"
            @click="goToLogin"
          >
            Go to Login
          </button>
        </template>
      </div>

      <!-- Help Text -->
      <div class="mt-6 pt-4 border-t border-gray-200">
        <p class="text-sm text-gray-500">
          If you continue to experience issues, please contact support.
        </p>
      </div>
    </div>
  </div>
</template>