# Deprecated Component Entity Tables

**Date**: January 10, 2026
**Reason**: Migrated to Generic Table System

## Files in This Directory

- ComponentListingsTable.vue → `configs/tables/components.config.js`
- ComponentListTable.vue → Generic rendering with specialized `CellComponentActions.vue`

## Special Notes

The Components entity table had custom delete logic with confirmation dialogs.
This was preserved in the new system via `components/table/cells/CellComponentActions.vue`.

## New System

The Components table now uses:
```vue
<GenericListingsTable
  :config="componentsConfig"
  :filtered-data="filteredComponents"
  :composable-data="{ data, isLoading, error, fetchData, total }"
/>
```

See: `components/component/catalog/_deprecated/README.md` for full migration details.
