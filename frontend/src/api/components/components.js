// api - components module
const baseURL = "components";

export default function ({components}) {
    return {
        /**
         * Get all items, or one page of them.
         * Called with nothing this is the request it always was, and the answer is the
         * whole array. Given a query - page, size, sort, search, or a field to filter
         * by - the same route answers with a page: content, page, size, totalElements,
         * totalPages.
         *
         * @param {Object} [query] - what to ask for; omit for the whole collection
         */
        getAll(query) {
            return components.get(baseURL, query ? { params: query } : undefined);
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
