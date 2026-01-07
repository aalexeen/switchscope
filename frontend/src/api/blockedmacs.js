// api - 2nd step
const baseURL = "blocked_macs";

export default function ({blockedMacs}) {
    return {
        getAll() {
            return blockedMacs.get(baseURL);
        },
        get(id) {
            return blockedMacs.get(`${baseURL}/${id}`);
        },
        create(payload) {
            return blockedMacs.post(baseURL, payload);
        },
        delete(id) {
            return blockedMacs.delete(`${baseURL}/${id}`);
        }
    };
}