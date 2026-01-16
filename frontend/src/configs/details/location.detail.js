/**
 * Detail View Configuration for Location
 */

export default {
  tableKey: 'locations',
  composable: 'useLocations',

  sections: {
    basicInfo: {
      fields: [
        { key: 'name', label: 'Name', fallback: '-' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'typeDisplayName', label: 'Location Type', fallback: '-' },
        { key: 'typeCode', label: 'Type Code', fallback: '-' },
        { key: 'address', label: 'Address', fallback: '-' },
        { key: 'parentLocationName', label: 'Parent Location', fallback: '-' },
        { key: 'parentLocationId', label: 'Parent Location ID', fallback: '-' },
        { key: 'childLocationIds', label: 'Child Location IDs', fallback: '-' },
        { key: 'capacityChildren', label: 'Children Capacity', fallback: '-' },
        { key: 'capacityEquipment', label: 'Equipment Capacity', fallback: '-' },
        { key: 'floorNumber', label: 'Floor Number', fallback: '-' },
        { key: 'roomNumber', label: 'Room Number', fallback: '-' },
        { key: 'coordinates', label: 'Coordinates', fallback: '-' },
        { key: 'accessRequirements', label: 'Access Requirements', fallback: '-' },
        { key: 'temperatureRange', label: 'Temperature Range', fallback: '-' },
        { key: 'humidityRange', label: 'Humidity Range', fallback: '-' },
        { key: 'powerCapacityWatts', label: 'Power Capacity (W)', fallback: '-' },
        { key: 'availableRackUnits', label: 'Available Rack Units', fallback: '-' },
        { key: 'hasUps', label: 'Has UPS', type: 'boolean' },
        { key: 'hasGenerator', label: 'Has Generator', type: 'boolean' },
        { key: 'fullPath', label: 'Full Path', fallback: '-' },
        { key: 'fullPathWithTypes', label: 'Full Path With Types', fallback: '-' },
        { key: 'level', label: 'Level', fallback: '-' },
        { key: 'hierarchyLevel', label: 'Hierarchy Level', fallback: '-' },
        { key: 'locationCategory', label: 'Location Category', fallback: '-' },
        { key: 'rootLocation', label: 'Root Location', type: 'boolean' },
        { key: 'hasChildren', label: 'Has Children', type: 'boolean' },
        { key: 'physical', label: 'Physical', type: 'boolean' },
        { key: 'virtual', label: 'Virtual', type: 'boolean' },
        { key: 'rackLike', label: 'Rack Like', type: 'boolean' },
        { key: 'roomLike', label: 'Room Like', type: 'boolean' },
        { key: 'buildingLike', label: 'Building Like', type: 'boolean' },
        { key: 'childrenCapacity', label: 'Children Capacity (Computed)', fallback: '-' },
        { key: 'equipmentCapacity', label: 'Equipment Capacity (Computed)', fallback: '-' },
        { key: 'availableChildrenSlots', label: 'Available Children Slots', fallback: '-' },
        { key: 'totalRackUnits', label: 'Total Rack Units', fallback: '-' },
        { key: 'hasBackupPower', label: 'Has Backup Power', type: 'boolean' },
        { key: 'childLocationCount', label: 'Child Location Count', fallback: '-' }
      ]
    }
  }
};
