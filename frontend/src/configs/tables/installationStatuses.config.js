/**
 * Table Configuration for Installation Statuses
 */

export default {
  entityName: 'Installation Status',
  entityNamePlural: 'Installation Statuses',
  entityKey: 'status',
  entityKeyPlural: 'statuses',
  icon: 'pi-wrench',
  iconColor: 'text-amber-600',

  routes: {
    list: '/catalog/installation-statuses',
    view: '/catalog/installation-statuses/:id'
  },

  composable: 'useInstallationStatuses',

  searchFields: ['name', 'code', 'displayName', 'description', 'statusCategory', 'colorCategory'],
  theme: 'amber',
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
      key: 'statusCategory',
      label: 'Category',
      type: 'text-badge',
      visible: true,
      transform: 'capitalize'
    },
    {
      key: 'colorCategory',
      label: 'Color',
      type: 'text-badge',
      visible: false,
      transform: 'capitalize'
    },
    {
      key: 'physicallyPresent',
      label: 'Physically Present',
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
      key: 'requiresAttention',
      label: 'Requires Attention',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'finalStatus',
      label: 'Final',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'terminalStatus',
      label: 'Terminal',
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
