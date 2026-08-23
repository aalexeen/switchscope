package net.switchscope;

/**
 * The mappers no MapStruct implementation is generated for, mocked so that the full application
 * context can start. Shared by every test that needs the whole context rather than a slice.
 */
public abstract class AbstractContextTest {

    // Mappers with no generated implementation
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.catalog.device.SwitchModelMapper switchModelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.catalog.device.RouterModelMapper routerModelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.catalog.device.AccessPointModelMapper accessPointModelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.catalog.connectivity.CableRunModelMapper cableRunModelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.catalog.connectivity.ConnectorModelMapper connectorModelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.catalog.connectivity.PatchPanelModelMapper patchPanelModelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.catalog.housing.RackModelMapper rackModelMapper;

    // ComponentController mappers
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.device.NetworkSwitchMapper networkSwitchMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.device.RouterMapper componentRouterMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.device.AccessPointMapper componentAccessPointMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.connectivity.CableRunMapper cableRunMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.connectivity.ConnectorMapper connectorMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.connectivity.PatchPanelMapper patchPanelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    protected net.switchscope.mapper.component.housing.RackMapper rackMapper;

}
