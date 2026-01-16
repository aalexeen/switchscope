/**
 * Entity Registry - Central configuration for all catalog entities
 * Used by composableFactory to generate type-safe, consistent composables
 * Also used by GenericCatalogView for View layer configuration
 *
 * Each entity configuration includes:
 * - entityName: camelCase plural for state variable and method names
 * - entityNameSingular: camelCase singular for CRUD operation messages
 * - singularDisplay: lowercase display name for singular form in messages
 * - pluralDisplay: lowercase display name for plural form in messages
 * - apiKey: key to access API module from api object
 * - searchFields: array of entity properties to search in
 * - computedFilters: optional object of computed property definitions
 * - listingsComponent: name of the ListingsTable component (for GenericCatalogView)
 * - theme: Tailwind color name for UI theming
 * - themeIntensity: Tailwind color intensity (e.g., '500', '600')
 */

export const entities = {
  componentNatures: {
    entityName: 'componentNatures',
    entityNameSingular: 'componentNature',
    singularDisplay: 'component nature',
    pluralDisplay: 'component natures',
    apiKey: 'componentNatures',
    searchFields: ['name', 'code', 'displayName', 'description'],
    computedFilters: {
      // No additional computed filters beyond total and active
    },
    // View layer configuration
    listingsComponent: 'ComponentNatureListingsTable',
    theme: 'indigo',
    themeIntensity: '500'
  },

  componentCategories: {
    entityName: 'componentCategories',
    entityNameSingular: 'componentCategory',
    singularDisplay: 'component category',
    pluralDisplay: 'component categories',
    apiKey: 'componentCategories',
    searchFields: ['name', 'code', 'displayName', 'description'],
    computedFilters: {
      systemCategories: (category) => category.systemCategory === true,
      userCategories: (category) => category.systemCategory !== true,
      infrastructureCategories: (category) => category.infrastructure === true
    },
    // View layer configuration
    listingsComponent: 'ComponentCategoryListingsTable',
    theme: 'green',
    themeIntensity: '600'
  },

  componentTypes: {
    entityName: 'componentTypes',
    entityNameSingular: 'componentType',
    singularDisplay: 'component type',
    pluralDisplay: 'component types',
    apiKey: 'componentTypes',
    searchFields: ['name', 'code', 'displayName', 'description', 'categoryCode', 'categoryDisplayName'],
    computedFilters: {
      systemTypes: (type) => type.systemType === true,
      userTypes: (type) => type.systemType !== true,
      housingTypes: (type) => type.housingComponent === true,
      connectivityTypes: (type) => type.connectivityComponent === true,
      supportTypes: (type) => type.supportComponent === true,
      moduleTypes: (type) => type.moduleComponent === true,
      networkingTypes: (type) => type.networkingEquipment === true
    },
    // View layer configuration
    listingsComponent: 'ComponentTypeListingsTable',
    theme: 'blue',
    themeIntensity: '600'
  },

  componentStatuses: {
    entityName: 'componentStatuses',
    entityNameSingular: 'componentStatus',
    singularDisplay: 'component status',
    pluralDisplay: 'component statuses',
    apiKey: 'componentStatuses',
    searchFields: ['name', 'code', 'displayName', 'description', 'lifecyclePhase'],
    computedFilters: {
      availableStatuses: (status) => status.available === true,
      operationalStatuses: (status) => status.operational === true,
      physicallyPresentStatuses: (status) => status.physicallyPresent === true,
      inventoryStatuses: (status) => status.inInventory === true,
      installableStatuses: (status) => status.canAcceptInstallations === true,
      attentionRequiredStatuses: (status) => status.requiresAttention === true,
      transitionStatuses: (status) => status.inTransition === true
    },
    // View layer configuration
    listingsComponent: 'ComponentStatusListingsTable',
    theme: 'purple',
    themeIntensity: '600'
  },

  componentModels: {
    entityName: 'componentModels',
    entityNameSingular: 'componentModel',
    singularDisplay: 'component model',
    pluralDisplay: 'component models',
    apiKey: 'componentModels',
    searchFields: [
      'name', 'manufacturer', 'modelNumber', 'modelDesignation',
      'partNumber', 'sku', 'description',
      'componentTypeCode', 'componentTypeDisplayName'
    ],
    computedFilters: {
      discontinuedModels: (model) => model.discontinued === true,
      endOfLifeModels: (model) => model.endOfLife === true,
      verifiedModels: (model) => model.verified === true
    },
    // View layer configuration
    listingsComponent: 'ComponentModelListingsTable',
    theme: 'indigo',
    themeIntensity: '500'
  },

  locationTypes: {
    entityName: 'locationTypes',
    entityNameSingular: 'locationType',
    singularDisplay: 'location type',
    pluralDisplay: 'location types',
    apiKey: 'locationTypes',
    searchFields: ['name', 'code', 'displayName', 'description', 'locationCategory'],
    computedFilters: {
      topLevelTypes: (type) => type.topLevel === true,
      middleLevelTypes: (type) => type.middleLevel === true,
      bottomLevelTypes: (type) => type.bottomLevel === true,
      physicalTypes: (type) => type.physicalLocation === true,
      virtualTypes: (type) => type.virtual === true,
      buildingLikeTypes: (type) => type.buildingLike === true,
      roomLikeTypes: (type) => type.roomLike === true,
      rackLikeTypes: (type) => type.rackLike === true,
      datacenterLikeTypes: (type) => type.datacenterLike === true,
      secureTypes: (type) => type.secureLocation === true
    },
    // View layer configuration
    listingsComponent: 'LocationTypeListingsTable',
    theme: 'teal',
    themeIntensity: '600'
  },

  installationStatuses: {
    entityName: 'installationStatuses',
    entityNameSingular: 'installationStatus',
    singularDisplay: 'installation status',
    pluralDisplay: 'installation statuses',
    apiKey: 'installationStatuses',
    searchFields: ['name', 'code', 'displayName', 'description', 'statusCategory', 'colorCategory'],
    computedFilters: {
      activeStatuses: (status) => status.active !== false,
      physicallyPresentStatuses: (status) => status.physicallyPresent === true,
      operationalStatuses: (status) => status.operational === true,
      attentionRequiredStatuses: (status) => status.requiresAttention === true,
      finalStatuses: (status) => status.finalStatus === true,
      errorStatuses: (status) => status.errorStatus === true,
      autoTransitionStatuses: (status) => status.hasAutoTransition === true,
      progressStatuses: (status) => status.progressStatus === true,
      successStatuses: (status) => status.successStatus === true,
      warningStatuses: (status) => status.warningStatus === true,
      terminalStatuses: (status) => status.terminalStatus === true
    },
    // View layer configuration
    listingsComponent: 'InstallationStatusListingsTable',
    theme: 'amber',
    themeIntensity: '600'
  },

  installableTypes: {
    entityName: 'installableTypes',
    entityNameSingular: 'installableType',
    singularDisplay: 'installable type',
    pluralDisplay: 'installable types',
    apiKey: 'installableTypes',
    searchFields: ['name', 'code', 'displayName', 'description', 'category'],
    computedFilters: {
      activeTypes: (type) => type.active !== false,
      deviceTypes: (type) => type.deviceType === true,
      connectivityTypes: (type) => type.connectivityType === true,
      housingTypes: (type) => type.housingType === true,
      powerTypes: (type) => type.powerType === true,
      rackMountable: (type) => type.requiresRackPosition === true,
      hotSwappableTypes: (type) => type.hotSwappable === true,
      powerManagedTypes: (type) => type.supportsPowerManagement === true,
      environmentalControlTypes: (type) => type.requiresEnvironmentalControl === true,
      highPriorityTypes: (type) => type.highPriority === true,
      lowPriorityTypes: (type) => type.lowPriority === true,
      specialHandlingTypes: (type) => type.requiresSpecialHandling === true
    },
    // View layer configuration
    listingsComponent: 'InstallableTypeListingsTable',
    theme: 'indigo',
    themeIntensity: '600'
  }
};
