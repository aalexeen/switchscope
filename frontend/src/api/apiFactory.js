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
       * @returns {Promise} Axios response with array of items
       */
      getAll() {
        return components.get(baseURL);
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
