# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**SwitchScope** is a comprehensive Network Infrastructure Management System built with Java 21, Spring Boot 3.5, Hibernate 7.2, and PostgreSQL. It manages network devices (switches, routers, access points), physical housing (racks), connectivity (cables, patch panels), and their installations across hierarchical physical locations.

**License**: Business Source License 1.1 (free for healthcare, education, government; becomes Apache 2.0 on Jan 1, 2029)

## Repository Structure

```
switchscope/
├── backend/                           # Java Spring Boot backend
│   ├── src/main/java/net/switchscope/
│   │   ├── model/                     # JPA entities
│   │   ├── repository/                # Data access layer
│   │   ├── service/                   # Business logic
│   │   ├── web/                       # REST controllers
│   │   ├── mapper/                    # MapStruct DTO mappers
│   │   ├── to/                        # Transfer Objects (DTOs)
│   │   ├── config/                    # Spring configuration
│   │   ├── error/                     # Exception handling
│   │   └── validation/                # Custom validators
│   ├── src/main/resources/
│   │   ├── db/changelog/              # Liquibase migrations
│   │   │   ├── init/                  # DDL changesets
│   │   │   ├── fill/                  # DML changesets
│   │   │   └── csv/                   # Seed data
│   │   └── config.properties          # App configuration (gitignored)
│   ├── src/test/                      # Tests with Testcontainers
│   ├── pom.xml                        # Maven configuration
│   └── mvnw, mvnw.cmd                 # Maven wrapper
├── frontend/                          # Vue.js 3 frontend
│   ├── src/
│   │   ├── api/                       # API client modules (axios)
│   │   ├── components/                # Reusable Vue components
│   │   │   ├── table/                 # Generic table system components
│   │   │   │   ├── cells/             # Cell renderer components
│   │   │   │   ├── GenericListingsTable.vue  # Table wrapper component
│   │   │   │   └── GenericListTable.vue      # Table row component
│   │   │   └── common/                # Common UI components
│   │   ├── composables/               # Vue 3 Composition API composables
│   │   │   ├── composableFactory.js   # Factory for creating catalog composables
│   │   │   ├── entityRegistry.js      # Catalog entity configurations
│   │   │   └── use*.js                # Entity-specific composables
│   │   ├── configs/                   # Configuration files
│   │   │   └── tables/                # Table configuration system
│   │   │       ├── tableRegistry.js   # Central table registry
│   │   │       └── *.config.js        # Per-table configurations
│   │   ├── plugins/                   # Vue plugins configuration
│   │   ├── router/                    # Vue Router configuration
│   │   ├── services/                  # Business logic services
│   │   ├── utils/                     # Utility functions
│   │   ├── views/                     # Page-level components (routed views)
│   │   │   ├── GenericTableView.vue   # Universal table view for ALL tables
│   │   │   ├── DashboardView.vue      # Main dashboard
│   │   │   ├── catalog/               # Catalog views (deprecated, moved to _deprecated)
│   │   │   ├── component/             # Component views (deprecated, moved to _deprecated)
│   │   │   ├── installation/          # Installation tracking views
│   │   │   ├── location/              # Location management views
│   │   │   └── port/                  # Port management views
│   │   ├── App.vue                    # Root Vue component
│   │   └── main.js                    # Application entry point
│   ├── public/                        # Static assets
│   ├── dist/                          # Production build output
│   ├── package.json                   # NPM dependencies
│   ├── vite.config.js                 # Vite configuration (dev server on port 3001)
│   ├── tailwind.config.js             # Tailwind CSS configuration
│   └── postcss.config.js              # PostCSS configuration
├── validate_schema.py                 # Database schema validator
├── .local.props                       # Local DB config (gitignored)
├── .mcp.json                          # MCP server configuration
├── CLAUDE.md                          # This file
└── README.md                          # Project documentation
```

## Common Development Commands

### Backend (Maven)

```bash
# Navigate to backend
cd backend

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test                    # Unit tests
./mvnw integration-test        # Integration tests
./mvnw verify                  # Full test suite

# Build
./mvnw clean package           # Build JAR
./mvnw clean install           # Install to local Maven repo

# Liquibase database migrations
./mvnw liquibase:update        # Apply migrations
./mvnw liquibase:status        # Check migration status
./mvnw liquibase:rollback      # Rollback last change
./mvnw liquibase:dropAll      # Drop All tables

# Code quality
./mvnw checkstyle:check        # Check code style
./mvnw spotbugs:check          # Static analysis
```

### Frontend (NPM/Vite)

```bash
# Navigate to frontend
cd frontend

# Install dependencies
npm install

# Run development server (http://localhost:3001)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run mock JSON server (development only)
npm run server
```

### Database Schema Validation

```bash
# Validate database schema matches Liquibase definitions
python3 validate_schema.py
```

This script:
- Compares actual PostgreSQL schema with Liquibase YAML definitions
- Identifies missing tables, columns, type mismatches, and constraint issues
- Provides SQL remediation commands
- Uses configuration from `.local.props`
- Exit codes: 0 (pass), 1 (critical issues), 2 (warnings only)

### Configuration Files

- **Backend config**:
  - `backend/src/main/resources/application.yaml` - Main Spring Boot configuration
  - `backend/src/main/resources/application-local.yaml` - Local overrides (gitignored, optional)
  - Database credentials via environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
  - Encryption key via environment variable: `APP_ENCRYPTION_KEY` (default: `change-me-in-production-32chars`)
