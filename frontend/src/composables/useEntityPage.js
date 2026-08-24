/**
 * useEntityPage - one page of an entity's rows, read from the server
 *
 * The entity composables keep a singleton list of everything, which is what the dropdowns and the
 * dashboards read; a table cannot share it. A table that paged that list would either page a copy
 * of the whole table - which is the thing pagination exists to stop - or overwrite the cache with
 * one page and leave every dropdown on the screen showing twenty options out of two hundred.
 *
 * So this is separate state, created per view rather than shared: the rows of the page the user is
 * looking at, and enough about the collection to offer the next one. Searching and ordering are
 * the server's too, because they have to be: a search that filtered only the current page would
 * find what happens to be on it.
 */

import { ref } from 'vue';
import api from '@/api';

/**
 * @param {string} apiKey - key of the API module, the same key the table is registered under
 * @param {Object} [options]
 * @param {number} [options.size] - rows per page to start with
 * @param {string[]} [options.searchIn] - fields the server should search; all its text if omitted
 * @param {string} [options.sort] - initial ordering, as 'field' or 'field:desc'
 */
export function useEntityPage(apiKey, options = {}) {
  const module = api[apiKey];
  if (!module) {
    throw new Error(`No API module registered as '${apiKey}'; a table reads its pages through one`);
  }

  const rows = ref([]);
  const total = ref(0);
  const totalPages = ref(0);
  const page = ref(0);
  const size = ref(options.size ?? 20);
  const sort = ref(options.sort ?? null);
  const search = ref('');
  const searchIn = options.searchIn ?? [];
  const isLoading = ref(false);
  const error = ref(null);

  /** How many rows there are with nothing searched for, so a search can say "3 of 137". */
  const collectionSize = ref(0);

  const query = () => {
    const asked = { page: page.value, size: size.value };
    if (sort.value) {
      asked.sort = sort.value;
    }
    const term = search.value.trim();
    if (term) {
      asked.search = term;
      if (searchIn.length) {
        asked.searchIn = searchIn.join(',');
      }
    }
    return asked;
  };

  const load = async () => {
    isLoading.value = true;
    error.value = null;
    try {
      const { data } = await module.getAll(query());
      rows.value = data.content;
      total.value = data.totalElements;
      totalPages.value = data.totalPages;
      if (!search.value.trim()) {
        collectionSize.value = data.totalElements;
      }
      // Deleting the last row of the last page leaves the view past the end of a collection that
      // still has rows; stepping back is what the user meant by deleting it. Only ever backwards:
      // retrying the page it is already on would be a loop, and an empty page for any other reason
      // than "past the end" is an answer, not a mistake to correct.
      const lastPage = Math.max(0, totalPages.value - 1);
      if (rows.value.length === 0 && lastPage < page.value) {
        page.value = lastPage;
        return load();
      }
    } catch (err) {
      console.error(`Error loading a page of ${apiKey}:`, err);
      error.value = err.response?.data?.detail || err.message || 'Failed to load';
      rows.value = [];
      total.value = 0;
      totalPages.value = 0;
    } finally {
      isLoading.value = false;
    }
    return undefined;
  };

  const goToPage = (wanted) => {
    const last = Math.max(0, totalPages.value - 1);
    page.value = Math.min(Math.max(0, wanted), last);
    return load();
  };

  const setSize = (wanted) => {
    size.value = wanted;
    page.value = 0;
    return load();
  };

  /** A new search starts at the first page: page four of the old answer is not page four of this one. */
  const setSearch = (term) => {
    search.value = term ?? '';
    page.value = 0;
    return load();
  };

  const setSort = (spec) => {
    sort.value = spec || null;
    page.value = 0;
    return load();
  };

  return {
    rows, total, totalPages, page, size, sort, search, collectionSize, isLoading, error,
    load, goToPage, setSize, setSearch, setSort
  };
}
