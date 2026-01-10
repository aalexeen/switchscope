# Deprecated View Components

**Date**: January 10, 2026
**Reason**: Unified under GenericTableView

## What Happened?

All table-specific view components were replaced by a single universal `GenericTableView.vue`:

### Before:
- `catalog/GenericCatalogView.vue` - for catalog tables
- `component/ComponentView.vue` - for Components entity table
- Each view had its own logic for search, filtering, composables

### After:
- `GenericTableView.vue` - universal view for ALL tables
- Uses `meta.tableKey` from router to load configuration
- Uses `tableRegistry` to get config and composable
- Unified search, filtering, and display logic

## Files in This Directory

### catalog/
- `GenericCatalogView.vue` → Replaced by `/views/GenericTableView.vue`

### component/
- `ComponentView.vue` → Replaced by `/views/GenericTableView.vue`

## How It Works Now

Router specifies which table to show:
```javascript
{
  path: "/components",
  component: GenericTableView,
  meta: {
    tableKey: 'components' // Looks up in tableRegistry
  }
}
```

GenericTableView loads:
1. Config from `tableRegistry[tableKey]`
2. Composable from `composableRegistry[tableKey]`
3. Renders with unified UI

## Benefits

- ✅ Single source of truth for table rendering
- ✅ Consistent UI across all tables
- ✅ Easy to add new tables (just config + registry entry)
- ✅ Reduced code duplication

## When Can We Delete?

After 2 weeks of successful testing (around January 24, 2026).

## Rollback Plan

If issues arise:
1. Restore these view files
2. Update router to use old components
3. Report issue for investigation

## See Also

- `TABLE_REFACTOR_COMPLETE.md` - Full refactoring documentation
- `components/_deprecated/` - Deprecated table components
