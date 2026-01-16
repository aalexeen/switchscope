/**
 * Table Configuration for Locations
 * Defines structure, columns, and behavior for locations table
 */

export default {
  // Entity metadata
  entityName: 'Location',
  entityNamePlural: 'Locations',
  entityKey: 'location',
  entityKeyPlural: 'locations',

  // Icon configuration (PrimeIcons)
  icon: 'pi-map-marker',
  iconColor: 'text-orange-600',

  routes: {
    list: '/locations',
    view: '/locations/:id',
    create: '/locations/create',
    edit: '/locations/edit/:id'
  },

  // Composable reference
  composable: 'useLocations',

  // Search configuration
  searchFields: [
    'name',
    'typeDisplayName',
    'address',
    'parentLocationName',
    'fullPath',
    'description'
  ],

  // Theme configuration (Tailwind colors)
  theme: 'orange',
  themeIntensity: '600',

  // Table columns configuration
  columns: [
    {
      key: 'name',
      label: 'Name',
      type: 'text',
      visible: true,
      sortable: true
    },
    {
      key: 'typeDisplayName',
      label: 'Type',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'Unknown'
    },
    {
      key: 'parentLocationName',
      label: 'Parent Location',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'None'
    },
    {
      key: 'fullPath',
      label: 'Full Path',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'address',
      label: 'Address',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'floorNumber',
      label: 'Floor',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'roomNumber',
      label: 'Room',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'hasUps',
      label: 'UPS',
      type: 'boolean-icon',
      visible: false,
      sortable: true
    },
    {
      key: 'hasGenerator',
      label: 'Generator',
      type: 'boolean-icon',
      visible: false,
      sortable: true
    },
    {
      key: 'description',
      label: 'Description',
      type: 'text-truncate',
      visible: true,
      sortable: false,
      fallback: 'No description'
    },
    {
      key: 'actions',
      label: 'Actions',
      type: 'actions',
      visible: true,
      sortable: false,
      actions: ['view', 'edit', 'delete']
    }
  ]
};
