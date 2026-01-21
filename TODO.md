# SwitchScope - Implementation Guide

## Entity Relationship Map (FK Dependencies)

### Core Entity Hierarchy

```
ComponentCategoryEntity (catalog)
    │
    │ 1:N (categoryId)
    ▼
ComponentTypeEntity (catalog)
    │
    │ 1:N (componentTypeId)
    ▼
ComponentModel (catalog) ──────────────────┐
    │                                      │
    │ 1:N (specific model FK)              │
    ▼                                      │
Component (instance)                       │
    │ - componentTypeId ◄──────────────────┘
    │ - componentStatusId ──► ComponentStatusEntity (catalog)
    │ - componentNatureId ──► ComponentNatureEntity (catalog)
    │ - parentComponentId ──► Component (self-reference)
    │ - installationId ────► Installation
    │
    │ 1:N
    ▼
Port (abstract, STI)
    ├── EthernetPort
    └── FiberPort
```

### Location Hierarchy

```
LocationTypeEntity (catalog)
    │
    │ 1:N (typeId)
    ▼
Location
    │ - parentLocationId ──► Location (self-reference, hierarchy)
    │
    │ 1:N
    ▼
Installation
    │ - locationId ──────────► Location
    │ - componentId ─────────► Component (housing)
    │ - installedItemTypeId ─► InstallableTypeEntity (catalog)
    │ - statusId ────────────► InstallationStatusEntity (catalog)
```

### Complete FK Reference Table

| Entity | Field | Related Entity | Notes |
|--------|-------|----------------|-------|
| **ComponentTypeEntity** | categoryId | ComponentCategoryEntity | Required |
| **ComponentModel** | componentTypeId | ComponentTypeEntity | Required |
| **Component** | componentTypeId | ComponentTypeEntity | Required |
| **Component** | componentStatusId | ComponentStatusEntity | Required |
| **Component** | componentNatureId | ComponentNatureEntity | Optional |
| **Component** | parentComponentId | Component | Optional, self-ref |
| **Component** | installationId | Installation | Optional |
| **NetworkSwitch** | switchModelId | SwitchModel | Optional |
| **Rack** | rackTypeId | RackModelEntity | Required |
| **Port** | deviceId | Device | Required |
| **Port** | connectorId | Connector | Optional |
| **Location** | typeId | LocationTypeEntity | Required |
| **Location** | parentLocationId | Location | Optional, self-ref |
| **Installation** | locationId | Location | Required |
| **Installation** | componentId | Component | Optional (housing) |
| **Installation** | installedItemTypeId | InstallableTypeEntity | Required |
| **Installation** | statusId | InstallationStatusEntity | Required |
| **CableRun** | cableModelId | CableRunModel | Optional |
| **CableRun** | startLocationId | Location | Optional |
| **CableRun** | endLocationId | Location | Optional |
| **Connector** | connectorModelId | ConnectorModel | Optional |
| **Connector** | cableRunId | CableRun | Required |
| **PatchPanel** | patchPanelModelId | PatchPanelModel | Optional |

---

## Editable View Implementation Guide

### Phase 1: Backend - Gold Standard Update Pattern

#### 1.1 Create UpdatableCrudService Interface

**File:** `backend/src/main/java/net/switchscope/service/UpdatableCrudService.java`

```java
public interface UpdatableCrudService<E, T extends BaseTo> extends CrudService<E> {
    E updateFromDto(UUID id, T dto);
}
```

#### 1.2 Update Service Implementation

