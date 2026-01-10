/**
 * Table Registry - Central registry for all table configurations
 *
 * This file imports and registers all table configurations and their composables.
 * Used by GenericTableView to dynamically load the correct configuration.
 *
 * To add a new table:
 * 1. Create config file in configs/tables/
 * 2. Import config here
 * 3. Add to tableRegistry
 * 4. Add composable to composableRegistry
 * 5. Add route with meta.tableKey
 */

// Import all table configurations
import componentNaturesConfig from './componentNatures.config.js';
import componentCategoriesConfig from './componentCategories.config.js';
import componentTypesConfig from './componentTypes.config.js';
import componentStatusesConfig from './componentStatuses.config.js';
import componentModelsConfig from './componentModels.config.js';
import locationTypesConfig from './locationTypes.config.js';
import installationStatusesConfig from './installationStatuses.config.js';
import installableTypesConfig from './installableTypes.config.js';
import componentsConfig from './components.config.js';

// Import all composables
import { useComponentNatures } from '@/composables/useComponentNatures';
import { useComponentCategories } from '@/composables/useComponentCategories';
import { useComponentTypes } from '@/composables/useComponentTypes';
import { useComponentStatuses } from '@/composables/useComponentStatuses';
import { useComponentModels } from '@/composables/useComponentModels';
import { useLocationTypes } from '@/composables/useLocationTypes';
import { useInstallationStatuses } from '@/composables/useInstallationStatuses';
import { useInstallableTypes } from '@/composables/useInstallableTypes';
import { useComponents } from '@/composables/useComponents';

/**
 * Table Registry
 * Maps table keys to their configuration objects
 */
export const tableRegistry = {
  // Catalog tables
  componentNatures: componentNaturesConfig,
  componentCategories: componentCategoriesConfig,
  componentTypes: componentTypesConfig,
  componentStatuses: componentStatusesConfig,
  componentModels: componentModelsConfig,
  locationTypes: locationTypesConfig,
  installationStatuses: installationStatusesConfig,
  installableTypes: installableTypesConfig,

  // Entity tables
  components: componentsConfig
};

/**
 * Composable Registry
 * Maps table keys to their composable factory functions
 */
export const composableRegistry = {
  // Catalog composables
  componentNatures: useComponentNatures,
  componentCategories: useComponentCategories,
  componentTypes: useComponentTypes,
  componentStatuses: useComponentStatuses,
  componentModels: useComponentModels,
  locationTypes: useLocationTypes,
  installationStatuses: useInstallationStatuses,
  installableTypes: useInstallableTypes,

  // Entity composables
  components: useComponents
};

/**
 * Get table configuration by key
 * @param {string} tableKey - The table key
 * @returns {Object} Table configuration
 */
export function getTableConfig(tableKey) {
  const config = tableRegistry[tableKey];
  if (!config) {
    throw new Error(`Table configuration not found for key: ${tableKey}`);
  }
  return config;
}

/**
 * Get composable by table key
 * @param {string} tableKey - The table key
 * @returns {Function} Composable factory function
 */
export function getComposable(tableKey) {
  const composable = composableRegistry[tableKey];
  if (!composable) {
    throw new Error(`Composable not found for key: ${tableKey}`);
  }
  return composable;
}
