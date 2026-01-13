/**
 * Detail View Configuration for Router Model
 * Displays comprehensive specifications for network routers
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
    // PRIORITY 2: Router Classification
    // ============================================
    routerClassification: {
      title: 'Router Classification',
      priority: 2,
      collapsible: false,
      fields: [
        { key: 'routerClass', label: 'Router Class', fallback: '-' },
        { key: 'targetDeployment', label: 'Target Deployment', fallback: '-' },
        { key: 'enterpriseRouter', label: 'Enterprise Router', type: 'boolean' },
        { key: 'hasAdvancedRouting', label: 'Advanced Routing', type: 'boolean' }
      ]
    },

    // ============================================
    // PRIORITY 3: Interface Configuration
    // ============================================
    interfaceConfiguration: {
      title: 'Interface Configuration',
      priority: 3,
      collapsible: false,
      fields: [
        { key: 'interfaceSummary', label: 'Interface Summary', type: 'summary', fallback: '-' },
        { key: 'ethernetPorts', label: 'Total Ethernet Ports', fallback: '-' },
        { key: 'gigabitEthernetPorts', label: 'Gigabit Ethernet (1G)', fallback: '0' },
        { key: 'tenGigPorts', label: '10 Gigabit Ethernet (10G)', fallback: '0' },
        { key: 'sfpPorts', label: 'SFP Ports', fallback: '0' },
        { key: 'serialWanPorts', label: 'Serial WAN Ports', fallback: '0' }
      ]
    },

    // ============================================
    // PRIORITY 4: Routing Capabilities
    // ============================================
    routingCapabilities: {
      title: 'Routing Capabilities',
      priority: 4,
      collapsible: true,
      fields: [
        { key: 'supportsBgp', label: 'BGP Support', type: 'boolean' },
        { key: 'supportsOspf', label: 'OSPF Support', type: 'boolean' },
        { key: 'supportsNat', label: 'NAT Support', type: 'boolean' }
      ]
    },

    // ============================================
    // PRIORITY 5: Security & VPN
    // ============================================
    securityVpn: {
      title: 'Security & VPN',
      priority: 5,
      collapsible: true,
      fields: [
        { key: 'supportsFirewall', label: 'Firewall Support', type: 'boolean' },
        { key: 'supportsIpsecVpn', label: 'IPsec VPN Support', type: 'boolean' },
        { key: 'hasVpnCapability', label: 'VPN Capability', type: 'boolean' },
        { key: 'maxVpnTunnels', label: 'Max VPN Tunnels', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 6: WAN Interfaces
    // ============================================
    wanInterfaces: {
      title: 'WAN Interface Support',
      priority: 6,
      collapsible: true,
      fields: [
        { key: 'supportedWanInterfaces', label: 'Supported WAN Interfaces', type: 'array', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 7: Physical Specifications
    // ============================================
    physicalSpecifications: {
      title: 'Physical Specifications',
      priority: 7,
      collapsible: true,
      fields: [
        { key: 'formFactor', label: 'Form Factor', fallback: '-' },
        { key: 'rackUnits', label: 'Rack Units (U)', fallback: '-' },
        { key: 'dimensionsMm', label: 'Dimensions (W×D×H mm)', fallback: '-' },
        { key: 'weightKg', label: 'Weight (kg)', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 8: Performance Metrics
    // ============================================
    performanceMetrics: {
      title: 'Performance Metrics',
      priority: 8,
      collapsible: true,
      fields: [
        { key: 'routingThroughputGbps', label: 'Routing Throughput (Gbps)', fallback: '-' },
        { key: 'firewallThroughputGbps', label: 'Firewall Throughput (Gbps)', fallback: '-' },
        { key: 'vpnThroughputMbps', label: 'VPN Throughput (Mbps)', fallback: '-' },
        { key: 'powerEfficiency', label: 'Power Efficiency (Gbps/W)', fallback: '-' }
      ]
    },

    // ============================================
    // PRIORITY 9: Power & Environmental
    // ============================================
    powerEnvironmental: {
      title: 'Power & Environmental',
      priority: 9,
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
    },

    // ============================================
    // PRIORITY 10: Management & Monitoring
    // ============================================
    managementMonitoring: {
      title: 'Management & Monitoring',
      priority: 10,
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
