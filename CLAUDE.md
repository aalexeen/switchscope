# CLAUDE.md

Guidance for Claude Code working with SwitchScope repository.

## Project Overview

**SwitchScope**: Network Infrastructure Management System
- **Stack**: Java 21, Spring Boot 3.5, Hibernate 7.2, PostgreSQL, Vue.js 3, Vite
- **License**: Business Source License 1.1 (→ Apache 2.0 on 2029-01-01)
- **Architecture**: Full-stack (Java backend + Vue frontend)
- **Ports**: Backend 8090, Frontend 3001

## Key Technologies

**Backend**: Java 21, Spring Boot 3.5.5, Hibernate 7.2, PostgreSQL 13+, Liquibase 4.30, MapStruct 1.5.5, Lombok, SNMP4J 3.9.6, SSHJ 0.40.0, Testcontainers 1.19.3
**Frontend**: Vue 3.5.13 (Composition API), Vue Router 4.5, Vite 6.0.5, Axios 1.7.9, Tailwind CSS 3.4.17, PrimeIcons 7.0, Vue Toastification 2.0

## Commands

**Backend**:
```bash
cd backend
./mvnw spring-boot:run           # Run app
./mvnw test                       # Unit tests
./mvnw integration-test           # Integration tests
./mvnw verify                     # Full test suite
./mvnw clean package              # Build JAR
./mvnw liquibase:update           # Apply migrations
./mvnw liquibase:status           # Check migration status
./mvnw liquibase:dropAll          # Drop all tables
./mvnw checkstyle:check           # Check code style
./mvnw spotbugs:check             # Static analysis
```

**Frontend**:
```bash
cd frontend
npm install                       # Install dependencies
npm run dev                       # Dev server (port 3001)
npm run build                     # Production build
npm run preview                   # Preview production build
```

**Database**:
```bash
python3 validate_schema.py        # Validate schema matches Liquibase
```

**URLs**:
- Backend: http://localhost:8090 (Swagger: http://localhost:8090/swagger-ui.html)
- Frontend: http://localhost:3001

## Architecture Patterns

### Backend Patterns

**1. Layered Architecture**: Entity → Repository → Service → Controller
- Separation of concerns
- Transactional boundaries at service layer
- DTOs separate API from domain model

**2. Type-Driven Behavior**:
- `ComponentTypeEntity` defines component capabilities at runtime
- `LocationTypeEntity` defines hierarchy rules
- Allows configuration changes without code deployment

**3. Generic Base Classes**:
- `BaseRepository<T>` - adds `getExisted()`, `deleteExisted()`
- `CrudService<T>` - generic CRUD operations interface
- `AbstractCrudController<T>` - standard REST endpoints
- `BaseMapper<E, T>` - MapStruct entity ↔ DTO conversion

**4. Catalog Pattern**:
- `ComponentModel` catalog for manufacturer specifications (Single Table Inheritance)
- `ComponentNatureEntity` catalog for component nature classifications
- Separates product catalog from component instances
- Concrete models: `SwitchModel`, `RouterModel`, `RackModelEntity`, `AccessPointModel`, `PatchPanelModel`, `CableRunModel`
- **Important**: Discriminator values in entity classes must match CSV data exactly

**Entity Hierarchy**:
```
BaseEntity (UUID v7, timestamps)
└── NamedEntity (name, description)
    └── BaseCodedEntity (code, displayName, active, sortOrder) → Catalog entities
    └── Component (Single Table Inheritance, discriminator: component_class)
        ├── Device (abstract)
        │   └── HasPortsImpl (@MappedSuperclass)
        │       ├── NetworkSwitch, Router, AccessPoint, PatchPanel
        ├── Rack, CableRun, Connector
```

**Core Domain Entities**:
- **Component**: Abstract base for all infrastructure, Single Table Inheritance with `component_class` discriminator
- **Location**: Hierarchical physical locations (building → floor → room → rack), self-referential parent-child hierarchy
- **Installation**: Links Component to Location (or Component to parent Component), tracks physical installation with rack positioning
- **Port**: Abstract base for network ports (Single Table Inheritance), implementations: `EthernetPort`, `FiberPort`

