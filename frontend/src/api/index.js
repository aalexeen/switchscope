// api - 3rd step
import instance from "./instance";

import blockedmacsModule from "./blockedmacs";
import authModule from "./authentication";
import componentsModule from "./components/components";
import componentNaturesModule from "./components/componentNatures";
import componentModelsModule from "./components/catalog/componentModels";

export default {
    blockedmacs: blockedmacsModule(instance),
    authentication: authModule(instance),
    components: componentsModule(instance),
    componentNatures: componentNaturesModule(instance),
    componentModels: componentModelsModule(instance)
}
