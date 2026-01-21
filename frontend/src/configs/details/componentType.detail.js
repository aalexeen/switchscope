/**
 * Detail View Configuration for Component Type
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
  tableKey: 'componentTypes', // Key to look up in composableRegistry
  composable: 'useComponentTypes',

  sections: {
    basicInfo: {
      title: 'Basic Information',
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
          editType: 'readonly', // Code should not be editable after creation
          editable: false
        },
        {
          key: 'categoryId',
          label: 'Category',
          editType: 'searchable-select',
          required: true,
          relation: {
            composable: 'useComponentCategories',
            dataKey: 'componentCategories',
            valueKey: 'id',
            labelKey: 'displayName',
            searchFields: ['name', 'code', 'displayName']
          }
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
          key: 'systemType',
          label: 'System Type',
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
          placeholder: 'e.g., text-blue-600'
        },
        {
          key: 'iconClass',
          label: 'Icon Class',
          fallback: '-',
          editType: 'icon-class',
          placeholder: 'e.g., pi-server'
        }
      ]
    },
    physicalProperties: {
      title: 'Physical Properties',
      fields: [
        {
          key: 'requiresRackSpace',
          label: 'Requires Rack Space',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'typicalRackUnits',
          label: 'Typical Rack Units',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 48 }
        },
        {
          key: 'canContainComponents',
          label: 'Can Contain Components',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'installable',
          label: 'Installable',
          type: 'boolean',
          editType: 'checkbox'
        }
      ]
    },
    technicalProperties: {
      title: 'Technical Properties',
      fields: [
        {
          key: 'requiresManagement',
          label: 'Requires Management',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'supportsSnmp',
          label: 'Supports SNMP',
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
          key: 'canHaveIpAddress',
          label: 'Can Have IP Address',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'processesNetworkTraffic',
          label: 'Processes Network Traffic',
          type: 'boolean',
          editType: 'checkbox'
        }
      ]
    },
    powerAndCooling: {
      title: 'Power & Cooling',
      fields: [
        {
          key: 'requiresPower',
          label: 'Requires Power',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'typicalPowerConsumptionWatts',
          label: 'Typical Power Consumption (W)',
          fallback: '-',
          editType: 'number',
          validation: { min: 0, max: 10000 }
        },
        {
          key: 'generatesHeat',
          label: 'Generates Heat',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'needsCooling',
          label: 'Needs Cooling',
          type: 'boolean',
          editType: 'checkbox'
        }
      ]
    },
    maintenance: {
      title: 'Maintenance',
      fields: [
        {
          key: 'recommendedMaintenanceIntervalMonths',
          label: 'Maintenance Interval (Months)',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 120 }
        },
        {
          key: 'typicalLifespanYears',
          label: 'Typical Lifespan (Years)',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 50 }
        }
      ]
    },
    containment: {
      title: 'Containment Rules',
      enabled: true,
      fields: [
        {
          key: 'allowedChildTypeCodes',
          label: 'Allowed Child Type Codes',
          fallback: 'None',
          editType: 'readonly', // Complex field - view only for now
          editable: false
        },
        {
          key: 'allowedChildCategoryCodes',
          label: 'Allowed Child Category Codes',
          fallback: 'None',
          editType: 'readonly', // Complex field - view only for now
          editable: false
        }
      ]
    },
    computed: {
      title: 'Computed Properties',
      fields: [
        {
          key: 'housingComponent',
          label: 'Housing Component',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'connectivityComponent',
          label: 'Connectivity Component',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'supportComponent',
          label: 'Support Component',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'moduleComponent',
          label: 'Module Component',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'networkingEquipment',
          label: 'Networking Equipment',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'powerConsumptionCategory',
          label: 'Power Consumption Category',
          fallback: '-',
          editType: 'readonly',
          editable: false
        }
      ]
    }
  }
};