**Package Structure**: `net.switchscope/{model, repository, service, web, mapper, to, config, error, validation, util}`

**Database**:
- UUID v7 primary keys via `@UuidGenerator(style = VERSION_7)` (better performance than UUID v4)
- Single Table Inheritance (component_class discriminator: RACK, NETWORK_SWITCH, ROUTER, etc.)
- Liquibase: `init/` (DDL) → `fill/` (DML), CSV seed data
- Schema: `switchscope`, snake_case naming
- Automatic timestamps: `@CreationTimestamp`, `@UpdateTimestamp` on BaseEntity

**Liquibase**:
- Master: `db.changelog-master.yaml`
- Execution order: Master includes init files (DDL), then fill files (DML)
- Sequential numbering: `01-users.yaml`, `10-component-types.yaml`
- CSV format: separator `;`, quotchar `'`
- Fresh start: `./mvnw liquibase:dropAll && ./mvnw liquibase:update`
- Always use: `relativeToChangelogFile: true` in includes
- Never modify existing changesets, create new ones

**Security**:
- Spring Security with HTTP Basic authentication (stateless sessions)
- RBAC: Admin endpoints `/api/admin/**` require `ADMIN` role, API `/api/**` require authentication
- Password encryption via `EncryptedStringConverter` (AES/GCM/NoPadding)
- `PasswordEncryptionUtil` for encrypting passwords in CSV seed data
- CORS allows localhost and local network origins
- Custom validators: `@NoHtml` prevents HTML injection

**MapStruct**:
- All mappers extend `BaseMapper<Entity, To>`
- Configuration: `MapStructConfig` with `componentModel = "spring"`
- Use `@Mapping` for custom field mappings
- Ignore audit fields in `updateFromTo()` mappings

**Testing**:
- Test base classes: `AbstractCrudControllerTest`, `AbstractCatalogControllerTest`, `CrudSmokeControllerTest`
- Integration tests: `@SpringBootTest` with TestcontainersConfiguration (PostgreSQL, Kafka, Redis)
- Controller tests: `@WebMvcTest` with `MockMvc`, custom `TestSecurityConfig` disables security

### Frontend Patterns

**1. Configuration-Driven Table System (CRITICAL)**
- **ONE** view for ALL tables: `GenericTableView.vue`
- Dynamic config via `route.meta.tableKey`
- Zero code duplication, 72% code reduction (~2,840 → ~890 lines)
- Consistent UI/UX across all tables
- Add new table = create config file only

**2. API Module Pattern (Factory Function - CRITICAL)**
```javascript
// ✅ CORRECT: Factory function
const baseURL = "path/to/endpoint";
export default function ({components}) {
    return {
        getAll() { return components.get(baseURL); },
        get(id) { return components.get(`${baseURL}/${id}`); },
        create(payload) { return components.post(baseURL, payload); },
        update(id, payload) { return components.put(`${baseURL}/${id}`, payload); },
        delete(id) { return components.delete(`${baseURL}/${id}`); }
    };
}

// Register in api/index.js:
import myModule from "./myModule";
export default {
    myModule: myModule(instance)  // Initialize with instance
}

// Usage in composables:
import api from '@/api';
const response = await api.myModule.getAll();
const data = response.data;  // Extract data from axios response
```

**Common Mistakes**:
- ❌ Direct import of axios instance: `import instance from './instance'`
- ❌ Named exports: `export const myAPI = { ... }`
- ❌ Async/await in module definition (handle in composables)
- ❌ Extracting `.data` in module (return full axios response)