**Pattern:** Load existing → Handle FK changes → MapStruct updateFromTo → Save

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class YourEntityService implements UpdatableCrudService<YourEntity, YourEntityTo> {

    private final YourEntityRepository repository;
    private final YourEntityMapper mapper;

    @Override
    @Transactional
    public YourEntity updateFromDto(UUID id, YourEntityTo dto) {
        // 1. Load existing with associations
        YourEntity existing = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new NotFoundException("Entity not found"));

        // 2. Use mapper for partial update (preserves ignored fields)
        mapper.updateFromTo(existing, dto);

        // 3. Save and return
        return repository.save(existing);
    }
}
```

#### 1.2.1 Handling FK Relations in Service (IMPORTANT!)

When entity has FK relations (e.g., `categoryId`), the mapper ignores the association
to preserve it. You must handle FK changes **manually** in the service:

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentTypeService implements UpdatableCrudService<ComponentTypeEntity, ComponentTypeTo> {

    private final ComponentTypeRepository repository;
    private final ComponentCategoryRepository categoryRepository;  // FK repository
    private final ComponentTypeMapper mapper;

    @Override
    @Transactional
    public ComponentTypeEntity updateFromDto(UUID id, ComponentTypeTo dto) {
        // 1. Load existing entity with all associations
        ComponentTypeEntity existing = repository.findByIdWithCategory(id)
                .orElseThrow(() -> new NotFoundException("Component type not found"));

        // 2. Handle FK change BEFORE mapper (mapper ignores 'category')
        if (dto.getCategoryId() != null &&
                !Objects.equals(dto.getCategoryId(), existing.getCategory().getId())) {
            ComponentCategoryEntity newCategory = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            existing.setCategory(newCategory);
        }

        // 3. Use mapper to update other fields (preserves properties)
        mapper.updateFromTo(existing, dto);

        // 4. Save and return
        return repository.save(existing);
    }
}
```

**Key Points:**
- Mapper has `@Mapping(target = "category", ignore = true)` — it won't update FK
- DTO contains `categoryId` (UUID), not the full `category` entity
- Service must manually: check if changed → load related entity → set it
- Order matters: handle FK changes BEFORE calling mapper

#### 1.3 Repository: Add Fetch Join Query

```java
@Query("SELECT e FROM YourEntity e LEFT JOIN FETCH e.association WHERE e.id = :id")
Optional<YourEntity> findByIdWithAssociations(@Param("id") UUID id);
```

#### 1.4 Mapper: Configure updateFromTo

```java
@Mapper(config = MapStructConfig.class)
public interface YourEntityMapper extends BaseMapper<YourEntity, YourEntityTo> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "associations", ignore = true)  // Preserve collections
    @Mapping(target = "properties", ignore = true)    // Preserve maps
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    YourEntity updateFromTo(@MappingTarget YourEntity entity, YourEntityTo to);
}
```

#### 1.5 Controller: Auto-detect UpdatableCrudService

`AbstractCatalogController` already handles this automatically:
- If service implements `UpdatableCrudService` → uses `updateFromDto()`
- Otherwise → fallback to legacy pattern

---

### Phase 2: Frontend - Edit Mode in GenericDetailView

#### 2.1 Detail Config with Edit Types

**File:** `frontend/src/configs/details/yourEntity.detail.js`

```javascript
export default {
  tableKey: 'yourEntities',
  composable: 'useYourEntities',

  sections: {
    basicInfo: {
      fields: [
        // Text input
        {
          key: 'name',
          label: 'Name',
          editType: 'input',
          required: true,
          validation: { maxLength: 128 }
        },

        // Read-only field (e.g., code)
        {
          key: 'code',
          label: 'Code',
          editType: 'readonly',
          editable: false
        },

        // Textarea
        {
          key: 'description',
          label: 'Description',
          editType: 'textarea',
          rows: 3
        },

        // Boolean toggle
        {
          key: 'active',
          label: 'Active',
          type: 'boolean',
          editType: 'checkbox'
        },

        // Number input
        {
          key: 'sortOrder',
          label: 'Sort Order',
          editType: 'number',
          validation: { min: 0, max: 999 }
        },

        // FK relation with searchable dropdown
        {
          key: 'categoryId',
          label: 'Category',
          editType: 'searchable-select',
          required: true,
          relation: {
            composable: 'useComponentCategories',
            dataKey: 'componentCategories',
            valueKey: 'id',
            labelKey: 'displayName',
            searchFields: ['name', 'code', 'displayName']
          }
        },

        // Color class picker
        {
          key: 'colorClass',
          label: 'Color',
          editType: 'color-class'
        },

        // Icon picker
        {
          key: 'iconClass',
          label: 'Icon',
          editType: 'icon-class'
        }
      ]
    }
  }
};
```

#### 2.2 Available Edit Types

