/**
 * API Module for Racks
 * Handles all rack-related API calls
 */

const baseURL = "housing/racks";

export default function ({components}) {
    return {
        /**
         * Get all racks
         * @returns {Promise} Axios response with racks array
         */
        getAll() {
            return components.get(baseURL);
        },

        /**
         * Get a specific rack by ID
         * @param {string} id - Rack UUID
         * @returns {Promise} Axios response with rack object
         */
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },

        /**
         * Create a new rack
         * @param {Object} payload - Rack data
         * @returns {Promise} Axios response with created rack
         */
        create(payload) {
            return components.post(baseURL, payload);
        },

        /**
         * Update an existing rack
         * @param {string} id - Rack UUID
         * @param {Object} payload - Updated rack data
         * @returns {Promise} Axios response with updated rack
         */
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },

        /**
         * Delete a rack
         * @param {string} id - Rack UUID
         * @returns {Promise} Axios response
         */
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
