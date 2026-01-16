/**
 * Detail View Configuration for Patch Panel
 */

export default {
  tableKey: 'patchPanels',
  composable: 'usePatchPanels',

  sections: {
    basicInfo: {
      fields: [
        { key: 'name', label: 'Name', fallback: '-' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'patchPanelModelName', label: 'Patch Panel Model', fallback: '-' },
        { key: 'portCount', label: 'Port Count', fallback: '-' },
        { key: 'panelType', label: 'Panel Type', fallback: '-' },
        { key: 'connectorType', label: 'Connector Type', fallback: '-' },
        { key: 'rackUnits', label: 'Rack Units', fallback: '-' },
        { key: 'rackPosition', label: 'Rack Position', fallback: '-' },
        { key: 'panelLabel', label: 'Panel Label', fallback: '-' },
        { key: 'portLabelingScheme', label: 'Port Labeling Scheme', fallback: '-' },
        { key: 'mountingType', label: 'Mounting Type', fallback: '-' },
        { key: 'terminationType', label: 'Termination Type', fallback: '-' },
        { key: 'categoryRating', label: 'Category Rating', fallback: '-' },
        { key: 'shielded', label: 'Shielded', type: 'boolean' },
        { key: 'cableManagement', label: 'Cable Management', type: 'boolean' },
        { key: 'cableManagementType', label: 'Cable Management Type', fallback: '-' },
        { key: 'installationNotes', label: 'Installation Notes', fallback: '-' },
        { key: 'cableRunIds', label: 'Cable Run IDs', fallback: '-' },
        { key: 'fiberPanel', label: 'Fiber Panel', type: 'boolean' },
        { key: 'copperPanel', label: 'Copper Panel', type: 'boolean' },
        { key: 'highDensity', label: 'High Density', type: 'boolean' },
        { key: 'portDensityInfo', label: 'Port Density Info', fallback: '-' },
        { key: 'cableRunCount', label: 'Cable Run Count', fallback: '-' },
        { key: 'componentTypeDisplayName', label: 'Component Type', fallback: '-' },
        { key: 'componentTypeCode', label: 'Type Code', fallback: '-' },
        { key: 'componentStatusDisplayName', label: 'Status', fallback: '-' },
        { key: 'componentStatusCode', label: 'Status Code', fallback: '-' },
        { key: 'componentNatureCode', label: 'Nature Code', fallback: '-' },
        { key: 'operational', label: 'Operational', type: 'boolean' },
        { key: 'installed', label: 'Installed', type: 'boolean' },
        { key: 'componentPath', label: 'Component Path', fallback: '-' },
        { key: 'locationAddress', label: 'Location Address', fallback: '-' },
        { key: 'installationId', label: 'Installation ID', fallback: '-' },
        { key: 'parentComponentName', label: 'Parent Component', fallback: '-' },
        { key: 'parentComponentId', label: 'Parent Component ID', fallback: '-' },
        { key: 'lastMaintenanceDate', label: 'Last Maintenance', format: 'date', fallback: '-' },
        { key: 'nextMaintenanceDate', label: 'Next Maintenance', format: 'date', fallback: '-' }
      ]
    }
  }
};
