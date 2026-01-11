<script setup>
import Card from '@/components/Card.vue';
import { RouterLink } from 'vue-router';
</script>

<template>
  <section class="bg-green-50 min-h-screen py-10">
    <div class="container m-auto px-6">
      <h1 class="text-4xl font-bold mb-8 text-green-800 text-center">
        SwitchScope Dashboard
      </h1>
      
      <!-- Catalog Section -->
      <div class="mb-10">
        <h2 class="text-2xl font-bold mb-4 text-gray-700">
          Catalogs
        </h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card bg="bg-blue-100">
            <h3 class="text-lg font-bold mb-2">
              Component Categories
            </h3>
            <RouterLink
              to="/catalog/component-categories"
              class="inline-block bg-blue-500 text-white rounded-lg px-4 py-2 hover:bg-blue-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-blue-100">
            <h3 class="text-lg font-bold mb-2">
              Component Models
            </h3>
            <RouterLink
              to="/catalog/component-models"
              class="inline-block bg-blue-500 text-white rounded-lg px-4 py-2 hover:bg-blue-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-blue-100">
            <h3 class="text-lg font-bold mb-2">
              Component Natures
            </h3>
            <RouterLink
              to="/catalog/component-natures"
              class="inline-block bg-blue-500 text-white rounded-lg px-4 py-2 hover:bg-blue-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-blue-100">
            <h3 class="text-lg font-bold mb-2">
              Component Statuses
            </h3>
            <RouterLink
              to="/catalog/component-statuses"
              class="inline-block bg-blue-500 text-white rounded-lg px-4 py-2 hover:bg-blue-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-blue-100">
            <h3 class="text-lg font-bold mb-2">
              Component Types
            </h3>
            <RouterLink
              to="/catalog/component-types"
              class="inline-block bg-blue-500 text-white rounded-lg px-4 py-2 hover:bg-blue-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-blue-100">
            <h3 class="text-lg font-bold mb-2">
              Installable Types
            </h3>
            <RouterLink
              to="/catalog/installable-types"
              class="inline-block bg-blue-500 text-white rounded-lg px-4 py-2 hover:bg-blue-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-blue-100">
            <h3 class="text-lg font-bold mb-2">
              Installation Statuses
            </h3>
            <RouterLink
              to="/catalog/installation-statuses"
              class="inline-block bg-blue-500 text-white rounded-lg px-4 py-2 hover:bg-blue-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-blue-100">
            <h3 class="text-lg font-bold mb-2">
              Location Types
            </h3>
            <RouterLink
              to="/catalog/location-types"
              class="inline-block bg-blue-500 text-white rounded-lg px-4 py-2 hover:bg-blue-600"
            >
              Open
            </RouterLink>
          </Card>
        </div>
      </div>

      <!-- Components Section -->
      <div class="mb-10">
        <h2 class="text-2xl font-bold mb-4 text-gray-700">
          Components
        </h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card bg="bg-green-100">
            <h3 class="text-lg font-bold mb-2">
              All Components
            </h3>
            <RouterLink
              to="/components"
              class="inline-block bg-green-500 text-white rounded-lg px-4 py-2 hover:bg-green-600"
            >
              Open
            </RouterLink>
          </Card>
        </div>
        
        <!-- Devices -->
        <h3 class="text-xl font-semibold mt-6 mb-3 text-gray-600">
          Devices
        </h3>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card bg="bg-green-100">
            <h3 class="text-lg font-bold mb-2">
              All Devices
            </h3>
            <RouterLink
              to="/devices"
              class="inline-block bg-green-500 text-white rounded-lg px-4 py-2 hover:bg-green-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-green-100">
            <h3 class="text-lg font-bold mb-2">
              Network Switches
            </h3>
            <RouterLink
              to="/components/devices/switches"
              class="inline-block bg-green-500 text-white rounded-lg px-4 py-2 hover:bg-green-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-green-100">
            <h3 class="text-lg font-bold mb-2">
              Routers
            </h3>
            <RouterLink
              to="/components/devices/routers"
              class="inline-block bg-green-500 text-white rounded-lg px-4 py-2 hover:bg-green-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-green-100">
            <h3 class="text-lg font-bold mb-2">
              Access Points
            </h3>
            <RouterLink
              to="/components/devices/access-points"
              class="inline-block bg-green-500 text-white rounded-lg px-4 py-2 hover:bg-green-600"
            >
              Open
            </RouterLink>
          </Card>
        </div>

        <!-- Connectivity -->
        <h3 class="text-xl font-semibold mt-6 mb-3 text-gray-600">
          Connectivity
        </h3>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card bg="bg-green-100">
            <h3 class="text-lg font-bold mb-2">
              Cable Runs
            </h3>
            <RouterLink
              to="/components/connectivity/cable-runs"
              class="inline-block bg-green-500 text-white rounded-lg px-4 py-2 hover:bg-green-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-green-100">
            <h3 class="text-lg font-bold mb-2">
              Connectors
            </h3>
            <RouterLink
              to="/components/connectivity/connectors"
              class="inline-block bg-green-500 text-white rounded-lg px-4 py-2 hover:bg-green-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-green-100">
            <h3 class="text-lg font-bold mb-2">
              Patch Panels
            </h3>
            <RouterLink
              to="/components/connectivity/patch-panels"
              class="inline-block bg-green-500 text-white rounded-lg px-4 py-2 hover:bg-green-600"
            >
              Open
            </RouterLink>
          </Card>
        </div>

        <!-- Housing -->
        <h3 class="text-xl font-semibold mt-6 mb-3 text-gray-600">
          Housing
        </h3>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card bg="bg-green-100">
            <h3 class="text-lg font-bold mb-2">
              Racks
            </h3>
            <RouterLink
              to="/components/housing/racks"
              class="inline-block bg-green-500 text-white rounded-lg px-4 py-2 hover:bg-green-600"
            >
              Open
            </RouterLink>
          </Card>
        </div>
      </div>

      <!-- Other Sections -->
      <div class="mb-10">
        <h2 class="text-2xl font-bold mb-4 text-gray-700">
          Other
        </h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card bg="bg-purple-100">
            <h3 class="text-lg font-bold mb-2">
              Installations
            </h3>
            <RouterLink
              to="/installations"
              class="inline-block bg-purple-500 text-white rounded-lg px-4 py-2 hover:bg-purple-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-orange-100">
            <h3 class="text-lg font-bold mb-2">
              Locations
            </h3>
            <RouterLink
              to="/locations"
              class="inline-block bg-orange-500 text-white rounded-lg px-4 py-2 hover:bg-orange-600"
            >
              Open
            </RouterLink>
          </Card>
          <Card bg="bg-cyan-100">
            <h3 class="text-lg font-bold mb-2">
              Ports
            </h3>
            <RouterLink
              to="/ports"
              class="inline-block bg-cyan-500 text-white rounded-lg px-4 py-2 hover:bg-cyan-600"
            >
              Open
            </RouterLink>
          </Card>
        </div>
      </div>
    </div>
  </section>
</template>