- **Database credentials**: `.local.props` (for validation script and MCP server, gitignored)
- **MCP server config**: `.mcp.json` (PostgreSQL, context7, sequential-thinking, playwright)

**Note**: Local configuration files are gitignored. Use environment variables or create `application-local.yaml` for local overrides.

## High-Level Architecture

### Domain Model

SwitchScope uses a **rich domain model** with deep inheritance hierarchies:

#### Entity Hierarchy

```
BaseEntity (UUID v7, timestamps)
└── NamedEntity (name, description)
    └── BaseCodedEntity (code, displayName, active, sortOrder)
        └── Catalog entities (ComponentTypeEntity, LocationTypeEntity, etc.)

BaseEntity
└── NamedEntity
    └── Component (abstract, Single Table Inheritance)
        ├── Device (abstract)
        │   └── HasPortsImpl (@MappedSuperclass for devices with ports)
        │       ├── NetworkSwitch (PoE, stacking, VLAN management)
        │       ├── Router (routing protocols, WAN interfaces)
        │       ├── AccessPoint (SSIDs, wireless bands)
        │       └── PatchPanel (passive, port management)
        ├── Rack (housing for equipment)
        └── CableRun, Connector (connectivity)
```

#### Core Domain Entities

- **Component**: Abstract base for all infrastructure (switches, racks, cables)
  - Uses **Single Table Inheritance** with `component_class` discriminator
  - Delegates behavior to `ComponentTypeEntity` (type-driven behavior pattern)
  - Self-referential parent-child relationships for modular equipment
  - Key relationships: ComponentType, ComponentStatus, Installation, ComponentModel

- **Location**: Hierarchical physical locations (building → floor → room → rack)
  - Self-referential parent-child hierarchy
  - Type-driven behavior via `LocationTypeEntity`
  - Tracks infrastructure (power, UPS, cooling, rack units)

- **Installation**: Links Component to Location (or Component to parent Component)
  - Tracks physical installation with rack positioning
  - Status tracking via `InstallationStatusEntity`
  - Audit fields: installedBy, removedBy, timestamps

- **Port**: Abstract base for network ports (Single Table Inheritance)
  - Implementations: `EthernetPort`, `FiberPort`
  - Comprehensive attributes: status, speed, VLAN, PoE, traffic stats
  - One-to-one with `Connector` for physical cabling

### Package Structure

```
net.switchscope/
├── model/              # Domain entities (Component, Location, Port, Installation)
│   ├── component/      # Component hierarchy (device, housing, connectivity)
│   ├── location/       # Location hierarchy
│   ├── installation/   # Installation tracking
│   └── port/           # Network ports (Ethernet, Fiber)
├── repository/         # Spring Data JPA repositories (extends BaseRepository)
├── service/            # Business logic (implements CrudService interface)
├── web/                # REST controllers (extends AbstractCrudController)
├── mapper/             # MapStruct DTO mappers (implements BaseMapper)
├── to/                 # Transfer Objects (DTOs for API)
├── config/             # Spring configuration (Security, OpenAPI, etc.)
├── error/              # Exception handling (NotFoundException, etc.)
├── validation/         # Custom validators (@NoHtml, etc.)
└── util/               # Utilities (PasswordEncryptionUtil, etc.)
```

### Architectural Patterns

1. **Layered Architecture**: Entity → Repository → Service → Controller
   - Clear separation of concerns
   - Transactional boundaries at service layer
   - DTOs separate API from domain model

2. **Type-Driven Behavior**:
   - `ComponentTypeEntity` defines component capabilities at runtime
   - `LocationTypeEntity` defines hierarchy rules
   - Allows configuration changes without code deployment

3. **Generic Base Classes**:
   - `BaseRepository<T>` - adds `getExisted()`, `deleteExisted()`
   - `CrudService<T>` - generic CRUD operations interface
   - `AbstractCrudController<T>` - standard REST endpoints
   - `BaseMapper<E, T>` - MapStruct entity ↔ DTO conversion

4. **Catalog Pattern**:
   - `ComponentModel` catalog for manufacturer specifications (abstract base with Single Table Inheritance)
   - `ComponentNatureEntity` catalog for component nature classifications (active, passive, hybrid, etc.)
   - Separates product catalog from component instances
   - Concrete models: `SwitchModel`, `RouterModel`, `RackModelEntity`, `AccessPointModel`, `PatchPanelModel`, `CableRunModel`
   - **Important**: Discriminator values in entity classes must match CSV data exactly

### Database Management

#### Liquibase Structure

```
backend/src/main/resources/db/changelog/
├── db.changelog-master.yaml       # Master changelog (includes all others)
├── init/                          # DDL: table creation (01-users to 90-fk)
├── fill/                          # DML: data population
└── csv/                           # CSV seed data for catalogs
```

**Execution Order**: Master file includes init files first (DDL), then fill files (DML)

**File Naming Convention**: Sequential numbering determines execution order (e.g., `01-users.yaml`, `10-component-categories-catalog.yaml`)

#### Key Database Features

1. **UUID v7 Primary Keys**: Time-ordered UUIDs via Hibernate 7.2's `@UuidGenerator(style = VERSION_7)`
   - Better database performance than random UUIDs
   - Defined in `BaseEntity`, inherited by all entities

