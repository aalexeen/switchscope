/**
 * Table Configuration for Installable Types
 */

export default {
  entityName: 'Installable Type',
  entityNamePlural: 'Installable Types',
  entityKey: 'type',
  entityKeyPlural: 'types',
  icon: 'pi-cog',
  iconColor: 'text-indigo-600',

  routes: {
    list: '/catalog/installable-types',
    view: '/catalog/installable-types/:id'
  },

  composable: 'useInstallableTypes',

  searchFields: ['name', 'code', 'displayName', 'description', 'entityClass'],
  theme: 'indigo',
  themeIntensity: '600',

  columns: [
    {
      key: 'name',
      label: 'Name',
      type: 'icon-text',
      visible: true
    },
    {
      key: 'code',
      label: 'Code',
      type: 'code',
      visible: true
    },
    {
      key: 'displayName',
      label: 'Display Name',
      type: 'text',
      visible: true,
      fallback: '-'
    },
    {
      key: 'active',
      label: 'Status',
      type: 'status-badge',
      visible: true
    },
    {
      key: 'entityClass',
      label: 'Entity Class',
      type: 'code',
      visible: true
    },
    {
      key: 'deviceType',
      label: 'Device',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'connectivityType',
      label: 'Connectivity',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'supportType',
      label: 'Support',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'requiresRackPosition',
      label: 'Rack Position',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'hotSwappable',
      label: 'Hot Swappable',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'highPriority',
      label: 'High Priority',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'description',
      label: 'Description',
      type: 'text-truncate',
      visible: true,
      maxWidth: 'max-w-xs'
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