| Type | Component | Use Case |
|------|-----------|----------|
| `input` | Text input | Names, codes, short text |
| `textarea` | Multiline input | Descriptions |
| `checkbox` / `boolean` | Toggle switch | Boolean flags |
| `number` | Number input | Sort order, quantities |
| `select` | Simple dropdown | Static options |
| `searchable-select` | SearchableDropdown | FK relations |
| `color-class` | Color picker | Tailwind color classes |
| `icon-class` | Icon picker | PrimeIcons classes |
| `readonly` | Display only | Immutable fields (code) |

#### 2.2.1 Searchable Select for FK Relations (Complete Example)

**Use Case:** ComponentType has FK to ComponentCategory (`categoryId`)

**Frontend Config:** `componentType.detail.js`
```javascript
{
  key: 'categoryId',           // DTO field name (UUID)
  label: 'Category',
  editType: 'searchable-select',
  required: true,
  relation: {
    composable: 'useComponentCategories',  // Composable to load options
    dataKey: 'componentCategories',        // Property name in composable result
    valueKey: 'id',                        // Option value field (sent to API)
    labelKey: 'displayName',               // Option display text
    searchFields: ['name', 'code', 'displayName']  // Fields for filtering
  }
}
```

**How It Works:**
1. `EditFieldRenderer` detects `editType: 'searchable-select'`
2. Loads relation composable dynamically: `composableRegistry['componentCategories']()`
3. Fetches data: `composable.fetchComponentCategories()`
4. Passes options to `SearchableDropdown` component
5. User selects category → `categoryId` (UUID) stored in form
6. On save → DTO sent to backend with `categoryId`
7. Backend service handles FK change (see section 1.2.1)

**SearchableDropdown Features:**
- Type-ahead search filtering
- Keyboard navigation (↑↓ Enter Escape)
- Shows icon/color from option if available
- Clear button to deselect
- Scrollable list (max 6 visible)

#### 2.3 Register in Detail View Registry

**File:** `frontend/src/configs/details/detailViewRegistry.js`

```javascript
import yourEntityDetail from './yourEntity.detail';

export const detailViewRegistry = {
  // ... existing
  yourEntity: yourEntityDetail,
};
```

#### 2.4 Add Route with detailKey

**File:** `frontend/src/router/index.js`

```javascript
{
  path: "/catalog/your-entities/:id",
  name: "your-entity-detail",
  component: GenericDetailView,
  meta: {
    requiresAuth: true,
    roles: ['USER', 'ADMIN'],
    detailKey: 'yourEntity'
  }
}
```

---

### Phase 3: Components Reference

#### 3.1 SearchableDropdown

**File:** `frontend/src/components/form/SearchableDropdown.vue`

**Features:**
- Search input with filtering
- Scrollable list (max 6 visible items)
- Keyboard navigation (up/down/enter/escape)
- Clear selection option
- Loading state
- Icon and color support from options

**Props:**
```javascript
{
  modelValue: [String, Number, null],  // Selected value
  options: Array,                       // Available options
  valueKey: String,                     // Default: 'id'
  labelKey: String,                     // Default: 'name'
  searchFields: Array,                  // Fields to search
  placeholder: String,
  disabled: Boolean,
  loading: Boolean,
  clearable: Boolean                    // Default: true
}
```

#### 3.2 EditFieldRenderer

**File:** `frontend/src/components/form/EditFieldRenderer.vue`

Renders appropriate input component based on `field.editType`.
Automatically loads relation data for `searchable-select` type.

---

### Phase 4: Checklist for New Editable Entity

#### Backend Checklist

- [ ] Repository: Add `findByIdWithAssociations()` method with JOIN FETCH
- [ ] Service: Implement `UpdatableCrudService<E, T>` interface
- [ ] Service: Implement `updateFromDto()` method
- [ ] Mapper: Configure `updateFromTo()` with proper `@Mapping(ignore=true)` for:
  - `id`
  - Collections/associations
  - `properties` (Map fields)
  - `createdAt`, `updatedAt`

#### Frontend Checklist

