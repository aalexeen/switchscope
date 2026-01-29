/**
 * Detail View Configuration for Network Switch
 *
 * Field access levels (from backend @FieldAccess annotations):
 * - REQUIRED: Cannot be nullified by anyone (name, componentStatusId, componentTypeId)
 * - ADMIN_NULLABLE: Only admin can nullify (description, manufacturer, model, etc.)
 * - USER_WRITABLE: Any authenticated user can nullify (most switch-specific fields)
 * - READ_ONLY: Computed/system fields, cannot be modified
 */

export default {
  tableKey: 'networkSwitches',
  composable: 'useNetworkSwitches',

  sections: {
    basicInfo: {
      title: 'Basic Information',
      fields: [
        {
          key: 'name',
          label: 'Name',
          fallback: '-',
          editType: 'input',
          required: true,
          validation: { minLength: 2, maxLength: 128 }
        },
        {
          key: 'description',
          label: 'Description',
          fallback: 'No description',
          editType: 'textarea',
          rows: 3,
          validation: { maxLength: 1024 }
        },
        {
          key: 'manufacturer',
          label: 'Manufacturer',
          fallback: 'N/A',
          editType: 'input',
          validation: { maxLength: 128 }
        },
        {
          key: 'model',
          label: 'Model',
          fallback: 'N/A',
          editType: 'input',
          validation: { maxLength: 128 }
        },
        {
          key: 'serialNumber',
          label: 'Serial Number',
          fallback: 'N/A',
          editType: 'input',
          validation: { maxLength: 128 }
        },
        {
          key: 'partNumber',
          label: 'Part Number',
          fallback: 'N/A',
          editType: 'input',
          validation: { maxLength: 128 }
        },
        {
          key: 'switchModelId',
          label: 'Switch Model (Catalog)',
          editType: 'searchable-select',
          relation: {
            composable: 'useComponentModels',
            dataKey: 'componentModels',
            valueKey: 'id',
            labelKey: 'name',
            searchFields: ['name', 'manufacturer', 'modelNumber'],
            filter: (item) => item.modelClass === 'SWITCH_MODEL'
          }
        },
        {
          key: 'switchModelName',
          label: 'Switch Model Name',
          fallback: '-',
          editType: 'readonly',
          editable: false
        }
      ]
    },
    classification: {
      title: 'Classification',
      fields: [
        {
          key: 'componentTypeId',
          label: 'Component Type',
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
        {
          key: 'componentTypeCode',
          label: 'Type Code',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'componentStatusId',
          label: 'Status',
          editType: 'searchable-select',
          required: true,
          relation: {
            composable: 'useComponentStatuses',
            dataKey: 'componentStatuses',
            valueKey: 'id',
            labelKey: 'displayName',
            searchFields: ['name', 'code', 'displayName']
          }
        },
        {
          key: 'componentStatusCode',
          label: 'Status Code',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'componentNatureId',
          label: 'Component Nature',
          editType: 'searchable-select',
          relation: {
            composable: 'useComponentNatures',
            dataKey: 'componentNatures',
            valueKey: 'id',
            labelKey: 'displayName',
            searchFields: ['name', 'code', 'displayName']
          }
        },
        {
          key: 'componentNatureCode',
          label: 'Nature Code',
          fallback: '-',
          editType: 'readonly',
          editable: false
        }
      ]
    },
    networkManagement: {
      title: 'Network Management',
      fields: [
        {
          key: 'managementIp',
          label: 'Management IP',
          fallback: 'N/A',
          editType: 'input',
          placeholder: '192.168.1.1'
        },
        {
          key: 'subnetMask',
          label: 'Subnet Mask',
          fallback: 'N/A',
          editType: 'input',
          placeholder: '255.255.255.0'
        },
        {
          key: 'defaultGateway',
          label: 'Default Gateway',
          fallback: 'N/A',
          editType: 'input',
          placeholder: '192.168.1.254'
        },
        {
          key: 'managementVlan',
          label: 'Management VLAN',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 4094 }
        },
        {
          key: 'networkConfiguration',
          label: 'Network Configuration',
          fallback: '-',
          editType: 'readonly',
          editable: false
        }
      ]
    },
    firmware: {
      title: 'Firmware & Software',
      fields: [
        {
          key: 'firmwareVersion',
          label: 'Firmware Version',
          fallback: 'N/A',
          editType: 'input',
          validation: { maxLength: 128 }
        },
        {
          key: 'softwareVersion',
          label: 'Software Version',
          fallback: 'N/A',
          editType: 'input',
          validation: { maxLength: 128 }
        },
        {
          key: 'bootLoaderVersion',
          label: 'Boot Loader Version',
          fallback: 'N/A',
          editType: 'input',
          validation: { maxLength: 128 }
        }
      ]
    },
    snmp: {
      title: 'SNMP Configuration',
      fields: [
        {
          key: 'snmpVersion',
          label: 'SNMP Version',
          fallback: '-',
          editType: 'select',
          options: [
            { value: 'v1', label: 'SNMP v1' },
            { value: 'v2c', label: 'SNMP v2c' },
            { value: 'v3', label: 'SNMP v3' }
          ]
        },
        {
          key: 'snmpCommunityRead',
          label: 'SNMP Read Community',
          fallback: '-',
          editType: 'input',
          placeholder: 'Read community string'
        },
        {
          key: 'snmpCommunityWrite',
          label: 'SNMP Write Community',
          fallback: '-',
          editType: 'input',
          placeholder: 'Write community string'
        }
      ]
    },
    monitoring: {
      title: 'Monitoring',
      fields: [
        {
          key: 'monitored',
          label: 'Monitored',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'monitoringIntervalSeconds',
          label: 'Monitoring Interval (s)',
          fallback: '-',
          editType: 'number',
          validation: { min: 30, max: 86400 }
        },
        {
          key: 'monitoringStatus',
          label: 'Monitoring Status',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'lastPingTime',
          label: 'Last Ping Time',
          format: 'date',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'lastPingSuccess',
          label: 'Last Ping Success',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'uptimeSeconds',
          label: 'Uptime (s)',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'cpuUtilizationPercent',
          label: 'CPU Utilization (%)',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'memoryUtilizationPercent',
          label: 'Memory Utilization (%)',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'temperatureCelsius',
          label: 'Temperature (°C)',
          fallback: '-',
          editType: 'readonly',
          editable: false
        }
      ]
    },
    switchCapabilities: {
      title: 'Switch Capabilities',
      fields: [
        {
          key: 'maxPorts',
          label: 'Max Ports',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 512 }
        },
        {
          key: 'portCount',
          label: 'Current Port Count',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'switchingCapacityGbps',
          label: 'Switching Capacity (Gbps)',
          fallback: '-',
          editType: 'number',
          validation: { min: 0, max: 10000 }
        },
        {
          key: 'forwardingRateMpps',
          label: 'Forwarding Rate (Mpps)',
          fallback: '-',
          editType: 'number',
          validation: { min: 0, max: 10000 }
        },
        {
          key: 'macAddressTableSize',
          label: 'MAC Table Size',
          fallback: '-',
          editType: 'number',
          validation: { min: 0 }
        },
        {
          key: 'packetBufferSizeMb',
          label: 'Packet Buffer (MB)',
          fallback: '-',
          editType: 'number',
          validation: { min: 0 }
        }
      ]
    },
    poe: {
      title: 'Power over Ethernet (PoE)',
      fields: [
        {
          key: 'supportsPoe',
          label: 'Supports PoE',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'poeBudgetWatts',
          label: 'PoE Budget (W)',
          fallback: '-',
          editType: 'number',
          validation: { min: 0, max: 10000 }
        },
        {
          key: 'poeCapability',
          label: 'PoE Capability',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'availablePoeBudget',
          label: 'Available PoE Budget (W)',
          fallback: '-',
          editType: 'readonly',
          editable: false
        }
      ]
    },
    stacking: {
      title: 'Stacking',
      fields: [
        {
          key: 'supportsStacking',
          label: 'Supports Stacking',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'stackId',
          label: 'Stack ID',
          fallback: '-',
          editType: 'number',
          validation: { min: 1 }
        },
        {
          key: 'stackPriority',
          label: 'Stack Priority',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 15 }
        },
        {
          key: 'maxStackMembers',
          label: 'Max Stack Members',
          fallback: '-',
          editType: 'number',
          validation: { min: 2, max: 16 }
        },
        {
          key: 'stackable',
          label: 'Stackable',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'inStack',
          label: 'In Stack',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'stackMaster',
          label: 'Stack Master',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        }
      ]
    },
    vlan: {
      title: 'VLAN Configuration',
      fields: [
        {
          key: 'vlanSupport',
          label: 'VLAN Support',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'maxVlans',
          label: 'Max VLANs',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 4094 }
        },
        {
          key: 'nativeVlan',
          label: 'Native VLAN',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 4094 }
        },
        {
          key: 'voiceVlan',
          label: 'Voice VLAN',
          fallback: '-',
          editType: 'number',
          validation: { min: 1, max: 4094 }
        }
      ]
    },
    features: {
      title: 'Features',
      fields: [
        {
          key: 'spanningTreeSupport',
          label: 'Spanning Tree Support',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'qosSupport',
          label: 'QoS Support',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'portMirroring',
          label: 'Port Mirroring',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'linkAggregation',
          label: 'Link Aggregation',
          type: 'boolean',
          editType: 'checkbox'
        }
      ]
    },
    physical: {
      title: 'Physical Characteristics',
      fields: [
        {
          key: 'consolePortType',
          label: 'Console Port Type',
          fallback: 'N/A',
          editType: 'select',
          options: [
            { value: 'RJ45', label: 'RJ45' },
            { value: 'USB', label: 'USB' },
            { value: 'DB9', label: 'DB9' },
            { value: 'Mini-USB', label: 'Mini-USB' }
          ]
        },
        {
          key: 'hasRedundantPower',
          label: 'Redundant Power',
          type: 'boolean',
          editType: 'checkbox'
        },
        {
          key: 'operatingTemperatureMin',
          label: 'Operating Temp Min (°C)',
          fallback: '-',
          editType: 'number',
          validation: { min: -40, max: 100 }
        },
        {
          key: 'operatingTemperatureMax',
          label: 'Operating Temp Max (°C)',
          fallback: '-',
          editType: 'number',
          validation: { min: -40, max: 100 }
        }
      ]
    },
    installation: {
      title: 'Installation',
      fields: [
        {
          key: 'parentComponentId',
          label: 'Parent Component',
          editType: 'searchable-select',
          relation: {
            composable: 'useComponents',
            dataKey: 'components',
            valueKey: 'id',
            labelKey: 'name',
            searchFields: ['name', 'serialNumber', 'manufacturer']
          }
        },
        {
          key: 'parentComponentName',
          label: 'Parent Component Name',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'installationId',
          label: 'Installation ID',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'componentPath',
          label: 'Component Path',
          fallback: '-',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'locationAddress',
          label: 'Location Address',
          fallback: 'Not installed',
          editType: 'readonly',
          editable: false
        }
      ]
    },
    maintenance: {
      title: 'Maintenance',
      fields: [
        {
          key: 'lastMaintenanceDate',
          label: 'Last Maintenance',
          fallback: '-',
          editType: 'date',
          format: 'date'
        },
        {
          key: 'nextMaintenanceDate',
          label: 'Next Maintenance',
          fallback: '-',
          editType: 'date',
          format: 'date'
        }
      ]
    },
    computed: {
      title: 'Computed Properties',
      fields: [
        {
          key: 'operational',
          label: 'Operational',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'installed',
          label: 'Installed',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'managed',
          label: 'Managed',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        },
        {
          key: 'reachable',
          label: 'Reachable',
          type: 'boolean',
          editType: 'readonly',
          editable: false
        }
      ]
    }
  }
};
