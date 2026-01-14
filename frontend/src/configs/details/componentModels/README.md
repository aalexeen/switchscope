# Component Models Detail View Configurations

This directory contains type-specific detail view configurations for component models in the catalog.

## 📁 File Structure

```
componentModels/
├── _baseFields.js               # Shared field definitions and utilities
├── switchModel.detail.js        # Switch model configuration (81 fields, 15 sections)
├── routerModel.detail.js        # Router model configuration (73 fields, 16 sections)
├── accessPointModel.detail.js   # Access point configuration (76 fields, 16 sections)
├── rackModel.detail.js          # Rack model configuration (66 fields, 15 sections)
└── README.md                    # This file
```

## 🎯 Architecture

### Design Principles

1. **Type-Specific Views** - Each model type has its own optimized configuration
2. **Priority-Based Organization** - Sections ordered from most to least important
3. **Progressive Disclosure** - Critical info visible, details in collapsible sections
4. **Shared Base Fields** - Common sections extracted to `_baseFields.js`
5. **Conditional Rendering** - Optional sections shown only when relevant

### Field Organization

Each configuration organizes fields into **priority tiers**:

#### **Tier 1: Always Visible (Priority 1-3)**
- Overview (12 fields)
- Classification (4-7 fields)
- Key Specifications (6-9 fields)
- **Total: 22-28 fields visible immediately**

#### **Tier 2: Collapsible Technical Details (Priority 4-9)**
- Type-specific features
- Performance metrics
- Physical specifications
- Power & environmental
- Management capabilities
- **Total: 30-50 fields in collapsible sections**

#### **Tier 3: Business & Metadata (Priority 10-17)**
- Additional specifications (deviceSpecs map - conditional)
- Product information
- Lifecycle & dates
- Documentation
- Certifications
- Pricing
- Internal notes
- **Total: 26-31 fields in collapsible sections**

## 📊 Configuration Breakdown

### Switch Model (`switchModel.detail.js`)

**Total Fields:** 82 fields in 16 sections

**Sections:**
1. Overview (12 fields) - Always visible
2. Switch Classification (7 fields) - Always visible
3. Port Configuration (9 fields) - Always visible
4. PoE Specifications (4 fields) - Conditional, collapsible
5. Physical Specifications (4 fields) - Collapsible
6. Power & Environmental (14 fields) - Collapsible
7. Performance Metrics (5 fields) - Collapsible
8. Network Features (8 fields) - Collapsible
9. Management & Monitoring (6 fields) - Collapsible
10. Additional Specifications (1 field - map) - Conditional, collapsible
11. Product Information (5 fields) - Collapsible
12. Lifecycle & Dates (5 fields) - Collapsible
13. Documentation & Support (3 fields) - Collapsible
14. Certifications & Compliance (2 fields) - Collapsible
15. Pricing (3 fields) - Collapsible
16. Internal Notes & Metadata (7 fields) - Collapsible

**Unique Features:**
- Conditional PoE section (shown only if `supportsPoe === true`)
- Conditional Additional Specifications section (shown only if `deviceSpecs` has data)
- Port summary with breakdown by speed
- Network features (VLANs, QoS, ACLs, etc.)

---

### Router Model (`routerModel.detail.js`)

**Total Fields:** 74 fields in 17 sections

**Sections:**
1. Overview (12 fields) - Always visible
2. Router Classification (4 fields) - Always visible
3. Interface Configuration (6 fields) - Always visible
4. Routing Capabilities (3 fields) - Collapsible
5. Security & VPN (4 fields) - Collapsible
6. WAN Interfaces (1 field - array) - Collapsible
7. Physical Specifications (4 fields) - Collapsible
8. Performance Metrics (4 fields) - Collapsible
9. Power & Environmental (13 fields) - Collapsible
10. Additional Specifications (1 field - map) - Conditional, collapsible
11. Management & Monitoring (6 fields) - Collapsible
12. Product Information (5 fields) - Collapsible
13. Lifecycle & Dates (5 fields) - Collapsible
14. Documentation & Support (3 fields) - Collapsible
15. Certifications & Compliance (2 fields) - Collapsible
16. Pricing (3 fields) - Collapsible
17. Internal Notes & Metadata (7 fields) - Collapsible

**Unique Features:**
- Routing protocol support (BGP, OSPF)
- VPN capabilities and tunnel limits
- Firewall throughput metrics
- WAN interface types array
- Conditional Additional Specifications section (shown only if `deviceSpecs` has data)

---

### Access Point Model (`accessPointModel.detail.js`)

**Total Fields:** 77 fields in 17 sections

