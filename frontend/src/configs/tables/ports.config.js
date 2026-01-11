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
    'portLabel',
    'deviceName',
    'connectorName',
    'status',
    'operationalStatus',
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
      label: 'Port #',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'portLabel',
      label: 'Label',
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
      fallback: 'Unknown'
    },
    {
      key: 'connectorName',
      label: 'Connector',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'None'
    },
    {
      key: 'status',
      label: 'Status',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'DOWN'
    },
    {
      key: 'operationalStatus',
      label: 'Op Status',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'UNKNOWN'
    },
    {
      key: 'speedMbps',
      label: 'Speed (Mbps)',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'duplexMode',
      label: 'Duplex',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'portMode',
      label: 'Mode',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'poeEnabled',
      label: 'PoE',
      type: 'boolean-icon',
      visible: false,
      sortable: true
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
