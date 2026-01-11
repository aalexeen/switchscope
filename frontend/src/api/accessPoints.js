/**
 * API Module for Access Points
 * Handles all access point-related API calls
 */

const baseURL = "devices/access-points";

export default function ({components}) {
    return {
        /**
         * Get all access points
         * @returns {Promise} Axios response with access points array
         */
        getAll() {
            return components.get(baseURL);
        },

        /**
         * Get a specific access point by ID
         * @param {string} id - Access Point UUID
         * @returns {Promise} Axios response with access point object
         */
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },

        /**
         * Create a new access point
         * @param {Object} payload - Access Point data
         * @returns {Promise} Axios response with created access point
         */
        create(payload) {
            return components.post(baseURL, payload);
        },

        /**
         * Update an existing access point
         * @param {string} id - Access Point UUID
         * @param {Object} payload - Updated access point data
         * @returns {Promise} Axios response with updated access point
         */
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },

        /**
         * Delete an access point
         * @param {string} id - Access Point UUID
         * @returns {Promise} Axios response
         */
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
