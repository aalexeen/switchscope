/**
 * Detail View Configuration for Installation Status
 */

export default {
  tableKey: 'installationStatuses',
  composable: 'useInstallationStatuses',

  sections: {
    workflow: {
      title: 'Status Transitions',
      enabled: true
    },
    basicInfo: {
      fields: [
        { key: 'displayName', label: 'Display Name', fallback: '-' },
        { key: 'code', label: 'Code' },
        { key: 'description', label: 'Description', fallback: 'No description' },
        { key: 'active', label: 'Active', type: 'boolean' },
        { key: 'statusCategory', label: 'Status Category', fallback: '-' },
        { key: 'statusOrder', label: 'Status Order', fallback: '-' },
        { key: 'sortOrder', label: 'Sort Order', fallback: '-' },
        { key: 'colorCategory', label: 'Color Category', fallback: '-' },
        { key: 'iconClass', label: 'Icon Class', fallback: '-' },
        { key: 'physicallyPresent', label: 'Physically Present', type: 'boolean' },
        { key: 'operational', label: 'Operational', type: 'boolean' },
        { key: 'requiresAttention', label: 'Requires Attention', type: 'boolean' },
        { key: 'finalStatus', label: 'Final Status', type: 'boolean' },
        { key: 'errorStatus', label: 'Error Status', type: 'boolean' },
        { key: 'progressStatus', label: 'Progress Status', type: 'boolean' },
        { key: 'successStatus', label: 'Success Status', type: 'boolean' },
        { key: 'warningStatus', label: 'Warning Status', type: 'boolean' },
        { key: 'terminalStatus', label: 'Terminal Status', type: 'boolean' },
        { key: 'allowsStatusChange', label: 'Allows Status Change', type: 'boolean' },
        { key: 'autoTransitionMinutes', label: 'Auto Transition (Minutes)', fallback: '-' },
        { key: 'hasAutoTransition', label: 'Has Auto Transition', type: 'boolean' },
        { key: 'urgencyLevel', label: 'Urgency Level', fallback: '-' },
        { key: 'allowsEquipmentOperation', label: 'Allows Equipment Operation', type: 'boolean' },
        { key: 'allowsMaintenance', label: 'Allows Maintenance', type: 'boolean' },
        { key: 'requiresDocumentation', label: 'Requires Documentation', type: 'boolean' },
        { key: 'notifiesStakeholders', label: 'Notifies Stakeholders', type: 'boolean' }
      ]
    }
  }
};
