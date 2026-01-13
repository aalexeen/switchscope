/**
 * Detail View Configuration for Component Type
 */

export default {
  tableKey: 'componentTypes', // Key to look up in composableRegistry
  composable: 'useComponentTypes',

  sections: {
    containment: {
      title: 'Containment Rules',
      enabled: true
    },
    basicInfo: {
      fields: [
        { key: 'displayName', label: 'Display Name', fallback: '-' },
        { key: 'code', label: 'Code' },
        { key: 'categoryDisplayName', label: 'Category', fallback: '-' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'systemType', label: 'System Type', type: 'boolean' },
        { key: 'requiresRackSpace', label: 'Requires Rack Space', type: 'boolean' },
        { key: 'typicalRackUnits', label: 'Typical Rack Units', fallback: '-' },
        { key: 'canContainComponents', label: 'Can Contain Components', type: 'boolean' },
        { key: 'isInstallable', label: 'Is Installable', type: 'boolean' },
        { key: 'requiresManagement', label: 'Requires Management', type: 'boolean' },
        { key: 'supportsSnmp', label: 'Supports SNMP', type: 'boolean' },
        { key: 'hasFirmware', label: 'Has Firmware', type: 'boolean' },
        { key: 'canHaveIpAddress', label: 'Can Have IP Address', type: 'boolean' },
        { key: 'processesNetworkTraffic', label: 'Processes Network Traffic', type: 'boolean' },
        { key: 'requiresPower', label: 'Requires Power', type: 'boolean' },
        { key: 'typicalPowerConsumptionWatts', label: 'Typical Power Consumption (W)', fallback: '-' },
        { key: 'generatesHeat', label: 'Generates Heat', type: 'boolean' },
        { key: 'needsCooling', label: 'Needs Cooling', type: 'boolean' },
        { key: 'recommendedMaintenanceIntervalMonths', label: 'Maintenance Interval (Months)', fallback: '-' },
        { key: 'typicalLifespanYears', label: 'Typical Lifespan (Years)', fallback: '-' },
        { key: 'sortOrder', label: 'Sort Order', fallback: '-' },
        { key: 'colorClass', label: 'Color Class', fallback: '-' },
        { key: 'iconClass', label: 'Icon Class', fallback: '-' }
      ]
    }
  }
};
