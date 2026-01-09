// api - location types module
const baseURL = "catalogs/location-types";

export default function ({components}) {
    return {
        getAll() {
            return components.get(baseURL);
        },
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },
        create(payload) {
            return components.post(baseURL, payload);
        },
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
