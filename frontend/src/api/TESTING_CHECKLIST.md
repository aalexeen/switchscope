# API Refactor Testing Checklist

## Quick Verification Steps

After refactoring API modules, verify that all catalog pages work correctly.

### 1. Component Natures (`/catalog/component-natures`)
- [ ] Page loads without errors
- [ ] Search functionality works
- [ ] Data displays in table
- [ ] Refresh button works
- [ ] View/Edit/Delete buttons visible

### 2. Component Categories (`/catalog/component-categories`)
- [ ] Page loads without errors
- [ ] Search functionality works
- [ ] Data displays in table
- [ ] Refresh button works
- [ ] View/Edit/Delete buttons visible

### 3. Component Types (`/catalog/component-types`)
- [ ] Page loads without errors
- [ ] Search functionality works
- [ ] Data displays in table
- [ ] Refresh button works
- [ ] View/Edit/Delete buttons visible

### 4. Component Statuses (`/catalog/component-statuses`)
- [ ] Page loads without errors
- [ ] Search functionality works
- [ ] Data displays in table
- [ ] Refresh button works
- [ ] View/Edit/Delete buttons visible

### 5. Component Models (`/catalog/component-models`)
- [ ] Page loads without errors
- [ ] Search functionality works
- [ ] Data displays in table
- [ ] Refresh button works
- [ ] View/Edit/Delete buttons visible

### 6. Location Types (`/catalog/location-types`)
- [ ] Page loads without errors
- [ ] Search functionality works
- [ ] Data displays in table
- [ ] Refresh button works
- [ ] View/Edit/Delete buttons visible

### 7. Installation Statuses (`/catalog/installation-statuses`)
- [ ] Page loads without errors
- [ ] Search functionality works
- [ ] Data displays in table
- [ ] Refresh button works
- [ ] View/Edit/Delete buttons visible

### 8. Installable Types (`/catalog/installable-types`)
- [ ] Page loads without errors
- [ ] Search functionality works
- [ ] Data displays in table
- [ ] Refresh button works
- [ ] View/Edit/Delete buttons visible

## Browser Console Check

Open browser DevTools console (F12) and verify:
- [ ] No JavaScript errors
- [ ] No 404 errors for API calls
- [ ] API responses return expected data structure

## Common Issues

If you encounter errors:

1. **Import Error**: Make sure `apiFactory.js` exists in `/src/api/`
2. **Module Not Found**: Check `api/index.js` has correct import
3. **404 API Error**: Verify baseURL in registry matches backend endpoint
4. **Function Not Found**: Check that factory returns all 5 methods (getAll, get, create, update, delete)

## Rollback Plan

If critical issues occur:

```bash
# Restore old API modules from deprecated
cd frontend/src/api
cp _deprecated/*.js components/
cp _deprecated/componentModels.js components/catalog/
cp _deprecated/locationTypes.js locations/
cp _deprecated/installationStatuses.js installations/
cp _deprecated/installableTypes.js installations/

# Restore old index.js (from git)
git checkout api/index.js
```

## Success Criteria

✅ Refactor is successful if:
- All 8 catalog pages load without errors
- All CRUD operations work (can view, search, refresh)
- No console errors
- API calls return correct data
- User experience is unchanged

## Performance Check

Optional: Compare before/after performance:
- [ ] Initial page load time similar
- [ ] API response time unchanged
- [ ] Search response time unchanged
- [ ] Bundle size reduced (check dev tools network tab)

---

**Last Updated**: January 9, 2026
**Status**: ✅ Ready for Testing