**Sections:**
1. Overview (12 fields) - Always visible
2. WiFi Specifications (5 fields) - Always visible
3. Frequency Band Support (4 fields) - Always visible
4. Coverage Specifications (4 fields) - Collapsible
5. Security Features (3 fields) - Collapsible
6. Advanced WiFi Features (4 fields) - Collapsible
7. Management Capabilities (6 fields) - Collapsible
8. PoE Specifications (2 fields) - Conditional, collapsible
9. Physical Specifications (5 fields) - Collapsible
10. Additional Specifications (1 field - map) - Conditional, collapsible
11. Power & Environmental (11 fields) - Collapsible
12. Product Information (5 fields) - Collapsible
13. Lifecycle & Dates (5 fields) - Collapsible
14. Documentation & Support (3 fields) - Collapsible
15. Certifications & Compliance (2 fields) - Collapsible
16. Pricing (3 fields) - Collapsible
17. Internal Notes & Metadata (7 fields) - Collapsible

**Unique Features:**
- WiFi standard and generation detection
- Band support summary (2.4GHz, 5GHz, 6GHz)
- Coverage area metrics
- WiFi 6/6E/7 indicators
- Mesh, MU-MIMO, Beamforming support
- Controller vs. cloud management
- Conditional Additional Specifications section (shown only if `deviceSpecs` has data)

---

### Rack Model (`rackModel.detail.js`)

**Total Fields:** 66 fields in 15 sections

**Sections:**
1. Overview (12 fields) - Always visible
2. Rack Classification (5 fields) - Always visible
3. Dimensions & Capacity (7 fields) - Always visible
4. Rack Type Characteristics (5 fields) - Collapsible
5. Physical Constraints (2 fields) - Collapsible
6. Environmental Features (6 fields) - Collapsible
7. Security Features (2 fields) - Collapsible
8. Installation Characteristics (1 field) - Collapsible
9. Environmental Specifications (6 fields) - Collapsible
10. Product Information (3 fields) - Collapsible
11. Lifecycle & Dates (5 fields) - Collapsible
12. Documentation & Support (3 fields) - Collapsible
13. Certifications & Compliance (2 fields) - Collapsible
14. Pricing (3 fields) - Collapsible
15. Internal Notes & Metadata (7 fields) - Collapsible

**Unique Features:**
- Rack size categories (compact, half-height, full-height)
- Mounting type indicators (wall, floor, enclosed, open frame)
- Weight and power capacity constraints
- Ventilation, cooling, and cable management features
- Door types and security features

---

## 🔧 Base Fields Utility (`_baseFields.js`)

Provides reusable section definitions and helper functions:

### Exported Sections
- `overviewSection` - Common overview fields (12 fields)
- `productInformationSection` - Product details (5 fields)
- `lifecycleDatesSection` - Dates and warranty (5 fields)
- `documentationSupportSection` - Documentation URLs (3 fields)
- `certificationsComplianceSection` - Certifications (2 fields)
- `pricingSection` - Pricing information (3 fields)
- `internalNotesSection` - Metadata and notes (7 fields)
- `physicalSpecificationsSection` - Physical specs for devices (4 fields)
- `powerEnvironmentalSection` - Power and environment for devices (13 fields)
- `managementMonitoringSection` - Management for devices (6 fields)

### Helper Functions

#### `createBaseSections(startPriority)`
Creates base sections with dynamic priority assignment.

```javascript
import { createBaseSections } from './_baseFields';

const sections = {
  ...mySpecificSections,
  ...createBaseSections(10) // Assigns priorities 10-15
};
```

#### `createDeviceBaseSections(startPriority)`
Creates device-specific base sections (physical, power, management).

```javascript
import { createDeviceBaseSections } from './_baseFields';

const sections = {
  ...myDeviceSections,
  ...createDeviceBaseSections(5) // Assigns priorities 5-7
};
```

#### `mergeSections(specificSections, baseSections)`
Merges type-specific and base sections.

```javascript
import { mergeSections, createBaseSections } from './_baseFields';

const sections = mergeSections(
  mySwitchSections,
  createBaseSections(10)
);
```

## 📝 Field Type Reference

All configurations support these field types:

| Type | Description | Example |
|------|-------------|---------|
| `heading` | Large prominent text | Model designation |
| `badge` | Colored status badge | Lifecycle status |
| `boolean` | ✓/✗ icon | Active, stackable |
| `summary` | Computed summary text | Port summary, power summary |
| `url` | Clickable link | Documentation URLs |
| `currency` | Formatted price | MSRP, list price |
| `date` | Formatted date | Release date |
| `datetime` | Formatted date+time | Created at, updated at |
| `textarea` | Multi-line text | Internal notes |
| `array` | Array of values | WAN interface types |
| `map` | Key-value pairs display | Device specifications (deviceSpecs) |

