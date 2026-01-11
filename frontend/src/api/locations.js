/**
 * API Module for Locations
 * Factory function pattern - receives axios instance, returns CRUD methods
 */

const baseURL = "locations";

export default function ({components}) {
    return {
        /**
         * Get all locations
         * @returns {Promise} Axios response with locations array
         */
        getAll() {
            return components.get(baseURL);
        },

        /**
         * Get a single location by ID
         * @param {string} id - Location UUID
         * @returns {Promise} Axios response with location object
         */
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },

        /**
         * Create a new location
         * @param {Object} payload - Location data
         * @returns {Promise} Axios response with created location
         */
        create(payload) {
            return components.post(baseURL, payload);
        },

        /**
         * Update an existing location
         * @param {string} id - Location UUID
         * @param {Object} payload - Updated location data
         * @returns {Promise} Axios response with updated location
         */
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },

        /**
         * Delete a location
         * @param {string} id - Location UUID
         * @returns {Promise} Axios response
         */
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
