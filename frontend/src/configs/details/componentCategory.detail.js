/**
 * Detail View Configuration for Component Category
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
  tableKey: 'componentCategories', // Key to look up in composableRegistry
  composable: 'useComponentCategories',

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
          editType: 'readonly', // Code should not be editable after creation
          editable: false
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
          key: 'systemCategory',
          label: 'System Category',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'infrastructure',
          label: 'Infrastructure',
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
          placeholder: 'e.g., pi-folder'
        }
      ]
    }
    // relatedTables can be added later when needed
    // relatedTables: [
    //   {
    //     key: 'componentTypes',
    //     title: 'Component Types',
    //     tableConfig: componentTypesConfig,
    //     filterBy: 'categoryId'
    //   }
    // ]
  }
};