## 🎨 Section Properties

Each section supports these properties:

```javascript
{
  title: 'Section Title',           // Display title
  priority: 1,                       // Sort order (1 = first)
  collapsible: true,                 // Can be collapsed?
  condition: (model) => boolean,     // Show section conditionally
  fields: [...]                      // Field definitions
}
```

### Conditional Rendering Example

```javascript
poeSpecifications: {
  title: 'PoE Specifications',
  collapsible: true,
  condition: (model) => model.supportsPoe === true,  // Only show if PoE supported
  fields: [...]
}
```

## 🚀 Usage

### In Detail View Component

```javascript
import switchModelConfig from '@/configs/details/componentModels/switchModel.detail';
import routerModelConfig from '@/configs/details/componentModels/routerModel.detail';
import accessPointModelConfig from '@/configs/details/componentModels/accessPointModel.detail';
import rackModelConfig from '@/configs/details/componentModels/rackModel.detail';

// Select config based on discriminator type
const configs = {
  'SWITCH_MODEL': switchModelConfig,
  'ROUTER_MODEL': routerModelConfig,
  'ACCESS_POINT_MODEL': accessPointModelConfig,
  'RACK_MODEL': rackModelConfig
};

const config = configs[model.discriminatorType] || switchModelConfig;
```

### Rendering Sections

```javascript
// Sort sections by priority
const sortedSections = Object.entries(config.sections)
  .sort(([, a], [, b]) => (a.priority || 99) - (b.priority || 99));

// Render each section
sortedSections.forEach(([key, section]) => {
  // Check condition if present
  if (section.condition && !section.condition(model)) {
    return; // Skip this section
  }

  // Render section with fields
  renderSection(section);
});
```

## 📈 Statistics

| Model Type | Total Fields | Visible by Default | Sections | Conditional Sections |
|------------|--------------|-------------------|----------|---------------------|
| Switch | 82 | 28 | 16 | 2 (PoE, Additional Specs) |
| Router | 74 | 22 | 17 | 1 (Additional Specs) |
| Access Point | 77 | 21 | 17 | 2 (PoE, Additional Specs) |
| Rack | 66 | 24 | 15 | 0 |

## 🎯 Benefits

1. **User-Focused** - Shows only relevant information per device type
2. **Progressive Disclosure** - Critical info first, details on demand
3. **Maintainable** - Shared base sections reduce duplication
4. **Scalable** - Easy to add new model types
5. **Flexible** - Conditional sections adapt to data
6. **Organized** - Logical grouping by functional area
7. **Comprehensive** - All available fields documented and accessible

## 🔄 Adding New Model Types

1. Create new config file: `{modelType}.detail.js`
2. Import base sections from `_baseFields.js`
3. Define type-specific sections (priority 1-9)
4. Merge with base sections (priority 10+)
5. Add discriminator mapping in detail view component

Example:

```javascript
// firewallModel.detail.js
import { createBaseSections, createDeviceBaseSections } from './_baseFields';

export default {
  tableKey: 'componentModels',
  composable: 'useComponentModels',

  sections: {
    overview: { /* ... */ },

    // Firewall-specific sections (priority 2-9)
    firewallCapabilities: { priority: 2, /* ... */ },
    securityFeatures: { priority: 3, /* ... */ },

    // Device base sections (priority 5-7)
    ...createDeviceBaseSections(5),

    // Common base sections (priority 10-15)
    ...createBaseSections(10)
  }
};
```

## 📚 Implementation Status

All steps completed! ✅

1. ✅ **Detail view component created** - `ComponentModelDetailView.vue` handles all model types with discriminator-based config selection
2. ✅ **Added to detail view registry** - All 4 model configs registered in `detailViewRegistry.js`
3. ✅ **Routing configured** - Route `/catalog/component-models/:id` maps to ComponentModelDetailView
4. ✅ **Navigation from table view** - View action in table routes to detail page
5. ✅ **Field type renderers implemented** - `FieldRenderer.vue` supports all 11 field types including new `map` type for deviceSpecs

### Components

- **`ComponentModelDetailView.vue`** - Main detail view component with type detection, section sorting, collapsible sections, and conditional rendering
- **`FieldRenderer.vue`** - Renders 11 field types: heading, badge, boolean, summary, url, currency, date, datetime, textarea, array, map
- **`GenericDetailView.vue`** - Generic detail view for other catalog entities (componentCategory, componentNature, etc.)
