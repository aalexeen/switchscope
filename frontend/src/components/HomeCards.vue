<script setup>
import Card from '@/components/Card.vue';
import { RouterLink } from 'vue-router';
</script>

<template>
    <section class="py-4">
        <div class="container-xl lg:container m-auto">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 p-4 rounded-lg">
                <Card>
                    <h2 class="text-2xl font-bold">For Users</h2>
                    <p class="mt-2 mb-4">
                        Remove the mac here
                    </p>
                    <RouterLink
                        to="/removemac-table"
                        class="inline-block bg-black text-white rounded-lg px-4 py-2 hover:bg-gray-700">
                        Remove mac
                    </RouterLink>
                </Card>
                <Card bg="bg-green-100">
                    <h2 class="text-2xl font-bold">For Admins</h2>
                    <p class="mt-2 mb-4">
                        Get the additional information about the history of the mac-addresses removing
                    </p>
                    <RouterLink
                        to="/users/add"
                        class="inline-block bg-green-500 text-white rounded-lg px-4 py-2 hover:bg-green-600">
                        Additional
                    </RouterLink>
                </Card>
            </div>
        </div>
    </section>
</template>