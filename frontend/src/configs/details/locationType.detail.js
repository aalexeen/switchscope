/**
 * Detail View Configuration for Location Type
 */

export default {
  tableKey: 'locationTypes',
  composable: 'useLocationTypes',

  sections: {
    containment: {
      title: 'Hierarchy Rules',
      enabled: true,
      childKey: 'allowedChildTypeIds',
      parentKey: 'allowedParentTypeIds',
      childLabel: 'Can contain these location types:',
      parentLabel: 'Can be child of these location types:'
    },
    basicInfo: {
      fields: [
        { key: 'displayName', label: 'Display Name', fallback: '-' },
        { key: 'code', label: 'Code' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'active', label: 'Active', type: 'boolean' },
        { key: 'locationCategory', label: 'Location Category', fallback: '-' },
        { key: 'hierarchyLevel', label: 'Hierarchy Level', fallback: '-' },
        { key: 'canHaveChildren', label: 'Can Have Children', type: 'boolean' },
        { key: 'canHoldEquipment', label: 'Can Hold Equipment', type: 'boolean' },
        { key: 'requiresAddress', label: 'Requires Address', type: 'boolean' },
        { key: 'physicalLocation', label: 'Physical Location', type: 'boolean' },
        { key: 'virtual', label: 'Virtual', type: 'boolean' },
        { key: 'rackLike', label: 'Rack Like', type: 'boolean' },
        { key: 'roomLike', label: 'Room Like', type: 'boolean' },
        { key: 'buildingLike', label: 'Building Like', type: 'boolean' },
        { key: 'maxChildrenCount', label: 'Max Children Count', fallback: '-' },
        { key: 'maxEquipmentCount', label: 'Max Equipment Count', fallback: '-' },
        { key: 'defaultRackUnits', label: 'Default Rack Units', fallback: '-' },
        { key: 'requiresAccessControl', label: 'Requires Access Control', type: 'boolean' },
        { key: 'requiresClimateControl', label: 'Requires Climate Control', type: 'boolean' },
        { key: 'requiresPowerManagement', label: 'Requires Power Management', type: 'boolean' },
        { key: 'requiresMonitoring', label: 'Requires Monitoring', type: 'boolean' },
        { key: 'topLevel', label: 'Top Level', type: 'boolean' },
        { key: 'middleLevel', label: 'Middle Level', type: 'boolean' },
        { key: 'bottomLevel', label: 'Bottom Level', type: 'boolean' },
        { key: 'specialType', label: 'Special Type', type: 'boolean' },
        { key: 'hasCapacityLimits', label: 'Has Capacity Limits', type: 'boolean' },
        { key: 'secureLocation', label: 'Secure Location', type: 'boolean' },
        { key: 'datacenterLike', label: 'Datacenter Like', type: 'boolean' },
        { key: 'colorCategory', label: 'Color Category', fallback: '-' },
        { key: 'iconClass', label: 'Icon Class', fallback: '-' },
        { key: 'mapSymbol', label: 'Map Symbol', fallback: '-' },
        { key: 'sortOrder', label: 'Sort Order', fallback: '-' }
      ]
    }
  }
};
