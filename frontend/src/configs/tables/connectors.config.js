/**
 * Table Configuration for Connectors
 * Defines structure, columns, and behavior for connectors table
 */

export default {
  // Entity metadata
  entityName: 'Connector',
  entityNamePlural: 'Connectors',
  entityKey: 'connector',
  entityKeyPlural: 'connectors',

  // Icon configuration (PrimeIcons)
  icon: 'pi-circle',
  iconColor: 'text-indigo-600',

  routes: {
    list: '/components/connectivity/connectors',
    view: '/components/connectivity/connectors/:id',
    create: '/components/connectivity/connectors/create',
    edit: '/components/connectivity/connectors/edit/:id'
  },

  // Composable reference
  composable: 'useConnectors',

  // Search configuration
  searchFields: [
    'name',
    'connectorType',
    'gender',
    'mountingType',
    'description'
  ],

  // Theme configuration (Tailwind colors)
  theme: 'indigo',
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
      key: 'connectorType',
      label: 'Connector Type',
      type: 'badge',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'gender',
      label: 'Gender',
      type: 'badge',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'mountingType',
      label: 'Mounting',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'pinCount',
      label: 'Pins',
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
