/**
 * API Module for Connectors
 * Handles all connector-related API calls
 */

const baseURL = "connectivity/connectors";

export default function ({components}) {
    return {
        /**
         * Get all connectors
         * @returns {Promise} Axios response with connectors array
         */
        getAll() {
            return components.get(baseURL);
        },

        /**
         * Get a specific connector by ID
         * @param {string} id - Connector UUID
         * @returns {Promise} Axios response with connector object
         */
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },

        /**
         * Create a new connector
         * @param {Object} payload - Connector data
         * @returns {Promise} Axios response with created connector
         */
        create(payload) {
            return components.post(baseURL, payload);
        },

        /**
         * Update an existing connector
         * @param {string} id - Connector UUID
         * @param {Object} payload - Updated connector data
         * @returns {Promise} Axios response with updated connector
         */
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },

        /**
         * Delete a connector
         * @param {string} id - Connector UUID
         * @returns {Promise} Axios response
         */
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
