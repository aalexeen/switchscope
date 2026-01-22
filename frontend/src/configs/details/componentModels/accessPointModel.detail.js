/**
 * Detail View Configuration for Access Point Model
 * Displays comprehensive specifications for wireless access points
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
    // PRIORITY 2: WiFi Specifications
    // ============================================
    wifiSpecifications: {
      title: 'WiFi Specifications',
      priority: 2,
      collapsible: false,
      fields: [
        { key: 'wifiStandard', label: 'WiFi Standard', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'wifiGeneration', label: 'WiFi Generation', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'wifi6', label: 'WiFi 6 (802.11ax)', type: 'boolean', editType: 'checkbox' },
        { key: 'maxClients', label: 'Max Clients', fallback: '-', editType: 'number', validation: { min: 0, max: 10000 } },
        { key: 'maxThroughputMbps', label: 'Max Throughput (Mbps)', fallback: '-', editType: 'number', validation: { min: 0, max: 100000 } }
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
        { key: 'bandSummary', label: 'Band Summary', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'dualBand', label: 'Dual Band', type: 'boolean', editType: 'checkbox' },
        { key: 'supports5Ghz', label: '5 GHz Support', type: 'boolean', editType: 'checkbox' },
        { key: 'supports6Ghz', label: '6 GHz Support (WiFi 6E)', type: 'boolean', editType: 'checkbox' }
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
        { key: 'indoorCoverageSqm', label: 'Indoor Coverage (m²)', fallback: '-', editType: 'number', validation: { min: 0, max: 100000 } },
        { key: 'outdoorCoverageSqm', label: 'Outdoor Coverage (m²)', fallback: '-', editType: 'number', validation: { min: 0, max: 100000 } },
        { key: 'indoorUse', label: 'Indoor Use', type: 'boolean', editType: 'checkbox' },
        { key: 'outdoorUse', label: 'Outdoor Use', type: 'boolean', editType: 'checkbox' }
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
        { key: 'supportsWpa3', label: 'WPA3 Support', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsEnterpriseAuth', label: 'Enterprise Authentication (802.1X)', type: 'boolean', editType: 'checkbox' },
        { key: 'enterprise', label: 'Enterprise Grade', type: 'boolean', editType: 'checkbox' }
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
        { key: 'hasAdvancedFeatures', label: 'Has Advanced Features', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsMesh', label: 'Mesh Support', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsMuMimo', label: 'MU-MIMO Support', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsBeamforming', label: 'Beamforming Support', type: 'boolean', editType: 'checkbox' }
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
        { key: 'managementSummary', label: 'Management Summary', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'controllerManaged', label: 'Controller Managed', type: 'boolean', editType: 'checkbox' },
        { key: 'cloudManaged', label: 'Cloud Managed', type: 'boolean', editType: 'checkbox' },
        { key: 'webManagement', label: 'Web Management', type: 'boolean', editType: 'checkbox' },
        { key: 'supportsSsh', label: 'SSH Support', type: 'boolean', editType: 'checkbox' },
        { key: 'snmpVersions', label: 'SNMP Versions', fallback: '-', editType: 'input', validation: { maxLength: 64 } }
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
        { key: 'poeRequired', label: 'PoE Required', type: 'boolean', editType: 'checkbox' },
        { key: 'poeStandard', label: 'PoE Standard', fallback: '-', editType: 'input', validation: { maxLength: 64 } }
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
        { key: 'formFactor', label: 'Form Factor', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'dimensionsMm', label: 'Dimensions (W×D×H mm)', fallback: '-', editType: 'input', validation: { maxLength: 64 } },
        { key: 'weightKg', label: 'Weight (kg)', fallback: '-', editType: 'number', validation: { min: 0.001, max: 1000 } },
        { key: 'weightG', label: 'Weight (g)', fallback: '-', editType: 'number', validation: { min: 1, max: 1000000 } },
        { key: 'ipRating', label: 'IP Rating', fallback: '-', editType: 'input', validation: { maxLength: 8 } }
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
        { key: 'powerSummary', label: 'Power Summary', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'powerConsumptionWatts', label: 'Power Consumption (W)', fallback: '-', editType: 'number', validation: { min: 0, max: 10000 } },
        { key: 'maxPowerConsumptionWatts', label: 'Max Power Consumption (W)', fallback: '-', editType: 'number', validation: { min: 0, max: 10000 } },
        { key: 'powerInputType', label: 'Power Input Type', fallback: 'PoE', editType: 'input', validation: { maxLength: 32 } },
        { key: 'powerEfficiency', label: 'Power Efficiency (Mbps/W)', fallback: '-', editType: 'number', validation: { min: 0, max: 1000 } },
        { key: 'operatingTemperatureRange', label: 'Operating Temperature', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'operatingTemperatureMin', label: 'Min Operating Temp (°C)', fallback: '-', editType: 'number', validation: { min: -40, max: 100 } },
        { key: 'operatingTemperatureMax', label: 'Max Operating Temp (°C)', fallback: '-', editType: 'number', validation: { min: -40, max: 100 } },
        { key: 'humidityRange', label: 'Operating Humidity', type: 'summary', fallback: '-', editType: 'readonly', editable: false },
        { key: 'operatingHumidityMin', label: 'Min Operating Humidity (%)', fallback: '-', editType: 'number', validation: { min: 0, max: 100 } },
        { key: 'operatingHumidityMax', label: 'Max Operating Humidity (%)', fallback: '-', editType: 'number', validation: { min: 0, max: 100 } }
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
        { key: 'deviceSpecs', label: 'Device Specifications', type: 'map', fallback: 'No additional specifications', editType: 'readonly', editable: false }
      ]
    },

    ...createBaseSections(11)
  }
};
