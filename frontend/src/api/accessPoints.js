/**
 * API Module for Access Points
 * Handles all access point-related API calls
 */

const baseURL = "devices/access-points";

export default function ({components}) {
    return {
        /**
         * Get all access points
         * Called with nothing this is the request it always was, and the answer is the
         * whole array. Given a query - page, size, sort, search, or a field to filter
         * by - the same route answers with a page: content, page, size, totalElements,
         * totalPages.
         *
         * @param {Object} [query] - what to ask for; omit for the whole collection
         * @returns {Promise} Axios response with access points array
         */
        getAll(query) {
            return components.get(baseURL, query ? { params: query } : undefined);
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
