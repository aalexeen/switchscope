// api - 2nd step
const baseURL = "auth";

export default function ({authentification}) {
    return {
        logIn(payload, config = {}) {
            return authentification.post(`${baseURL}/login`, payload, config);
        },
        signUp(payload, config = {}) {
            return authentification.post(`profile`, payload, config);
        },
        logOut(config = {}) {
            return authentification.post(`${baseURL}/logout`, {}, config);
        },
        checkAuth(config = {}) {
            return authentification.get(`${baseURL}/check`, config);
        },
        getProfile(config = {}) {
            return authentification.get(`profile`, config);
        }
    };
}