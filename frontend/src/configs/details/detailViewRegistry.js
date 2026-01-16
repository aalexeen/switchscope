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
import componentDetail from './component.detail';
import deviceDetail from './device.detail';
import installableTypeDetail from './installableType.detail';
import networkSwitchDetail from './networkSwitch.detail';
import routerDetail from './router.detail';
import accessPointDetail from './accessPoint.detail';
import cableRunDetail from './cableRun.detail';
import connectorDetail from './connector.detail';
import patchPanelDetail from './patchPanel.detail';
import rackDetail from './rack.detail';
import installationDetail from './installation.detail';
import installationStatusDetail from './installationStatus.detail';
import locationTypeDetail from './locationType.detail';
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
  component: componentDetail,
  device: deviceDetail,
  installableType: installableTypeDetail,
  networkSwitch: networkSwitchDetail,
  router: routerDetail,
  accessPoint: accessPointDetail,
  cableRun: cableRunDetail,
  connector: connectorDetail,
  patchPanel: patchPanelDetail,
  rack: rackDetail,
  installation: installationDetail,
  installationStatus: installationStatusDetail,
  locationType: locationTypeDetail,
  // Component Models (by discriminator type)
  switchModel: switchModelDetail,
  routerModel: routerModelDetail,
  accessPointModel: accessPointModelDetail,
  rackModel: rackModelDetail
};

export default detailViewRegistry;
