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
    { key: 'modelDesignation', label: 'Model', type: 'heading', fallback: '-' },
    { key: 'name', label: 'Name', fallback: '-' },
    { key: 'manufacturer', label: 'Manufacturer', fallback: '-' },
    { key: 'modelNumber', label: 'Model Number', fallback: '-' },
    { key: 'componentTypeDisplayName', label: 'Component Type', fallback: '-' },
    { key: 'categoryName', label: 'Category', fallback: '-' },
    { key: 'lifecycleStatus', label: 'Lifecycle Status', type: 'badge', fallback: 'Unknown' },
    { key: 'active', label: 'Active', type: 'boolean' },
    { key: 'discontinued', label: 'Discontinued', type: 'boolean' },
    { key: 'endOfLife', label: 'End of Life', type: 'boolean' },
    { key: 'currentlySupported', label: 'Currently Supported', type: 'boolean' },
    { key: 'availableForPurchase', label: 'Available for Purchase', type: 'boolean' }
  ]
};

/**
 * Product Information section - Common to all component models
 */
export const productInformationSection = {
  title: 'Product Information',
  collapsible: true,
  fields: [
    { key: 'productFamily', label: 'Product Family', fallback: '-' },
    { key: 'series', label: 'Series', fallback: '-' },
    { key: 'partNumber', label: 'Part Number', fallback: '-' },
    { key: 'sku', label: 'SKU', fallback: '-' },
    { key: 'description', label: 'Description', fallback: 'No description' }
  ]
};

/**
 * Lifecycle & Dates section - Common to all component models
 */
export const lifecycleDatesSection = {
  title: 'Lifecycle & Dates',
  collapsible: true,
  fields: [
    { key: 'releaseDate', label: 'Release Date', type: 'date', fallback: '-' },
    { key: 'discontinueDate', label: 'Discontinue Date', type: 'date', fallback: '-' },
    { key: 'warrantyYears', label: 'Warranty (Years)', fallback: '-' },
    { key: 'expectedLifespanYears', label: 'Expected Lifespan (Years)', fallback: '-' },
    { key: 'maintenanceIntervalMonths', label: 'Maintenance Interval (Months)', fallback: '-' }
  ]
};

/**
 * Documentation & Support section - Common to all component models
 */
export const documentationSupportSection = {
  title: 'Documentation & Support',
  collapsible: true,
  fields: [
    { key: 'datasheetUrl', label: 'Datasheet URL', type: 'url', fallback: '-' },
    { key: 'manualUrl', label: 'Manual URL', type: 'url', fallback: '-' },
    { key: 'supportUrl', label: 'Support URL', type: 'url', fallback: '-' }
  ]
};

/**
 * Certifications & Compliance section - Common to all component models
 */
export const certificationsComplianceSection = {
  title: 'Certifications & Compliance',
  collapsible: true,
  fields: [
    { key: 'certifications', label: 'Certifications', fallback: '-' },
    { key: 'complianceStandards', label: 'Compliance Standards', fallback: '-' }
  ]
};

/**
 * Pricing section - Common to all component models
 */
export const pricingSection = {
  title: 'Pricing',
  collapsible: true,
  fields: [
    { key: 'msrp', label: 'MSRP', type: 'currency', fallback: '-' },
    { key: 'listPrice', label: 'List Price', type: 'currency', fallback: '-' },
    { key: 'currencyCode', label: 'Currency', fallback: 'USD' }
  ]
};

/**
 * Internal Notes & Metadata section - Common to all component models
 */
export const internalNotesSection = {
  title: 'Internal Notes & Metadata',
  collapsible: true,
  fields: [
    { key: 'verified', label: 'Verified', type: 'boolean' },
    { key: 'verificationDate', label: 'Verification Date', type: 'date', fallback: '-' },
    { key: 'notes', label: 'Internal Notes', type: 'textarea', fallback: 'No notes' },
    { key: 'createdAt', label: 'Created At', type: 'datetime', fallback: '-' },
    { key: 'updatedAt', label: 'Updated At', type: 'datetime', fallback: '-' },
    { key: 'createdDate', label: 'Created Date', type: 'datetime', fallback: '-' },
    { key: 'updatedDate', label: 'Updated Date', type: 'datetime', fallback: '-' }
  ]
};

/**
 * Physical Specifications section - Common to device models
 */
export const physicalSpecificationsSection = {
  title: 'Physical Specifications',
  collapsible: true,
  fields: [
    { key: 'formFactor', label: 'Form Factor', fallback: '-' },
    { key: 'rackUnits', label: 'Rack Units (U)', fallback: '-' },
    { key: 'dimensionsMm', label: 'Dimensions (W×D×H mm)', fallback: '-' },
    { key: 'weightKg', label: 'Weight (kg)', fallback: '-' }
  ]
};

/**
 * Power & Environmental section - Common to device models
 */
export const powerEnvironmentalSection = {
  title: 'Power & Environmental',
  collapsible: true,
  fields: [
    { key: 'powerSummary', label: 'Power Summary', type: 'summary', fallback: '-' },
    { key: 'powerConsumptionWatts', label: 'Power Consumption (W)', fallback: '-' },
    { key: 'maxPowerConsumptionWatts', label: 'Max Power Consumption (W)', fallback: '-' },
    { key: 'powerInputType', label: 'Power Input Type', fallback: 'AC' },
    { key: 'redundantPowerSupply', label: 'Redundant Power Supply', type: 'boolean' },
    { key: 'fanless', label: 'Fanless', type: 'boolean' },
    { key: 'noiseLevelDb', label: 'Noise Level (dB)', fallback: '-' },
    { key: 'operatingTemperatureRange', label: 'Operating Temperature', type: 'summary', fallback: '-' },
    { key: 'operatingTemperatureMin', label: 'Min Operating Temp (°C)', fallback: '-' },
    { key: 'operatingTemperatureMax', label: 'Max Operating Temp (°C)', fallback: '-' },
    { key: 'humidityRange', label: 'Operating Humidity', type: 'summary', fallback: '-' },
    { key: 'operatingHumidityMin', label: 'Min Operating Humidity (%)', fallback: '-' },
    { key: 'operatingHumidityMax', label: 'Max Operating Humidity (%)', fallback: '-' }
  ]
};

/**
 * Management & Monitoring section - Common to device models
 */
export const managementMonitoringSection = {
  title: 'Management & Monitoring',
  collapsible: true,
  fields: [
    { key: 'managementSummary', label: 'Management Summary', type: 'summary', fallback: '-' },
    { key: 'managementInterfaces', label: 'Management Interfaces', fallback: '-' },
    { key: 'webManagement', label: 'Web Management', type: 'boolean' },
    { key: 'supportsSsh', label: 'SSH Support', type: 'boolean' },
    { key: 'supportsTelnet', label: 'Telnet Support', type: 'boolean' },
    { key: 'snmpVersions', label: 'SNMP Versions', fallback: '-' }
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
