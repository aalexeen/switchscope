/**
 * API Factory - Generic CRUD operations factory
 *
 * Creates a standard API module with common CRUD operations.
 * Eliminates code duplication across API modules.
 *
 * @param {string} baseURL - The base URL for the API endpoint (e.g., "catalogs/component-natures")
 * @returns {Function} Factory function that accepts axios instance and returns API methods
 *
 * @example
 * // In api/index.js:
 * import { createApiModule } from './apiFactory';
 *
 * export default {
 *   componentNatures: createApiModule('catalogs/component-natures')(instance),
 *   componentCategories: createApiModule('catalogs/component-categories')(instance),
 * }
 */
export function createApiModule(baseURL) {
  return function({ components }) {
    return {
      /**
       * Get all items
       * Called with nothing this is the request it always was, and the answer is the
       * whole array. Given a query - page, size, sort, search, or a field to filter
       * by - the same route answers with a page: content, page, size, totalElements,
       * totalPages.
       *
       * @param {Object} [query] - what to ask for; omit for the whole collection
       * @returns {Promise} Axios response with array of items
       */
      getAll(query) {
        return components.get(baseURL, query ? { params: query } : undefined);
      },

      /**
       * Get single item by ID
       * @param {string} id - Item UUID
       * @returns {Promise} Axios response with single item
       */
      get(id) {
        return components.get(`${baseURL}/${id}`);
      },

      /**
       * Create new item
       * @param {Object} payload - Item data
       * @returns {Promise} Axios response with created item
       */
      create(payload) {
        return components.post(baseURL, payload);
      },

      /**
       * Update existing item
       * @param {string} id - Item UUID
       * @param {Object} payload - Updated item data
       * @returns {Promise} Axios response with updated item
       */
      update(id, payload) {
        return components.put(`${baseURL}/${id}`, payload);
      },

      /**
       * Delete item
       * @param {string} id - Item UUID
       * @returns {Promise} Axios response
       */
      delete(id) {
        return components.delete(`${baseURL}/${id}`);
      }
    };
  };
}
