/**
 * API Module for Ports
 * Factory function pattern - receives axios instance, returns CRUD methods
 */

const baseURL = "ports";

export default function ({components}) {
    return {
        /**
         * Get all ports
         * @returns {Promise} Axios response with ports array
         */
        getAll() {
            return components.get(baseURL);
        },

        /**
         * Get a single port by ID
         * @param {string} id - Port UUID
         * @returns {Promise} Axios response with port object
         */
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },

        /**
         * Create a new port
         * @param {Object} payload - Port data
         * @returns {Promise} Axios response with created port
         */
        create(payload) {
            return components.post(baseURL, payload);
        },

        /**
         * Update an existing port
         * @param {string} id - Port UUID
         * @param {Object} payload - Updated port data
         * @returns {Promise} Axios response with updated port
         */
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },

        /**
         * Delete a port
         * @param {string} id - Port UUID
         * @returns {Promise} Axios response
         */
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
