/**
 * Table Configuration for Component Types
 */

export default {
  entityName: 'Component Type',
  entityNamePlural: 'Component Types',
  entityKey: 'type',
  entityKeyPlural: 'types',
  icon: 'pi-tag',
  iconColor: 'text-blue-600',

  routes: {
    list: '/catalog/component-types',
    view: '/catalog/component-types/:id'
  },

  composable: 'useComponentTypes',

  searchFields: ['name', 'code', 'displayName', 'description', 'categoryCode', 'categoryDisplayName'],
  theme: 'blue',
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
      key: 'categoryDisplayName',
      label: 'Category',
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
    // Type Category is shown via individual boolean flags
    {
      key: 'systemType',
      label: 'System',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'housingComponent',
      label: 'Housing',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'connectivityComponent',
      label: 'Connectivity',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'moduleComponent',
      label: 'Module',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'networkingEquipment',
      label: 'Networking',
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
