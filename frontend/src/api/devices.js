/**
 * API Module for Devices
 * Handles all device-related API calls (switches, routers, access points, etc.)
 */

const baseURL = "devices";

export default function ({components}) {
    return {
        /**
         * Get all devices
         * @returns {Promise} Axios response with devices array
         */
        getAll() {
            return components.get(baseURL);
        },

        /**
         * Get a specific device by ID
         * @param {string} id - Device UUID
         * @returns {Promise} Axios response with device object
         */
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },

        /**
         * Create a new device
         * @param {Object} payload - Device data
         * @returns {Promise} Axios response with created device
         */
        create(payload) {
            return components.post(baseURL, payload);
        },

        /**
         * Update an existing device
         * @param {string} id - Device UUID
         * @param {Object} payload - Updated device data
         * @returns {Promise} Axios response with updated device
         */
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },

        /**
         * Delete a device
         * @param {string} id - Device UUID
         * @returns {Promise} Axios response
         */
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
