/**
 * Detail View Configuration for Component Status
 */

export default {
  tableKey: 'componentStatuses', // Key to look up in composableRegistry
  composable: 'useComponentStatuses',

  sections: {
    workflow: {
      title: 'Status Transitions',
      enabled: true
    },
    basicInfo: {
      fields: [
        { key: 'displayName', label: 'Display Name', fallback: '-' },
        { key: 'code', label: 'Code' },
        { key: 'lifecyclePhase', label: 'Lifecycle Phase', fallback: '-' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'available', label: 'Available', type: 'boolean' },
        { key: 'operational', label: 'Operational', type: 'boolean' },
        { key: 'canAcceptInstallations', label: 'Can Accept Installations', type: 'boolean' },
        { key: 'requiresAttention', label: 'Requires Attention', type: 'boolean' },
        { key: 'physicallyPresent', label: 'Physically Present', type: 'boolean' },
        { key: 'inInventory', label: 'In Inventory', type: 'boolean' },
        { key: 'inTransition', label: 'In Transition', type: 'boolean' },
        { key: 'canBeReserved', label: 'Can Be Reserved', type: 'boolean' },
        { key: 'sortOrder', label: 'Sort Order', fallback: '-' },
        { key: 'colorClass', label: 'Color Class', fallback: '-' },
        { key: 'iconClass', label: 'Icon Class', fallback: '-' }
      ]
    }
  }
};
