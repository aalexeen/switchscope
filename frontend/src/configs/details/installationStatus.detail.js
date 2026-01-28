/**
 * Detail View Configuration for Installation Status
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
  tableKey: 'installationStatuses',
  composable: 'useInstallationStatuses',

  sections: {
    workflow: {
      title: 'Status Transitions',
      enabled: true
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
          validation: { maxLength: 512 }
        },
        {
          key: 'active',
          label: 'Active',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'statusCategory',
          label: 'Status Category',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'statusOrder',
          label: 'Status Order',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 100 }
        },
        {
          key: 'sortOrder',
          label: 'Sort Order',
          fallback: '0',
          editType: 'number',
          validation: { min: 0, max: 999 }
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
          placeholder: 'e.g., pi-check'
        },
        {
          key: 'physicallyPresent',
          label: 'Physically Present',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'operational',
          label: 'Operational',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'requiresAttention',
          label: 'Requires Attention',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'finalStatus',
          label: 'Final Status',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'errorStatus',
          label: 'Error Status',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'progressStatus',
          label: 'Progress Status',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'successStatus',
          label: 'Success Status',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'warningStatus',
          label: 'Warning Status',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'terminalStatus',
          label: 'Terminal Status',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'allowsStatusChange',
          label: 'Allows Status Change',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'autoTransitionMinutes',
          label: 'Auto Transition (Minutes)',
          fallback: '-',
          editType: 'number',
          validation: { min: 1 }
        },
        {
          key: 'hasAutoTransition',
          label: 'Has Auto Transition',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'urgencyLevel',
          label: 'Urgency Level',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'allowsEquipmentOperation',
          label: 'Allows Equipment Operation',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'allowsMaintenance',
          label: 'Allows Maintenance',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'requiresDocumentation',
          label: 'Requires Documentation',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'notifiesStakeholders',
          label: 'Notifies Stakeholders',
          type: 'boolean',
          editType: 'checkbox'
        }
      ]
    }
  }
};
