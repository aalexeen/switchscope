/**
 * Table Configuration for Component Models
 */

export default {
  entityName: 'Component Model',
  entityNamePlural: 'Component Models',
  entityKey: 'model',
  entityKeyPlural: 'models',
  icon: 'pi-box',
  iconColor: 'text-indigo-600',

  routes: {
    list: '/catalog/component-models',
    view: '/catalog/component-models/:id'
  },

  composable: 'useComponentModels',

  searchFields: ['name', 'manufacturer', 'modelNumber', 'modelDesignation', 'partNumber', 'sku', 'description', 'componentType.code', 'componentType.displayName'],
  theme: 'indigo',
  themeIntensity: '500',

  columns: [
    {
      key: 'name',
      label: 'Name',
      type: 'text',
      visible: true
    },
    {
      key: 'manufacturer',
      label: 'Manufacturer',
      type: 'text',
      visible: true
    },
    {
      key: 'modelNumber',
      label: 'Model Number',
      type: 'code',
      visible: true
    },
    {
      key: 'componentType.displayName',
      label: 'Type',
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
      key: 'partNumber',
      label: 'Part Number',
      type: 'code',
      visible: false
    },
    {
      key: 'sku',
      label: 'SKU',
      type: 'code',
      visible: false
    },
    {
      key: 'discontinued',
      label: 'Discontinued',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'endOfLife',
      label: 'EOL',
      headerTitle: 'End of Life',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'verified',
      label: 'Verified',
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
