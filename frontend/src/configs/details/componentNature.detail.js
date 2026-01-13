/**
 * Detail View Configuration for Component Nature
 */

export default {
  tableKey: 'componentNatures', // Key to look up in composableRegistry
  composable: 'useComponentNatures',

  sections: {
    basicInfo: {
      fields: [
        { key: 'displayName', label: 'Display Name', fallback: '-' },
        { key: 'code', label: 'Code' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'requiresManagement', label: 'Requires Management', type: 'boolean' },
        { key: 'canHaveIpAddress', label: 'Can Have IP Address', type: 'boolean' },
        { key: 'hasFirmware', label: 'Has Firmware', type: 'boolean' },
        { key: 'processesNetworkTraffic', label: 'Processes Network Traffic', type: 'boolean' },
        { key: 'supportsSnmpMonitoring', label: 'Supports SNMP Monitoring', type: 'boolean' },
        { key: 'powerConsumptionCategory', label: 'Power Consumption Category', fallback: 'none' },
        { key: 'sortOrder', label: 'Sort Order', fallback: '-' },
        { key: 'colorClass', label: 'Color Class', fallback: '-' },
        { key: 'iconClass', label: 'Icon Class', fallback: '-' }
      ]
    }
  }
};
