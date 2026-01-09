// api - 3rd step
import instance from "./instance";
import { createApiModule } from "./apiFactory";

// Legacy modules (not yet migrated to factory pattern)
import blockedmacsModule from "./blockedmacs";
import authModule from "./authentication";
import componentsModule from "./components/components";

/**
 * API Modules Registry
 *
 * Most modules use the createApiModule factory for standard CRUD operations.
 * Legacy modules (blockedmacs, authentication, components) retain custom implementations.
 */
export default {
    // Legacy modules with custom logic
    blockedmacs: blockedmacsModule(instance),
    authentication: authModule(instance),
    components: componentsModule(instance),

    // Standard catalog modules using factory pattern
    componentNatures: createApiModule('catalogs/component-natures')(instance),
    componentCategories: createApiModule('catalogs/component-categories')(instance),
    componentTypes: createApiModule('catalogs/component-types')(instance),
    componentStatuses: createApiModule('catalogs/component-statuses')(instance),
    componentModels: createApiModule('catalogs/component-models')(instance),
    locationTypes: createApiModule('catalogs/location-types')(instance),
    installationStatuses: createApiModule('catalogs/installation-statuses')(instance),
    installableTypes: createApiModule('catalogs/installable-types')(instance)
}