2. **Single Table Inheritance**:
   - Components table stores all component types
   - Discriminator: `component_class` (RACK, NETWORK_SWITCH, ROUTER, etc.)
   - Allows polymorphic queries, simpler JOINs

3. **CSV-Based Seed Data**:
   - Reference data in CSV files (component types, statuses, location types)
   - Loaded via Liquibase `loadData` changesets
   - Pre-defined UUID v7 values for referential integrity

4. **Automatic Timestamps**: `@CreationTimestamp`, `@UpdateTimestamp` on BaseEntity

5. **Schema Naming**: All tables and columns use `switchscope` schema with snake_case naming

## Important Implementation Notes

### Key Architectural Decisions

1. **Single Table Inheritance for Components**: All component types (switches, racks, cables) stored in one table
   - **Pros**: Simple queries, good performance for polymorphic associations
   - **Cons**: Many nullable columns, less data integrity enforcement
   - **Trade-off**: Chosen for flexibility and query simplicity over storage optimization

2. **UUID v7 over UUID v4**: Time-ordered UUIDs provide better database index performance
   - Sequential nature reduces index fragmentation
   - Still globally unique like UUID v4
   - Requires Hibernate 7.2+ support

3. **Type-Driven Behavior**: Component capabilities defined by `ComponentTypeEntity` not code
   - Allows runtime configuration changes
   - New component types can be added via database without code deployment
   - Behavioral methods delegated to type entity

4. **Separation of Catalog and Instances**:
   - `ComponentModel` = product catalog (manufacturer specifications)
   - `Component` = actual physical instances
   - Enables tracking multiple instances of same model

### Working with Components

When adding new component types:
1. Extend appropriate base class (`Device`, `HasPortsImpl`, or `Component` directly)
2. Add discriminator value to `@DiscriminatorValue` annotation
3. Define catalog entry in CSV file: `13-component-types-catalog.csv`
4. Create corresponding `ComponentModel` subclass if needed
5. Add Liquibase changelog for type-specific tables
6. Create Service, Repository, Controller, Mapper, and TO classes

**Example**: To add a new device type like "Firewall":
- Create `Firewall.java` extending `Device` or `HasPortsImpl`
- Add `@DiscriminatorValue("FIREWALL")`
- Create `FirewallService`, `FirewallRepository`, `FirewallController`, `FirewallMapper`, `FirewallTo`
- Update CSV with new component type entry
- Create Liquibase changelogs: `init/XX-component-firewall.yaml`, `fill/XX-fill-firewalls.yaml`

### Working with Entities

- All entities extend `BaseEntity` (or `NamedEntity` for named entities)
- Use `@NoHtml` validation to prevent HTML injection
- Override `equals()`, `hashCode()`, `toString()` when adding fields
- Prefer `@ManyToOne(fetch = LAZY)` for relationships
- Use `@CreationTimestamp`, `@UpdateTimestamp` for audit timestamps

### Working with Liquibase

- **Adding tables**: Create YAML in `init/`, add include in master changelog (maintaining sequential order)
- **Adding data**: Create CSV in `csv/`, create fill YAML in `fill/`
- **Testing migrations**: Run `./mvnw liquibase:update`, then `python3 validate_schema.py`
- **Column naming**: Use snake_case in database, camelCase in Java
- **Always use**: `relativeToChangelogFile: true` in includes
- **Fresh start**: Use `./mvnw liquibase:dropAll` then `./mvnw liquibase:update` to rebuild from scratch
- **CSV format**: Use semicolon (`;`) as separator, single quotes (`'`) as quotchar in loadData changesets
- **Encrypting passwords in CSV files**:
  - Use `PasswordEncryptionUtil` to generate encrypted passwords
  - Run: `java -cp target/classes net.switchscope.util.PasswordEncryptionUtil`
  - Or run `main()` method from IDE
  - Copy generated encrypted values to CSV files
  - Utility loads encryption key from `application-local.yaml` or uses default

### Security

- **Spring Security** with HTTP Basic authentication (stateless sessions)
- **User/Role model** for RBAC (Role-Based Access Control)
  - Admin endpoints: `/api/admin/**` require `ADMIN` role
  - API endpoints: `/api/**` require authentication
  - Public endpoints: Swagger UI, API docs, favicon
- **Password encryption** via `EncryptedStringConverter` for entity fields
  - Uses AES/GCM/NoPadding encryption algorithm
  - Encryption key configured in `application.yaml`: `app.encryption.key`
  - Device passwords stored encrypted in database
- **PasswordEncryptionUtil** utility class for encrypting passwords in CSV seed data
  - Standalone utility: `backend/src/main/java/net/switchscope/util/PasswordEncryptionUtil.java`
  - Run `main()` method to generate encrypted passwords for CSV files
  - Loads encryption key from `application-local.yaml` or uses default
  - Uses same encryption algorithm as `EncryptedStringConverter`
- **CORS configuration** allows localhost and local network origins
- **Custom validators**: `@NoHtml` prevents HTML injection attacks
- **Authentication**: HTTP Basic with `UserDetailsService` backed by database

### MapStruct Mapping

- All mappers extend `BaseMapper<Entity, To>`
- Configuration in `MapStructConfig`: `componentModel = "spring"`
- Use `@Mapping` to handle custom field mappings
- Ignore audit fields in `updateFromTo()` mappings
- Handle relationships: map IDs vs full objects

### Testing

