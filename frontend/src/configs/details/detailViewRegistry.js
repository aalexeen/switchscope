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
import installableTypeDetail from './installableType.detail';
import installationStatusDetail from './installationStatus.detail';
// Component Model detail configs (type-specific)
import switchModelDetail from './componentModels/switchModel.detail';
import routerModelDetail from './componentModels/routerModel.detail';
import accessPointModelDetail from './componentModels/accessPointModel.detail';
import rackModelDetail from './componentModels/rackModel.detail';

export const detailViewRegistry = {
  componentCategory: componentCategoryDetail,
  componentNature: componentNatureDetail,
  componentStatus: componentStatusDetail,
  componentType: componentTypeDetail,
  installableType: installableTypeDetail,
  installationStatus: installationStatusDetail,
  // Component Models (by discriminator type)
  switchModel: switchModelDetail,
  routerModel: routerModelDetail,
  accessPointModel: accessPointModelDetail,
  rackModel: rackModelDetail
};

export default detailViewRegistry;
