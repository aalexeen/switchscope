<script setup>
import { reactive, ref } from 'vue';
import { useAuth } from '@/composables/useAuth';

// Props and emits
defineProps({
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['submit', 'success', 'error']);

// Use the auth composable
const { register } = useAuth();

// Form state
const form = reactive({
  name: '',
  email: '',
  password: '',
  confirmPassword: ''
});

// Validation errors
const validationErrors = ref({});

// Validate form
const validateForm = () => {
  const errors = {};

  if (!form.name.trim()) {
    errors.name = 'Full name is required';
  }

  if (!form.email.trim()) {
    errors.email = 'Email is required';
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'Please enter a valid email address';
  }

  if (!form.password) {
    errors.password = 'Password is required';
  } else if (form.password.length < 6) {
    errors.password = 'Password must be at least 6 characters long';
  }

  if (!form.confirmPassword) {
    errors.confirmPassword = 'Please confirm your password';
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match';
  }

  validationErrors.value = errors;
  return Object.keys(errors).length === 0;
};

// Handle form submission
const handleSubmit = async () => {
  // Clear previous validation errors
  validationErrors.value = {};

  // Validate form
  if (!validateForm()) {
    return;
  }

  try {
    emit('submit'); // Notify parent that submission started

    // Prepare user data for registration
    const userData = {
      name: form.name.trim(),
      email: form.email.trim(),
      password: form.password
    };

    // Use auth composable for registration
    const response = await register(userData);

    // Emit success event with response data
    emit('success', {
      user: response.data,
      message: 'Registration successful! Please log in to continue.'
    });

  } catch (error) {
    console.error('Registration error:', error);
    console.log('Error response:', error.response); // Debug log
    console.log('Error data:', error.response?.data); // Debug log
    console.log('Error status:', error.response?.status); // Debug log
    console.log('Invalid params:', error.response?.data?.invalid_params); // Debug log for backend structure

    // Parse error details
    let errorInfo = {
      title: 'Registration Failed',
      message: 'Registration failed. Please try again.',
      details: null,
      canRetry: true,
      isEmailAlreadyExists: false
    };

    if (error.response) {
      const status = error.response.status;
      const data = error.response.data;
      
      // Check if the error message indicates email already exists (from backend validation)
      const errorMessage = data?.message || data?.detail || '';
      const fieldErrors = data?.errors || data?.invalid_params || {}; // Check backend's invalid_params structure
      const emailFieldError = fieldErrors?.email || '';
      
      const isEmailExistsError = 
        errorMessage.toLowerCase().includes('email already exists') || 
        errorMessage.toLowerCase().includes('user with this email already exists') ||
        emailFieldError.toLowerCase().includes('email already exists') ||
        emailFieldError.toLowerCase().includes('user with this email already exists');

      switch (status) {
        case 409:
          errorInfo = {
            title: 'Email Already Exists',
            message: 'An account with this email address already exists.',
            details: 'Please use a different email address or try logging in instead.',
            canRetry: true,
            isEmailAlreadyExists: true
          };
          break;
        case 400:
        case 422:
          if (isEmailExistsError) {
            // Backend validation error specifically for email already exists
            errorInfo = {
              title: 'Email Already Exists',
              message: 'An account with this email address already exists.',
              details: emailFieldError || 'Please use a different email address or try logging in instead.',
              canRetry: true,
              isEmailAlreadyExists: true
            };
          } else {
            // Get the actual error details from backend
            const backendDetails = data?.detail || data?.title || 'Please check your input and try again.';
            const invalidParams = data?.invalid_params || {};
            let detailsMessage = backendDetails;
            
            // If there are multiple field errors, show them
            if (Object.keys(invalidParams).length > 0) {
              const fieldErrorMessages = Object.entries(invalidParams)
                .map(([field, msg]) => `${field}: ${msg}`)
                .join(', ');
              detailsMessage = fieldErrorMessages;
            }
            
            errorInfo = {
              title: 'Invalid Data',
              message: 'The registration data is invalid.',
              details: detailsMessage,
              canRetry: true,
              isEmailAlreadyExists: false
            };
          }
          break;
        case 500:
          errorInfo = {
            title: 'Server Error',
            message: 'A server error occurred during registration.',
            details: 'Please try again later or contact support if the problem persists.',
            canRetry: true,
            isEmailAlreadyExists: false
          };
          break;
        default:
          if (data?.message) {
            errorInfo.message = data.message;
            errorInfo.details = 'Please try again or contact support if needed.';
          }
          errorInfo.isEmailAlreadyExists = isEmailExistsError;
      }
    } else if (error.code === 'NETWORK_ERROR') {
      errorInfo = {
        title: 'Network Error',
        message: 'Unable to connect to the server.',
        details: 'Please check your internet connection and try again.',
        canRetry: true,
        isEmailAlreadyExists: false
      };
    }

    // Debug log to see the processed error info
    console.log('Processed error info:', errorInfo); // Debug log

    // Emit error event
    emit('error', errorInfo);
  }
};
</script>

<template>
  <div class="bg-white px-6 py-8 mb-4 shadow-md rounded-md border m-4 md:m-0">
    <form @submit.prevent="handleSubmit">
      <h2 class="text-3xl text-center font-semibold mb-6">
        Create Account
      </h2>

      <!-- Full Name Field -->
      <div class="mb-4">
        <label
          for="name"
          class="block text-gray-700 font-bold mb-2"
        >
          Full Name
        </label>
        <input
          id="name"
          v-model="form.name"
          type="text"
          name="name"
          class="border rounded w-full py-2 px-3"
          :class="{ 'border-red-500': validationErrors.name }"
          placeholder="Enter your full name"
          :disabled="loading"
          required
        >
        <p
          v-if="validationErrors.name"
          class="text-red-500 text-sm mt-1"
        >
          {{ validationErrors.name }}
        </p>
      </div>

      <!-- Email Field -->
      <div class="mb-4">
        <label
          for="email"
          class="block text-gray-700 font-bold mb-2"
        >
          Email Address
        </label>
        <input
          id="email"
          v-model="form.email"
          type="email"
          name="email"
          class="border rounded w-full py-2 px-3"
          :class="{ 'border-red-500': validationErrors.email }"
          placeholder="Enter your email"
          :disabled="loading"
          required
        >
        <p
          v-if="validationErrors.email"
          class="text-red-500 text-sm mt-1"
        >
          {{ validationErrors.email }}
        </p>
      </div>

      <!-- Password Field -->
      <div class="mb-4">
        <label
          for="password"
          class="block text-gray-700 font-bold mb-2"
        >
          Password
        </label>
        <input
          id="password"
          v-model="form.password"
          type="password"
          name="password"
          class="border rounded w-full py-2 px-3"
          :class="{ 'border-red-500': validationErrors.password }"
          placeholder="Enter your password"
          :disabled="loading"
          minlength="6"
          required
        >
        <p
          v-if="validationErrors.password"
          class="text-red-500 text-sm mt-1"
        >
          {{ validationErrors.password }}
        </p>
      </div>

      <!-- Confirm Password Field -->
      <div class="mb-6">
        <label
          for="confirmPassword"
          class="block text-gray-700 font-bold mb-2"
        >
          Confirm Password
        </label>
        <input
          id="confirmPassword"
          v-model="form.confirmPassword"
          type="password"
          name="confirmPassword"
          class="border rounded w-full py-2 px-3"
          :class="{ 'border-red-500': validationErrors.confirmPassword }"
          placeholder="Confirm your password"
          :disabled="loading"
          minlength="6"
          required
        >
        <p
          v-if="validationErrors.confirmPassword"
          class="text-red-500 text-sm mt-1"
        >
          {{ validationErrors.confirmPassword }}
        </p>
      </div>

      <!-- Submit Button -->
      <div>
        <button
          class="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded-full w-full focus:outline-none focus:shadow-outline disabled:opacity-50 disabled:cursor-not-allowed"
          type="submit"
          :disabled="loading"
        >
          <span
            v-if="loading"
            class="flex items-center justify-center"
          >
            <svg
              class="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              />
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              />
            </svg>
            Creating Account...
          </span>
          <span v-else>Sign Up</span>
        </button>
      </div>

      <!-- Login Link -->
      <div class="mt-4 text-center">
        <p class="text-gray-600">
          Already have an account?
          <router-link 
            :to="{ name: 'login' }" 
            class="text-green-500 hover:text-green-600 font-medium"
          >
            Login
          </router-link>
        </p>
      </div>
    </form>
  </div>
</template>