/**
 * Base Field Definitions for Component Models
 * Shared field configurations across all component model types
 *
 * Usage: Import and merge with type-specific sections
 */

/**
 * Overview section - Common to all component models
 * Priority: 1 (Always visible)
 */
export const overviewSection = {
  title: 'Overview',
  priority: 1,
  collapsible: false,
  fields: [
    { key: 'modelDesignation', label: 'Model', type: 'heading', fallback: '-', editType: 'readonly', editable: false },
    { key: 'name', label: 'Name', fallback: '-', editType: 'input', required: true, validation: { maxLength: 128 } },
    { key: 'manufacturer', label: 'Manufacturer', fallback: '-', editType: 'input', required: true, validation: { maxLength: 128 } },
    { key: 'modelNumber', label: 'Model Number', fallback: '-', editType: 'input', required: true, validation: { maxLength: 128 } },
    {
      key: 'componentTypeId',
      label: 'Component Type',
      fallback: '-',
      viewKey: 'componentTypeDisplayName',
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
    { key: 'categoryName', label: 'Category', fallback: '-', editType: 'readonly', editable: false },
    { key: 'lifecycleStatus', label: 'Lifecycle Status', type: 'badge', fallback: 'Unknown', editType: 'readonly', editable: false },
    { key: 'active', label: 'Active', type: 'boolean', editType: 'checkbox' },
    { key: 'discontinued', label: 'Discontinued', type: 'boolean', editType: 'checkbox' },
    { key: 'endOfLife', label: 'End of Life', type: 'boolean', editType: 'checkbox' },
    { key: 'currentlySupported', label: 'Currently Supported', type: 'boolean', editType: 'readonly', editable: false },
    { key: 'availableForPurchase', label: 'Available for Purchase', type: 'boolean', editType: 'readonly', editable: false }
  ]
};

/**
 * Product Information section - Common to all component models
 */
export const productInformationSection = {
  title: 'Product Information',
  collapsible: true,
  fields: [
    { key: 'productFamily', label: 'Product Family', fallback: '-', editType: 'input', validation: { maxLength: 128 } },
    { key: 'series', label: 'Series', fallback: '-', editType: 'input', validation: { maxLength: 128 } },
    { key: 'partNumber', label: 'Part Number', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
    { key: 'sku', label: 'SKU', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
    { key: 'description', label: 'Description', fallback: 'No description', editType: 'textarea', validation: { maxLength: 2000 } }
  ]
};

/**
 * Lifecycle & Dates section - Common to all component models
 */
export const lifecycleDatesSection = {
  title: 'Lifecycle & Dates',
  collapsible: true,
  fields: [
    { key: 'releaseDate', label: 'Release Date', type: 'date', fallback: '-', editType: 'date' },
    { key: 'discontinueDate', label: 'Discontinue Date', type: 'date', fallback: '-', editType: 'date' },
    { key: 'warrantyYears', label: 'Warranty (Years)', fallback: '-', editType: 'number', validation: { min: 0, max: 100 } },
    { key: 'expectedLifespanYears', label: 'Expected Lifespan (Years)', fallback: '-', editType: 'number', validation: { min: 0, max: 50 } },
    { key: 'maintenanceIntervalMonths', label: 'Maintenance Interval (Months)', fallback: '-', editType: 'number', validation: { min: 0, max: 120 } }
  ]
};

/**
 * Documentation & Support section - Common to all component models
 */
export const documentationSupportSection = {
  title: 'Documentation & Support',
  collapsible: true,
  fields: [
    { key: 'datasheetUrl', label: 'Datasheet URL', type: 'url', fallback: '-', editType: 'input', validation: { maxLength: 512 } },
    { key: 'manualUrl', label: 'Manual URL', type: 'url', fallback: '-', editType: 'input', validation: { maxLength: 512 } },
    { key: 'supportUrl', label: 'Support URL', type: 'url', fallback: '-', editType: 'input', validation: { maxLength: 512 } }
  ]
};

/**
 * Certifications & Compliance section - Common to all component models
 */
export const certificationsComplianceSection = {
  title: 'Certifications & Compliance',
  collapsible: true,
  fields: [
    { key: 'certifications', label: 'Certifications', fallback: '-', editType: 'textarea', validation: { maxLength: 1000 } },
    { key: 'complianceStandards', label: 'Compliance Standards', fallback: '-', editType: 'textarea', validation: { maxLength: 1000 } }
  ]
};

/**
 * Pricing section - Common to all component models
 */
export const pricingSection = {
  title: 'Pricing',
  collapsible: true,
  fields: [
    { key: 'msrp', label: 'MSRP', type: 'currency', fallback: '-', editType: 'number', validation: { min: 0, max: 10000000 } },
    { key: 'listPrice', label: 'List Price', type: 'currency', fallback: '-', editType: 'number', validation: { min: 0, max: 10000000 } },
    { key: 'currencyCode', label: 'Currency', fallback: 'USD', editType: 'input', validation: { maxLength: 3 } }
  ]
};

/**
 * Internal Notes & Metadata section - Common to all component models
 */
export const internalNotesSection = {
  title: 'Internal Notes & Metadata',
  collapsible: true,
  fields: [
    { key: 'verified', label: 'Verified', type: 'boolean', editType: 'checkbox' },
    { key: 'verificationDate', label: 'Verification Date', type: 'date', fallback: '-', editType: 'date' },
    { key: 'notes', label: 'Internal Notes', type: 'textarea', fallback: 'No notes', editType: 'textarea', validation: { maxLength: 5000 } },
    { key: 'createdAt', label: 'Created At', type: 'datetime', fallback: '-', editType: 'readonly', editable: false },
    { key: 'updatedAt', label: 'Updated At', type: 'datetime', fallback: '-', editType: 'readonly', editable: false },
    { key: 'createdDate', label: 'Created Date', type: 'datetime', fallback: '-', editType: 'readonly', editable: false },
    { key: 'updatedDate', label: 'Updated Date', type: 'datetime', fallback: '-', editType: 'readonly', editable: false }
  ]
};

/**
 * Physical Specifications section - Common to device models
 */
export const physicalSpecificationsSection = {
  title: 'Physical Specifications',
  collapsible: true,
  fields: [
    { key: 'formFactor', label: 'Form Factor', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
    { key: 'rackUnits', label: 'Rack Units (U)', fallback: '-', editType: 'number', validation: { min: 1, max: 48 } },
    { key: 'dimensionsMm', label: 'Dimensions (W×D×H mm)', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
    { key: 'weightKg', label: 'Weight (kg)', fallback: '-', editType: 'number', validation: { min: 0.001, max: 1000 } }
  ]
};

/**
 * Power & Environmental section - Common to device models
 */
export const powerEnvironmentalSection = {
  title: 'Power & Environmental',
  collapsible: true,
  fields: [
    { key: 'powerSummary', label: 'Power Summary', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
    { key: 'powerConsumptionWatts', label: 'Power Consumption (W)', fallback: '-', editType: 'number', validation: { min: 0, max: 10000 } },
    { key: 'maxPowerConsumptionWatts', label: 'Max Power Consumption (W)', fallback: '-', editType: 'number', validation: { min: 0, max: 10000 } },
    { key: 'powerInputType', label: 'Power Input Type', fallback: 'AC', editType: 'input', validation: { maxLength: 32 } },
    { key: 'redundantPowerSupply', label: 'Redundant Power Supply', type: 'boolean', editType: 'checkbox' },
    { key: 'fanless', label: 'Fanless', type: 'boolean', editType: 'checkbox' },
    { key: 'noiseLevelDb', label: 'Noise Level (dB)', fallback: '-', editType: 'number', validation: { min: 0, max: 100 } },
    { key: 'operatingTemperatureRange', label: 'Operating Temperature', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
    { key: 'operatingTemperatureMin', label: 'Min Operating Temp (°C)', fallback: '-', editType: 'number', validation: { min: -40, max: 100 } },
    { key: 'operatingTemperatureMax', label: 'Max Operating Temp (°C)', fallback: '-', editType: 'number', validation: { min: -40, max: 100 } },
    { key: 'humidityRange', label: 'Operating Humidity', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
    { key: 'operatingHumidityMin', label: 'Min Operating Humidity (%)', fallback: '-', editType: 'number', validation: { min: 0, max: 100 } },
    { key: 'operatingHumidityMax', label: 'Max Operating Humidity (%)', fallback: '-', editType: 'number', validation: { min: 0, max: 100 } }
  ]
};

/**
 * Management & Monitoring section - Common to device models
 */
export const managementMonitoringSection = {
  title: 'Management & Monitoring',
  collapsible: true,
  fields: [
    { key: 'managementSummary', label: 'Management Summary', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
    { key: 'managementInterfaces', label: 'Management Interfaces', fallback: '-', editType: 'input', validation: { maxLength: 128 } },
    { key: 'webManagement', label: 'Web Management', type: 'boolean', editType: 'checkbox' },
    { key: 'supportsSsh', label: 'SSH Support', type: 'boolean', editType: 'checkbox' },
    { key: 'supportsTelnet', label: 'Telnet Support', type: 'boolean', editType: 'checkbox' },
    { key: 'snmpVersions', label: 'SNMP Versions', fallback: '-', editType: 'input', validation: { maxLength: 64 } }
  ]
};

/**
 * Helper function to create base sections with dynamic priorities
 * @param {number} startPriority - Starting priority number
 * @returns {Object} Base sections with assigned priorities
 */
export function createBaseSections(startPriority = 10) {
  return {
    productInformation: { ...productInformationSection, priority: startPriority },
    lifecycleDates: { ...lifecycleDatesSection, priority: startPriority + 1 },
    documentationSupport: { ...documentationSupportSection, priority: startPriority + 2 },
    certificationsCompliance: { ...certificationsComplianceSection, priority: startPriority + 3 },
    pricing: { ...pricingSection, priority: startPriority + 4 },
    internalNotes: { ...internalNotesSection, priority: startPriority + 5 }
  };
}

/**
 * Helper function to create device-specific base sections
 * @param {number} startPriority - Starting priority number
 * @returns {Object} Device base sections with assigned priorities
 */
export function createDeviceBaseSections(startPriority = 5) {
  return {
    physicalSpecifications: { ...physicalSpecificationsSection, priority: startPriority },
    powerEnvironmental: { ...powerEnvironmentalSection, priority: startPriority + 1 },
    managementMonitoring: { ...managementMonitoringSection, priority: startPriority + 2 }
  };
}

/**
 * Helper function to merge sections with proper priority ordering
 * @param {Object} specificSections - Type-specific sections
 * @param {Object} baseSections - Base sections to merge
 * @returns {Object} Merged sections
 */
export function mergeSections(specificSections, baseSections) {
  return {
    ...specificSections,
    ...baseSections
  };
}
