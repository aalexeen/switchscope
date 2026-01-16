/**
 * Table Configuration for Routers
 * Defines structure, columns, and behavior for routers table
 */

export default {
  // Entity metadata
  entityName: 'Router',
  entityNamePlural: 'Routers',
  entityKey: 'router',
  entityKeyPlural: 'routers',

  // Icon configuration (PrimeIcons)
  icon: 'pi-share-alt',
  iconColor: 'text-orange-600',

  routes: {
    list: '/components/devices/routers',
    view: '/components/devices/routers/:id',
    create: '/components/devices/routers/create',
    edit: '/components/devices/routers/edit/:id'
  },

  // Composable reference
  composable: 'useRouters',

  // Search configuration
  searchFields: [
    'name',
    'ipAddress',
    'hostname',
    'manufacturer',
    'model',
    'serialNumber',
    'description',
    'managementProtocol',
    'routingProtocols'
  ],

  // Theme configuration (Tailwind colors)
  theme: 'orange',
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
      key: 'ipAddress',
      label: 'IP Address',
      type: 'code',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'hostname',
      label: 'Hostname',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
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
      key: 'managementProtocol',
      label: 'Mgmt Protocol',
      type: 'badge',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'routingProtocols',
      label: 'Routing Protocols',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'wanInterfaceCount',
      label: 'WAN Ports',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: '0'
    },
    {
      key: 'portCount',
      label: 'LAN Ports',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: '0'
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