- [ ] Create detail config: `frontend/src/configs/details/yourEntity.detail.js`
- [ ] Define all fields with appropriate `editType`
- [ ] For FK fields: configure `relation` object with composable info
- [ ] Register in `detailViewRegistry.js`
- [ ] Ensure route has `meta.detailKey`
- [ ] Ensure table config has `routes.view` pointing to detail route

---

## Implementation Status

### Completed

- [x] **ComponentCategory** - Full edit support
  - Backend: UpdatableCrudService, findByIdWithComponentTypes
  - Frontend: All field types including color-class, icon-class
  - Properties preserved on update

- [x] **ComponentType** - Full edit support with FK relation
  - Backend: UpdatableCrudService, findByIdWithCategory, manual FK handling
  - Frontend: searchable-select for categoryId (dropdown with search)
  - Example of handling FK changes in service layer
  - Fixed GenericDetailView plural→singular conversion bug (`types` → `type`)

### Pending

- [ ] **ComponentStatus** - Workflow transitions (complex)
- [ ] **ComponentNature** - Simple catalog
- [ ] **ComponentModel** - Has componentTypeId FK
- [ ] **LocationType** - Parent-child hierarchy
- [ ] **InstallationStatus** - Workflow transitions
- [ ] **InstallableType** - Simple catalog
- [ ] **Location** - Has typeId, parentLocationId FKs
- [ ] **Installation** - Multiple FKs (complex)
- [ ] **Component** - Multiple FKs (complex)

---

## Architecture Summary

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND                                 │
├─────────────────────────────────────────────────────────────────┤
│  GenericDetailView.vue                                          │
│    ├── View Mode: FieldRenderer (read-only display)             │
│    └── Edit Mode: EditFieldRenderer (form inputs)               │
│                      └── SearchableDropdown (for FK fields)     │
│                                                                 │
│  Config-driven: detailConfig.sections.basicInfo.fields[]        │
│    - editType: 'input' | 'searchable-select' | 'checkbox' | ... │
│    - relation: { composable, dataKey, valueKey, labelKey }      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP PUT /api/catalogs/entity/:id
                              │ Body: EntityTo (DTO)
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         BACKEND                                  │
├─────────────────────────────────────────────────────────────────┤
│  AbstractCatalogController                                      │
│    └── update() checks: service instanceof UpdatableCrudService │
│          ├── YES → service.updateFromDto(id, dto)               │
│          └── NO  → legacy: mapper.toEntity() + service.update() │
│                                                                 │
│  Service (implements UpdatableCrudService)                      │
│    └── updateFromDto(id, dto):                                  │
│          1. repository.findByIdWithAssociations(id)             │
│          2. mapper.updateFromTo(existing, dto)                  │
│          3. repository.save(existing)                           │
│                                                                 │
│  Mapper (@MappingTarget)                                        │
│    └── updateFromTo(): ignores id, associations, properties     │
│        → Only updates specified fields                          │
│        → Preserves collections and Map<> fields                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Files Modified/Created

### Backend

| File | Action | Description |
|------|--------|-------------|
| `service/UpdatableCrudService.java` | Created | New interface with updateFromDto |
| `service/component/ComponentCategoryService.java` | Modified | Implements UpdatableCrudService |
| `service/component/ComponentTypeService.java` | Modified | Implements UpdatableCrudService, handles FK change |
| `repository/component/ComponentCategoryRepository.java` | Modified | Added findByIdWithComponentTypes |
| `repository/component/ComponentTypeRepository.java` | Modified | Added findByIdWithCategory |
| `web/AbstractCatalogController.java` | Modified | Auto-detect UpdatableCrudService |

### Frontend

| File | Action | Description |
|------|--------|-------------|
| `components/form/SearchableDropdown.vue` | Created | Dropdown with search for FK fields |
| `components/form/EditFieldRenderer.vue` | Created | Renders edit inputs by type |
| `views/GenericDetailView.vue` | Modified | Added edit mode, fixed plural→singular bug |
| `configs/details/componentCategory.detail.js` | Modified | Added editType to all fields |
| `configs/details/componentType.detail.js` | Modified | Added editType, searchable-select for categoryId |
| `views/GenericTableView.vue` | Modified | Edit button navigates to ?edit=true |
