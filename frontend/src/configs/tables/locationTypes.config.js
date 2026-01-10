/**
 * Table Configuration for Location Types
 */

export default {
  entityName: 'Location Type',
  entityNamePlural: 'Location Types',
  entityKey: 'type',
  entityKeyPlural: 'types',
  icon: 'pi-map-marker',
  iconColor: 'text-teal-600',

  routes: {
    list: '/catalog/location-types',
    view: '/catalog/location-types/:id'
  },

  composable: 'useLocationTypes',

  searchFields: ['name', 'code', 'displayName', 'description', 'locationCategory'],
  theme: 'teal',
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
      key: 'locationCategory',
      label: 'Category',
      type: 'text-badge',
      visible: true,
      transform: 'capitalize'
    },
    {
      key: 'topLevel',
      label: 'Top Level',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'middleLevel',
      label: 'Middle Level',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'bottomLevel',
      label: 'Bottom Level',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'physicalLocation',
      label: 'Physical',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'virtual',
      label: 'Virtual',
      type: 'boolean-icon',
      visible: false,
      align: 'center'
    },
    {
      key: 'secureLocation',
      label: 'Secure',
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
