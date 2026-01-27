/**
 * Detail View Configuration for Installable Type
 *
 * Field edit types:
 * - input: text input (default)
 * - textarea: multiline text
 * - checkbox/boolean: toggle switch
 * - number: numeric input
 * - select: simple dropdown with options array
 * - searchable-select: dropdown with search for FK relations
 * - color-class: Tailwind color class picker
 * - icon-class: PrimeIcons class picker
 * - readonly: non-editable display
 */

export default {
  tableKey: 'installableTypes',
  composable: 'useInstallableTypes',

  sections: {
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
          validation: { maxLength: 512 }
        },
        {
          key: 'category',
          label: 'Category',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'active',
          label: 'Active',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'requiresRackPosition',
          label: 'Requires Rack Position',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'hasStandardRackUnits',
          label: 'Has Standard Rack Units',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'supportsPowerManagement',
          label: 'Supports Power Management',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'requiresEnvironmentalControl',
          label: 'Requires Environmental Control',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'requiresShutdown',
          label: 'Requires Shutdown',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'hotSwappable',
          label: 'Hot Swappable',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'typicalInstallationTimeMinutes',
          label: 'Typical Installation Time (Minutes)',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 1440 }
        },
        {
          key: 'estimatedInstallationTime',
          label: 'Estimated Installation Time',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'defaultRackUnits',
          label: 'Default Rack Units',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 10 }
        },
        {
          key: 'maxWeightKg',
          label: 'Max Weight (kg)',
          fallback: '-',
          editType: 'number',
          validation: { min: 0.1, max: 500, step: 0.1 }
        },
        {
          key: 'installationPriority',
          label: 'Installation Priority',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 10 }
        },
        {
          key: 'priorityLevel',
          label: 'Priority Level',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'sortOrder',
          label: 'Sort Order',
          fallback: '0',
          editType: 'number',
          validation: { min: 0, max: 999 }
        },
        {
          key: 'deviceType',
          label: 'Device Type',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'connectivityType',
          label: 'Connectivity Type',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'housingType',
          label: 'Housing Type',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'powerType',
          label: 'Power Type',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'implemented',
          label: 'Implemented',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'requiresSpecialHandling',
          label: 'Requires Special Handling',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'highPriority',
          label: 'High Priority',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'lowPriority',
          label: 'Low Priority',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        }
      ]
    }
  }
};
