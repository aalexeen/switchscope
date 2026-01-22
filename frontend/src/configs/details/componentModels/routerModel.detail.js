/**
 * Detail View Configuration for Router Model
 * Displays comprehensive specifications for network routers
 */

import {
  createBaseSections,
  managementMonitoringSection,
  overviewSection,
  physicalSpecificationsSection,
  powerEnvironmentalSection
} from './_baseFields';

export default {
  tableKey: 'componentModels',
  composable: 'useComponentModels',

  sections: {
    // ============================================
    // PRIORITY 1: Overview (Always visible)
    // ============================================
    overview: { ...overviewSection },

    // ============================================
    // PRIORITY 2: Router Classification
    // ============================================
    routerClassification: {
      title: 'Router Classification',
      priority: 2,
      collapsible: false,
      fields: [
        { key: 'routerClass', label: 'Router Class', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'targetDeployment', label: 'Target Deployment', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'enterpriseRouter', label: 'Enterprise Router', type: 'boolean', editType: 'checkbox' },
        { key: 'hasAdvancedRouting', label: 'Advanced Routing', type: 'boolean', editType: 'checkbox' }
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
        { key: 'interfaceSummary', label: 'Interface Summary', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'ethernetPorts', label: 'Total Ethernet Ports', fallback: '-', editType: 'number', validation: { min: 0, max: 512 } },
        { key: 'gigabitEthernetPorts', label: 'Gigabit Ethernet (1G)', fallback: '0', editType: 'number', validation: { min: 0, max: 256 } },
        { key: 'tenGigPorts', label: '10 Gigabit Ethernet (10G)', fallback: '0', editType: 'number', validation: { min: 0, max: 128 } },
        { key: 'sfpPorts', label: 'SFP Ports', fallback: '0', editType: 'number', validation: { min: 0, max: 128 } },
        { key: 'serialWanPorts', label: 'Serial WAN Ports', fallback: '0', editType: 'number', validation: { min: 0, max: 64 } }
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
        { key: 'supportsBgp', label: 'BGP Support', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsOspf', label: 'OSPF Support', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsNat', label: 'NAT Support', type: 'boolean', editType: 'checkbox' }
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
        { key: 'supportsFirewall', label: 'Firewall Support', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsIpsecVpn', label: 'IPsec VPN Support', type: 'boolean', editType: 'checkbox' },
        { key: 'hasVpnCapability', label: 'VPN Capability', type: 'boolean', editType: 'checkbox' },
        { key: 'maxVpnTunnels', label: 'Max VPN Tunnels', fallback: '-', editType: 'number', validation: { min: 0, max: 100000 } }
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
        { key: 'supportedWanInterfaces', label: 'Supported WAN Interfaces', type: 'array', fallback: '-', editType: 'readonly', editable: false }
      ]
    },

    // ============================================
    // PRIORITY 7: Physical Specifications
    // ============================================
    physicalSpecifications: { ...physicalSpecificationsSection, priority: 7 },

    // ============================================
    // PRIORITY 8: Performance Metrics
    // ============================================
    performanceMetrics: {
      title: 'Performance Metrics',
      priority: 8,
      collapsible: true,
      fields: [
        { key: 'routingThroughputGbps', label: 'Routing Throughput (Gbps)', fallback: '-', editType: 'number', validation: { min: 0, max: 100000 } },
        { key: 'firewallThroughputGbps', label: 'Firewall Throughput (Gbps)', fallback: '-', editType: 'number', validation: { min: 0, max: 100000 } },
        { key: 'vpnThroughputMbps', label: 'VPN Throughput (Mbps)', fallback: '-', editType: 'number', validation: { min: 0, max: 100000 } },
        { key: 'powerEfficiency', label: 'Power Efficiency (Gbps/W)', fallback: '-', editType: 'number', validation: { min: 0, max: 1000 } }
      ]
    },

    // ============================================
    // PRIORITY 9: Power & Environmental
    // ============================================
    powerEnvironmental: { ...powerEnvironmentalSection, priority: 9 },

    // ============================================
    // PRIORITY 10: Management & Monitoring
    // ============================================
    managementMonitoring: { ...managementMonitoringSection, priority: 10 },

    // ============================================
    // PRIORITY 10: Additional Specifications (deviceSpecs map)
    // ============================================
    additionalSpecifications: {
      title: 'Additional Specifications',
      priority: 10,
      collapsible: true,
      condition: (model) => model.deviceSpecs && Object.keys(model.deviceSpecs).length > 0,
      fields: [
        { key: 'deviceSpecs', label: 'Device Specifications', type: 'map', fallback: 'No additional specifications', editType: 'readonly', editable: false }
      ]
    },

    ...createBaseSections(11)
  }
};
