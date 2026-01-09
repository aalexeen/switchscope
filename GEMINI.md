# GEMINI.md

This file provides guidance to Gemini (and other AI agents) when working with code in this repository.

## Project Overview

**SwitchScope** is a comprehensive Network Infrastructure Management System built with Java 21, Spring Boot 3.5.5, Hibernate 7.2.0, and PostgreSQL. It manages network devices (switches, routers, access points), physical housing (racks), connectivity (cables, patch panels), and their installations across hierarchical physical locations.

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
│   │   │   └── component/             # Component-specific components
│   │   │       └── catalog/           # Catalog UI components (Natures, Models)
│   │   ├── composables/               # Vue 3 Composition API composables
│   │   ├── plugins/                   # Vue plugins configuration
│   │   ├── router/                    # Vue Router configuration
│   │   ├── services/                  # Business logic services
│   │   ├── utils/                     # Utility functions
│   │   ├── views/                     # Page-level components (routed views)
│   │   │   ├── catalog/               # Catalog management views
│   │   │   ├── component/             # Component management views
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
├── GEMINI.md                          # This file
├── CLAUDE.md                          # Guidance for Claude
└── README.md                          # Project documentation
```

## Common Development Commands

### Backend (Maven)

```bash
# Navigate to backend
cd backend

# Run the application (Runs on port 8090)
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
./mvnw liquibase:dropAll       # Drop All tables

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

# Run development server (Runs on port 3001)
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
  - `backend/src/main/resources/application.yaml` - Main Spring Boot configuration (Port 8090).
  - `backend/src/main/resources/application-local.yaml` - Local overrides (gitignored, optional).
  - Database credentials via environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
  - Encryption key via environment variable: `APP_ENCRYPTION_KEY` (default: `change-me-in-production-32chars`).
- **Database credentials**: `.local.props` (for validation script and MCP server, gitignored).
- **Frontend config**:
  - `frontend/src/api/instance.js` - API Base URL configuration.
  - `frontend/vite.config.js` - Vite config (Port 3001).

**Note**: Local configuration files are gitignored. Use environment variables or create `application-local.yaml` for local overrides.

## High-Level Architecture

### Domain Model (Backend)

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

- **Component**: Abstract base for all infrastructure.
  - Uses **Single Table Inheritance** with `component_class` discriminator.
  - Delegates behavior to `ComponentTypeEntity` (type-driven behavior pattern).
  - Self-referential parent-child relationships for modular equipment.
  - Key relationships: ComponentType, ComponentStatus, Installation, ComponentModel.

- **Location**: Hierarchical physical locations (building → floor → room → rack).
  - Self-referential parent-child hierarchy.
  - Type-driven behavior via `LocationTypeEntity`.
  - Tracks infrastructure (power, UPS, cooling, rack units).

- **Installation**: Links Component to Location (or Component to parent Component).
  - Tracks physical installation with rack positioning.
  - Status tracking via `InstallationStatusEntity`.
  - Audit fields: installedBy, removedBy, timestamps.

- **Port**: Abstract base for network ports (Single Table Inheritance).
  - Implementations: `EthernetPort`, `FiberPort`.
  - Comprehensive attributes: status, speed, VLAN, PoE, traffic stats.
  - One-to-one with `Connector` for physical cabling.

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

1. **Layered Architecture**: Entity → Repository → Service → Controller.
   - Clear separation of concerns.
   - Transactional boundaries at service layer.
   - DTOs separate API from domain model.

2. **Type-Driven Behavior**:
   - `ComponentTypeEntity` defines component capabilities at runtime.
   - `LocationTypeEntity` defines hierarchy rules.
   - Allows configuration changes without code deployment.

3. **Generic Base Classes**:
   - `BaseRepository<T>` - adds `getExisted()`, `deleteExisted()`.
   - `CrudService<T>` - generic CRUD operations interface.
   - `AbstractCrudController<T>` - standard REST endpoints.
   - `BaseMapper<E, T>` - MapStruct entity ↔ DTO conversion.

4. **Catalog Pattern**:
   - `ComponentModel` catalog for manufacturer specifications (abstract base with Single Table Inheritance).
   - `ComponentNatureEntity` catalog for component nature classifications.
   - Separates `ComponentModel` (catalog) from `Component` (instance).
   - Concrete models: `SwitchModel`, `RouterModel`, `RackModelEntity`, etc.
   - **Important**: Discriminator values in entity classes must match CSV data exactly.

