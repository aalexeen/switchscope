/**
 * Detail View Configuration for Rack
 */

export default {
  tableKey: 'racks',
  composable: 'useRacks',

  sections: {
    basicInfo: {
      fields: [
        { key: 'name', label: 'Name', fallback: '-' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'manufacturer', label: 'Manufacturer', fallback: 'N/A' },
        { key: 'model', label: 'Model', fallback: 'N/A' },
        { key: 'serialNumber', label: 'Serial Number', fallback: 'N/A' },
        { key: 'partNumber', label: 'Part Number', fallback: 'N/A' },
        { key: 'rackTypeName', label: 'Rack Type', fallback: '-' },
        { key: 'rackUnitsTotal', label: 'Rack Units Total', fallback: '-' },
        { key: 'occupiedRackUnits', label: 'Occupied Rack Units', fallback: '-' },
        { key: 'availableRackUnits', label: 'Available Rack Units', fallback: '-' },
        { key: 'utilizationPercentage', label: 'Utilization (%)', fallback: '-' },
        { key: 'hasAvailableSpace', label: 'Has Available Space', type: 'boolean' },
        { key: 'dimensionsDescription', label: 'Dimensions', fallback: '-' },
        { key: 'actualWidthInches', label: 'Actual Width (in)', fallback: '-' },
        { key: 'actualDepthInches', label: 'Actual Depth (in)', fallback: '-' },
        { key: 'actualHeightInches', label: 'Actual Height (in)', fallback: '-' },
        { key: 'frontDoorType', label: 'Front Door Type', fallback: '-' },
        { key: 'rearDoorType', label: 'Rear Door Type', fallback: '-' },
        { key: 'hasSidePanels', label: 'Side Panels', type: 'boolean' },
        { key: 'hasFrontDoor', label: 'Front Door', type: 'boolean' },
        { key: 'hasRearDoor', label: 'Rear Door', type: 'boolean' },
        { key: 'powerOutlets', label: 'Power Outlets', fallback: '-' },
        { key: 'powerCapacityWatts', label: 'Power Capacity (W)', fallback: '-' },
        { key: 'hasPdu', label: 'Has PDU', type: 'boolean' },
        { key: 'pduType', label: 'PDU Type', fallback: '-' },
        { key: 'coolingType', label: 'Cooling Type', fallback: '-' },
        { key: 'hasFans', label: 'Has Fans', type: 'boolean' },
        { key: 'fanCount', label: 'Fan Count', fallback: '-' },
        { key: 'hasLocks', label: 'Has Locks', type: 'boolean' },
        { key: 'lockType', label: 'Lock Type', fallback: '-' },
        { key: 'hasTemperatureMonitoring', label: 'Temperature Monitoring', type: 'boolean' },
        { key: 'hasHumidityMonitoring', label: 'Humidity Monitoring', type: 'boolean' },
        { key: 'hasPowerMonitoring', label: 'Power Monitoring', type: 'boolean' },
        { key: 'environmentalMonitoring', label: 'Environmental Monitoring', type: 'boolean' },
        { key: 'cableManagementType', label: 'Cable Management Type', fallback: '-' },
        { key: 'hasCableTrays', label: 'Cable Trays', type: 'boolean' },
        { key: 'floorAnchored', label: 'Floor Anchored', type: 'boolean' },
        { key: 'floorAnchorType', label: 'Floor Anchor Type', fallback: '-' },
        { key: 'emptyWeightKg', label: 'Empty Weight (kg)', fallback: '-' },
        { key: 'maxLoadWeightKg', label: 'Max Load (kg)', fallback: '-' },
        { key: 'wallMountable', label: 'Wall Mountable', type: 'boolean' },
        { key: 'outdoorRated', label: 'Outdoor Rated', type: 'boolean' },
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
