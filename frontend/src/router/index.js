import { createRouter, createWebHistory } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import DashboardView from "@/views/DashboardView.vue";
import RemoveMacView from "@/views/RemoveMacView.vue";
import NotFoundView from "@/views/NotFoundView.vue";
import MacView from "@/views/MacView.vue";
import AddUserView from "@/views/AddUserView.vue";
import AllUsersView from "@/views/AllUsersView.vue";
import UserView from "@/views/UserView.vue";
import EditUserView from "@/views/EditUserView.vue";
import Login from "@/views/Login.vue";
import Registration from "@/views/Registration.vue";
import ComponentView from "@/views/component/ComponentView.vue";
import ComponentNatureView from "@/views/catalog/ComponentNatureView.vue";
import ComponentCategoryView from "@/views/catalog/ComponentCategoryView.vue";
import ComponentTypeView from "@/views/catalog/ComponentTypeView.vue";
import ComponentStatusView from "@/views/catalog/ComponentStatusView.vue";
import ComponentModelView from "@/views/catalog/ComponentModelView.vue";
import LocationTypeView from "@/views/catalog/LocationTypeView.vue";
import InstallationStatusView from "@/views/catalog/InstallationStatusView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      name: "login",
      component: Login,
      meta: { requiresGuest: true }, // New meta property for guest-only pages
    },
    {
      path: "/register", // Add registration route
      name: "register",
      component: Registration,
      meta: { requiresGuest: true }, // Only allow access if not authenticated
    },
    {
      path: "/",
      name: "home",
      component: HomeView,
      meta: { 
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/dashboard",
      name: "dashboard",
      component: DashboardView,
      meta: {
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/components",
      name: "components",
      component: ComponentView,
      meta: {
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/catalog/component-natures",
      name: "component-natures",
      component: ComponentNatureView,
      meta: {
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/catalog/component-categories",
      name: "component-categories",
      component: ComponentCategoryView,
      meta: {
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/catalog/component-types",
      name: "component-types",
      component: ComponentTypeView,
      meta: {
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/catalog/component-statuses",
      name: "component-statuses",
      component: ComponentStatusView,
      meta: {
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/catalog/component-models",
      name: "component-models",
      component: ComponentModelView,
      meta: {
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/catalog/location-types",
      name: "location-types",
      component: LocationTypeView,
      meta: {
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/catalog/installation-statuses",
      name: "installation-statuses",
      component: InstallationStatusView,
      meta: {
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/removemac",
      name: "removemac",
      component: RemoveMacView,
      meta: {
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/removemac-table", // Add new table view route
      name: "removemac-table",
      component: RemoveMacView,
      meta: { 
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/removemac/:id",
      name: "macview",
      component: MacView,
      meta: { 
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access
      },
    },
    {
      path: "/user",
      name: "user-profile",
      component: () => import("@/views/MyAccount.vue"),
      meta: { 
        requiresAuth: true,
        roles: ['USER', 'ADMIN'] // Both users and admins can access their own profile
      },
    },
    {
      path: "/users/allusers/",
      name: "all-users",
      component: AllUsersView,
      meta: { 
        requiresAuth: true,
        roles: ['ADMIN'] // Only admins can view all users
      },
    },
    {
      path: "/users/allusers/:id",
      name: "userview",
      component: UserView,
      meta: { 
        requiresAuth: true,
        roles: ['ADMIN'] // Only admins can view other users
      },
    },
    {
      path: "/users/add/",
      name: "add-user",
      component: AddUserView,
      meta: { 
        requiresAuth: true,
        roles: ['ADMIN'] // Only admins can add users
      },
    },
    {
      path: "/users/edit/:id",
      name: "edit-user",
      component: EditUserView,
      meta: { 
        requiresAuth: true,
        roles: ['ADMIN'] // Only admins can edit users
      },
    },
    {
      path: "/:catchAll(.*)",
      name: "not-found",
      component: NotFoundView,
    },
  ],
});

router.beforeEach(async (to, from, next) => {
  // Import the composable inside the guard to avoid circular dependencies
  const { useAuth } = await import("@/composables/useAuth");
  const { isLoggedIn, verifyAuth, hasAnyRole } = useAuth();

  // Check if the route requires authentication
  if (to.meta.requiresAuth) {
    // If not logged in locally, redirect to login
    if (!isLoggedIn.value) {
      next({
        name: "login",
        query: { redirect: to.fullPath },
      });
      return;
    }

    // Check if the route requires specific roles
    if (to.meta.roles && to.meta.roles.length > 0) {
      // Check if user has any of the required roles
      if (!hasAnyRole(to.meta.roles)) {
        // User doesn't have required role, redirect to home or show unauthorized
        next({ 
          name: "home", 
          query: { error: "unauthorized" } 
        });
        return;
      }
    }

    // Optional: Verify authentication with server
    // Uncomment this if you want server-side verification on every route change
    /*
    try {
      const isValid = await verifyAuth();
      if (!isValid) {
        next({ 
          name: "login", 
          query: { redirect: to.fullPath } 
        });
        return;
      }
    } catch (error) {
      console.error('Auth verification failed:', error);
      next({ 
        name: "login", 
        query: { redirect: to.fullPath } 
      });
      return;
    }
    */
  }

  // Check if the route requires guest (not authenticated)
  if (to.meta.requiresGuest && isLoggedIn.value) {
    next({ name: "home" });
    return;
  }

  // Allow navigation
  next();
});

export default router;