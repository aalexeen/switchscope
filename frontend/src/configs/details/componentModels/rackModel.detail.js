/**
 * Detail View Configuration for Rack Model
 * Displays comprehensive specifications for server/network racks
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
    // PRIORITY 2: Rack Classification
    // ============================================
    rackClassification: {
      title: 'Rack Classification',
      priority: 2,
      collapsible: false,
      fields: [
        { key: 'rackCategory', label: 'Rack Category', fallback: '-' },
        { key: 'sizeCategory', label: 'Size Category', fallback: '-' },
        { key: 'compact', label: 'Compact', type: 'boolean' },
        { key: 'halfHeight', label: 'Half Height', type: 'boolean' },
        { key: 'fullHeight', label: 'Full Height', type: 'boolean' }
      ]
    },

    // ============================================
    // PRIORITY 3: Dimensions & Capacity
    // ============================================
    dimensionsCapacity: {
      title: 'Dimensions & Capacity',
      priority: 3,
      collapsible: false,
      fields: [
        { key: 'dimensionsDescription', label: 'Dimensions Summary', type: 'summary', fallback: '-' },
        { key: 'standardWidthInches', label: 'Standard Width (inches)', fallback: '19' },
        { key: 'standardDepthInches', label: 'Standard Depth (inches)', fallback: '-' },
        { key: 'typicalCapacityU', label: 'Typical Capacity (U)', fallback: '-' },
        { key: 'maxCapacityU', label: 'Max Capacity (U)', fallback: '-' },
        { key: 'dimensionsMm', label: 'Dimensions (W×D×H mm)', fallback: '-' },
        { key: 'weightKg', label: 'Weight (kg)', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 4: Rack Type Characteristics
    // ============================================
    typeCharacteristics: {
      title: 'Rack Type Characteristics',
      priority: 4,
      collapsible: true,
      fields: [
        { key: 'wallMountable', label: 'Wall Mountable', type: 'boolean' },
        { key: 'floorStanding', label: 'Floor Standing', type: 'boolean' },
        { key: 'enclosed', label: 'Enclosed', type: 'boolean' },
        { key: 'openFrame', label: 'Open Frame', type: 'boolean' },
        { key: 'outdoorRated', label: 'Outdoor Rated', type: 'boolean' }
      ]
    },

    // ============================================
    // PRIORITY 5: Physical Constraints
    // ============================================
    physicalConstraints: {
      title: 'Physical Constraints',
      priority: 5,
      collapsible: true,
      fields: [
        { key: 'maxWeightCapacityKg', label: 'Max Weight Capacity (kg)', fallback: '-' },
        { key: 'maxPowerCapacityWatts', label: 'Max Power Capacity (W)', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 6: Environmental Features
    // ============================================
    environmentalFeatures: {
      title: 'Environmental Features',
      priority: 6,
      collapsible: true,
      fields: [
        { key: 'featuresSummary', label: 'Features Summary', type: 'summary', fallback: '-' },
        { key: 'hasVentilation', label: 'Ventilation', type: 'boolean' },
        { key: 'hasCableManagement', label: 'Cable Management', type: 'boolean' },
        { key: 'hasPowerDistribution', label: 'Power Distribution', type: 'boolean' },
        { key: 'hasCoolingSystem', label: 'Cooling System', type: 'boolean' },
        { key: 'coolingType', label: 'Cooling Type', fallback: 'Passive' }
      ]
    },

    // ============================================
    // PRIORITY 7: Security Features
    // ============================================
    securityFeatures: {
      title: 'Security Features',
      priority: 7,
      collapsible: true,
      fields: [
        { key: 'hasLocks', label: 'Has Locks', type: 'boolean' },
        { key: 'doorType', label: 'Door Type', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 8: Installation Characteristics
    // ============================================
    installationCharacteristics: {
      title: 'Installation Characteristics',
      priority: 8,
      collapsible: true,
      fields: [
        { key: 'assemblyTimeHours', label: 'Assembly Time (hours)', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 9: Environmental Specifications
    // ============================================
    environmentalSpecifications: {
      title: 'Environmental Specifications',
      priority: 9,
      collapsible: true,
      fields: [
        { key: 'operatingTemperatureRange', label: 'Operating Temperature', type: 'summary', fallback: '-' },
        { key: 'operatingTemperatureMin', label: 'Min Operating Temp (°C)', fallback: '-' },
        { key: 'operatingTemperatureMax', label: 'Max Operating Temp (°C)', fallback: '-' },
        { key: 'humidityRange', label: 'Operating Humidity', type: 'summary', fallback: '-' },
        { key: 'operatingHumidityMin', label: 'Min Operating Humidity (%)', fallback: '-' },
        { key: 'operatingHumidityMax', label: 'Max Operating Humidity (%)', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 10: Product Information
    // ============================================
    productInformation: {
      title: 'Product Information',
      priority: 10,
      collapsible: true,
      fields: [
        { key: 'partNumber', label: 'Part Number', fallback: '-' },
        { key: 'sku', label: 'SKU', fallback: '-' },
        { key: 'description', label: 'Description', fallback: 'No description' }
      ]
    },

    // ============================================
    // PRIORITY 11: Lifecycle & Dates
    // ============================================
    lifecycleDates: {
      title: 'Lifecycle & Dates',
      priority: 11,
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
    // PRIORITY 12: Documentation & Support
    // ============================================
    documentationSupport: {
      title: 'Documentation & Support',
      priority: 12,
      collapsible: true,
      fields: [
        { key: 'datasheetUrl', label: 'Datasheet URL', type: 'url', fallback: '-' },
        { key: 'manualUrl', label: 'Manual URL', type: 'url', fallback: '-' },
        { key: 'supportUrl', label: 'Support URL', type: 'url', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 13: Certifications & Compliance
    // ============================================
    certificationsCompliance: {
      title: 'Certifications & Compliance',
      priority: 13,
      collapsible: true,
      fields: [
        { key: 'certifications', label: 'Certifications', fallback: '-' },
        { key: 'complianceStandards', label: 'Compliance Standards', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 14: Pricing
    // ============================================
    pricing: {
      title: 'Pricing',
      priority: 14,
      collapsible: true,
      fields: [
        { key: 'msrp', label: 'MSRP', type: 'currency', fallback: '-' },
        { key: 'listPrice', label: 'List Price', type: 'currency', fallback: '-' },
        { key: 'currencyCode', label: 'Currency', fallback: 'USD' }
      ]
    },

    // ============================================
    // PRIORITY 15: Internal Notes & Metadata
    // ============================================
    internalNotes: {
      title: 'Internal Notes & Metadata',
      priority: 15,
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
