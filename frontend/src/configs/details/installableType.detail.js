/**
 * Detail View Configuration for Installable Type
 */

export default {
  tableKey: 'installableTypes',
  composable: 'useInstallableTypes',

  sections: {
    basicInfo: {
      fields: [
        { key: 'displayName', label: 'Display Name', fallback: '-' },
        { key: 'code', label: 'Code' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'entityClass', label: 'Entity Class', fallback: '-' },
        { key: 'active', label: 'Active', type: 'boolean' },
        { key: 'requiresRackPosition', label: 'Requires Rack Position', type: 'boolean' },
        { key: 'hasStandardRackUnits', label: 'Has Standard Rack Units', type: 'boolean' },
        { key: 'supportsPowerManagement', label: 'Supports Power Management', type: 'boolean' },
        { key: 'requiresEnvironmentalControl', label: 'Requires Environmental Control', type: 'boolean' },
        { key: 'requiresShutdown', label: 'Requires Shutdown', type: 'boolean' },
        { key: 'hotSwappable', label: 'Hot Swappable', type: 'boolean' },
        {
          key: 'typicalInstallationTimeMinutes',
          label: 'Typical Installation Time (Minutes)',
          fallback: '-'
        },
        { key: 'estimatedInstallationTime', label: 'Estimated Installation Time', fallback: '-' },
        { key: 'defaultRackUnits', label: 'Default Rack Units', fallback: '-' },
        { key: 'maxWeightKg', label: 'Max Weight (kg)', fallback: '-' },
        { key: 'installationPriority', label: 'Installation Priority', fallback: '-' },
        { key: 'priorityLevel', label: 'Priority Level', fallback: '-' },
        { key: 'deviceType', label: 'Device Type', type: 'boolean' },
        { key: 'connectivityType', label: 'Connectivity Type', type: 'boolean' },
        { key: 'supportType', label: 'Support Type', type: 'boolean' },
        { key: 'requiresSpecialHandling', label: 'Requires Special Handling', type: 'boolean' },
        { key: 'highPriority', label: 'High Priority', type: 'boolean' },
        { key: 'lowPriority', label: 'Low Priority', type: 'boolean' },
        { key: 'sortOrder', label: 'Sort Order', fallback: '-' }
      ]
    }
  }
};
