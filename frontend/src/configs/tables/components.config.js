/**
 * Table Configuration for Components
 *
 * This is for the main Components entity (actual physical components),
 * not to be confused with catalog entities (Component Types, Component Natures, etc.)
 */

export default {
  entityName: 'Component',
  entityNamePlural: 'Components',
  entityKey: 'component',
  entityKeyPlural: 'components',
  icon: 'pi-server',
  iconColor: 'text-green-600',

  routes: {
    list: '/components',
    view: '/components/:id',
    create: '/components/create',
    edit: '/components/edit/:id'
  },

  composable: 'useComponents',

  // Search configuration
  searchFields: ['name', 'manufacturer', 'model', 'serialNumber', 'description'],

  // Theme for UI
  theme: 'green',
  themeIntensity: '500',

  columns: [
    {
      key: 'name',
      label: 'Name',
      type: 'text',  // Components don't have iconClass/colorClass fields
      visible: true,
      sortable: true
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
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'componentTypeDisplayName',
      label: 'Component Type',
      type: 'text-badge',
      visible: true,
      badgeColor: 'bg-blue-100 text-blue-800',
      fallback: 'Unknown'
    },
    {
      key: 'componentStatusDisplayName',
      label: 'Status',
      type: 'text-badge',
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
      type: 'actions',  // Standard action icons like other tables
      visible: true,
      actions: ['view', 'edit', 'delete']
    }
  ]
};
