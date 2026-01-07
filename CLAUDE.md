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
├── validate_schema.py                 # Database schema validator
├── .local.props                       # Local DB config (gitignored)
├── .mcp.json                          # MCP server configuration
├── CLAUDE.md                          # This file
└── README.md                          # Project documentation
```

**Note**: The repository is backend-only currently.

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
   - `ComponentModel` catalog for manufacturer specifications
   - Separates product catalog from component instances
   - Concrete models: `SwitchModel`, `RouterModel`, `RackModelEntity`

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

## Technology Stack

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

1. **Schema changes**: Create Liquibase changelog → Apply migration → Validate with script
2. **Entity changes**: Update entity → Update mapper → Update TO → Update service/controller
3. **Testing**: Write tests → Run `./mvnw verify` → Check coverage
4. **Code style**: Follow Google Java Style Guide → Run checkstyle before commit
5. **Commits**: Descriptive messages, atomic changes, reference issue numbers
6. **Security**: Encrypt sensitive data in CSV files using `PasswordEncryptionUtil`
7. **API testing**: Use Swagger UI at http://localhost:8090/ for interactive API testing
8. **Browser testing**: Use MCP `playwright` for automated E2E testing of Swagger UI

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

## Important Files

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

## API Documentation

Once running, access:
- **Swagger UI**: http://localhost:8090/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8090/v3/api-docs
- **Actuator**: http://localhost:8090/actuator

**Note**: Application runs on port **8090** (configured in `application.yaml`)

**Swagger UI Features**:
- Interactive API testing interface at root path `/`
- All REST endpoints documented with request/response schemas
- Try-it-out functionality with authentication support
- Accessible via MCP `playwright` server for automated testing

## Troubleshooting

