/**
 * Detail View Configuration for Cable Run
 */

export default {
  tableKey: 'cableRuns',
  composable: 'useCableRuns',

  sections: {
    basicInfo: {
      fields: [
        { key: 'name', label: 'Name', fallback: '-' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'cableModelName', label: 'Cable Model', fallback: '-' },
        { key: 'cableLengthMeters', label: 'Cable Length (m)', fallback: '-' },
        { key: 'cableType', label: 'Cable Type', fallback: '-' },
        { key: 'cableCategory', label: 'Cable Category', fallback: '-' },
        { key: 'installationMethod', label: 'Installation Method', fallback: '-' },
        { key: 'cablePathway', label: 'Cable Pathway', fallback: '-' },
        { key: 'tested', label: 'Tested', type: 'boolean' },
        { key: 'testDate', label: 'Test Date', format: 'date', fallback: '-' },
        { key: 'testResult', label: 'Test Result', fallback: '-' },
        { key: 'testNotes', label: 'Test Notes', fallback: '-' },
        { key: 'cableId', label: 'Cable ID', fallback: '-' },
        { key: 'circuitId', label: 'Circuit ID', fallback: '-' },
        { key: 'installationNotes', label: 'Installation Notes', fallback: '-' },
        { key: 'startLocationName', label: 'Start Location', fallback: '-' },
        { key: 'startLocationId', label: 'Start Location ID', fallback: '-' },
        { key: 'endLocationName', label: 'End Location', fallback: '-' },
        { key: 'endLocationId', label: 'End Location ID', fallback: '-' },
        { key: 'locationIds', label: 'Location IDs', fallback: '-' },
        { key: 'connectorIds', label: 'Connector IDs', fallback: '-' },
        { key: 'passed', label: 'Passed', type: 'boolean' },
        { key: 'requiresTesting', label: 'Requires Testing', type: 'boolean' },
        { key: 'pointToPoint', label: 'Point-to-Point', type: 'boolean' },
        { key: 'multiPoint', label: 'Multi-Point', type: 'boolean' },
        { key: 'orderedLocationPath', label: 'Ordered Location Path', fallback: '-' },
        { key: 'connectorCount', label: 'Connector Count', fallback: '-' },
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
