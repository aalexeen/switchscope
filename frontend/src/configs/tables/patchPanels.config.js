/**
 * Table Configuration for Patch Panels
 * Defines structure, columns, and behavior for patch panels table
 */

export default {
  // Entity metadata
  entityName: 'Patch Panel',
  entityNamePlural: 'Patch Panels',
  entityKey: 'patchPanel',
  entityKeyPlural: 'patchPanels',

  // Icon configuration (PrimeIcons)
  icon: 'pi-table',
  iconColor: 'text-emerald-600',

  routes: {
    list: '/components/connectivity/patch-panels',
    view: '/components/connectivity/patch-panels/:id',
    create: '/components/connectivity/patch-panels/create',
    edit: '/components/connectivity/patch-panels/edit/:id'
  },

  // Composable reference
  composable: 'usePatchPanels',

  // Search configuration
  searchFields: [
    'name',
    'manufacturer',
    'model',
    'serialNumber',
    'description'
  ],

  // Theme configuration (Tailwind colors)
  theme: 'emerald',
  themeIntensity: '600',

  // Table columns configuration
  columns: [
    {
      key: 'name',
      label: 'Name',
      type: 'text',
      visible: true,
      sortable: true
    },
    {
      key: 'manufacturer',
      label: 'Manufacturer',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'model',
      label: 'Model',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'serialNumber',
      label: 'Serial Number',
      type: 'code',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'portCount',
      label: 'Ports',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: '0'
    },
    {
      key: 'rackUnits',
      label: 'Rack Units',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'componentTypeDisplayName',
      label: 'Type',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'componentStatusDisplayName',
      label: 'Status',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'Unknown'
    },
    {
      key: 'description',
      label: 'Description',
      type: 'text-truncate',
      visible: false,
      sortable: false,
      fallback: 'No description'
    },
    {
      key: 'actions',
      label: 'Actions',
      type: 'actions',
      visible: true,
      sortable: false,
      actions: ['view', 'edit', 'delete']
    }
  ]
};