**3. Composable Pattern (Singleton)**
```javascript
// Shared state OUTSIDE composable function
const items = ref([]);
const isLoading = ref(false);
const isInitialized = ref(false);
const lastFetchTime = ref(null);
const CACHE_DURATION = 5 * 60 * 1000;

export const useMyData = () => {
  const toast = useToast();

  const isCacheValid = () => {
    if (!lastFetchTime.value) return false;
    return Date.now() - lastFetchTime.value < CACHE_DURATION;
  };

  const fetchItems = async (forceRefresh = false) => {
    if (!forceRefresh && isCacheValid() && items.value.length > 0) return items.value;
    isLoading.value = true;
    error.value = null;
    try {
      const response = await api.myModule.getAll();
      items.value = response.data;
      lastFetchTime.value = Date.now();
      isInitialized.value = true;
      return items.value;
    } catch (err) {
      error.value = err.message;
      toast.error('Failed to load items');
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  const deleteItem = async (id) => {
    try {
      await api.myModule.delete(id);
      const index = items.value.findIndex(item => item.id === id);
      if (index !== -1) items.value.splice(index, 1);
      toast.success('Item deleted successfully');
    } catch (err) {
      toast.error('Failed to delete item');
      throw err;
    }
  };

  return {
    items: computed(() => items.value),
    isLoading: computed(() => isLoading.value),
    totalItems: computed(() => items.value.length),
    fetchItems,
    deleteItem
  };
}
```

**When to use singleton**:
- Catalog data (component types, statuses, etc.)
- Data shared across views
- Expensive data that shouldn't be re-fetched

**When NOT to use singleton**:
- Form state (local to component)
- Temporary UI state
- User-specific filters

**4. Table Configuration Pattern**
```javascript
export default {
  entityName: 'My Entity',
  entityNamePlural: 'My Entities',
  entityKey: 'entity',
  entityKeyPlural: 'entities',
  icon: 'pi-icon',               // PrimeIcons class
  iconColor: 'text-blue-600',    // Tailwind color
  composable: 'useMyEntities',
  searchFields: ['name', 'code', 'description'],
  theme: 'blue',                 // Tailwind color name
  themeIntensity: '600',         // Tailwind intensity
  columns: [
    { key: 'name', label: 'Name', type: 'icon-text', visible: true, sortable: true },
    { key: 'code', label: 'Code', type: 'code', visible: true, sortable: true },
    { key: 'active', label: 'Status', type: 'status-badge', visible: true },
    { key: 'description', label: 'Description', type: 'text-truncate', visible: false },
    { key: 'actions', label: 'Actions', type: 'actions', visible: true,
      actions: ['view', 'edit', 'delete'] }
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

**5. Router Integration Pattern**
```javascript
{
  path: "/catalog/component-natures",
  name: "component-natures",
  component: GenericTableView,
  meta: {
    requiresAuth: true,
    roles: ['USER', 'ADMIN'],
    tableKey: 'componentNatures'  // Lookup key in tableRegistry
  }
}
```

**Routing Best Practices**:
- Always add `meta: { requiresAuth: true }` for protected routes
- Specify roles: `roles: ['USER', 'ADMIN']`
- Import views at top of router file (not lazy loading)
- Use consistent naming: route `name` matches component name (kebab-case)

**Structure**:
```
frontend/src/
├── api/                      # Factory function pattern
│   ├── instance.js           # Axios instances with base URLs and interceptors
│   ├── index.js              # API registry
│   └── *.js                  # Modules (devices, routers, etc.)
├── composables/              # Singleton pattern for shared state
│   ├── composableFactory.js  # Factory for creating catalog composables
│   ├── entityRegistry.js     # Catalog entity configurations
│   └── use*.js               # Entity-specific composables
├── configs/tables/           # Table configurations
│   ├── tableRegistry.js      # Central table and composable registry
│   └── *.config.js           # Per-table config
├── components/table/         # Generic table components
│   ├── GenericListingsTable.vue  # Wrapper (stats, refresh, column toggle)
│   ├── GenericListTable.vue      # Row renderer with dynamic cells
│   └── cells/                    # Cell renderer components (8 types)
└── views/
    └── GenericTableView.vue  # Universal table view for ALL tables
