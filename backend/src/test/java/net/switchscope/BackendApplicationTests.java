package net.switchscope;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
class BackendApplicationTests {

    // Mock missing MapStruct mappers required by controllers to allow context startup
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.catalog.device.SwitchModelMapper switchModelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.catalog.device.RouterModelMapper routerModelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.catalog.device.AccessPointModelMapper accessPointModelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.catalog.connectivity.CableRunModelMapper cableRunModelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.catalog.connectivity.ConnectorModelMapper connectorModelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.catalog.connectivity.PatchPanelModelMapper patchPanelModelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.catalog.housing.RackModelMapper rackModelMapper;

    // ComponentController mappers
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.device.NetworkSwitchMapper networkSwitchMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.device.RouterMapper componentRouterMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.device.AccessPointMapper componentAccessPointMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.connectivity.CableRunMapper cableRunMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.connectivity.ConnectorMapper connectorMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.connectivity.PatchPanelMapper patchPanelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.housing.RackMapper rackMapper;

    @Test
    void contextLoads() {
    }
}
