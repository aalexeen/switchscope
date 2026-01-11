/**
 * Table Configuration for Racks
 * Defines structure, columns, and behavior for racks table
 */

export default {
  // Entity metadata
  entityName: 'Rack',
  entityNamePlural: 'Racks',
  entityKey: 'rack',
  entityKeyPlural: 'racks',

  // Icon configuration (PrimeIcons)
  icon: 'pi-box',
  iconColor: 'text-slate-600',

  // Composable reference
  composable: 'useRacks',

  // Search configuration
  searchFields: [
    'name',
    'manufacturer',
    'model',
    'serialNumber',
    'description'
  ],

  // Theme configuration (Tailwind colors)
  theme: 'slate',
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
      key: 'heightUnits',
      label: 'Height (U)',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'width',
      label: 'Width (mm)',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'depth',
      label: 'Depth (mm)',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'maxLoadWeight',
      label: 'Max Load (kg)',
      type: 'text',
      visible: false,
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
      visible: true,
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