- **Test structure**: `backend/src/test/java/net/switchscope/`
- **Test base classes** for reusable test logic:
  - `AbstractCrudControllerTest` - Base class for CRUD controller tests
  - `AbstractCatalogControllerTest` - Base class for catalog controller tests
  - `CrudSmokeControllerTest` - Smoke tests for basic CRUD operations
- **Integration tests**: Use `@SpringBootTest` with `TestcontainersConfiguration`
  - Testcontainers provides PostgreSQL, Kafka, Redis containers for testing
- **Controller tests**: Use `@WebMvcTest` with `MockMvc` for REST controller testing
  - Custom `TestSecurityConfig` disables security for easier testing
- **Test utilities**: `TestEntity` and `TestEntityController` for testing base classes
- **Spring Boot DevTools** enabled for development-time testing

### Frontend Implementation Notes

#### API Module Pattern (Critical)

**ALWAYS use the factory function pattern** when creating new API modules:

```javascript
// ✅ CORRECT Pattern
const baseURL = "your/endpoint/path";

export default function ({components}) {  // Receives axios instance
    return {
        getAll() {
            return components.get(baseURL);
        },
        get(id) {
            return components.get(`${baseURL}/${id}`);
        }
        // ... other methods
    };
}
```

**Common Mistakes to Avoid**:
- ❌ Direct import of axios instance: `import instance from './instance'`
- ❌ Named exports instead of default: `export const myAPI = { ... }`
- ❌ Async/await in module definition (handle in composables)
- ❌ Extracting `.data` in module (return full axios response)

**Module Registration Checklist**:
1. Create module file with factory function
2. Add import to `api/index.js`
3. Add to exports object with instance: `myModule: myModule(instance)`
4. Use in composables via: `import api from '@/api'`
5. Extract data in composable: `response.data`

#### Configuration-Driven Table System

**CRITICAL**: The frontend uses a **unified, configuration-driven system** for ALL tables (catalogs and entities). This eliminates ~72% code duplication and provides consistent UI/UX.

**Single Universal View**:
- **`GenericTableView.vue`** (`frontend/src/views/`) - ONE view for ALL tables
  - Loads configuration dynamically via `route.meta.tableKey`
  - Handles CRUD operations uniformly
  - Search, filtering, column visibility toggle
  - Delete confirmation dialogs with system item protection

**Configuration Structure**:
```
frontend/src/configs/tables/
├── tableRegistry.js              # Central registry (tableKey → config & composable)
├── componentNatures.config.js    # Component Natures catalog
├── componentTypes.config.js      # Component Types catalog
├── componentStatuses.config.js   # Component Statuses catalog
├── componentModels.config.js     # Component Models catalog
├── componentCategories.config.js # Component Categories catalog
├── locationTypes.config.js       # Location Types catalog
├── installationStatuses.config.js # Installation Statuses catalog
├── installableTypes.config.js    # Installable Types catalog
└── components.config.js          # Components entity
```

**Generic Components**:
```
frontend/src/components/table/
├── GenericListingsTable.vue   # Wrapper (stats, refresh, column toggle)
├── GenericListTable.vue       # Row renderer with dynamic cells
└── cells/                     # Cell renderer components (8 types)
    ├── CellText.vue           # Plain text
    ├── CellCode.vue           # Monospace code
    ├── CellIconText.vue       # Icon + text (catalogs)
    ├── CellStatusBadge.vue    # Active/Inactive badge
    ├── CellBooleanIcon.vue    # Boolean check/times icons
    ├── CellBadge.vue          # Colored badges
    └── CellActions.vue        # View/Edit/Delete icons
```

**Table Configuration Example**:
```javascript
// configs/tables/componentNatures.config.js
export default {
  entityName: 'Component Nature',
  entityNamePlural: 'Component Natures',
  icon: 'pi-box',
  iconColor: 'text-blue-600',
  composable: 'useComponentNatures',
  searchFields: ['name', 'code', 'displayName'],
  theme: 'indigo',
  themeIntensity: '500',
  columns: [
    { key: 'name', label: 'Name', type: 'icon-text', visible: true },
    { key: 'code', label: 'Code', type: 'code', visible: true },
    { key: 'active', label: 'Status', type: 'status-badge', visible: true },
    { key: 'requiresManagement', label: 'Mgmt', type: 'boolean-icon', visible: false },
    { key: 'actions', label: 'Actions', type: 'actions', visible: true, actions: ['view', 'edit', 'delete'] }
  ]
};
```

**Cell Renderer Types**:
| Type | Component | Use Case |
|------|-----------|----------|
| `text` | CellText | Names, descriptions |
| `code` | CellCode | Codes, serial numbers |
| `icon-text` | CellIconText | Catalog items with icons |
| `status-badge` | CellStatusBadge | Active/Inactive status |
| `boolean-icon` | CellBooleanIcon | Boolean flags |
| `badge` | CellBadge | Categories, tags |
| `text-truncate` | CellText | Long text with tooltip |
| `actions` | CellActions | CRUD action buttons |

**Router Integration**:
```javascript
{
  path: "/catalog/component-natures",
  component: GenericTableView,
  meta: {
    requiresAuth: true,
    roles: ['USER', 'ADMIN'],
    tableKey: 'componentNatures'  // Lookup key in tableRegistry
  }
}
```

**Key Features**:
1. **Delete Confirmation**: Confirmation dialog before deletion (in CellActions.vue)
2. **System Protection**: System items (systemType=true) cannot be deleted
3. **Column Toggle**: "Show All Columns" button for expandable columns
4. **Unified Search**: Real-time case-insensitive search across configured fields
5. **Toast Notifications**: Success/error feedback for all operations

