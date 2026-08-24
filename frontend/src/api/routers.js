/**
 * API Module for Routers
 * Handles all router-related API calls
 */

const baseURL = "devices/routers";

export default function ({components}) {
    return {
        /**
         * Get all routers
         * Called with nothing this is the request it always was, and the answer is the
         * whole array. Given a query - page, size, sort, search, or a field to filter
         * by - the same route answers with a page: content, page, size, totalElements,
         * totalPages.
         *
         * @param {Object} [query] - what to ask for; omit for the whole collection
         * @returns {Promise} Axios response with routers array
         */
        getAll(query) {
            return components.get(baseURL, query ? { params: query } : undefined);
        },

        /**
         * Get a specific router by ID
         * @param {string} id - Router UUID
         * @returns {Promise} Axios response with router object
         */
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },

        /**
         * Create a new router
         * @param {Object} payload - Router data
         * @returns {Promise} Axios response with created router
         */
        create(payload) {
            return components.post(baseURL, payload);
        },

        /**
         * Update an existing router
         * @param {string} id - Router UUID
         * @param {Object} payload - Updated router data
         * @returns {Promise} Axios response with updated router
         */
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },

        /**
         * Delete a router
         * @param {string} id - Router UUID
         * @returns {Promise} Axios response
         */
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
