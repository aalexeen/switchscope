/**
 * API Module for Network Switches
 * Handles all network switch-related API calls
 */

const baseURL = "devices/switches";

export default function ({components}) {
    return {
        /**
         * Get all network switches
         * @returns {Promise} Axios response with switches array
         */
        getAll() {
            return components.get(baseURL);
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
