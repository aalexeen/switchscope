/**
 * Detail View Configuration for Access Point Model
 * Displays comprehensive specifications for wireless access points
 */

export default {
  tableKey: 'componentModels',
  composable: 'useComponentModels',

  sections: {
    // ============================================
    // PRIORITY 1: Overview (Always visible)
    // ============================================
    overview: {
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
    },

    // ============================================
    // PRIORITY 2: WiFi Specifications
    // ============================================
    wifiSpecifications: {
      title: 'WiFi Specifications',
      priority: 2,
      collapsible: false,
      fields: [
        { key: 'wifiStandard', label: 'WiFi Standard', fallback: '-' },
        { key: 'wifiGeneration', label: 'WiFi Generation', fallback: '-' },
        { key: 'wifi6', label: 'WiFi 6 (802.11ax)', type: 'boolean' },
        { key: 'maxClients', label: 'Max Clients', fallback: '-' },
        { key: 'maxThroughputMbps', label: 'Max Throughput (Mbps)', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 3: Frequency Band Support
    // ============================================
    bandSupport: {
      title: 'Frequency Band Support',
      priority: 3,
      collapsible: false,
      fields: [
        { key: 'bandSummary', label: 'Band Summary', type: 'summary', fallback: '-' },
        { key: 'dualBand', label: 'Dual Band', type: 'boolean' },
        { key: 'supports5Ghz', label: '5 GHz Support', type: 'boolean' },
        { key: 'supports6Ghz', label: '6 GHz Support (WiFi 6E)', type: 'boolean' }
      ]
    },

    // ============================================
    // PRIORITY 4: Coverage Specifications
    // ============================================
    coverageSpecifications: {
      title: 'Coverage Specifications',
      priority: 4,
      collapsible: true,
      fields: [
        { key: 'indoorCoverageSqm', label: 'Indoor Coverage (m²)', fallback: '-' },
        { key: 'outdoorCoverageSqm', label: 'Outdoor Coverage (m²)', fallback: '-' },
        { key: 'indoorUse', label: 'Indoor Use', type: 'boolean' },
        { key: 'outdoorUse', label: 'Outdoor Use', type: 'boolean' }
      ]
    },

    // ============================================
    // PRIORITY 5: Security Features
    // ============================================
    securityFeatures: {
      title: 'Security Features',
      priority: 5,
      collapsible: true,
      fields: [
        { key: 'supportsWpa3', label: 'WPA3 Support', type: 'boolean' },
        { key: 'supportsEnterpriseAuth', label: 'Enterprise Authentication (802.1X)', type: 'boolean' },
        { key: 'enterprise', label: 'Enterprise Grade', type: 'boolean' }
      ]
    },

    // ============================================
    // PRIORITY 6: Advanced WiFi Features
    // ============================================
    advancedFeatures: {
      title: 'Advanced WiFi Features',
      priority: 6,
      collapsible: true,
      fields: [
        { key: 'hasAdvancedFeatures', label: 'Has Advanced Features', type: 'boolean' },
        { key: 'supportsMesh', label: 'Mesh Support', type: 'boolean' },
        { key: 'supportsMuMimo', label: 'MU-MIMO Support', type: 'boolean' },
        { key: 'supportsBeamforming', label: 'Beamforming Support', type: 'boolean' }
      ]
    },

    // ============================================
    // PRIORITY 7: Management Capabilities
    // ============================================
    managementCapabilities: {
      title: 'Management Capabilities',
      priority: 7,
      collapsible: true,
      fields: [
        { key: 'managementSummary', label: 'Management Summary', type: 'summary', fallback: '-' },
        { key: 'controllerManaged', label: 'Controller Managed', type: 'boolean' },
        { key: 'cloudManaged', label: 'Cloud Managed', type: 'boolean' },
        { key: 'webManagement', label: 'Web Management', type: 'boolean' },
        { key: 'supportsSsh', label: 'SSH Support', type: 'boolean' },
        { key: 'snmpVersions', label: 'SNMP Versions', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 8: PoE Specifications
    // ============================================
    poeSpecifications: {
      title: 'PoE Specifications',
      priority: 8,
      collapsible: true,
      condition: (model) => model.poeRequired === true,
      fields: [
        { key: 'poeRequired', label: 'PoE Required', type: 'boolean' },
        { key: 'poeStandard', label: 'PoE Standard', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 9: Physical Specifications
    // ============================================
    physicalSpecifications: {
      title: 'Physical Specifications',
      priority: 9,
      collapsible: true,
      fields: [
        { key: 'formFactor', label: 'Form Factor', fallback: '-' },
        { key: 'dimensionsMm', label: 'Dimensions (W×D×H mm)', fallback: '-' },
        { key: 'weightKg', label: 'Weight (kg)', fallback: '-' },
        { key: 'weightG', label: 'Weight (g)', fallback: '-' },
        { key: 'ipRating', label: 'IP Rating', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 10: Power & Environmental
    // ============================================
    powerEnvironmental: {
      title: 'Power & Environmental',
      priority: 10,
      collapsible: true,
      fields: [
        { key: 'powerSummary', label: 'Power Summary', type: 'summary', fallback: '-' },
        { key: 'powerConsumptionWatts', label: 'Power Consumption (W)', fallback: '-' },
        { key: 'maxPowerConsumptionWatts', label: 'Max Power Consumption (W)', fallback: '-' },
        { key: 'powerInputType', label: 'Power Input Type', fallback: 'PoE' },
        { key: 'powerEfficiency', label: 'Power Efficiency (Mbps/W)', fallback: '-' },
        { key: 'operatingTemperatureRange', label: 'Operating Temperature', type: 'summary', fallback: '-' },
        { key: 'operatingTemperatureMin', label: 'Min Operating Temp (°C)', fallback: '-' },
        { key: 'operatingTemperatureMax', label: 'Max Operating Temp (°C)', fallback: '-' },
        { key: 'humidityRange', label: 'Operating Humidity', type: 'summary', fallback: '-' },
        { key: 'operatingHumidityMin', label: 'Min Operating Humidity (%)', fallback: '-' },
        { key: 'operatingHumidityMax', label: 'Max Operating Humidity (%)', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 10: Additional Specifications (deviceSpecs map)
    // ============================================
    additionalSpecifications: {
      title: 'Additional Specifications',
      priority: 10,
      collapsible: true,
      condition: (model) => model.deviceSpecs && Object.keys(model.deviceSpecs).length > 0,
      fields: [
        { key: 'deviceSpecs', label: 'Device Specifications', type: 'map', fallback: 'No additional specifications' }
      ]
    },

    // ============================================
    // PRIORITY 11: Product Information
    // ============================================
    productInformation: {
      title: 'Product Information',
      priority: 11,
      collapsible: true,
      fields: [
        { key: 'productFamily', label: 'Product Family', fallback: '-' },
        { key: 'series', label: 'Series', fallback: '-' },
        { key: 'partNumber', label: 'Part Number', fallback: '-' },
        { key: 'sku', label: 'SKU', fallback: '-' },
        { key: 'description', label: 'Description', fallback: 'No description' }
      ]
    },

    // ============================================
    // PRIORITY 12: Lifecycle & Dates
    // ============================================
    lifecycleDates: {
      title: 'Lifecycle & Dates',
      priority: 12,
      collapsible: true,
      fields: [
        { key: 'releaseDate', label: 'Release Date', type: 'date', fallback: '-' },
        { key: 'discontinueDate', label: 'Discontinue Date', type: 'date', fallback: '-' },
        { key: 'warrantyYears', label: 'Warranty (Years)', fallback: '-' },
        { key: 'expectedLifespanYears', label: 'Expected Lifespan (Years)', fallback: '-' },
        { key: 'maintenanceIntervalMonths', label: 'Maintenance Interval (Months)', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 13: Documentation & Support
    // ============================================
    documentationSupport: {
      title: 'Documentation & Support',
      priority: 13,
      collapsible: true,
      fields: [
        { key: 'datasheetUrl', label: 'Datasheet URL', type: 'url', fallback: '-' },
        { key: 'manualUrl', label: 'Manual URL', type: 'url', fallback: '-' },
        { key: 'supportUrl', label: 'Support URL', type: 'url', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 14: Certifications & Compliance
    // ============================================
    certificationsCompliance: {
      title: 'Certifications & Compliance',
      priority: 14,
      collapsible: true,
      fields: [
        { key: 'certifications', label: 'Certifications', fallback: '-' },
        { key: 'complianceStandards', label: 'Compliance Standards', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 15: Pricing
    // ============================================
    pricing: {
      title: 'Pricing',
      priority: 15,
      collapsible: true,
      fields: [
        { key: 'msrp', label: 'MSRP', type: 'currency', fallback: '-' },
        { key: 'listPrice', label: 'List Price', type: 'currency', fallback: '-' },
        { key: 'currencyCode', label: 'Currency', fallback: 'USD' }
      ]
    },

    // ============================================
    // PRIORITY 16: Internal Notes & Metadata
    // ============================================
    internalNotes: {
      title: 'Internal Notes & Metadata',
      priority: 16,
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
    }
  }
};
