<script setup>
import { ref, onMounted } from 'vue';
import { useAuth } from '@/composables/useAuth';
import { useToast } from 'vue-toastification';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

const { getProfile } = useAuth();
const toast = useToast();

const userProfile = ref(null);
const isLoading = ref(true);
const error = ref(null);

const fetchUserProfile = async () => {
    try {
        isLoading.value = true;
        error.value = null;

        const profile = await getProfile();
        userProfile.value = profile;

    } catch (err) {
        console.error('Error fetching user profile:', err);
        error.value = err;
        toast.error('Failed to load user profile');
    } finally {
        isLoading.value = false;
    }
};

onMounted(() => {
    fetchUserProfile();
});

const formatDate = (dateString) => {
    if (!dateString) return 'Not available';

    try {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch (error) {
        return 'Invalid date';
    }
};

const refreshProfile = () => {
    fetchUserProfile();
};
</script>

<template>
    <section class="bg-green-50 min-h-screen">
        <div class="container m-auto py-10 px-6">

            <!-- Page Header -->
            <div class="text-center mb-10">
                <h1 class="text-4xl font-bold text-green-700 mb-4">My Account</h1>
                <p class="text-gray-600">Manage your account information and settings</p>
            </div>

            <!-- Loading Spinner -->
            <div v-if="isLoading" class="text-center text-gray-500 py-6">
                <PulseLoader />
            </div>

            <!-- Error State -->
            <div v-else-if="error" class="text-center py-12">
                <div class="bg-red-50 border border-red-200 rounded-lg p-6 max-w-md mx-auto">
                    <div class="text-red-500 mb-4">
                        <svg class="w-12 h-12 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.728-.833-2.498 0L4.316 15.5c-.77.833.192 2.5 1.732 2.5z" />
                        </svg>
                    </div>
                    <h2 class="text-xl font-bold text-red-700 mb-2">Unable to Load Profile</h2>
                    <p class="text-red-600 mb-4">There was an error loading your profile information.</p>
                    <button
                        @click="refreshProfile"
                        class="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded transition-colors">
                        Try Again
                    </button>
                </div>
            </div>

            <!-- User Profile Content -->
            <div v-else-if="userProfile" class="max-w-4xl mx-auto">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">

                    <!-- Basic Information -->
                    <div class="bg-white p-6 rounded-lg shadow-md">
                        <div class="flex items-center justify-between mb-6">
                            <h2 class="text-2xl font-bold text-green-700">Basic Information</h2>
                            <button
                                @click="refreshProfile"
                                class="text-green-600 hover:text-green-700 p-2 rounded-full hover:bg-green-50 transition-colors"
                                title="Refresh profile">
                                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                                </svg>
                            </button>
                        </div>

                        <div class="space-y-4">
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
                                <p class="text-gray-900 bg-gray-50 p-3 rounded border">
                                    {{ userProfile.name || 'Not specified' }}
                                </p>
                            </div>

                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
                                <p class="text-gray-900 bg-gray-50 p-3 rounded border">
                                    {{ userProfile.email || 'Not specified' }}
                                </p>
                            </div>

                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">User ID</label>
                                <p class="text-gray-500 bg-gray-50 p-3 rounded border font-mono text-sm">
                                    #{{ userProfile.id }}
                                </p>
                            </div>
                        </div>
                    </div>

                    <!-- Account Status -->
                    <div class="bg-white p-6 rounded-lg shadow-md">
                        <h2 class="text-2xl font-bold text-green-700 mb-6">Account Status</h2>

                        <div class="space-y-4">
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">Account Status</label>
                                <div class="p-3 rounded border" :class="[
                                    userProfile.enabled
                                        ? 'text-green-800 bg-green-50 border-green-200'
                                        : 'text-red-800 bg-red-50 border-red-200'
                                ]">
                                    <div class="flex items-center">
                                        <div class="w-3 h-3 rounded-full mr-2" :class="[
                                            userProfile.enabled ? 'bg-green-500' : 'bg-red-500'
                                        ]"></div>
                                        {{ userProfile.enabled ? 'Active' : 'Inactive' }}
                                    </div>
                                </div>
                            </div>

                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">Member Since</label>
                                <p class="text-gray-900 bg-gray-50 p-3 rounded border">
                                    {{ formatDate(userProfile.registered) }}
                                </p>
                            </div>

                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">User Roles</label>
                                <div class="bg-gray-50 p-3 rounded border">
                                    <div v-if="userProfile.roles && userProfile.roles.length > 0" class="flex flex-wrap gap-2">
                                        <span
                                            v-for="role in userProfile.roles"
                                            :key="role"
                                            class="inline-block bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-1 rounded-full">
                                            {{ role }}
                                        </span>
                                    </div>
                                    <p v-else class="text-gray-500 text-sm">No roles assigned</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Admin Restaurants (if any) -->
                <div v-if="userProfile.adminRestaurants && userProfile.adminRestaurants.length > 0" class="bg-white p-6 rounded-lg shadow-md mt-6">
                    <h2 class="text-2xl font-bold text-green-700 mb-6">Admin Restaurants</h2>

                    <div class="bg-blue-50 border border-blue-200 rounded-lg p-4">
                        <div class="flex flex-wrap gap-2">
                            <span
                                v-for="restaurantId in userProfile.adminRestaurants"
                                :key="restaurantId"
                                class="inline-block bg-blue-100 text-blue-800 text-sm font-medium px-3 py-1 rounded-full">
                                Restaurant ID: {{ restaurantId }}
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Actions Section -->
                <div class="bg-white p-6 rounded-lg shadow-md mt-6">
                    <h2 class="text-2xl font-bold text-green-700 mb-6">Account Actions</h2>

                    <div class="flex flex-wrap gap-4">
                        <button
                            class="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-6 rounded-lg transition-colors"
                            @click="toast.info('Profile editing feature coming soon!')">
                            Edit Profile
                        </button>

                        <button
                            class="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-6 rounded-lg transition-colors"
                            @click="toast.info('Change password feature coming soon!')">
                            Change Password
                        </button>

                        <button
                            class="bg-gray-500 hover:bg-gray-600 text-white font-bold py-2 px-6 rounded-lg transition-colors"
                            @click="refreshProfile">
                            Refresh Data
                        </button>
                    </div>
                </div>

                <!-- Account Summary -->
                <div class="bg-gradient-to-r from-green-500 to-green-600 text-white p-6 rounded-lg shadow-md mt-6">
                    <h2 class="text-2xl font-bold mb-4">Account Summary</h2>
                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-center">
                        <div class="bg-white bg-opacity-20 rounded-lg p-4">
                            <div class="text-2xl font-bold">{{ userProfile.roles?.length || 0 }}</div>
                            <div class="text-sm opacity-90">Active Roles</div>
                        </div>
                        <div class="bg-white bg-opacity-20 rounded-lg p-4">
                            <div class="text-2xl font-bold">{{ userProfile.adminRestaurants?.length || 0 }}</div>
                            <div class="text-sm opacity-90">Admin Restaurants</div>
                        </div>
                        <div class="bg-white bg-opacity-20 rounded-lg p-4">
                            <div class="text-2xl font-bold">{{ userProfile.enabled ? 'Yes' : 'No' }}</div>
                            <div class="text-sm opacity-90">Account Active</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</template>