import "@/assets/main.css";
import "primeicons/primeicons.css";
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";
import router from "@/router";
import axios from "axios";
import authService from "@/services/auth"; // Adjust path as needed

import { createApp, isProxy } from "vue";
import App from "./App.vue";

import ApiPlugin from "@/plugins/api";
import LoadPlugin from "@/plugins/load";

// Configure axios defaults
// axios.defaults.baseURL = import.meta.env.VITE_API_URL || "/api";

// Initialize Basic Auth if user is already authenticated
authService.initializeAuth();

// Add axios interceptor to handle 401 responses
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Clear auth data and redirect to login on 401
      authService.logout();
      router.push({ name: "login" });
    }
    return Promise.reject(error);
  }
);

const app = createApp(App);

// Toast options
const toastOptions = {
  position: "top-right",
  timeout: 3000,
  closeOnClick: true,
  pauseOnHover: true,
  draggable: true,
  draggablePercent: 0.6,
};
// Register plugins
app.use(router);
app.use(Toast, toastOptions);
app.use(ApiPlugin);
app.use(LoadPlugin);

app.mount("#app");
