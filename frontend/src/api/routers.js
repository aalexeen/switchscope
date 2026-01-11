/**
 * API Module for Routers
 * Handles all router-related API calls
 */

const baseURL = "devices/routers";

export default function ({components}) {
    return {
        /**
         * Get all routers
         * @returns {Promise} Axios response with routers array
         */
        getAll() {
            return components.get(baseURL);
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
