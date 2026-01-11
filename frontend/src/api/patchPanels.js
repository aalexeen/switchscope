/**
 * API Module for Patch Panels
 * Handles all patch panel-related API calls
 */

const baseURL = "connectivity/patch-panels";

export default function ({components}) {
    return {
        /**
         * Get all patch panels
         * @returns {Promise} Axios response with patch panels array
         */
        getAll() {
            return components.get(baseURL);
        },

        /**
         * Get a specific patch panel by ID
         * @param {string} id - Patch Panel UUID
         * @returns {Promise} Axios response with patch panel object
         */
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },

        /**
         * Create a new patch panel
         * @param {Object} payload - Patch Panel data
         * @returns {Promise} Axios response with created patch panel
         */
        create(payload) {
            return components.post(baseURL, payload);
        },

        /**
         * Update an existing patch panel
         * @param {string} id - Patch Panel UUID
         * @param {Object} payload - Updated patch panel data
         * @returns {Promise} Axios response with updated patch panel
         */
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },

        /**
         * Delete a patch panel
         * @param {string} id - Patch Panel UUID
         * @returns {Promise} Axios response
         */
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
