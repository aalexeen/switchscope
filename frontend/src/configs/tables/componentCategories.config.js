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
    // Category Type is determined by multiple boolean flags (housingCategory, connectivityCategory, etc.)
    // For now, we'll hide this column and show it in extended mode via boolean flags
    {
      key: 'housingCategory',
      label: 'Housing',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'connectivityCategory',
      label: 'Connectivity',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'supportCategory',
      label: 'Support',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'moduleCategory',
      label: 'Module',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
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
      key: 'actions',
      label: 'Actions',
      type: 'actions',
      visible: true,
      actions: ['view', 'edit', 'delete']
    }
  ]
};
