/**
 * Table Configuration for Access Points
 * Defines structure, columns, and behavior for access points table
 */

export default {
  // Entity metadata
  entityName: 'Access Point',
  entityNamePlural: 'Access Points',
  entityKey: 'accessPoint',
  entityKeyPlural: 'accessPoints',

  // Icon configuration (PrimeIcons)
  icon: 'pi-wifi',
  iconColor: 'text-purple-600',

  routes: {
    list: '/components/devices/access-points',
    view: '/components/devices/access-points/:id',
    create: '/components/devices/access-points/create',
    edit: '/components/devices/access-points/edit/:id'
  },

  // Composable reference
  composable: 'useAccessPoints',

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
    'ssids',
    'wirelessBands'
  ],

  // Theme configuration (Tailwind colors)
  theme: 'purple',
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
      key: 'ssids',
      label: 'SSIDs',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'wirelessBands',
      label: 'Wireless Bands',
      type: 'badge',
      visible: true,
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
      key: 'maxClients',
      label: 'Max Clients',
      type: 'text',
      visible: false,
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
