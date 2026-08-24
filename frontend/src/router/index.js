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
// Universal Generic Table View - handles ALL table pages (catalogs + entities)
import GenericTableView from "@/views/GenericTableView.vue";
// Universal Generic Detail View - handles ALL detail pages
import GenericDetailView from "@/views/GenericDetailView.vue";
// Specialized Component Model Detail View
import ComponentModelDetailView from "@/views/ComponentModelDetailView.vue";
// Permission codes, so that a route names a constant rather than a string
import { RESOURCE, read } from "@/configs/permissions";

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
      },
    },
    {
      path: "/dashboard",
      name: "dashboard",
      component: DashboardView,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: "/components",
      name: "components",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.components),
        tableKey: 'components' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/components/:id",
      name: "component-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.components),
        tableKey: 'components',
        detailKey: 'component'
      },
    },
    {
      path: "/devices",
      name: "devices",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.devices),
        tableKey: 'devices' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/devices/:id",
      name: "device-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.devices),
        tableKey: 'devices',
        detailKey: 'device'
      },
    },
    {
      path: "/components/devices/switches",
      name: "network-switches",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.networkSwitches),
        tableKey: 'networkSwitches' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/components/devices/switches/:id",
      name: "network-switch-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.networkSwitches),
        tableKey: 'networkSwitches',
        detailKey: 'networkSwitch'
      },
    },
    {
      path: "/components/devices/routers",
      name: "routers",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.routers),
        tableKey: 'routers' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/components/devices/routers/:id",
      name: "router-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.routers),
        tableKey: 'routers',
        detailKey: 'router'
      },
    },
    {
      path: "/components/devices/access-points",
      name: "access-points",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.accessPoints),
        tableKey: 'accessPoints' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/components/devices/access-points/:id",
      name: "access-point-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.accessPoints),
        tableKey: 'accessPoints',
        detailKey: 'accessPoint'
      },
    },
    {
      path: "/components/connectivity/cable-runs",
      name: "cable-runs",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.cableRuns),
        tableKey: 'cableRuns' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/components/connectivity/cable-runs/:id",
      name: "cable-run-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.cableRuns),
        tableKey: 'cableRuns',
        detailKey: 'cableRun'
      },
    },
    {
      path: "/components/connectivity/connectors",
      name: "connectors",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.connectors),
        tableKey: 'connectors' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/components/connectivity/connectors/:id",
      name: "connector-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.connectors),
        tableKey: 'connectors',
        detailKey: 'connector'
      },
    },
    {
      path: "/components/connectivity/patch-panels",
      name: "patch-panels",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.patchPanels),
        tableKey: 'patchPanels' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/components/connectivity/patch-panels/:id",
      name: "patch-panel-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.patchPanels),
        tableKey: 'patchPanels',
        detailKey: 'patchPanel'
      },
    },
    {
      path: "/components/housing/racks",
      name: "racks",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.racks),
        tableKey: 'racks' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/components/housing/racks/:id",
      name: "rack-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.racks),
        tableKey: 'racks',
        detailKey: 'rack'
      },
    },
    {
      path: "/installations",
      name: "installations",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.installations),
        tableKey: 'installations' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/installations/:id",
      name: "installation-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.installations),
        tableKey: 'installations',
        detailKey: 'installation'
      },
    },
    {
      path: "/locations",
      name: "locations",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.locations),
        tableKey: 'locations' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/locations/:id",
      name: "location-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.locations),
        tableKey: 'locations',
        detailKey: 'location'
      },
    },
    {
      path: "/ports",
      name: "ports",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.ports),
        tableKey: 'ports' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/ports/:id",
      name: "port-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.ports),
        tableKey: 'ports',
        detailKey: 'port'
      },
    },
    // Catalog routes - all use GenericTableView with tableKey metadata
    {
      path: "/catalog/component-natures",
      name: "component-natures",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.componentNatures),
        tableKey: 'componentNatures' // Tells GenericTableView which table config to use
      },
    },
    {
      path: "/catalog/component-natures/:id",
      name: "component-nature-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.componentNatures),
        detailKey: 'componentNature'
      },
    },
    {
      path: "/catalog/component-categories",
      name: "component-categories",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.componentCategories),
        tableKey: 'componentCategories'
      },
    },
    {
      path: "/catalog/component-categories/:id",
      name: "component-category-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.componentCategories),
        detailKey: 'componentCategory'
      },
    },
    {
      path: "/catalog/component-types",
      name: "component-types",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.componentTypes),
        tableKey: 'componentTypes'
      },
    },
    {
      path: "/catalog/component-types/:id",
      name: "component-type-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.componentTypes),
        detailKey: 'componentType'
      },
    },
    {
      path: "/catalog/component-statuses",
      name: "component-statuses",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.componentStatuses),
        tableKey: 'componentStatuses'
      },
    },
    {
      path: "/catalog/component-statuses/:id",
      name: "component-status-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.componentStatuses),
        detailKey: 'componentStatus'
      },
    },
    {
      path: "/catalog/component-models",
      name: "component-models",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.componentModels),
        tableKey: 'componentModels'
      },
    },
    {
      path: "/catalog/component-models/:id",
      name: "component-model-detail",
      component: ComponentModelDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.componentModels),
      },
    },
    {
      path: "/catalog/location-types",
      name: "location-types",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.locationTypes),
        tableKey: 'locationTypes'
      },
    },
    {
      path: "/catalog/location-types/:id",
      name: "location-type-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.locationTypes),
        tableKey: 'locationTypes',
        detailKey: 'locationType'
      },
    },
    {
      path: "/catalog/installation-statuses",
      name: "installation-statuses",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.installationStatuses),
        tableKey: 'installationStatuses'
      },
    },
    {
      path: "/catalog/installation-statuses/:id",
      name: "installation-status-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.installationStatuses),
        tableKey: 'installationStatuses',
        detailKey: 'installationStatus'
      },
    },
    {
      path: "/catalog/installable-types",
      name: "installable-types",
      component: GenericTableView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.installableTypes),
        tableKey: 'installableTypes'
      },
    },
    {
      path: "/catalog/installable-types/:id",
      name: "installable-type-detail",
      component: GenericDetailView,
      meta: {
        requiresAuth: true,
        permission: read(RESOURCE.installableTypes),
        tableKey: 'installableTypes',
        detailKey: 'installableType'
      },
    },
    {
      path: "/removemac",
      name: "removemac",
      component: RemoveMacView,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: "/removemac-table", // Add new table view route
      name: "removemac-table",
      component: RemoveMacView,
      meta: { 
        requiresAuth: true,
      },
    },
    {
      path: "/removemac/:id",
      name: "macview",
      component: MacView,
      meta: { 
        requiresAuth: true,
      },
    },
    {
      path: "/user",
      name: "user-profile",
      component: () => import("@/views/MyAccount.vue"),
      meta: { 
        requiresAuth: true,
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
  // Import the composables inside the guard to avoid circular dependencies
  const { useAuth } = await import("@/composables/useAuth");
  const { usePermissions } = await import("@/composables/usePermissions");
  const { isLoggedIn, hasAnyRole } = useAuth();
  const { can } = usePermissions();

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

    // A page that lists something is gated on the permission to read it, which is what the
    // server would answer the page's own request with. Sending them to a page whose every
    // request comes back 403 is not a kinder outcome than not opening it.
    if (to.meta.permission && !can(to.meta.permission)) {
      next({
        name: "home",
        query: { error: "unauthorized" }
      });
      return;
    }

    // Roles still gate /users/**: there is no user domain in the permission model at all - no
    // /api/users, no user.* permission - so a code to check here would be one this frontend
    // invented. The four routes stay on the role until the backend has an opinion.
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
