/**
 * Table Configuration for Component Natures
 *
 * This configuration defines the structure, columns, and behavior
 * for the Component Natures catalog table.
 */

export default {
  // Entity Metadata
  entityName: 'Component Nature',
  entityNamePlural: 'Component Natures',
  entityKey: 'nature',           // Singular key for props
  entityKeyPlural: 'natures',    // Plural key for filtered data prop

  // Visual Identity
  icon: 'pi-box',
  iconColor: 'text-blue-600',

  // Routes
  routes: {
    list: '/catalog/component-natures',
    view: '/catalog/component-natures/:id',
    create: '/catalog/component-natures/create',
    edit: '/catalog/component-natures/edit/:id'
  },

  // Composable reference (for documentation)
  composable: 'useComponentNatures',

  // Search configuration
  searchFields: ['name', 'code', 'displayName', 'description'],

  // Theme for UI
  theme: 'indigo',
  themeIntensity: '500',

  // Column Definitions
  // Each column defines how data should be displayed and what features are available
  columns: [
    {
      key: 'name',
      label: 'Name',
      type: 'icon-text',           // Display with icon and text
      visible: true,                // Show by default
      sortable: true,
      width: 'w-64'
    },
    {
      key: 'code',
      label: 'Code',
      type: 'code',                 // Monospace code style
      visible: true,
      sortable: true,
      width: 'w-32'
    },
    {
      key: 'displayName',
      label: 'Display Name',
      type: 'text',
      visible: true,
      sortable: true,
      fallback: '-'                 // Show '-' if null/undefined
    },
    {
      key: 'active',
      label: 'Status',
      type: 'status-badge',         // Active/Inactive badge
      visible: true,
      sortable: true,
      align: 'left'
    },
    {
      key: 'requiresManagement',
      label: 'Mgmt',
      headerTitle: 'Requires Management',
      type: 'boolean-icon',         // Check/times icon
      visible: false,               // Hidden by default - Extended mode only
      align: 'center'
    },
    {
      key: 'canHaveIpAddress',
      label: 'IP',
      headerTitle: 'Can Have IP Address',
      type: 'boolean-icon',
      visible: false,               // Hidden by default
      align: 'center'
    },
    {
      key: 'hasFirmware',
      label: 'FW',
      headerTitle: 'Has Firmware',
      type: 'boolean-icon',
      visible: false,               // Hidden by default
      align: 'center'
    },
    {
      key: 'processesNetworkTraffic',
      label: 'Traffic',
      headerTitle: 'Processes Network Traffic',
      type: 'boolean-icon',
      visible: false,               // Hidden by default
      align: 'center'
    },
    {
      key: 'supportsSnmpMonitoring',
      label: 'SNMP',
      headerTitle: 'Supports SNMP Monitoring',
      type: 'boolean-icon',
      visible: false,               // Hidden by default
      align: 'center'
    },
    {
      key: 'powerConsumptionCategory',
      label: 'Power',
      type: 'text-badge',           // Text in badge format
      visible: false,               // Hidden by default
      transform: 'capitalize',      // Text transformation
      fallback: 'none'
    },
    {
      key: 'description',
      label: 'Description',
      type: 'text-truncate',        // Truncated text with tooltip
      visible: true,
      maxWidth: 'max-w-xs'          // 20rem max width
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
      type: 'actions',              // Action buttons (view/edit/delete)
      visible: true,
      actions: ['view', 'edit', 'delete']
    }
  ]
};
