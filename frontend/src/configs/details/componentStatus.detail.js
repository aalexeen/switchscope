/**
 * Detail View Configuration for Component Status
 */

export default {
  tableKey: 'componentStatuses',
  composable: 'useComponentStatuses',

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
          key: 'lifecyclePhase',
          label: 'Lifecycle Phase',
          fallback: '-',
          editType: 'input',
          validation: { maxLength: 64 }
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
          key: 'available',
          label: 'Available',
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
          key: 'canAcceptInstallations',
          label: 'Can Accept Installations',
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
          key: 'physicallyPresent',
          label: 'Physically Present',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'inInventory',
          label: 'In Inventory',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'inTransition',
          label: 'In Transition',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'canBeReserved',
          label: 'Can Be Reserved',
          type: 'boolean',
          editType: 'checkbox'
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
          placeholder: 'e.g., text-green-600'
        },
        {
          key: 'iconClass',
          label: 'Icon Class',
          fallback: '-',
          editType: 'icon-class',
          placeholder: 'e.g., pi-check-circle'
        }
      ]
    }
  }
};