### Database Management

- **UUID v7 Primary Keys**: Time-ordered UUIDs for performance (Hibernate 7.2 support).
- **Single Table Inheritance**: All components in one table for polymorphic queries.
- **Liquibase**: Manages schema changes. `master` includes `init/` (DDL) and `fill/` (DML).
- **Seed Data**: CSV files in `src/main/resources/db/changelog/csv/`.
- **Automatic Timestamps**: `@CreationTimestamp`, `@UpdateTimestamp` on BaseEntity.
- **Schema Naming**: All tables and columns use `switchscope` schema with snake_case naming.

## Important Implementation Notes

### Key Architectural Decisions

1. **Single Table Inheritance for Components**: Chosen for flexibility and query simplicity over storage optimization.
2. **UUID v7**: Provides better database index performance than random UUIDs while maintaining global uniqueness.
3. **Type-Driven Behavior**: Allows runtime configuration changes via `ComponentTypeEntity`.
4. **Separation of Catalog and Instances**: Enables tracking multiple instances of the same model.

### Working with Components (Backend)

When adding new component types:
1. Extend appropriate base class (`Device`, `HasPortsImpl`, or `Component` directly).
2. Add `@DiscriminatorValue`.
3. Define catalog entry in CSV file: `13-component-types-catalog.csv`.
4. Create corresponding `ComponentModel` subclass if needed.
5. Create Service, Repository, Controller, Mapper, and TO classes.
6. Create Liquibase changelogs for type-specific tables.

### Working with Entities

- All entities extend `BaseEntity` (or `NamedEntity`).
- Use `@NoHtml` validation to prevent HTML injection.
- Override `equals()`, `hashCode()`, `toString()` cautiously.
- Prefer `@ManyToOne(fetch = LAZY)` for relationships.

### Working with Liquibase

- **Adding tables**: Create YAML in `init/`, add include in master changelog (maintain sequential order).
- **Adding data**: Create CSV in `csv/`, create fill YAML in `fill/`.
- **Testing migrations**: Run `./mvnw liquibase:update`, then `python3 validate_schema.py`.
- **Column naming**: snake_case in database, camelCase in Java.
- **Always use**: `relativeToChangelogFile: true` in includes.

### Security

- **Spring Security**: HTTP Basic authentication (stateless).
- **Password Encryption**:
  - Backend: `EncryptedStringConverter` for entity fields (AES/GCM/NoPadding).
  - CSV Seed Data: Use `PasswordEncryptionUtil` to generate encrypted strings.
  - Encryption key: Configured in `application.yaml` (`app.encryption.key`).
- **Frontend Auth**: Vue.js handles login via `authService`, storing credentials in `localStorage`.

### Frontend Implementation Notes

#### API Module Pattern (CRITICAL)

**ALWAYS use the factory function pattern** when creating new API modules.

**Correct Pattern:**
```javascript
// api/componentNatures.js
const baseURL = "catalogs/component-natures";

export default function ({components}) {  // Factory receives instance
    return {
        getAll() {
            return components.get(baseURL); // Returns axios response
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

**Module Registration (`api/index.js`):**
```javascript
import instance from "./instance";
import componentNaturesModule from "./componentNatures";

export default {
    componentNatures: componentNaturesModule(instance) // Initialize with instance
}
```

#### Component Architecture Pattern

**Container/Presentational Pattern** (recommended for all pages):
1.  **PageView.vue (Container)**: Manages state, calls composables, handles events.
2.  **ListingsTable.vue (Presentational)**: Props for data, emits events (view, edit, delete).
3.  **ListTable.vue (Row)**: Renders single row.

#### Composables Pattern

**Use Singleton Pattern** for shared data (like catalogs):

```javascript
const items = ref([]);
const isInitialized = ref(false);

