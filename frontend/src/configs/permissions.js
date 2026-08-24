// @/configs/permissions.js

/**
 * Permission codes, in one place.
 *
 * The backend refuses by code: `<domain>.<resource>:<action>`, checked against the authorities the
 * server itself issued. The frontend only decides what to show, but it decides it with the same
 * strings, and a mistyped string here is silent - the button simply never appears and nothing
 * complains. That is the client-side version of the hole the permission model was built to close:
 * the annotations were there, the mechanism was not.
 *
 * Two things make a typo loud instead:
 *
 * - nothing writes a code as a literal. Resources are referenced as `RESOURCE.racks`, and a
 *   mistyped key is `undefined`, which the helpers below throw on - at module load, since routes
 *   and table configs build their codes while the app is starting;
 * - `tools/check-permission-codes.mjs` diffs this file against the backend's permission seed, the
 *   same CSV `generate_permission_seed.py --check` holds to the annotations. A code that is
 *   spelled consistently but wrongly is exactly what that catches.
 *
 * Keys are table keys (`meta.tableKey`, `tableRegistry`) so that one entity is one lookup wherever
 * the question comes up.
 */
export const RESOURCE = Object.freeze({
  // Catalogs
  componentNatures: 'catalog.component-nature',
  componentCategories: 'catalog.component-category',
  componentTypes: 'catalog.component-type',
  componentStatuses: 'catalog.component-status',
  componentModels: 'catalog.component-model',
  locationTypes: 'catalog.location-type',
  installationStatuses: 'catalog.installation-status',
  installableTypes: 'catalog.installable-type',

  // Components and their kinds
  components: 'component',
  devices: 'component.device',
  networkSwitches: 'component.network-switch',
  routers: 'component.router',
  accessPoints: 'component.access-point',
  cableRuns: 'component.cable-run',
  connectors: 'component.connector',
  patchPanels: 'component.patch-panel',
  racks: 'component.rack',

  // Everything else the API models
  installations: 'installation',
  locations: 'location',
  ports: 'port'
});

/**
 * Builds a code, refusing to build one out of a resource that does not exist.
 *
 * @param {string} resource - a value from {@link RESOURCE}
 * @param {string} action - read, create, update or delete
 * @returns {string} the permission code the backend checks
 */
export function permission(resource, action) {
  if (typeof resource !== 'string' || resource.length === 0) {
    throw new Error(
      `Unknown permission resource: ${resource}. Reference a RESOURCE constant, not a literal.`
    );
  }
  return `${resource}:${action}`;
}

/** Reading is what gates a route: reaching a page at all means listing what is on it. */
export const read = (resource) => permission(resource, 'read');

/** Updating is what gates the edit button, in the table row and on the detail page. */
export const update = (resource) => permission(resource, 'update');

/** Named `remove` because `delete` is a keyword; the action it names is `delete`. */
export const remove = (resource) => permission(resource, 'delete');
