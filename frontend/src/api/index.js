// api - 3rd step
import instance from "./instance";

import blockedmacsModule from "./blockedmacs";
import authModule from "./authentication";

export default {
    blockedmacs: blockedmacsModule(instance),
    authentication: authModule(instance)
}