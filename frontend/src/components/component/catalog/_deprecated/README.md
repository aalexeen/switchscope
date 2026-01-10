# Deprecated Components

**Date**: January 10, 2026
**Reason**: Migrated to Generic Table System

## What Happened?

These components were replaced by a configuration-driven generic table system:
- **Old**: 18 components (9 ListingsTable + 9 ListTable) = ~3,195 lines
- **New**: 9 config files + 2 generic components + 8 cell renderers = ~890 lines
- **Reduction**: 72% less code

## New System Location

- **Configs**: `src/configs/tables/*.config.js`
- **Generic Components**: `src/components/table/`
- **Cell Renderers**: `src/components/table/cells/`

## Migration Details

All catalog tables now use:
```vue
<GenericListingsTable
  :config="configFromFile"
  :filtered-data="..."
  :composable-data="..."
/>
```

## Files in This Directory

### Component Catalog Tables (10 files)
- ComponentNatureListingsTable.vue → componentNatures.config.js
- ComponentNatureListTable.vue → (generic rendering)
- ComponentCategoryListingsTable.vue → componentCategories.config.js
- ComponentCategoryListTable.vue → (generic rendering)
- ComponentTypeListingsTable.vue → componentTypes.config.js
- ComponentTypeListTable.vue → (generic rendering)
- ComponentStatusListingsTable.vue → componentStatuses.config.js
- ComponentStatusListTable.vue → (generic rendering)
- ComponentModelListingsTable.vue → componentModels.config.js
- ComponentModelListTable.vue → (generic rendering)

## When Can We Delete?

After 2 weeks of successful testing (around January 24, 2026), these files can be safely deleted.

## Rollback Plan

If issues arise, restore these files and revert changes in:
- `views/catalog/GenericCatalogView.vue`

## See Also

- `TABLE_REFACTOR_COMPLETE.md` - Full refactoring documentation
- `API_REFACTOR_SUMMARY.md` - Previous refactoring
