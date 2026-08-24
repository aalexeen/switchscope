/**
 * API Module for Devices
 * Handles all device-related API calls (switches, routers, access points, etc.)
 */

const baseURL = "devices";

export default function ({components}) {
    return {
        /**
         * Get all devices
         * Called with nothing this is the request it always was, and the answer is the
         * whole array. Given a query - page, size, sort, search, or a field to filter
         * by - the same route answers with a page: content, page, size, totalElements,
         * totalPages.
         *
         * @param {Object} [query] - what to ask for; omit for the whole collection
         * @returns {Promise} Axios response with devices array
         */
        getAll(query) {
            return components.get(baseURL, query ? { params: query } : undefined);
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
