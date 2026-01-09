// api - 3rd step
import instance from "./instance";

import blockedmacsModule from "./blockedmacs";
import authModule from "./authentication";
import componentsModule from "./components";
import componentNaturesModule from "./componentNatures";
import componentModelsModule from "./componentModels";

export default {
    blockedmacs: blockedmacsModule(instance),
    authentication: authModule(instance),
    components: componentsModule(instance),
    componentNatures: componentNaturesModule(instance),
    componentModels: componentModelsModule(instance)
}