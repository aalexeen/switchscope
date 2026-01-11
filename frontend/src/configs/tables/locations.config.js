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

  // Composable reference
  composable: 'useLocations',

  // Search configuration
  searchFields: [
    'name',
    'code',
    'locationTypeName',
    'address',
    'city',
    'country',
    'parentLocationName',
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
      key: 'code',
      label: 'Code',
      type: 'code',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'locationTypeName',
      label: 'Type',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
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
      key: 'address',
      label: 'Address',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'city',
      label: 'City',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'postalCode',
      label: 'Postal Code',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'country',
      label: 'Country',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'latitude',
      label: 'Latitude',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'longitude',
      label: 'Longitude',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
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
