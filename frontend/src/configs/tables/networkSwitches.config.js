/**
 * Table Configuration for Network Switches
 * Defines structure, columns, and behavior for network switches table
 */

export default {
  // Entity metadata
  entityName: 'Network Switch',
  entityNamePlural: 'Network Switches',
  entityKey: 'networkSwitch',
  entityKeyPlural: 'networkSwitches',

  // Icon configuration (PrimeIcons)
  icon: 'pi-sitemap',
  iconColor: 'text-cyan-600',

  routes: {
    list: '/components/devices/switches',
    view: '/components/devices/switches/:id',
    create: '/components/devices/switches/create',
    edit: '/components/devices/switches/edit/:id'
  },

  // Composable reference
  composable: 'useNetworkSwitches',

  // Search configuration
  searchFields: [
    'name',
    'ipAddress',
    'hostname',
    'manufacturer',
    'model',
    'serialNumber',
    'description',
    'managementProtocol'
  ],

  // Theme configuration (Tailwind colors)
  theme: 'cyan',
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
      label: 'Protocol',
      type: 'badge',
      visible: true,
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