**Benefits**:
- ✅ 72% code reduction (~2,840 → ~890 lines)
- ✅ Zero code duplication
- ✅ Consistent UI/UX across all tables
- ✅ Add new table = create config file only
- ✅ Bug fixes apply to all tables automatically

#### Composables Pattern

**Use Singleton Pattern** for shared data:

```javascript
// Shared state OUTSIDE the composable function
const items = ref([]);
const isLoading = ref(false);
const isInitialized = ref(false);

export function useMyData() {
  // Only fetch if not initialized
  const fetchItems = async (force = false) => {
    if (isInitialized.value && !force) return;
    // ... fetch logic
  };

  return { items, fetchItems, ... };
}
```

**When to use singleton**:
- Catalog data (component types, statuses, etc.)
- Data that should be shared across views
- Expensive data that shouldn't be re-fetched

**When NOT to use singleton**:
- Form state (local to component)
- Temporary UI state
- User-specific filters

#### Routing Best Practices

- Always add `meta: { requiresAuth: true }` for protected routes
- Specify roles: `roles: ['USER', 'ADMIN']`
- Import views at top of router file (not lazy loading for now)
- Use consistent naming: route `name` should match component name (kebab-case)

## Technology Stack

### Backend
- **Java 21** (LTS)
- **Spring Boot 3.5.5** (Web, Security, Data JPA, Actuator, WebSocket)
- **Hibernate ORM 7.2.0** (upgraded for UUID v7 support)
- **Jakarta Persistence API 3.2.0**
- **PostgreSQL 13+** with switchscope schema
- **Liquibase 4.30.0** for schema migrations
- **MapStruct 1.5.5** for DTO mapping
- **Lombok** for boilerplate reduction
- **SNMP4J 3.9.6** for SNMP monitoring
- **SSHJ 0.40.0** for SSH device communication
- **ExpectIt 0.9.0** for CLI automation
- **Testcontainers 1.19.3** for integration testing
- **SpringDoc OpenAPI 2.7.0** for API documentation

### Frontend
- **Vue.js 3.5.13** with Composition API
- **Vue Router 4.5.0** for client-side routing
- **Vite 6.0.5** for fast build tooling
- **Axios 1.7.9** for HTTP client
- **Tailwind CSS 3.4.17** for utility-first styling
- **PrimeIcons 7.0.0** for iconography
- **Vue Toastification 2.0.0** for notifications
- **Vue DevTools 7.6.8** for development debugging

## Frontend Architecture

### Project Structure

The frontend follows a standard Vue.js 3 application structure with Composition API:

```
frontend/src/
├── api/                    # API client layer
│   ├── instance.js         # Axios instance configuration (base URL, interceptors)
│   ├── index.js            # API exports barrel
│   ├── authentication.js   # Auth API endpoints
│   ├── components.js       # Components API endpoints
│   ├── componentNatures.js # Component Natures catalog API
│   ├── componentModels.js  # Component Models catalog API
│   └── blockedmacs.js      # Legacy API (to be migrated)
├── components/             # Reusable UI components
│   ├── component/          # Component management UI components
│   │   └── catalog/        # Catalog-specific UI (Natures, Models)
│   ├── Navbar.vue          # Main navigation
│   ├── Hero.vue            # Hero section
│   ├── HomeCards.vue       # Dashboard cards
│   └── [other components]  # Various UI components
├── composables/            # Vue 3 Composition API composables
│   ├── useComponentNatures.js  # Component Natures state management
│   ├── useComponentModels.js   # Component Models state management
│   └── [reusable logic]    # Shared reactive state and logic
├── views/                  # Page-level components (routed)
│   ├── DashboardView.vue   # Main dashboard
│   ├── Login.vue           # Authentication view
│   ├── catalog/            # Catalog management pages
│   ├── component/          # Component management pages
│   ├── installation/       # Installation tracking pages
│   ├── location/           # Location management pages
│   └── port/               # Port management pages
├── router/                 # Vue Router configuration
├── services/               # Business logic services
├── utils/                  # Utility functions
├── plugins/                # Vue plugins
├── App.vue                 # Root component
└── main.js                 # Application entry point
```

### Key Frontend Features

1. **Vue 3 Composition API**: Modern reactive programming with `<script setup>` syntax
2. **Vue Router**: Client-side routing for single-page application navigation
3. **Axios HTTP Client**: Centralized API communication with interceptors
4. **Tailwind CSS**: Utility-first CSS framework for rapid UI development
5. **Vite Dev Server**: Fast hot module replacement (HMR) on port 3001
6. **Component-Based Architecture**: Reusable Vue components with props and events
7. **Authentication**: Login system integrated with backend JWT/session authentication
8. **Toast Notifications**: User feedback via vue-toastification
9. **Responsive Design**: Mobile-first approach with Tailwind CSS

### API Integration

The frontend communicates with the Spring Boot backend via REST API using a **modular factory pattern**.

#### API Architecture

**Three-Step Pattern**:

1. **Step 1: Instance Configuration** (`api/instance.js`)
   - Creates named axios instances with base URLs and interceptors
   - Available instances: `user`, `admin`, `authentification`, `blockedMacs`, `components`
   - Each instance has automatic Basic Auth injection via interceptors

