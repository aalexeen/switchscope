/**
 * Table Configuration for Cable Runs
 * Defines structure, columns, and behavior for cable runs table
 */

export default {
  // Entity metadata
  entityName: 'Cable Run',
  entityNamePlural: 'Cable Runs',
  entityKey: 'cableRun',
  entityKeyPlural: 'cableRuns',

  // Icon configuration (PrimeIcons)
  icon: 'pi-link',
  iconColor: 'text-teal-600',

  // Composable reference
  composable: 'useCableRuns',

  // Search configuration
  searchFields: [
    'name',
    'cableType',
    'cableCategory',
    'length',
    'color',
    'description'
  ],

  // Theme configuration (Tailwind colors)
  theme: 'teal',
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
      key: 'cableType',
      label: 'Cable Type',
      type: 'badge',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'cableCategory',
      label: 'Category',
      type: 'badge',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'length',
      label: 'Length (m)',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'color',
      label: 'Color',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'shielded',
      label: 'Shielded',
      type: 'boolean-icon',
      visible: false,
      sortable: true,
      fallback: false
    },
    {
      key: 'indoor',
      label: 'Indoor',
      type: 'boolean-icon',
      visible: false,
      sortable: true,
      fallback: true
    },
    {
      key: 'plenum',
      label: 'Plenum',
      type: 'boolean-icon',
      visible: false,
      sortable: true,
      fallback: false
    },
    {
      key: 'componentTypeDisplayName',
      label: 'Type',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'componentStatusDisplayName',
      label: 'Status',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'Unknown'
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
