/**
 * Table Configuration for Installations
 * Defines structure, columns, and behavior for installations table
 */

export default {
  // Entity metadata
  entityName: 'Installation',
  entityNamePlural: 'Installations',
  entityKey: 'installation',
  entityKeyPlural: 'installations',

  // Icon configuration (PrimeIcons)
  icon: 'pi-sitemap',
  iconColor: 'text-cyan-600',

  // Composable reference
  composable: 'useInstallations',

  // Search configuration
  searchFields: [
    'componentName',
    'locationName',
    'installableTypeName',
    'installationStatusName',
    'installedBy',
    'notes'
  ],

  // Theme configuration (Tailwind colors)
  theme: 'cyan',
  themeIntensity: '600',

  // Table columns configuration
  columns: [
    {
      key: 'componentName',
      label: 'Component',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'locationName',
      label: 'Location',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'installableTypeName',
      label: 'Installation Type',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'installationStatusName',
      label: 'Status',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'Unknown'
    },
    {
      key: 'installDate',
      label: 'Install Date',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'installedBy',
      label: 'Installed By',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'rackPosition',
      label: 'Rack Position (U)',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'heightUnits',
      label: 'Height (U)',
      type: 'text',
      visible: false,
      sortable: true,
      fallback: 'N/A'
    },
    {
      key: 'notes',
      label: 'Notes',
      type: 'text-truncate',
      visible: true,
      sortable: false,
      fallback: 'No notes'
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
