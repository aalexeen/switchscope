// api - 3rd step
import instance from "./instance";

import blockedmacsModule from "./blockedmacs";
import authModule from "./authentication";
import componentsModule from "./components/components";
import componentNaturesModule from "./components/componentNatures";
import componentCategoriesModule from "./components/componentCategories";
import componentTypesModule from "./components/componentTypes";
import componentStatusesModule from "./components/componentStatuses";
import componentModelsModule from "./components/catalog/componentModels";
import locationTypesModule from "./locations/locationTypes";
import installationStatusesModule from "./installations/installationStatuses";

export default {
    blockedmacs: blockedmacsModule(instance),
    authentication: authModule(instance),
    components: componentsModule(instance),
    componentNatures: componentNaturesModule(instance),
    componentCategories: componentCategoriesModule(instance),
    componentTypes: componentTypesModule(instance),
    componentStatuses: componentStatusesModule(instance),
    componentModels: componentModelsModule(instance),
    locationTypes: locationTypesModule(instance),
    installationStatuses: installationStatusesModule(instance)
}
