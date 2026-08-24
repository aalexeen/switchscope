/**
 * API Module for Network Switches
 * Handles all network switch-related API calls
 */

const baseURL = "devices/switches";

export default function ({components}) {
    return {
        /**
         * Get all network switches
         * Called with nothing this is the request it always was, and the answer is the
         * whole array. Given a query - page, size, sort, search, or a field to filter
         * by - the same route answers with a page: content, page, size, totalElements,
         * totalPages.
         *
         * @param {Object} [query] - what to ask for; omit for the whole collection
         * @returns {Promise} Axios response with switches array
         */
        getAll(query) {
            return components.get(baseURL, query ? { params: query } : undefined);
        },

        /**
         * Get a specific switch by ID
         * @param {string} id - Switch UUID
         * @returns {Promise} Axios response with switch object
         */
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },

        /**
         * Create a new switch
         * @param {Object} payload - Switch data
         * @returns {Promise} Axios response with created switch
         */
        create(payload) {
            return components.post(baseURL, payload);
        },

        /**
         * Update an existing switch
         * @param {string} id - Switch UUID
         * @param {Object} payload - Updated switch data
         * @returns {Promise} Axios response with updated switch
         */
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },

        /**
         * Delete a switch
         * @param {string} id - Switch UUID
         * @returns {Promise} Axios response
         */
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
