/**
 * Detail View Configuration for Connector
 */

export default {
  tableKey: 'connectors',
  composable: 'useConnectors',

  sections: {
    basicInfo: {
      fields: [
        { key: 'name', label: 'Name', fallback: '-' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'connectorModelName', label: 'Connector Model', fallback: '-' },
        { key: 'connectorType', label: 'Connector Type', fallback: '-' },
        { key: 'connectorPosition', label: 'Connector Position', fallback: '-' },
        { key: 'gender', label: 'Gender', fallback: '-' },
        { key: 'connectorLabel', label: 'Connector Label', fallback: '-' },
        { key: 'colorCode', label: 'Color Code', fallback: '-' },
        { key: 'terminationQuality', label: 'Termination Quality', fallback: '-' },
        { key: 'installationNotes', label: 'Installation Notes', fallback: '-' },
        { key: 'cableRunName', label: 'Cable Run', fallback: '-' },
        { key: 'cableRunId', label: 'Cable Run ID', fallback: '-' },
        { key: 'portName', label: 'Port', fallback: '-' },
        { key: 'portId', label: 'Port ID', fallback: '-' },
        { key: 'startConnector', label: 'Start Connector', type: 'boolean' },
        { key: 'endConnector', label: 'End Connector', type: 'boolean' },
        { key: 'intermediateConnector', label: 'Intermediate Connector', type: 'boolean' },
        { key: 'needsRework', label: 'Needs Rework', type: 'boolean' },
        { key: 'goodQuality', label: 'Good Quality', type: 'boolean' },
        { key: 'connectedToPort', label: 'Connected to Port', type: 'boolean' },
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
