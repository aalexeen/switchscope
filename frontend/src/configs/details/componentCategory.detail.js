/**
 * Detail View Configuration for Component Category
 */

export default {
  tableKey: 'componentCategories', // Key to look up in composableRegistry
  composable: 'useComponentCategories',

  sections: {
    basicInfo: {
      fields: [
        { key: 'displayName', label: 'Display Name', fallback: '-' },
        { key: 'code', label: 'Code' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'systemCategory', label: 'System Category', type: 'boolean' },
        { key: 'infrastructure', label: 'Infrastructure', type: 'boolean' },
        { key: 'sortOrder', label: 'Sort Order', fallback: '-' },
        { key: 'colorClass', label: 'Color Class', fallback: '-' },
        { key: 'iconClass', label: 'Icon Class', fallback: '-' }
      ]
    },
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
