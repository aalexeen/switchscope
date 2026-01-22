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

import { createBaseSections, overviewSection } from './_baseFields';

export default {
  tableKey: 'componentModels',
  composable: 'useComponentModels',

  sections: {
    // ============================================
    // PRIORITY 1: Overview (Always visible)
    // ============================================
    overview: { ...overviewSection },

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

    ...createBaseSections(11)
  }
};
