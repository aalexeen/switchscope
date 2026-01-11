/**
 * Table Configuration for Devices
 *
 * This is for network devices (switches, routers, access points, etc.)
 * Devices are active network equipment that can be managed.
 */

export default {
  entityName: 'Device',
  entityNamePlural: 'Devices',
  entityKey: 'device',
  entityKeyPlural: 'devices',
  icon: 'pi-server',
  iconColor: 'text-green-600',

  routes: {
    list: '/devices',
    view: '/devices/:id',
    create: '/devices/create',
    edit: '/devices/edit/:id'
  },

  composable: 'useDevices',

  // Search configuration
  searchFields: ['name', 'manufacturer', 'model', 'serialNumber', 'ipAddress', 'hostname', 'description'],

  // Theme for UI
  theme: 'green',
  themeIntensity: '600',

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
      key: 'componentTypeDisplayName',
      label: 'Device Type',
      type: 'badge',
      visible: true,
      badgeColor: 'bg-blue-100 text-blue-800',
      fallback: 'Unknown'
    },
    {
      key: 'componentStatusDisplayName',
      label: 'Status',
      type: 'badge',
      visible: true,
      badgeColor: 'bg-green-100 text-green-800',
      fallback: 'Unknown'
    },
    {
      key: 'componentTypeCode',
      label: 'Type Code',
      type: 'code',
      visible: false,
      fallback: '-'
    },
    {
      key: 'componentStatusCode',
      label: 'Status Code',
      type: 'code',
      visible: false,
      fallback: '-'
    },
    {
      key: 'description',
      label: 'Description',
      type: 'text-truncate',
      visible: true,
      maxWidth: 'max-w-xs',
      fallback: 'No description'
    },
    {
      key: 'actions',
      label: 'Actions',
      type: 'actions',
      visible: true,
      actions: ['view', 'edit', 'delete']
    }
  ]
};