2. **Step 2: Module Definition** (e.g., `api/components.js`, `api/componentNatures.js`)
   - Modules are **factory functions** that receive instance object
   - Return object with CRUD methods: `getAll()`, `get(id)`, `create()`, `update()`, `delete()`
   - Each module defines its own `baseURL` relative to instance

3. **Step 3: Module Export** (`api/index.js`)
   - Imports all modules and instance
   - Exports unified API object with all modules initialized

#### API Module Pattern

**IMPORTANT**: All API modules must follow this factory function pattern:

```javascript
// ✅ CORRECT: Factory function pattern
// api/componentNatures.js
const baseURL = "catalogs/component-natures";

export default function ({components}) {  // Factory receives instance
    return {
        getAll() {
            return components.get(baseURL);  // Returns axios response
        },
        get(id) {
            return components.get(`${baseURL}/${id}`);
        },
        create(payload) {
            return components.post(baseURL, payload);
        },
        update(id, payload) {
            return components.put(`${baseURL}/${id}`, payload);
        },
        delete(id) {
            return components.delete(`${baseURL}/${id}`);
        }
    };
}
```

```javascript
// ❌ INCORRECT: Direct API calls (will not work)
import instance from './instance';

export const myAPI = {
    async getAll() {
        return await instance.get('/path');  // Wrong!
    }
};
```

**Module Registration** (`api/index.js`):

```javascript
import instance from "./instance";
import componentsModule from "./components";
import componentNaturesModule from "./componentNatures";

export default {
    components: componentsModule(instance),        // Initialize with instance
    componentNatures: componentNaturesModule(instance)
}
```

**Usage in Composables**:

```javascript
import api from '@/api';

// Access initialized modules
const response = await api.componentNatures.getAll();
const data = response.data;  // Extract data from axios response
```

#### API Configuration

- **Base URL**: `http://localhost:8090/api/` (configured in `api/instance.js`)
- **Authentication**: HTTP Basic auth via interceptors (credentials from localStorage)
- **Interceptors**: Automatic auth header injection for all requests
- **Error Handling**: Handled in composables with toast notifications
- **Response Format**: All methods return axios response objects (access `.data` property)

### Development Workflow (Frontend)

