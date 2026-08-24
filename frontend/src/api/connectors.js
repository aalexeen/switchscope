/**
 * API Module for Connectors
 * Handles all connector-related API calls
 */

const baseURL = "connectivity/connectors";

export default function ({components}) {
    return {
        /**
         * Get all connectors
         * Called with nothing this is the request it always was, and the answer is the
         * whole array. Given a query - page, size, sort, search, or a field to filter
         * by - the same route answers with a page: content, page, size, totalElements,
         * totalPages.
         *
         * @param {Object} [query] - what to ask for; omit for the whole collection
         * @returns {Promise} Axios response with connectors array
         */
        getAll(query) {
            return components.get(baseURL, query ? { params: query } : undefined);
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
