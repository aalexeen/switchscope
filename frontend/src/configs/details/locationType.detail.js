/**
 * Detail View Configuration for Location Type
 *
 * Field edit types:
 * - input: text input (default)
 * - textarea: multiline text
 * - checkbox/boolean: toggle switch
 * - number: numeric input
 * - select: simple dropdown with options array
 * - color-class: Tailwind color class picker
 * - icon-class: PrimeIcons class picker
 * - readonly: non-editable display
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
        {
          key: 'name',
          label: 'Name',
          fallback: '-',
          editType: 'input',
          required: true,
          validation: { maxLength: 128 }
        },
        {
          key: 'displayName',
          label: 'Display Name',
          fallback: '-',
          editType: 'input',
          required: true,
          validation: { maxLength: 128 }
        },
        {
          key: 'code',
          label: 'Code',
          editType: 'readonly',
          editable: false,
          required: true
        },
        {
          key: 'description',
          label: 'Description',
          fallback: 'No description',
          editType: 'textarea',
          rows: 3,
          validation: { maxLength: 1024 }
        },
        {
          key: 'active',
          label: 'Active',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'locationCategory',
          label: 'Location Category',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'hierarchyLevel',
          label: 'Hierarchy Level',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 100 }
        },
        {
          key: 'canHaveChildren',
          label: 'Can Have Children',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'canHoldEquipment',
          label: 'Can Hold Equipment',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'requiresAddress',
          label: 'Requires Address',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'physicalLocation',
          label: 'Physical Location',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'virtual',
          label: 'Virtual',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'rackLike',
          label: 'Rack Like',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'roomLike',
          label: 'Room Like',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'buildingLike',
          label: 'Building Like',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'maxChildrenCount',
          label: 'Max Children Count',
          fallback: '-',
          editType: 'number',
          validation: { min: 0 }
        },
        {
          key: 'maxEquipmentCount',
          label: 'Max Equipment Count',
          fallback: '-',
          editType: 'number',
          validation: { min: 0 }
        },
        {
          key: 'defaultRackUnits',
          label: 'Default Rack Units',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 100 }
        },
        {
          key: 'requiresAccessControl',
          label: 'Requires Access Control',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'requiresClimateControl',
          label: 'Requires Climate Control',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'requiresPowerManagement',
          label: 'Requires Power Management',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'requiresMonitoring',
          label: 'Requires Monitoring',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'topLevel',
          label: 'Top Level',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'middleLevel',
          label: 'Middle Level',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'bottomLevel',
          label: 'Bottom Level',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'specialType',
          label: 'Special Type',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'hasCapacityLimits',
          label: 'Has Capacity Limits',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'secureLocation',
          label: 'Secure Location',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'datacenterLike',
          label: 'Datacenter Like',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'colorCategory',
          label: 'Color Category',
          fallback: '-',
          editType: 'input',
          validation: { maxLength: 16 }
        },
        {
          key: 'iconClass',
          label: 'Icon Class',
          fallback: '-',
          editType: 'icon-class',
          placeholder: 'e.g., pi-building'
        },
        {
          key: 'mapSymbol',
          label: 'Map Symbol',
          fallback: '-',
          editType: 'input',
          validation: { maxLength: 16 }
        },
        {
          key: 'sortOrder',
          label: 'Sort Order',
          fallback: '0',
          editType: 'number',
          validation: { min: 0, max: 999 }
        }
      ]
    }
  }
};
