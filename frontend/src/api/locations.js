/**
 * API Module for Locations
 * Factory function pattern - receives axios instance, returns CRUD methods
 */

const baseURL = "locations";

export default function ({components}) {
    return {
        /**
         * Get all locations
         * Called with nothing this is the request it always was, and the answer is the
         * whole array. Given a query - page, size, sort, search, or a field to filter
         * by - the same route answers with a page: content, page, size, totalElements,
         * totalPages.
         *
         * @param {Object} [query] - what to ask for; omit for the whole collection
         * @returns {Promise} Axios response with locations array
         */
        getAll(query) {
            return components.get(baseURL, query ? { params: query } : undefined);
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
