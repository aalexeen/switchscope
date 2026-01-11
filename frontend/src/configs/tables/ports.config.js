/**
 * Table Configuration for Ports
 * Defines structure, columns, and behavior for ports table
 */

export default {
  // Entity metadata
  entityName: 'Port',
  entityNamePlural: 'Ports',
  entityKey: 'port',
  entityKeyPlural: 'ports',

  // Icon configuration (PrimeIcons)
  icon: 'pi-bolt',
  iconColor: 'text-cyan-600',

  // Composable reference
  composable: 'usePorts',

  // Search configuration
  searchFields: [
    'name',
    'portNumber',
    'portType',
    'speed',
    'deviceName',
    'macAddress',
    'status',
    'description'
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
      key: 'portNumber',
      label: 'Port Number',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'portType',
      label: 'Type',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'deviceName',
      label: 'Device',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'speed',
      label: 'Speed (Mbps)',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'enabled',
      label: 'Enabled',
      type: 'boolean-icon',
      visible: true,
      sortable: true,
      fallback: false
    },
    {
      key: 'status',
      label: 'Status',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'Unknown'
    },
    {
      key: 'macAddress',
      label: 'MAC Address',
      type: 'code',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'vlanId',
      label: 'VLAN ID',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'duplex',
      label: 'Duplex',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'mtu',
      label: 'MTU',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
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
