# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**SwitchScope** is a comprehensive Network Infrastructure Management System built with Java 21, Spring Boot 3.5, Hibernate 7.2, and PostgreSQL. It manages network devices (switches, routers, access points), physical housing (racks), connectivity (cables, patch panels), and their installations across hierarchical physical locations.

**License**: Business Source License 1.1 (free for healthcare, education, government; becomes Apache 2.0 on Jan 1, 2029)

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

- **Backend config**: `backend/src/main/resources/config.properties` (from `config.properties.example`)
- **Database credentials**: `.local.props` (for validation script and MCP server)
- **MCP server config**: `.mcp.json` (PostgreSQL connection for Claude Code)

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
└── util/               # Utilities
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
db/changelog/
├── db.changelog-master.yaml       # Master changelog (includes all others)
├── init/                          # DDL: table creation (01-users to 90-fk)
├── fill/                          # DML: data population
└── csv/                           # CSV seed data for catalogs
```

**Execution Order**: Master file includes init files first (DDL), then fill files (DML)

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

## Important Implementation Notes

### Working with Components

When adding new component types:
1. Extend appropriate base class (`Device`, `HasPortsImpl`, or `Component` directly)
2. Add discriminator value to `@DiscriminatorValue` annotation
3. Define catalog entry in CSV file: `13-component-types-catalog.csv`
4. Create corresponding `ComponentModel` subclass if needed
5. Add Liquibase changelog for type-specific tables
6. Create Service, Repository, Controller, Mapper, and TO classes

### Working with Entities

- All entities extend `BaseEntity` (or `NamedEntity` for named entities)
- Use `@NoHtml` validation to prevent HTML injection
- Override `equals()`, `hashCode()`, `toString()` when adding fields
- Prefer `@ManyToOne(fetch = LAZY)` for relationships
- Use `@CreationTimestamp`, `@UpdateTimestamp` for audit timestamps

### Working with Liquibase

- **Adding tables**: Create YAML in `init/`, add include in master changelog
- **Adding data**: Create CSV in `csv/`, create fill YAML in `fill/`
- **Testing migrations**: Run `./mvnw liquibase:update`, then `python3 validate_schema.py`
- **Column naming**: Use snake_case in database, camelCase in Java
- **Always use**: `relativeToChangelogFile: true` in includes

### Security

- Spring Security with OAuth2 Resource Server
- User/Role model for RBAC
- Password encryption via `EncryptedStringConverter`
- JWT-based authentication (as per README)
- Custom `@NoHtml` validator prevents HTML injection

### MapStruct Mapping

- All mappers extend `BaseMapper<Entity, To>`
- Configuration in `MapStructConfig`: `componentModel = "spring"`
- Use `@Mapping` to handle custom field mappings
- Ignore audit fields in `updateFromTo()` mappings
- Handle relationships: map IDs vs full objects

### Testing

- Testcontainers for integration tests (PostgreSQL, Kafka)
- Basic test structure in `src/test/java/net/switchscope/`
- Use `@SpringBootTest` with `TestcontainersConfiguration`

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

This repository has an MCP (Model Context Protocol) server configured for PostgreSQL access:
- Server name: `postgres`
- Connection string in `.mcp.json`
- Enables Claude Code to query the database directly
- Use for schema inspection and data validation

## Development Workflow

1. **Schema changes**: Create Liquibase changelog → Apply migration → Validate with script
2. **Entity changes**: Update entity → Update mapper → Update TO → Update service/controller
3. **Testing**: Write tests → Run `./mvnw verify` → Check coverage
4. **Code style**: Follow Google Java Style Guide → Run checkstyle before commit
5. **Commits**: Descriptive messages, atomic changes, reference issue numbers

## Important Files

- `pom.xml` - Maven dependencies and build configuration
- `db.changelog-master.yaml` - Master database changelog
- `validate_schema.py` - Schema validation script
- `.local.props` - Local database configuration (not in git)
- `config.properties` - Application configuration (not in git)
- `BackendApplication.java` - Spring Boot main class

## API Documentation

Once running, access:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Actuator**: http://localhost:8080/actuator

## Troubleshooting

- **Liquibase errors**: Check database connection in `config.properties`, verify schema exists
- **Hibernate errors**: Ensure Hibernate 7.2.0 is being used (check `pom.xml`)
- **UUID errors**: Verify using `@UuidGenerator(style = VERSION_7)` from Hibernate 7.2+
- **Schema validation failures**: Run `python3 validate_schema.py` for detailed diagnostics
- **MapStruct errors**: Ensure Lombok is processed before MapStruct in compiler config