```

**API Architecture (Three-Step Pattern)**:
1. **Instance Configuration** (`api/instance.js`): Named axios instances with base URLs and interceptors
2. **Module Definition** (e.g., `api/components.js`): Factory functions that receive instance, return CRUD methods
3. **Module Export** (`api/index.js`): Unified API object with all modules initialized

**API Configuration**:
- Base URL: `http://localhost:8090/api/`
- Authentication: HTTP Basic auth via interceptors (credentials from localStorage)
- Interceptors: Automatic auth header injection
- Error Handling: In composables with toast notifications
- Response Format: All methods return axios response objects (access `.data` property)

**Table System Features**:
1. Delete Confirmation: Dialog before deletion (in CellActions.vue)
2. System Protection: System items (systemType=true) cannot be deleted
3. Column Toggle: "Show All Columns" button for expandable columns
4. Unified Search: Real-time case-insensitive search across configured fields
5. Toast Notifications: Success/error feedback for all operations

## Adding New Entity (Frontend)

**Steps**:
1. **API Module**: Create `api/myEntity.js` with factory function
2. **Register API**: Import in `api/index.js`, add to exports: `myEntity: myEntityModule(instance)`
3. **Composable**: Create `composables/useMyEntities.js` (singleton pattern, 5min cache)
4. **Table Config**: Create `configs/tables/myEntity.config.js`
5. **Register Table**: Import in `tableRegistry.js`, add to `tableRegistry` and `composableRegistry`
6. **Route**: Add to `router/index.js` with `meta: { requiresAuth: true, roles: ['USER', 'ADMIN'], tableKey: 'myEntities' }`
7. **Navigation**: Update `DashboardView.vue` if needed

**Required Composable Methods**:
- `myEntities` (computed) - data array
- `fetchMyEntities()` - fetch function
- `searchMyEntities(query)` - search function
- `deleteMyEntity(id)` - delete function (singular!)
- `totalMyEntities` (computed) - total count
- `isLoading`, `error` - state refs

**Module Registration Checklist**:
1. Create module file with factory function
2. Add import to `api/index.js`
3. Add to exports object with instance: `myModule: myModule(instance)`
4. Use in composables via: `import api from '@/api'`
5. Extract data in composable: `response.data`

## Adding New Entity (Backend)

**Steps**:
1. **Entity**: Extend `BaseEntity`/`NamedEntity` in `model/`, add `@Entity`, `@Table(name = "table_name")`
2. **Repository**: Extend `BaseRepository<YourEntity>` in `repository/`
3. **Service**: Implement `CrudService<YourEntity>` in `service/`
4. **Controller**: Extend `AbstractCrudController<YourEntity, YourTo>` in `web/`
5. **DTO**: Create `YourTo` in `to/`, mapper `YourMapper extends BaseMapper<Entity, To>` in `mapper/`
6. **Liquibase**: Create `init/XX-your-entity.yaml`, `fill/XX-fill-your-entity.yaml`
7. **Master**: Add includes to `db.changelog-master.yaml` in sequential order

**Working with Components**:
1. Extend appropriate base class (`Device`, `HasPortsImpl`, or `Component` directly)
2. Add discriminator value: `@DiscriminatorValue("FIREWALL")`
3. Define catalog entry in CSV: `13-component-types-catalog.csv`
4. Create corresponding `ComponentModel` subclass if needed
5. Add Liquibase changelog for type-specific tables
6. Create Service, Repository, Controller, Mapper, TO classes

**Working with Entities**:
- All entities extend `BaseEntity` (or `NamedEntity` for named entities)
- Use `@NoHtml` validation to prevent HTML injection
- Override `equals()`, `hashCode()`, `toString()` when adding fields
- Prefer `@ManyToOne(fetch = LAZY)` for relationships
- Use `@CreationTimestamp`, `@UpdateTimestamp` for audit timestamps

**Encrypting Passwords in CSV**:
1. Add plain text passwords to `PasswordEncryptionUtil.main()` method
2. Run: `java -cp target/classes net.switchscope.util.PasswordEncryptionUtil`
3. Copy encrypted output values to CSV files
4. Ensure encryption key configured in `application-local.yaml`: `app.encryption.key`

## Key Files

