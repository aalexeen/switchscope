/**
 * Detail View Configuration for Switch Model
 * Displays comprehensive specifications for network switches
 *
 * Field edit types:
 * - input: text input
 * - textarea: multiline text
 * - checkbox/boolean: toggle switch
 * - number: numeric input
 * - searchable-select: dropdown with search for FK relations
 * - readonly: non-editable display
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
        { key: 'modelDesignation', label: 'Model', type: 'heading', fallback: '-', editType: 'readonly', editable: false },
        { key: 'name', label: 'Name', fallback: '-', editType: 'input', required: true, validation: { maxLength: 128 } },
        { key: 'manufacturer', label: 'Manufacturer', fallback: '-', editType: 'input', required: true, validation: { maxLength: 128 } },
        { key: 'modelNumber', label: 'Model Number', fallback: '-', editType: 'input', required: true, validation: { maxLength: 128 } },
        {
          key: 'componentTypeId',
          label: 'Component Type',
          fallback: '-',
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
    },

    // ============================================
    // PRIORITY 2: Switch Classification
    // ============================================
    switchClassification: {
      title: 'Switch Classification',
      priority: 2,
      collapsible: false,
      fields: [
        { key: 'switchClass', label: 'Switch Class', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'switchTier', label: 'Switch Tier', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'managed', label: 'Managed', type: 'boolean', editType: 'checkbox' },
        { key: 'enterprise', label: 'Enterprise Grade', type: 'boolean', editType: 'checkbox' },
        { key: 'layer3Routing', label: 'Layer 3 Routing', type: 'boolean', editType: 'checkbox' },
        { key: 'stackable', label: 'Stackable', type: 'boolean', editType: 'checkbox' },
        { key: 'maxStackUnits', label: 'Max Stack Units', fallback: '-', editType: 'number', validation: { min: 1, max: 16 } }
      ]
    },

    // ============================================
    // PRIORITY 3: Port Configuration
    // ============================================
    portConfiguration: {
      title: 'Port Configuration',
      priority: 3,
      collapsible: false,
      fields: [
        { key: 'portSummary', label: 'Port Summary', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'totalPorts', label: 'Total Ports', fallback: '-', editType: 'number', validation: { min: 0, max: 512 } },
        { key: 'fastEthernetPorts', label: 'Fast Ethernet (100M)', fallback: '0', editType: 'number', validation: { min: 0, max: 256 } },
        { key: 'gigabitPorts', label: 'Gigabit Ethernet (1G)', fallback: '0', editType: 'number', validation: { min: 0, max: 256 } },
        { key: 'tenGigPorts', label: '10 Gigabit Ethernet (10G)', fallback: '0', editType: 'number', validation: { min: 0, max: 128 } },
        { key: 'sfpPorts', label: 'SFP Ports', fallback: '0', editType: 'number', validation: { min: 0, max: 64 } },
        { key: 'sfpPlusPorts', label: 'SFP+ Ports', fallback: '0', editType: 'number', validation: { min: 0, max: 64 } },
        { key: 'consolePorts', label: 'Console Ports', fallback: '1', editType: 'number', validation: { min: 0, max: 8 } },
        { key: 'supportsHighSpeed', label: 'Supports High Speed (10G+)', type: 'boolean', editType: 'checkbox' }
      ]
    },

    // ============================================
    // PRIORITY 4: PoE Specifications
    // ============================================
    poeSpecifications: {
      title: 'PoE Specifications',
      priority: 4,
      collapsible: true,
      condition: (model) => model.supportsPoe === true,
      fields: [
        { key: 'supportsPoe', label: 'PoE Support', type: 'boolean', editType: 'checkbox' },
        { key: 'poeBudgetWatts', label: 'PoE Budget (Watts)', fallback: '-', editType: 'number', validation: { min: 0, max: 10000 } },
        { key: 'poeStandard', label: 'PoE Standard', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'hasPoePlus', label: 'PoE+ Support (802.3at)', type: 'boolean', editType: 'checkbox' }
      ]
    },

    // ============================================
    // PRIORITY 5: Physical Specifications
    // ============================================
    physicalSpecifications: {
      title: 'Physical Specifications',
      priority: 5,
      collapsible: true,
      fields: [
        { key: 'formFactor', label: 'Form Factor', fallback: '-', editType: 'input' },
        { key: 'rackUnits', label: 'Rack Units (U)', fallback: '-', editType: 'number', validation: { min: 1, max: 48 } },
        { key: 'dimensionsMm', label: 'Dimensions (W×D×H mm)', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'weightKg', label: 'Weight (kg)', fallback: '-', editType: 'number', validation: { min: 0.001, max: 1000 } }
      ]
    },

    // ============================================
    // PRIORITY 6: Power & Environmental
    // ============================================
    powerEnvironmental: {
      title: 'Power & Environmental',
      priority: 6,
      collapsible: true,
      fields: [
        { key: 'powerSummary', label: 'Power Summary', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'powerConsumptionWatts', label: 'Power Consumption (W)', fallback: '-', editType: 'number', validation: { min: 0, max: 10000 } },
        { key: 'maxPowerConsumptionWatts', label: 'Max Power Consumption (W)', fallback: '-', editType: 'number', validation: { min: 0, max: 10000 } },
        { key: 'powerInputType', label: 'Power Input Type', fallback: 'AC', editType: 'input', validation: { maxLength: 32 } },
        { key: 'redundantPowerSupply', label: 'Redundant Power Supply', type: 'boolean', editType: 'checkbox' },
        { key: 'powerEfficiency', label: 'Power Efficiency (Gbps/W)', fallback: '-', editType: 'number', validation: { min: 0, max: 1000 } },
        { key: 'fanless', label: 'Fanless', type: 'boolean', editType: 'checkbox' },
        { key: 'noiseLevelDb', label: 'Noise Level (dB)', fallback: '-', editType: 'number', validation: { min: 0, max: 100 } },
        { key: 'operatingTemperatureRange', label: 'Operating Temperature', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'operatingTemperatureMin', label: 'Min Operating Temp (°C)', fallback: '-', editType: 'number', validation: { min: -40, max: 100 } },
        { key: 'operatingTemperatureMax', label: 'Max Operating Temp (°C)', fallback: '-', editType: 'number', validation: { min: -40, max: 100 } },
        { key: 'humidityRange', label: 'Operating Humidity', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'operatingHumidityMin', label: 'Min Operating Humidity (%)', fallback: '-', editType: 'number', validation: { min: 0, max: 100 } },
        { key: 'operatingHumidityMax', label: 'Max Operating Humidity (%)', fallback: '-', editType: 'number', validation: { min: 0, max: 100 } }
      ]
    },

    // ============================================
    // PRIORITY 7: Performance Metrics
    // ============================================
    performanceMetrics: {
      title: 'Performance Metrics',
      priority: 7,
      collapsible: true,
      fields: [
        { key: 'switchingCapacityGbps', label: 'Switching Capacity (Gbps)', fallback: '-', editType: 'number', validation: { min: 0, max: 100000 } },
        { key: 'forwardingRateMpps', label: 'Forwarding Rate (Mpps)', fallback: '-', editType: 'number', validation: { min: 0, max: 100000 } },
        { key: 'macAddressTableSize', label: 'MAC Address Table Size', fallback: '-', editType: 'number', validation: { min: 0, max: 10000000 } },
        { key: 'bufferSizeMb', label: 'Buffer Size (MB)', fallback: '-', editType: 'number', validation: { min: 0, max: 10000 } },
        { key: 'latencyMicroseconds', label: 'Latency (μs)', fallback: '-', editType: 'number', validation: { min: 0, max: 10000 } }
      ]
    },

    // ============================================
    // PRIORITY 8: Network Features
    // ============================================
    networkFeatures: {
      title: 'Network Features',
      priority: 8,
      collapsible: true,
      fields: [
        { key: 'maxVlans', label: 'Max VLANs', fallback: '-', editType: 'number', validation: { min: 0, max: 65535 } },
        { key: 'supportsSpanningTree', label: 'Spanning Tree Protocol', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsLinkAggregation', label: 'Link Aggregation (LACP)', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsQos', label: 'Quality of Service (QoS)', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsIgmpSnooping', label: 'IGMP Snooping', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsPortMirroring', label: 'Port Mirroring', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsVoiceVlan', label: 'Voice VLAN', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsAcl', label: 'Access Control Lists (ACL)', type: 'boolean', editType: 'checkbox' }
      ]
    },

    // ============================================
    // PRIORITY 9: Management & Monitoring
    // ============================================
    managementMonitoring: {
      title: 'Management & Monitoring',
      priority: 9,
      collapsible: true,
      fields: [
        { key: 'managementSummary', label: 'Management Summary', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'managementInterfaces', label: 'Management Interfaces', fallback: '-', editType: 'input', validation: { maxLength: 256 } },
        { key: 'webManagement', label: 'Web Management', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsSsh', label: 'SSH Support', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsTelnet', label: 'Telnet Support', type: 'boolean', editType: 'checkbox' },
        { key: 'snmpVersions', label: 'SNMP Versions', fallback: '-', editType: 'input', validation: { maxLength: 64 } }
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
        { key: 'productFamily', label: 'Product Family', fallback: '-', editType: 'input', validation: { maxLength: 128 } },
        { key: 'series', label: 'Series', fallback: '-', editType: 'input', validation: { maxLength: 128 } },
        { key: 'partNumber', label: 'Part Number', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'sku', label: 'SKU', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'description', label: 'Description', fallback: 'No description', editType: 'textarea', validation: { maxLength: 2000 } }
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
        { key: 'releaseDate', label: 'Release Date', type: 'date', fallback: '-', editType: 'input', validation: { maxLength: 32 } },
        { key: 'discontinueDate', label: 'Discontinue Date', type: 'date', fallback: '-', editType: 'input', validation: { maxLength: 32 } },
        { key: 'warrantyYears', label: 'Warranty (Years)', fallback: '-', editType: 'number', validation: { min: 0, max: 100 } },
        { key: 'expectedLifespanYears', label: 'Expected Lifespan (Years)', fallback: '-', editType: 'number', validation: { min: 0, max: 50 } },
        { key: 'maintenanceIntervalMonths', label: 'Maintenance Interval (Months)', fallback: '-', editType: 'number', validation: { min: 0, max: 120 } }
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
        { key: 'datasheetUrl', label: 'Datasheet URL', type: 'url', fallback: '-', editType: 'input', validation: { maxLength: 512 } },
        { key: 'manualUrl', label: 'Manual URL', type: 'url', fallback: '-', editType: 'input', validation: { maxLength: 512 } },
        { key: 'supportUrl', label: 'Support URL', type: 'url', fallback: '-', editType: 'input', validation: { maxLength: 512 } }
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
        { key: 'certifications', label: 'Certifications', fallback: '-', editType: 'textarea', validation: { maxLength: 1000 } },
        { key: 'complianceStandards', label: 'Compliance Standards', fallback: '-', editType: 'textarea', validation: { maxLength: 1000 } }
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
        { key: 'msrp', label: 'MSRP', type: 'currency', fallback: '-', editType: 'number', validation: { min: 0, max: 10000000 } },
        { key: 'listPrice', label: 'List Price', type: 'currency', fallback: '-', editType: 'number', validation: { min: 0, max: 10000000 } },
        { key: 'currencyCode', label: 'Currency', fallback: 'USD', editType: 'input', validation: { maxLength: 3 } }
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
        { key: 'verified', label: 'Verified', type: 'boolean', editType: 'checkbox' },
        { key: 'verificationDate', label: 'Verification Date', type: 'date', fallback: '-', editType: 'input', validation: { maxLength: 32 } },
        { key: 'notes', label: 'Internal Notes', type: 'textarea', fallback: 'No notes', editType: 'textarea', validation: { maxLength: 5000 } },
        { key: 'createdAt', label: 'Created At', type: 'datetime', fallback: '-', editType: 'readonly', editable: false },
        { key: 'updatedAt', label: 'Updated At', type: 'datetime', fallback: '-', editType: 'readonly', editable: false },
        { key: 'createdDate', label: 'Created Date', type: 'datetime', fallback: '-', editType: 'readonly', editable: false },
        { key: 'updatedDate', label: 'Updated Date', type: 'datetime', fallback: '-', editType: 'readonly', editable: false }
      ]
    }
  }
};
