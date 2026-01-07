<script setup>
import { RouterLink } from 'vue-router';
import { reactive, defineProps, onMounted, ref, watchEffect, computed } from 'vue';
import UserList from './UserList.vue';
import axios from 'axios';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';


const state = reactive({
    users: [],
    isLoading: true
});

const props = defineProps({
    limit: Number,
    showButton: {
        type: Boolean,
        defaule: false
    }
});

// const localLimit = ref(props.limit);

// watchEffect(() => {
//     localLimit.value = props.limit;
// });

// const increment = () => {
//     localLimit.value = state.users.length;
// };

const showFullUserList = ref(false);

const toggleFullUserList = () => {
    showFullUserList.value = !showFullUserList.value;
}

const trunkatedUserList = computed(() => {
    let list = state.users.length;
    if (!showFullUserList.value) {
        list = props.limit;
    }
    return list;
});

onMounted(async () => {
    try {
        const response = await axios.get('/api/users');
        state.users = response.data;
        console.log('Fetching users Ok')
    } catch (error) {
        console.error('Error fetching users', error);
    } finally {
        state.isLoading = false;
    }
});
</script>

<template>
    <section class="bg-blue-50 px-4 py-10">
        <div class="container-xl lg:container m-auto">
            <h2 class="text-3xl font-bold text-green-500 mb-6 text-center">
                Browse Users
            </h2>
            <!-- Show loading spinner while loading is true -->
            <div v-if="state.isLoading" class="text-center text-gray-500 py-6">
                <PulseLoader />
            </div>

            <!-- Show mac listing when done loading   ############# || state.users.length -->
            <div v-else class="grid grid-cols-1 md:grid-cols-3 gap-6">
                <UserList v-for="user in state.users.slice(0, trunkatedUserList)" :key="user.id" :user="user" />
            </div>
        </div>

    </section>

    <!-- to do, send the limit like props to AllUserView for UserListings-->
    <section v-if="showButton" class="m-auto max-w-lg my-10 px-6">
        <div class="flex justify-center items-center v-screen">
            <button
                @click="toggleFullUserList"
                class="block bg-black text-white text-center py-4 px-8 rounded-xl hover:bg-gray-700">
                {{ showFullUserList ? 'View Less Users' : 'View All Users' }}
            </button>
        </div>
    </section>

    <!-- <RouterLink
        to="/users/allusers"
        class="block bg-black text-white text-center py-4 px-6 rounded-xl hover:bg-gray-700"
        >View All Users</RouterLink> -->
</template>