**Backend**:
- `backend/pom.xml` - Maven dependencies
- `backend/src/main/resources/db/changelog/db.changelog-master.yaml` - Master changelog
- `backend/src/main/resources/application.yaml` - Main config
- `backend/src/main/resources/application-local.yaml` - Local overrides (gitignored)
- `.local.props` - Local DB config (gitignored)
- `BaseEntity.java`, `BaseRepository.java`, `AbstractCrudController.java` - Base classes
- `PasswordEncryptionUtil.java` - Encrypting passwords for CSV

**Frontend**:
- `frontend/package.json` - NPM dependencies
- `frontend/vite.config.js` - Vite config
- `frontend/src/api/index.js` - API registry
- `frontend/src/api/instance.js` - Axios instances
- `frontend/src/configs/tables/tableRegistry.js` - Table registry
- `frontend/src/views/GenericTableView.vue` - Universal table view
- `frontend/src/router/index.js` - Routes
- `frontend/src/composables/composableFactory.js` - Catalog factory
- `frontend/src/components/table/` - Generic table components and cell renderers
- `frontend/src/views/DashboardView.vue` - Main dashboard

## Common Patterns

**Backend**:
- UUID v7: `@UuidGenerator(style = VERSION_7)`
- Single Table Inheritance: `@DiscriminatorValue("TYPE")`
- Lazy loading: `@ManyToOne(fetch = LAZY)`
- Validation: `@NoHtml`
- Timestamps: `@CreationTimestamp`, `@UpdateTimestamp`
- MapStruct: `@Mapper(config = MapStructConfig.class)`

**Frontend**:
- API: Factory function pattern (receives instance, returns methods)
- State: Singleton pattern (shared state outside composable)
- Caching: 5min cache with `isCacheValid()` check
- Search: Case-insensitive filtering across configured fields
- Delete: Function name singular (e.g., `deleteEntity` not `deleteEntities`)
- Routing: `meta: { requiresAuth: true, roles: ['USER', 'ADMIN'], tableKey: 'entityKey' }`
- Toast: Success/error notifications via vue-toastification

## Development Workflow

**Backend Workflow**:
1. Schema changes: Create Liquibase changelog → `./mvnw liquibase:update` → `python3 validate_schema.py`
2. Entity changes: Update entity → mapper → TO → service/controller
3. Testing: Write tests → `./mvnw verify`
4. Security: Encrypt passwords via `PasswordEncryptionUtil`

**Frontend Workflow**:
1. Component development: Create/modify in `components/` or `views/`
2. API integration: Add methods in `api/` modules (factory pattern)
3. Routing: Configure in `router/index.js`
4. Styling: Tailwind CSS utility classes
5. State: Composables for shared reactive state (singleton pattern)

**Full-Stack Workflow**:
1. Start backend: `cd backend && ./mvnw spring-boot:run` (port 8090)
2. Start frontend: `cd frontend && npm run dev` (port 3001)
3. Development: Make changes, test at http://localhost:3001
4. Commits: Descriptive messages, atomic changes

## MCP Servers

- **postgres**: Direct DB queries (`mcp__postgres__query`)
- **sequential-thinking**: Chain-of-thought reasoning (`mcp__sequential-thinking__sequentialthinking`)
- **context7**: Library docs (`mcp__context7__resolve-library-id`, `mcp__context7__query-docs`)
- **playwright**: Browser automation for Swagger UI testing

## Important Notes

**Backend**:
- Discriminator values in entity classes must match CSV data exactly
- Never modify existing Liquibase changesets, create new ones
- CSV format: separator `;`, quotchar `'`
- All entities extend BaseEntity (UUID v7, timestamps)
- Configuration files gitignored: `.local.props`, `application-local.yaml`
- Schema: `switchscope`, snake_case naming

**Frontend**:
- ALWAYS use factory function pattern for API modules
- Delete function must be singular name (e.g., `deleteEntity` not `deleteEntities`)
- Use singleton pattern for shared state (state outside composable)
- Column `type` must match cell renderer types
- `visible: false` columns accessible via "Show All Columns" button
- System items (systemType=true) automatically protected from deletion
- Toast notifications handled automatically by composables
