<script setup>
import logo from '@/assets/img/logo.png';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { onMounted, ref, watch } from 'vue';
import { useAuth } from '@/composables/useAuth';
import { useToast } from 'vue-toastification';

const route = useRoute();
const router = useRouter();
const toast = useToast();

// Use the composable
const { 
  isLoggedIn, 
  username, 
  logout, 
  initializeAuth, 
  isAdmin, 
  hasRole 
} = useAuth();

const isMenuOpen = ref(false);

onMounted(async () => {
  // Initialize auth on component mount
  await initializeAuth();
});

const handleLogout = async () => {
  try {
    await logout();
    toast.success('Logged out successfully');
    router.push('/login');
  } catch (error) {
    console.error('Logout error:', error);
    toast.error('Logout failed');
  }
};

const isActiveLink = (routerPath) => {
  return route.path === routerPath;
};

// Special function for Remove mac button to handle both card and table views
const isRemoveMacActive = () => {
  return route.path === '/removemac' || route.path === '/removemac-table';
};

// Function to check if user can access admin routes
const canAccessAdminRoutes = () => {
  return isAdmin.value;
};

watch(
  () => route.path,
  () => {
    isMenuOpen.value = false;
  }
);

</script>

<template>
  <nav class="bg-green-700 border-b border-green-500">
    <div class="mx-auto max-w-7xl px-2 sm:px-6 lg:px-8">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between">
        <div class="flex h-20 items-center justify-between">
          <!-- Logo -->
          <RouterLink
            class="flex flex-shrink-0 items-center mr-4"
            to="/"
          >
            <img
              class="h-10 w-auto"
              :src="logo"
              alt="SwitchScope"
            >
            <span class="hidden md:block text-white text-2xl font-bold ml-2">SwitchScope</span>
          </RouterLink>

          <button
            v-if="isLoggedIn"
            class="inline-flex items-center justify-center rounded-md p-2 text-white hover:bg-green-800 md:hidden"
            :aria-expanded="isMenuOpen"
            aria-label="Toggle navigation"
            @click="isMenuOpen = !isMenuOpen"
          >
            <i :class="isMenuOpen ? 'pi pi-times' : 'pi pi-bars'" />
          </button>
        </div>

        <!-- Only show navigation when logged in -->
        <div
          v-if="isLoggedIn"
          :class="[isMenuOpen ? 'block' : 'hidden', 'md:block md:ml-auto']"
        >
          <div class="flex flex-col gap-1 pb-3 md:flex-row md:items-center md:gap-0 md:space-x-2 md:pb-0">
            <!-- Home - Available to all authenticated users -->
            <RouterLink
              to="/"
              :class="[isActiveLink('/')
                         ? 'bg-green-900'
                         : 'hover:bg-gray-900 hover:text-white',
                       'text-white', 'px-3', 'py-2', 'rounded-md']"
            >
              Home
            </RouterLink>

            <!-- Dashboard - Available to both USER and ADMIN -->
            <RouterLink
              v-if="hasRole('USER') || hasRole('ADMIN')"
              to="/dashboard"
              :class="[isRemoveMacActive()
                         ? 'bg-green-900'
                         : 'hover:bg-gray-900 hover:text-white',
                       'text-white', 'px-3', 'py-2', 'rounded-md']"
            >
              Dashboard
            </RouterLink>

            <!-- Admin-only navigation items -->
            <template v-if="canAccessAdminRoutes()">
              <RouterLink
                to="/users/allusers"
                :class="[isActiveLink('/users/allusers')
                           ? 'bg-green-900'
                           : 'hover:bg-gray-900 hover:text-white',
                         'text-white', 'px-3', 'py-2', 'rounded-md']"
              >
                All Users
              </RouterLink>
              
              <RouterLink
                to="/users/add"
                :class="[isActiveLink('/users/add')
                           ? 'bg-green-900'
                           : 'hover:bg-gray-900 hover:text-white',
                         'text-white', 'px-3', 'py-2', 'rounded-md']"
              >
                Add User
              </RouterLink>
            </template>

            <!-- My Account - Available to all authenticated users -->
            <RouterLink
              to="/user"
              :class="[isActiveLink('/user')
                         ? 'bg-green-900'
                         : 'hover:bg-gray-900 hover:text-white',
                       'text-white', 'px-3', 'py-2', 'rounded-md']"
            >
              My Account
            </RouterLink>

            <!-- Logout Button -->
            <button
              class="text-white px-3 py-2 rounded-md hover:bg-red-700"
              @click="handleLogout"
            >
              Logout
            </button>

            <!-- User Name and Role Display -->
            <div class="flex items-center space-x-2">
              <span class="text-white px-3 py-2 bg-green-800 rounded-md">
                Welcome, {{ username }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>
