/**
 * Detail View Configuration for Component
 */

export default {
  tableKey: 'components',
  composable: 'useComponents',

  sections: {
    basicInfo: {
      fields: [
        { key: 'name', label: 'Name', fallback: '-' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'manufacturer', label: 'Manufacturer', fallback: 'N/A' },
        { key: 'model', label: 'Model', fallback: 'N/A' },
        { key: 'serialNumber', label: 'Serial Number', fallback: 'N/A' },
        { key: 'partNumber', label: 'Part Number', fallback: 'N/A' },
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
