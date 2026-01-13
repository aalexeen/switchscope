/**
 * Detail View Configuration for Switch Model
 * Displays comprehensive specifications for network switches
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
    // PRIORITY 2: Switch Classification
    // ============================================
    switchClassification: {
      title: 'Switch Classification',
      priority: 2,
      collapsible: false,
      fields: [
        { key: 'switchClass', label: 'Switch Class', fallback: '-' },
        { key: 'switchTier', label: 'Switch Tier', fallback: '-' },
        { key: 'managed', label: 'Managed', type: 'boolean' },
        { key: 'enterprise', label: 'Enterprise Grade', type: 'boolean' },
        { key: 'layer3Routing', label: 'Layer 3 Routing', type: 'boolean' },
        { key: 'stackable', label: 'Stackable', type: 'boolean' },
        { key: 'maxStackUnits', label: 'Max Stack Units', fallback: '-' }
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
        { key: 'portSummary', label: 'Port Summary', type: 'summary', fallback: '-' },
        { key: 'totalPorts', label: 'Total Ports', fallback: '-' },
        { key: 'fastEthernetPorts', label: 'Fast Ethernet (100M)', fallback: '0' },
        { key: 'gigabitPorts', label: 'Gigabit Ethernet (1G)', fallback: '0' },
        { key: 'tenGigPorts', label: '10 Gigabit Ethernet (10G)', fallback: '0' },
        { key: 'sfpPorts', label: 'SFP Ports', fallback: '0' },
        { key: 'sfpPlusPorts', label: 'SFP+ Ports', fallback: '0' },
        { key: 'consolePorts', label: 'Console Ports', fallback: '1' },
        { key: 'supportsHighSpeed', label: 'Supports High Speed (10G+)', type: 'boolean' }
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
        { key: 'supportsPoe', label: 'PoE Support', type: 'boolean' },
        { key: 'poeBudgetWatts', label: 'PoE Budget (Watts)', fallback: '-' },
        { key: 'poeStandard', label: 'PoE Standard', fallback: '-' },
        { key: 'hasPoePlus', label: 'PoE+ Support (802.3at)', type: 'boolean' }
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
        { key: 'formFactor', label: 'Form Factor', fallback: '-' },
        { key: 'rackUnits', label: 'Rack Units (U)', fallback: '-' },
        { key: 'dimensionsMm', label: 'Dimensions (W×D×H mm)', fallback: '-' },
        { key: 'weightKg', label: 'Weight (kg)', fallback: '-' }
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
        { key: 'powerSummary', label: 'Power Summary', type: 'summary', fallback: '-' },
        { key: 'powerConsumptionWatts', label: 'Power Consumption (W)', fallback: '-' },
        { key: 'maxPowerConsumptionWatts', label: 'Max Power Consumption (W)', fallback: '-' },
        { key: 'powerInputType', label: 'Power Input Type', fallback: 'AC' },
        { key: 'redundantPowerSupply', label: 'Redundant Power Supply', type: 'boolean' },
        { key: 'powerEfficiency', label: 'Power Efficiency (Gbps/W)', fallback: '-' },
        { key: 'fanless', label: 'Fanless', type: 'boolean' },
        { key: 'noiseLevelDb', label: 'Noise Level (dB)', fallback: '-' },
        { key: 'operatingTemperatureRange', label: 'Operating Temperature', type: 'summary', fallback: '-' },
        { key: 'operatingTemperatureMin', label: 'Min Operating Temp (°C)', fallback: '-' },
        { key: 'operatingTemperatureMax', label: 'Max Operating Temp (°C)', fallback: '-' },
        { key: 'humidityRange', label: 'Operating Humidity', type: 'summary', fallback: '-' },
        { key: 'operatingHumidityMin', label: 'Min Operating Humidity (%)', fallback: '-' },
        { key: 'operatingHumidityMax', label: 'Max Operating Humidity (%)', fallback: '-' }
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
        { key: 'switchingCapacityGbps', label: 'Switching Capacity (Gbps)', fallback: '-' },
        { key: 'forwardingRateMpps', label: 'Forwarding Rate (Mpps)', fallback: '-' },
        { key: 'macAddressTableSize', label: 'MAC Address Table Size', fallback: '-' },
        { key: 'bufferSizeMb', label: 'Buffer Size (MB)', fallback: '-' },
        { key: 'latencyMicroseconds', label: 'Latency (μs)', fallback: '-' }
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
        { key: 'maxVlans', label: 'Max VLANs', fallback: '-' },
        { key: 'supportsSpanningTree', label: 'Spanning Tree Protocol', type: 'boolean' },
        { key: 'supportsLinkAggregation', label: 'Link Aggregation (LACP)', type: 'boolean' },
        { key: 'supportsQos', label: 'Quality of Service (QoS)', type: 'boolean' },
        { key: 'supportsIgmpSnooping', label: 'IGMP Snooping', type: 'boolean' },
        { key: 'supportsPortMirroring', label: 'Port Mirroring', type: 'boolean' },
        { key: 'supportsVoiceVlan', label: 'Voice VLAN', type: 'boolean' },
        { key: 'supportsAcl', label: 'Access Control Lists (ACL)', type: 'boolean' }
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
        { key: 'managementSummary', label: 'Management Summary', type: 'summary', fallback: '-' },
        { key: 'managementInterfaces', label: 'Management Interfaces', fallback: '-' },
        { key: 'webManagement', label: 'Web Management', type: 'boolean' },
        { key: 'supportsSsh', label: 'SSH Support', type: 'boolean' },
        { key: 'supportsTelnet', label: 'Telnet Support', type: 'boolean' },
        { key: 'snmpVersions', label: 'SNMP Versions', fallback: '-' }
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
