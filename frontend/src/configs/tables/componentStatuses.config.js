/**
 * Table Configuration for Component Statuses
 */

export default {
  entityName: 'Component Status',
  entityNamePlural: 'Component Statuses',
  entityKey: 'status',
  entityKeyPlural: 'statuses',
  icon: 'pi-circle',
  iconColor: 'text-purple-600',

  routes: {
    list: '/catalog/component-statuses',
    view: '/catalog/component-statuses/:id'
  },

  composable: 'useComponentStatuses',

  searchFields: ['name', 'code', 'displayName', 'description', 'lifecyclePhase'],
  theme: 'purple',
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
      key: 'lifecyclePhase',
      label: 'Lifecycle Phase',
      type: 'text-badge',
      visible: true,
      transform: 'capitalize'
    },
    {
      key: 'available',
      label: 'Available',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'operational',
      label: 'Operational',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'physicallyPresent',
      label: 'Physically Present',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'inInventory',
      label: 'In Inventory',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'requiresAttention',
      label: 'Requires Attention',
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
