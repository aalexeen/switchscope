/**
 * Table Configuration for Component Categories
 */

export default {
  entityName: 'Component Category',
  entityNamePlural: 'Component Categories',
  entityKey: 'category',
  entityKeyPlural: 'categories',
  icon: 'pi-folder',
  iconColor: 'text-green-600',

  routes: {
    list: '/catalog/component-categories',
    view: '/catalog/component-categories/:id'
  },

  composable: 'useComponentCategories',

  searchFields: ['name', 'code', 'displayName', 'description'],
  theme: 'green',
  themeIntensity: '600',

  columns: [
    {
      key: 'name',
      label: 'Name',
      type: 'icon-text',
      visible: true,
      sortable: true
    },
    {
      key: 'code',
      label: 'Code',
      type: 'code',
      visible: true,
      sortable: true
    },
    {
      key: 'displayName',
      label: 'Display Name',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: '-'
    },
    {
      key: 'active',
      label: 'Status',
      type: 'status-badge',
      visible: true
    },
    {
      key: 'systemCategory',
      label: 'System',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'infrastructure',
      label: 'Infrastructure',
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
      key: 'colorClass',
      label: 'Color Class',
      type: 'text',
      visible: false,
      sortable: false
    },
    {
      key: 'iconClass',
      label: 'Icon Class',
      type: 'text',
      visible: false,
      sortable: false
    },
    {
      key: 'sortOrder',
      label: 'Sort Order',
      type: 'text',
      visible: false,
      sortable: true,
      align: 'center'
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
