/**
 * Detail View Configuration for Rack Model
 * Displays comprehensive specifications for server/network racks
 */

import { overviewSection } from './_baseFields';

export default {
  tableKey: 'componentModels',
  composable: 'useComponentModels',

  sections: {
    // ============================================
    // PRIORITY 1: Overview (Always visible)
    // ============================================
    overview: { ...overviewSection },

    // ============================================
    // PRIORITY 2: Rack Classification
    // ============================================
    rackClassification: {
      title: 'Rack Classification',
      priority: 2,
      collapsible: false,
      description: 'Computed from mounting, enclosure, and capacity fields.',
      fields: [
        { key: 'rackCategory', label: 'Rack Category', fallback: '-', editType: 'readonly', editable: false },
        { key: 'sizeCategory', label: 'Size Category', fallback: '-', editType: 'readonly', editable: false },
        { key: 'compact', label: 'Compact', type: 'boolean', editType: 'readonly', editable: false },
        { key: 'halfHeight', label: 'Half Height', type: 'boolean', editType: 'readonly', editable: false },
        { key: 'fullHeight', label: 'Full Height', type: 'boolean', editType: 'readonly', editable: false }
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
        { key: 'dimensionsDescription', label: 'Dimensions Summary', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'standardWidthInches', label: 'Standard Width (inches)', fallback: '19', editType: 'number', validation: { min: 10, max: 30 } },
        { key: 'standardDepthInches', label: 'Standard Depth (inches)', fallback: '-', editType: 'number', validation: { min: 20, max: 50 } },
        { key: 'typicalCapacityU', label: 'Typical Capacity (U)', fallback: '-', editType: 'number', validation: { min: 1, max: 100 } },
        { key: 'maxCapacityU', label: 'Max Capacity (U)', fallback: '-', editType: 'number', validation: { min: 1, max: 100 } },
        { key: 'dimensionsMm', label: 'Dimensions (W×D×H mm)', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'weightKg', label: 'Weight (kg)', fallback: '-', editType: 'number', validation: { min: 0.001, max: 1000 } }
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
        { key: 'wallMountable', label: 'Wall Mountable', type: 'boolean', editType: 'checkbox' },
        { key: 'floorStanding', label: 'Floor Standing', type: 'boolean', editType: 'checkbox' },
        { key: 'enclosed', label: 'Enclosed', type: 'boolean', editType: 'checkbox' },
        { key: 'openFrame', label: 'Open Frame', type: 'boolean', editType: 'checkbox' },
        { key: 'outdoorRated', label: 'Outdoor Rated', type: 'boolean', editType: 'checkbox' }
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
        { key: 'maxWeightCapacityKg', label: 'Max Weight Capacity (kg)', fallback: '-', editType: 'number', validation: { min: 50, max: 2000 } },
        { key: 'maxPowerCapacityWatts', label: 'Max Power Capacity (W)', fallback: '-', editType: 'number', validation: { min: 100, max: 50000 } }
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
        { key: 'featuresSummary', label: 'Features Summary', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'hasVentilation', label: 'Ventilation', type: 'boolean', editType: 'checkbox' },
        { key: 'hasCableManagement', label: 'Cable Management', type: 'boolean', editType: 'checkbox' },
        { key: 'hasPowerDistribution', label: 'Power Distribution', type: 'boolean', editType: 'checkbox' },
        { key: 'hasCoolingSystem', label: 'Cooling System', type: 'boolean', editType: 'checkbox' },
        { key: 'coolingType', label: 'Cooling Type', fallback: 'Passive', editType: 'input', validation: { maxLength: 32 } }
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
        { key: 'hasLocks', label: 'Has Locks', type: 'boolean', editType: 'checkbox' },
        { key: 'doorType', label: 'Door Type', fallback: '-', editType: 'input', validation: { maxLength: 32 } }
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
        { key: 'assemblyTimeHours', label: 'Assembly Time (hours)', fallback: '-', editType: 'number', validation: { min: 1, max: 24 } }
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
        { key: 'operatingTemperatureRange', label: 'Operating Temperature', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'operatingTemperatureMin', label: 'Min Operating Temp (°C)', fallback: '-', editType: 'number', validation: { min: -40, max: 100 } },
        { key: 'operatingTemperatureMax', label: 'Max Operating Temp (°C)', fallback: '-', editType: 'number', validation: { min: -40, max: 100 } },
        { key: 'humidityRange', label: 'Operating Humidity', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'operatingHumidityMin', label: 'Min Operating Humidity (%)', fallback: '-', editType: 'number', validation: { min: 0, max: 100 } },
        { key: 'operatingHumidityMax', label: 'Max Operating Humidity (%)', fallback: '-', editType: 'number', validation: { min: 0, max: 100 } }
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
        { key: 'partNumber', label: 'Part Number', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'sku', label: 'SKU', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'description', label: 'Description', fallback: 'No description', editType: 'textarea', validation: { maxLength: 2000 } }
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
        { key: 'releaseDate', label: 'Release Date', type: 'date', fallback: '-', editType: 'date' },
        { key: 'discontinueDate', label: 'Discontinue Date', type: 'date', fallback: '-', editType: 'date' },
        { key: 'warrantyYears', label: 'Warranty (Years)', fallback: '-', editType: 'number', validation: { min: 0, max: 20 } },
        { key: 'expectedLifespanYears', label: 'Expected Lifespan (Years)', fallback: '-', editType: 'number', validation: { min: 1, max: 50 } },
        { key: 'maintenanceIntervalMonths', label: 'Maintenance Interval (Months)', fallback: '-', editType: 'number', validation: { min: 1, max: 60 } }
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
        { key: 'datasheetUrl', label: 'Datasheet URL', type: 'url', fallback: '-', editType: 'input', validation: { maxLength: 512 } },
        { key: 'manualUrl', label: 'Manual URL', type: 'url', fallback: '-', editType: 'input', validation: { maxLength: 512 } },
        { key: 'supportUrl', label: 'Support URL', type: 'url', fallback: '-', editType: 'input', validation: { maxLength: 512 } }
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
        { key: 'certifications', label: 'Certifications', fallback: '-', editType: 'textarea', validation: { maxLength: 1000 } },
        { key: 'complianceStandards', label: 'Compliance Standards', fallback: '-', editType: 'textarea', validation: { maxLength: 1000 } }
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
        { key: 'msrp', label: 'MSRP', type: 'currency', fallback: '-', editType: 'number', validation: { min: 0, max: 10000000 } },
        { key: 'listPrice', label: 'List Price', type: 'currency', fallback: '-', editType: 'number', validation: { min: 0, max: 10000000 } },
        { key: 'currencyCode', label: 'Currency', fallback: 'USD', editType: 'input', validation: { maxLength: 3 } }
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
        { key: 'verified', label: 'Verified', type: 'boolean', editType: 'checkbox' },
        { key: 'verificationDate', label: 'Verification Date', type: 'date', fallback: '-', editType: 'date' },
        { key: 'notes', label: 'Internal Notes', type: 'textarea', fallback: 'No notes', editType: 'textarea', validation: { maxLength: 5000 } },
        { key: 'createdAt', label: 'Created At', type: 'datetime', fallback: '-', editType: 'readonly', editable: false },
        { key: 'updatedAt', label: 'Updated At', type: 'datetime', fallback: '-', editType: 'readonly', editable: false },
        { key: 'createdDate', label: 'Created Date', type: 'datetime', fallback: '-', editType: 'readonly', editable: false },
        { key: 'updatedDate', label: 'Updated Date', type: 'datetime', fallback: '-', editType: 'readonly', editable: false }
      ]
    }
  }
};
