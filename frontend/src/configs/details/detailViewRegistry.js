/**
 * Detail View Registry
 *
 * Central registry for all detail view configurations.
 * Maps detail view keys to their respective configurations.
 */

import componentCategoryDetail from './componentCategory.detail';
import componentNatureDetail from './componentNature.detail';
import componentStatusDetail from './componentStatus.detail';
import componentTypeDetail from './componentType.detail';

export const detailViewRegistry = {
  componentCategory: componentCategoryDetail,
  componentNature: componentNatureDetail,
  componentStatus: componentStatusDetail,
  componentType: componentTypeDetail
};

export default detailViewRegistry;
