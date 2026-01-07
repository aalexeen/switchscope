<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuth } from '@/composables/useAuth';
import RegistrationForm from '@/components/RegistrationForm.vue';
import RegistrationResult from '@/components/RegistrationResult.vue';

const router = useRouter();
const route = useRoute();
const { isLoggedIn } = useAuth();

// Component state
const currentView = ref('form'); // 'form', 'result'
const isLoading = ref(false);
const resultData = ref(null);
const resultType = ref(null); // 'success' or 'error'

// Refs for child components
const registrationFormRef = ref(null);

// Check if user is already logged in on component mount
onMounted(() => {
  // If user is already authenticated, redirect to home or intended page
  if (isLoggedIn.value) {
    const redirectTo = route.query.redirect || '/';
    router.push(redirectTo);
  }
});

// Handle form submission start
const handleSubmitStart = () => {
  isLoading.value = true;
};

// Handle successful registration
const handleRegistrationSuccess = (successData) => {
  isLoading.value = false;
  resultType.value = 'success';
  resultData.value = {
    ...successData,
    // Add any additional context for the success page
    showLoginPrompt: true,
    redirectAfterLogin: route.query.redirect || '/'
  };
  currentView.value = 'result';
};

// Handle registration error
const handleRegistrationError = (errorData) => {
  isLoading.value = false;
  resultType.value = 'error';
  resultData.value = {
    ...errorData,
    // Add context for better error handling
    canSwitchToLogin: true,
    preserveRedirect: !!route.query.redirect
  };
  currentView.value = 'result';
};

// Handle retry registration (go back to form with same data)
const handleRetry = () => {
  currentView.value = 'form';
  resultData.value = null;
  resultType.value = null;
  // Keep the form data as is for retry
};

// Handle reset registration (go back to form with clean data)
const handleReset = () => {
  currentView.value = 'form';
  resultData.value = null;
  resultType.value = null;
  
  // Reset the form data
  if (registrationFormRef.value) {
    registrationFormRef.value.resetForm();
  }
};
</script>

<template>
  <section class="bg-green-50 min-h-screen">
    <div class="container m-auto max-w-md py-24">
      
      <!-- Registration Form -->
      <RegistrationForm
        v-if="currentView === 'form'"
        ref="registrationFormRef"
        :loading="isLoading"
        @submit="handleSubmitStart"
        @success="handleRegistrationSuccess"
        @error="handleRegistrationError"
      />

      <!-- Registration Result -->
      <RegistrationResult
        v-if="currentView === 'result'"
        :type="resultType"
        :result="resultData"
        @retry="handleRetry"
        @reset="handleReset"
      />

    </div>
  </section>
</template>