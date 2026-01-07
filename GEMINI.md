# GEMINI.md

This file provides guidance to Gemini (and other AI agents) when working with code in this repository.

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
│   │   └── application.yaml           # App configuration
│   ├── src/test/                      # Tests with Testcontainers
│   ├── pom.xml                        # Maven configuration
│   └── mvnw, mvnw.cmd                 # Maven wrapper
├── frontend/                          # Vue 3 + Vite Frontend
│   ├── src/                           # Source code
│   │   ├── api/                       # Axios API configuration
│   │   ├── components/                # Vue components
│   │   ├── views/                     # Vue views/pages
│   │   └── router/                    # Vue Router
│   ├── package.json                   # NPM dependencies
│   └── vite.config.js                 # Vite configuration
├── validate_schema.py                 # Database schema validator
├── .local.props                       # Local DB config (gitignored)
├── .mcp.json                          # MCP server configuration
├── GEMINI.md                          # This file
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
./mvnw liquibase:dropAll      # Drop All tables

# Code quality
./mvnw checkstyle:check        # Check code style
./mvnw spotbugs:check          # Static analysis
```

### Frontend (NPM)

```bash
# Navigate to frontend
cd frontend

# Install dependencies
npm install

# Run development server (Runs on port 3000)
npm run dev

# Build for production
npm run build
```

### Database Schema Validation

```bash
# Validate database schema matches Liquibase definitions
python3 validate_schema.py
```

This script:
- Compares actual PostgreSQL schema with Liquibase YAML definitions
- Identifies missing tables, columns, type mismatches, and constraint issues
- Exit codes: 0 (pass), 1 (critical issues), 2 (warnings only)

### Configuration Files

- **Backend config**:
  - `backend/src/main/resources/application.yaml` - Main Spring Boot configuration (Port 8090).
  - `backend/src/main/resources/application-local.yaml` - Local overrides (gitignored).
- **Frontend config**:
  - `frontend/src/api/instance.js` - API Base URL configuration.
- **Database credentials**: `.local.props` (for validation script and MCP server, gitignored).

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

- **Component**: Abstract base for all infrastructure. Uses **Single Table Inheritance** with `component_class` discriminator.
- **Location**: Hierarchical physical locations (building → floor → room → rack).
- **Installation**: Links Component to Location.
- **Port**: Abstract base for network ports (Ethernet, Fiber).

### Architectural Patterns

1. **Layered Architecture**: Entity → Repository → Service → Controller.
2. **Type-Driven Behavior**: `ComponentTypeEntity` defines component capabilities at runtime.
3. **Generic Base Classes**: `BaseRepository<T>`, `CrudService<T>`, `AbstractCrudController<T>`.
4. **Catalog Pattern**: Separates `ComponentModel` (catalog) from `Component` (instance).

### Database Management

- **UUID v7 Primary Keys**: Time-ordered UUIDs for performance.
- **Single Table Inheritance**: All components in one table for polymorphic queries.
- **Liquibase**: Manages schema changes. `master` includes `init/` (DDL) and `fill/` (DML).

## Important Implementation Notes

### Security

- **Spring Security**: HTTP Basic authentication (stateless).
- **Frontend Auth**: Vue.js handles login via `authService`, storing credentials in `localStorage` (Basic Auth).
- **Password Encryption**:
  - Backend: `EncryptedStringConverter` for entity fields.
  - CSV Seed Data: `PasswordEncryptionUtil` for generating encrypted strings.
- **RBAC**: `User` and `Role` entities. Admin endpoints (`/api/admin/**`) require `ADMIN` role.

### Frontend Integration

- **Framework**: Vue 3 with Vite.
- **Styling**: TailwindCSS.
- **Connectivity**: Frontend connects to Backend on port **8090**.
- **Mock Data**: Do not check in `json` files with PII in `frontend/src/`.

### Working with Components (Backend)

When adding new component types:
1. Extend appropriate base class (`Device`, `HasPortsImpl`, etc).
2. Add `@DiscriminatorValue`.
3. Define catalog entry in `13-component-types-catalog.csv`.
4. Create Service, Repository, Controller, Mapper, and TO classes.
5. Create Liquibase changelogs.

## MCP Server & Tool Usage

This repository is optimized for use with AI agents via Model Context Protocol (MCP) or native tools.

### Available Tools

1.  **Database Access (`query`)**:
    *   Use this to run read-only SQL queries against the PostgreSQL database.
    *   Schema: `switchscope`. Tables use snake_case.

2.  **Reasoning (`sequentialthinking`)**:
    *   Use for complex debugging, architectural planning, or root cause analysis.

3.  **Library Docs (`resolve-library-id`, `query-docs`)**:
    *   Use to query documentation for Spring Boot, Hibernate, Vue.js, etc.
    *   Always resolve the library ID first.

4.  **Browser Automation (`browser_*`)**:
    *   Use to interact with the running application or Swagger UI.
    *   **Swagger UI**: `http://localhost:8090/swagger-ui.html`
    *   **Frontend**: `http://localhost:3000/`

## Development Workflow

1.  **Schema changes**: Create Liquibase changelog → Apply migration → Validate with script.
2.  **Frontend changes**: Verify build with `npm run build`.
3.  **Testing**:
    *   Backend: `./mvnw verify` (Integration tests use Testcontainers).
    *   API: Use Swagger UI.
4.  **Commits**: Use conventional commits (e.g., `feat:`, `fix:`).

## API Documentation

- **Swagger UI**: http://localhost:8090/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8090/v3/api-docs
