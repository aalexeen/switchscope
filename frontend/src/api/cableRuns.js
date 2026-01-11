/**
 * API Module for Cable Runs
 * Handles all cable run-related API calls
 */

const baseURL = "connectivity/cable-runs";

export default function ({components}) {
    return {
        /**
         * Get all cable runs
         * @returns {Promise} Axios response with cable runs array
         */
        getAll() {
            return components.get(baseURL);
        },

        /**
         * Get a specific cable run by ID
         * @param {string} id - Cable Run UUID
         * @returns {Promise} Axios response with cable run object
         */
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },

        /**
         * Create a new cable run
         * @param {Object} payload - Cable Run data
         * @returns {Promise} Axios response with created cable run
         */
        create(payload) {
            return components.post(baseURL, payload);
        },

        /**
         * Update an existing cable run
         * @param {string} id - Cable Run UUID
         * @param {Object} payload - Updated cable run data
         * @returns {Promise} Axios response with updated cable run
         */
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },

        /**
         * Delete a cable run
         * @param {string} id - Cable Run UUID
         * @returns {Promise} Axios response
         */
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
