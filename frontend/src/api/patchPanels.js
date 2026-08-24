/**
 * API Module for Patch Panels
 * Handles all patch panel-related API calls
 */

const baseURL = "connectivity/patch-panels";

export default function ({components}) {
    return {
        /**
         * Get all patch panels
         * Called with nothing this is the request it always was, and the answer is the
         * whole array. Given a query - page, size, sort, search, or a field to filter
         * by - the same route answers with a page: content, page, size, totalElements,
         * totalPages.
         *
         * @param {Object} [query] - what to ask for; omit for the whole collection
         * @returns {Promise} Axios response with patch panels array
         */
        getAll(query) {
            return components.get(baseURL, query ? { params: query } : undefined);
        },

        /**
         * Get a specific patch panel by ID
         * @param {string} id - Patch Panel UUID
         * @returns {Promise} Axios response with patch panel object
         */
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },

        /**
         * Create a new patch panel
         * @param {Object} payload - Patch Panel data
         * @returns {Promise} Axios response with created patch panel
         */
        create(payload) {
            return components.post(baseURL, payload);
        },

        /**
         * Update an existing patch panel
         * @param {string} id - Patch Panel UUID
         * @param {Object} payload - Updated patch panel data
         * @returns {Promise} Axios response with updated patch panel
         */
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },

        /**
         * Delete a patch panel
         * @param {string} id - Patch Panel UUID
         * @returns {Promise} Axios response
         */
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
