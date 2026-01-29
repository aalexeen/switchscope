/**
 * Detail View Configuration for Component
 *
 * Field access levels (from backend @FieldAccess annotations):
 * - REQUIRED: Cannot be nullified by anyone (name, componentStatusId, componentTypeId)
 * - ADMIN_NULLABLE: Only admin can nullify (description, manufacturer, model, etc.)
 * - USER_WRITABLE: Any authenticated user can nullify (maintenance dates, componentNatureId)
 * - READ_ONLY: Computed/system fields, cannot be modified
 *
 * Note: This is the base Component config. Device-specific configs extend this.
 */

export default {
  tableKey: 'components',
  composable: 'useComponents',

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
          validation: { minLength: 2, maxLength: 128 }
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
          key: 'manufacturer',
          label: 'Manufacturer',
          fallback: 'N/A',
          editType: 'input',
          validation: { maxLength: 128 }
        },
        {
          key: 'model',
          label: 'Model',
          fallback: 'N/A',
          editType: 'input',
          validation: { maxLength: 128 }
        },
        {
          key: 'serialNumber',
          label: 'Serial Number',
          fallback: 'N/A',
          editType: 'input',
          validation: { maxLength: 128 }
        },
        {
          key: 'partNumber',
          label: 'Part Number',
          fallback: 'N/A',
          editType: 'input',
          validation: { maxLength: 128 }
        }
      ]
    },
    classification: {
      title: 'Classification',
      fields: [
        {
          key: 'componentTypeId',
          label: 'Component Type',
          editType: 'searchable-select',
          required: true,
          relation: {
            composable: 'useComponentTypes',
            dataKey: 'componentTypes',
            valueKey: 'id',
            labelKey: 'displayName',
            searchFields: ['name', 'code', 'displayName']
          }
        },
        {
          key: 'componentTypeCode',
          label: 'Type Code',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'componentStatusId',
          label: 'Status',
          editType: 'searchable-select',
          required: true,
          relation: {
            composable: 'useComponentStatuses',
            dataKey: 'componentStatuses',
            valueKey: 'id',
            labelKey: 'displayName',
            searchFields: ['name', 'code', 'displayName']
          }
        },
        {
          key: 'componentStatusCode',
          label: 'Status Code',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'componentNatureId',
          label: 'Component Nature',
          editType: 'searchable-select',
          relation: {
            composable: 'useComponentNatures',
            dataKey: 'componentNatures',
            valueKey: 'id',
            labelKey: 'displayName',
            searchFields: ['name', 'code', 'displayName']
          }
        },
        {
          key: 'componentNatureCode',
          label: 'Nature Code',
          fallback: '-',
          editType: 'readonly',
          editable: false
        }
      ]
    },
    installation: {
      title: 'Installation',
      fields: [
        {
          key: 'parentComponentId',
          label: 'Parent Component',
          editType: 'searchable-select',
          relation: {
            composable: 'useComponents',
            dataKey: 'components',
            valueKey: 'id',
            labelKey: 'name',
            searchFields: ['name', 'serialNumber', 'manufacturer']
          }
        },
        {
          key: 'parentComponentName',
          label: 'Parent Component Name',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'installationId',
          label: 'Installation ID',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'componentPath',
          label: 'Component Path',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'locationAddress',
          label: 'Location Address',
          fallback: 'Not installed',
          editType: 'readonly',
          editable: false
        }
      ]
    },
    maintenance: {
      title: 'Maintenance',
      fields: [
        {
          key: 'lastMaintenanceDate',
          label: 'Last Maintenance',
          fallback: '-',
          editType: 'date',
          format: 'date'
        },
        {
          key: 'nextMaintenanceDate',
          label: 'Next Maintenance',
          fallback: '-',
          editType: 'date',
          format: 'date'
        }
      ]
    },
    computed: {
      title: 'Computed Properties',
      fields: [
        {
          key: 'operational',
          label: 'Operational',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'installed',
          label: 'Installed',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        }
      ]
    }
  }
};
