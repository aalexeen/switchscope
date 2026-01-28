/**
 * Detail View Configuration for Component Nature
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
  tableKey: 'componentNatures',
  composable: 'useComponentNatures',

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
          key: 'active',
          label: 'Active',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'requiresManagement',
          label: 'Requires Management',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'canHaveIpAddress',
          label: 'Can Have IP Address',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'hasFirmware',
          label: 'Has Firmware',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'processesNetworkTraffic',
          label: 'Processes Network Traffic',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'supportsSnmpMonitoring',
          label: 'Supports SNMP Monitoring',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'powerConsumptionCategory',
          label: 'Power Consumption Category',
          fallback: 'none',
          editType: 'input',
          validation: { maxLength: 64 }
        },
        {
          key: 'sortOrder',
          label: 'Sort Order',
          fallback: '0',
          editType: 'number',
          validation: { min: 0, max: 999 }
        },
        {
          key: 'colorClass',
          label: 'Color Class',
          fallback: '-',
          editType: 'color-class',
          placeholder: 'e.g., text-blue-600'
        },
        {
          key: 'iconClass',
          label: 'Icon Class',
          fallback: '-',
          editType: 'icon-class',
          placeholder: 'e.g., pi-folder'
        }
      ]
    }
  }
};
