/**
 * Detail View Configuration for Installation
 */

export default {
  tableKey: 'installations',
  composable: 'useInstallations',

  sections: {
    basicInfo: {
      fields: [
        { key: 'locationName', label: 'Location', fallback: '-' },
        { key: 'locationFullPath', label: 'Location Full Path', fallback: '-' },
        { key: 'componentName', label: 'Housing Component', fallback: '-' },
        { key: 'componentId', label: 'Housing Component ID', fallback: '-' },
        { key: 'installedItemTypeDisplayName', label: 'Installable Type', fallback: '-' },
        { key: 'installedItemTypeCode', label: 'Installable Type Code', fallback: '-' },
        { key: 'installedItemId', label: 'Installed Item ID', fallback: '-' },
        { key: 'statusDisplayName', label: 'Status', fallback: '-' },
        { key: 'statusCode', label: 'Status Code', fallback: '-' },
        { key: 'statusColorCategory', label: 'Status Color', fallback: '-' },
        { key: 'rackPosition', label: 'Rack Position (U)', fallback: '-' },
        { key: 'rackUnitHeight', label: 'Rack Unit Height (U)', fallback: '-' },
        { key: 'positionDescription', label: 'Position Description', fallback: '-' },
        { key: 'installedAt', label: 'Installed At', format: 'date', fallback: '-' },
        { key: 'removedAt', label: 'Removed At', format: 'date', fallback: '-' },
        { key: 'lastStatusChange', label: 'Last Status Change', format: 'date', fallback: '-' },
        { key: 'installedBy', label: 'Installed By', fallback: '-' },
        { key: 'removedBy', label: 'Removed By', fallback: '-' },
        { key: 'statusChangedBy', label: 'Status Changed By', fallback: '-' },
        { key: 'installationNotes', label: 'Installation Notes', fallback: '-' },
        { key: 'cableManagement', label: 'Cable Management', fallback: '-' },
        { key: 'installationLocationPath', label: 'Installation Location Path', fallback: '-' },
        { key: 'fullLocationDescription', label: 'Full Location Description', fallback: '-' },
        { key: 'directLocationInstallation', label: 'Direct Location Install', type: 'boolean' },
        { key: 'componentHousedInstallation', label: 'Component Housed Install', type: 'boolean' },
        { key: 'currentlyInstalled', label: 'Currently Installed', type: 'boolean' },
        { key: 'operational', label: 'Operational', type: 'boolean' },
        { key: 'requiresAttention', label: 'Requires Attention', type: 'boolean' },
        { key: 'rackMounted', label: 'Rack Mounted', type: 'boolean' },
        { key: 'rackRequired', label: 'Rack Required', type: 'boolean' },
        { key: 'supportsHotSwap', label: 'Supports Hot Swap', type: 'boolean' },
        { key: 'requiresShutdown', label: 'Requires Shutdown', type: 'boolean' },
        { key: 'occupiedRackUnits', label: 'Occupied Rack Units', fallback: '-' },
        { key: 'occupiedRackPositions', label: 'Occupied Rack Positions', fallback: '-' },
        { key: 'installationPriority', label: 'Installation Priority', fallback: '-' },
        { key: 'estimatedInstallationTime', label: 'Estimated Installation Time', fallback: '-' },
        { key: 'allowsEquipmentOperation', label: 'Allows Equipment Operation', type: 'boolean' },
        { key: 'allowsMaintenance', label: 'Allows Maintenance', type: 'boolean' },
        { key: 'statusUrgencyLevel', label: 'Status Urgency Level', fallback: '-' }
      ]
    }
  }
};
