/**
 * API Module for Installations
 * Factory function pattern - receives axios instance, returns CRUD methods
 */

const baseURL = "installations";

export default function ({components}) {
    return {
        /**
         * Get all installations
         * Called with nothing this is the request it always was, and the answer is the
         * whole array. Given a query - page, size, sort, search, or a field to filter
         * by - the same route answers with a page: content, page, size, totalElements,
         * totalPages.
         *
         * @param {Object} [query] - what to ask for; omit for the whole collection
         * @returns {Promise} Axios response with installations array
         */
        getAll(query) {
            return components.get(baseURL, query ? { params: query } : undefined);
        },

        /**
         * Get a single installation by ID
         * @param {string} id - Installation UUID
         * @returns {Promise} Axios response with installation object
         */
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },

        /**
         * Create a new installation
         * @param {Object} payload - Installation data
         * @returns {Promise} Axios response with created installation
         */
        create(payload) {
            return components.post(baseURL, payload);
        },

        /**
         * Update an existing installation
         * @param {string} id - Installation UUID
         * @param {Object} payload - Updated installation data
         * @returns {Promise} Axios response with updated installation
         */
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },

        /**
         * Delete an installation
         * @param {string} id - Installation UUID
         * @returns {Promise} Axios response
         */
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