export function useMyData() {
  const fetchItems = async (force = false) => {
    if (isInitialized.value && !force) return;
    // ... fetch logic
  };
  return { items, fetchItems };
}
```

## Technology Stack

### Backend
- **Java 21** (LTS)
- **Spring Boot 3.5.5**
- **Hibernate ORM 7.2.0**
- **Jakarta Persistence API 3.2.0**
- **PostgreSQL 13+**
- **Liquibase 4.30.0**
- **MapStruct 1.5.5**
- **Lombok**
- **Testcontainers 1.19.3**
- **SpringDoc OpenAPI 2.7.0**

### Frontend
- **Vue.js 3.5.13** with Composition API
- **Vue Router 4.5.0**
- **Vite 6.0.5**
- **Axios 1.7.9**
- **Tailwind CSS 3.4.17**
- **PrimeIcons 7.0.0**
- **Vue Toastification 2.0.0**

## Frontend Architecture

### Project Structure

```
frontend/src/
├── api/                    # API client layer (Factory Pattern)
│   ├── instance.js         # Axios instance configuration
│   ├── index.js            # API exports barrel
│   └── [modules].js        # API modules (factory functions)
├── components/             # Reusable UI components
│   ├── component/          # Component-specific components
│   └── catalog/            # Catalog UI components
├── composables/            # Vue 3 Composition API composables
├── views/                  # Page-level components (routed)
├── router/                 # Vue Router configuration
├── services/               # Business logic services
├── utils/                  # Utility functions
├── plugins/                # Vue plugins
├── App.vue                 # Root component
└── main.js                 # Application entry point
```

### API Integration
- **Step 1**: Instance Configuration (`api/instance.js`).
- **Step 2**: Module Definition (Factory functions).
- **Step 3**: Module Export (Initialize with instance in `api/index.js`).

## Tool Usage

This repository is optimized for use with AI agents. You have access to the following tools:

1.  **Database Access (`query`)**:
    *   Run read-only SQL queries against the PostgreSQL database.
    *   Schema: `switchscope`. Tables use snake_case.

2.  **Reasoning (`sequentialthinking`)**:
    *   Use for complex debugging, architectural planning, or root cause analysis.
    *   Supports hypothesis generation and verification.

3.  **Library Docs (`resolve-library-id`, `query-docs`)**:
    *   Use to retrieve up-to-date documentation for libraries (Spring Boot, Hibernate, Vue, etc.).
    *   Always resolve the library ID first.

4.  **Browser Automation (`browser_*`)**:
    *   Use to interact with the running application or Swagger UI.
    *   **Swagger UI**: `http://localhost:8090/swagger-ui.html`
    *   **Frontend**: `http://localhost:3001/`

5.  **Codebase Investigation (`delegate_to_agent: codebase_investigator`)**:
    *   Use for broad analysis, architectural mapping, or understanding system-wide dependencies.

## Development Workflow

### Backend Workflow
1.  **Schema changes**: Create Liquibase changelog → Apply migration → Validate with script.
2.  **Entity changes**: Update entity → Update mapper → Update TO → Update service/controller.
3.  **Testing**: Write tests → Run `./mvnw verify` → Check coverage.
4.  **Security**: Encrypt sensitive data in CSV files using `PasswordEncryptionUtil`.

### Frontend Workflow
1.  **Component development**: Create/modify Vue components in `components/` or `views/`.
2.  **API integration**: Add/update API methods in `api/` modules (Factory Pattern).
3.  **Routing**: Configure routes in `router/index.js`.
4.  **Testing**: Manual testing via dev server (`http://localhost:3001`).

### Common Development Scenarios

**Adding a new entity**:
1.  Create entity class extending `BaseEntity` in `model/`.
2.  Create repository interface extending `BaseRepository` in `repository/`.
3.  Create service class implementing `CrudService` in `service/`.
4.  Create controller extending `AbstractCrudController` in `web/`.
5.  Create DTO and mapper.
6.  Create Liquibase changelog and data file.
7.  Add includes to `db.changelog-master.yaml`.

**Adding a new API module (Frontend)**:
1.  Create new API module file in `frontend/src/api/` (e.g., `myModule.js`).
2.  **IMPORTANT**: Use factory function pattern (NOT direct exports).
3.  Register module in `frontend/src/api/index.js`.
4.  Create composable in `frontend/src/composables/`.
5.  Use in Vue components.

**Adding encrypted passwords to CSV seed data**:
1.  Run `java -cp target/classes net.switchscope.util.PasswordEncryptionUtil` (after build) or run `main()` in IDE.
2.  Copy generated encrypted values to CSV files.

## Important Files

- **Backend**: `backend/src/main/resources/application.yaml`, `backend/src/main/resources/db/changelog/db.changelog-master.yaml`, `backend/pom.xml`.
- **Frontend**: `frontend/vite.config.js`, `frontend/src/api/index.js`, `frontend/src/router/index.js`.
- **Root**: `validate_schema.py`, `.local.props`, `.mcp.json`.

## Application URLs

- **Swagger UI**: http://localhost:8090/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8090/v3/api-docs
- **Frontend Dev Server**: http://localhost:3001