1. **Component Development**: Create Vue components in `components/` or `views/`
2. **API Integration**: Add API methods in `api/` modules
3. **Routing**: Configure routes in `router/`
4. **Styling**: Use Tailwind CSS utility classes
5. **State Management**: Use composables for shared reactive state
6. **Testing**: Manual testing via dev server (http://localhost:3001)

### Frontend Configuration

- **Dev Server Port**: 3001 (configured in `vite.config.js`)
- **Backend API**: http://localhost:8090 (Spring Boot backend)
- **Path Alias**: `@` maps to `src/` directory
- **CSS Framework**: Tailwind CSS with PostCSS processing
- **Build Output**: `frontend/dist/` directory

## MCP Server Integration

This repository has MCP (Model Context Protocol) servers configured in `.mcp.json`:

### Available MCP Servers

1. **postgres** - PostgreSQL Database Access
   - Direct database querying capabilities
   - Schema inspection and data validation
   - Use `mcp__postgres__query` tool for SQL queries
   - Connection configured for `switchscope` schema

2. **sequential-thinking** - Advanced Reasoning
   - Structured problem-solving through chain-of-thought
   - Useful for complex debugging and architectural decisions
   - Use `mcp__sequential-thinking__sequentialthinking` tool
   - Supports hypothesis generation and verification

3. **context7** - Library Documentation
   - Up-to-date documentation for Java libraries and frameworks
   - Query Spring Boot, Hibernate, and other dependencies
   - Use `mcp__context7__resolve-library-id` to find libraries
   - Use `mcp__context7__query-docs` for specific documentation

4. **playwright** - Browser Automation for E2E Testing
   - Automated browser testing capabilities for Swagger UI and web interfaces
   - Useful for end-to-end testing scenarios
   - Can interact with Swagger UI at http://localhost:8090/
   - Multiple browser interaction tools available (navigate, click, fill forms, screenshots)
   - Use for testing API documentation accessibility and UI flows

## Development Workflow

### Backend Workflow
1. **Schema changes**: Create Liquibase changelog → Apply migration → Validate with script
2. **Entity changes**: Update entity → Update mapper → Update TO → Update service/controller
3. **Testing**: Write tests → Run `./mvnw verify` → Check coverage
4. **Code style**: Follow Google Java Style Guide → Run checkstyle before commit
5. **Security**: Encrypt sensitive data in CSV files using `PasswordEncryptionUtil`
6. **API testing**: Use Swagger UI at http://localhost:8090/ for interactive API testing

### Frontend Workflow
1. **Component development**: Create/modify Vue components in `components/` or `views/`
2. **API integration**: Add/update API methods in `api/` modules
3. **Routing**: Configure routes in `router/index.js`
4. **Styling**: Apply Tailwind CSS utility classes for UI styling
5. **Testing**: Manual testing via dev server at http://localhost:3001
6. **Build**: Run `npm run build` before deployment

### Full-Stack Workflow
1. **Start backend**: `cd backend && ./mvnw spring-boot:run` (port 8090)
2. **Start frontend**: `cd frontend && npm run dev` (port 3001)
3. **Development**: Make changes, test via browser at http://localhost:3001
4. **Commits**: Descriptive messages, atomic changes, reference issue numbers
5. **Browser testing**: Use MCP `playwright` for automated E2E testing

### Common Development Scenarios

**Adding a new entity**:
1. Create entity class extending `BaseEntity` or `NamedEntity` in `model/`
2. Create repository interface extending `BaseRepository<YourEntity>` in `repository/`
3. Create service class implementing `CrudService<YourEntity>` in `service/`
4. Create controller extending `AbstractCrudController<YourEntity, YourTo>` in `web/`
5. Create DTO (`YourTo`) in `to/` and mapper in `mapper/`
6. Create Liquibase changelog in `init/` and data file in `fill/`
7. Add includes to `db.changelog-master.yaml` in sequential order

**Modifying database schema**:
1. Create new Liquibase changeset (don't modify existing ones)
2. Run `cd backend && ./mvnw liquibase:update`
3. Run `python3 validate_schema.py` from project root
4. Update entity classes if needed
5. Test with `./mvnw test`

**Adding encrypted passwords to CSV seed data**:
1. Add plain text passwords to `PasswordEncryptionUtil.main()` method
2. Run the utility: `java -cp target/classes net.switchscope.util.PasswordEncryptionUtil`
3. Copy encrypted output values to CSV files (e.g., `51-network-switches.csv`)
4. Ensure encryption key is configured in `application-local.yaml` under `app.encryption.key`
5. Test that application can decrypt passwords after loading from CSV

**Querying the database directly**:
- Use MCP `postgres` server: Claude Code can query PostgreSQL via configured MCP server
- Use `mcp__postgres__query` tool with SQL queries
- Schema: `switchscope`, all tables use snake_case naming

**Getting library documentation**:
- Use MCP `context7` server for up-to-date library documentation
- First resolve library ID: `mcp__context7__resolve-library-id`
- Then query docs: `mcp__context7__query-docs`
- Useful for Spring Boot, Hibernate, Jakarta Persistence API questions

**Testing Swagger UI and API endpoints**:
- Use MCP `playwright` server for browser automation testing
- Navigate to Swagger UI: `mcp__playwright__browser_navigate` with http://localhost:8090/
- Take screenshots: `mcp__playwright__browser_take_screenshot`
- Test API documentation accessibility and functionality
- Useful for E2E testing of web interfaces

**Adding a new Vue component**:
1. Create component file in `frontend/src/components/` (e.g., `MyComponent.vue`)
2. Define component structure with `<template>`, `<script setup>`, and `<style>` sections
3. Import and use in parent components or views
4. Apply Tailwind CSS classes for styling
5. Test in browser via dev server

**Adding a new page/view**:
1. Create view component in `frontend/src/views/` (e.g., `MyPageView.vue`)
2. Add route configuration in `frontend/src/router/index.js`
3. Link to the new page from navigation components
4. Implement page layout and functionality
5. Test navigation and page rendering

**Adding a new API module (Frontend)**:
1. Create new API module file in `frontend/src/api/` (e.g., `myModule.js`)
2. **IMPORTANT**: Use factory function pattern (NOT direct exports):
   ```javascript
   const baseURL = "path/to/endpoint";
   export default function ({components}) {  // Receive instance
       return {
           getAll() { return components.get(baseURL); },
           get(id) { return components.get(`${baseURL}/${id}`); },
           // ... other methods
       };
   }
   ```
3. Register module in `frontend/src/api/index.js`:
   ```javascript
   import myModule from "./myModule";
   export default {
       // ... existing modules
       myModule: myModule(instance)  // Initialize with instance
   }
   ```
4. Create composable in `frontend/src/composables/` (e.g., `useMyModule.js`):
   - Import: `import api from '@/api';`
   - Call methods: `const response = await api.myModule.getAll();`
   - Extract data: `const data = response.data;`
   - Add error handling and toast notifications
   - Use singleton pattern for shared state if needed
5. Create Vue components and views using the composable
6. Add route in `router/index.js` if creating a new page

**Integrating a new API endpoint (existing module)**:
1. Add method to existing API module in `frontend/src/api/` (e.g., `components.js`)
2. Follow factory function pattern: return method from factory
3. Update composable to expose the new method
4. Call API method from Vue component (typically in `onMounted` or event handler)
5. Handle response data and errors appropriately
6. Display results in component template
7. Show user feedback via toast notifications

**Adding a new table to the configuration system**:
1. **Create table configuration** in `frontend/src/configs/tables/myEntity.config.js`:
   ```javascript
   export default {
     entityName: 'My Entity',
     entityNamePlural: 'My Entities',
     entityKey: 'entity',
     entityKeyPlural: 'entities',
     icon: 'pi-icon-name',           // PrimeIcons class
     iconColor: 'text-blue-600',      // Tailwind color
     composable: 'useMyEntities',
     searchFields: ['name', 'code', 'description'],
     theme: 'blue',                   // Tailwind color name
     themeIntensity: '600',           // Tailwind intensity
     columns: [
       { key: 'name', label: 'Name', type: 'icon-text', visible: true },
       { key: 'code', label: 'Code', type: 'code', visible: true },
       { key: 'active', label: 'Status', type: 'status-badge', visible: true },
       { key: 'description', label: 'Description', type: 'text-truncate', visible: false },
       { key: 'actions', label: 'Actions', type: 'actions', visible: true,
         actions: ['view', 'edit', 'delete'] }
     ]
   };
   ```

2. **Register in tableRegistry** (`frontend/src/configs/tables/tableRegistry.js`):
   ```javascript
   import myEntityConfig from './myEntity.config.js';
   import { useMyEntities } from '@/composables/useMyEntities';

   export const tableRegistry = {
     // ... existing tables
     myEntities: myEntityConfig
   };

   export const composableRegistry = {
     // ... existing composables
     myEntities: useMyEntities
   };
   ```

3. **Add route** in `frontend/src/router/index.js`:
   ```javascript
   {
     path: "/my-entities",
     name: "my-entities",
     component: GenericTableView,
     meta: {
       requiresAuth: true,
       roles: ['USER', 'ADMIN'],
       tableKey: 'myEntities'  // Must match tableRegistry key
     }
   }
   ```

4. **Ensure composable compatibility**:
   - Composable must export these properties and methods:
     - `myEntities` (ref/computed) - data array
     - `searchMyEntities(query)` - search function
     - `totalMyEntities` (ref/computed) - total count
     - `fetchMyEntities()` - fetch function
     - `deleteMyEntity(id)` - delete function (singular!)
     - `isLoading`, `error` - state refs
   - For catalog entities, use `createEntityComposable` factory from `composableFactory.js`
   - For non-catalog entities, follow `useComponents.js` pattern

**Important Notes**:
- Column `type` must match one of the cell renderer types (see Configuration-Driven Table System)
- `visible: false` columns are hidden by default but accessible via "Show All Columns" button
- Delete function name must be singular (e.g., `deleteMyEntity` not `deleteMyEntities`)
- System items (systemType=true, canBeDeleted=false) are automatically protected from deletion
- Toast notifications are handled automatically by composables

## Important Files

### Backend Files
- **Backend Root**: `backend/pom.xml` - Maven dependencies and build configuration
- **Database Schema**: `backend/src/main/resources/db/changelog/db.changelog-master.yaml` - Master changelog
- **Validation**: `validate_schema.py` - Schema validation script (root directory)
- **Local Config**: `.local.props` - Local database configuration (gitignored, root directory)
- **App Config**:
  - `backend/src/main/resources/application.yaml` - Main application configuration
  - `backend/src/main/resources/application-local.yaml` - Local overrides (gitignored, optional)
- **Main Class**: `backend/src/main/java/net/switchscope/BackendApplication.java` - Spring Boot entry point
- **Base Classes**:
  - `BaseEntity.java` - UUID v7 entity base with timestamps
  - `BaseRepository.java` - Repository with `getExisted()`, `deleteExisted()`
  - `AbstractCrudController.java` - Generic REST controller
- **Utilities**:
  - `PasswordEncryptionUtil.java` - Utility for encrypting passwords in CSV files

### Frontend Files
- **Frontend Root**: `frontend/package.json` - NPM dependencies and scripts
- **Build Config**: `frontend/vite.config.js` - Vite build and dev server configuration
- **Styling Config**:
  - `frontend/tailwind.config.js` - Tailwind CSS configuration
  - `frontend/postcss.config.js` - PostCSS processing configuration
- **App Entry**: `frontend/src/main.js` - Application initialization and plugin setup
- **Root Component**: `frontend/src/App.vue` - Root Vue component
- **API Configuration**:
  - `frontend/src/api/instance.js` - Axios instance with base URL and interceptors
  - `frontend/src/api/index.js` - API exports barrel
- **Router**: `frontend/src/router/index.js` - Vue Router configuration
- **Configuration-Driven Table System**:
  - `frontend/src/views/GenericTableView.vue` - **Universal table view for ALL tables**
  - `frontend/src/configs/tables/tableRegistry.js` - Central table and composable registry
  - `frontend/src/configs/tables/*.config.js` - Per-table configuration files (9 tables)
  - `frontend/src/components/table/GenericListingsTable.vue` - Table wrapper component
  - `frontend/src/components/table/GenericListTable.vue` - Table row renderer
  - `frontend/src/components/table/cells/` - Cell renderer components (8 types)
- **Composables**:
  - `frontend/src/composables/composableFactory.js` - Factory for catalog composables
  - `frontend/src/composables/entityRegistry.js` - Catalog entity configurations
  - `frontend/src/composables/useComponents.js` - Components entity composable
  - `frontend/src/composables/use*.js` - Entity-specific composables
- **Key Views**:
  - `frontend/src/views/DashboardView.vue` - Main dashboard
  - `frontend/src/views/Login.vue` - Authentication view
  - `frontend/src/views/catalog/_deprecated/` - Old catalog views (deprecated)
  - `frontend/src/views/component/_deprecated/` - Old component views (deprecated)

## Application URLs

### Backend (Spring Boot)
Once running, access:
- **Swagger UI**: http://localhost:8090/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8090/v3/api-docs
- **Actuator**: http://localhost:8090/actuator
- **REST API Base**: http://localhost:8090/api

**Note**: Backend runs on port **8090** (configured in `application.yaml`)

**Swagger UI Features**:
- Interactive API testing interface at root path `/`
- All REST endpoints documented with request/response schemas
- Try-it-out functionality with authentication support
- Accessible via MCP `playwright` server for automated testing

### Frontend (Vue.js)
Once running, access:
- **Dev Server**: http://localhost:3001
- **Production Build**: Served via backend or separate web server from `frontend/dist/`

**Note**: Frontend dev server runs on port **3001** (configured in `vite.config.js`)

## Troubleshooting

