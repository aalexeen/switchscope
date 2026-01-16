package net.switchscope.service.component;

import net.switchscope.model.component.InstallableCategory;
import net.switchscope.repository.installation.InstallableTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallableComponentRegistryTest {

    @Test
    void registryResolvesCategoriesAndImplementationStatus() {
        InstallableTypeRepository repository = mock(InstallableTypeRepository.class);
        when(repository.findAllCodes()).thenReturn(List.of("NETWORK_SWITCH", "FIREWALL"));

        InstallableComponentRegistry registry = new InstallableComponentRegistry(repository);
        registry.init();

        assertThat(registry.isDeviceType("NETWORK_SWITCH")).isTrue();
        assertThat(registry.isConnectivityType("PATCH_PANEL")).isTrue();
        assertThat(registry.isHousingType("RACK")).isTrue();
        assertThat(registry.isPowerType("UPS")).isFalse();

        assertThat(registry.getCategoryByCode("ROUTER"))
                .contains(InstallableCategory.DEVICE);

        assertThat(registry.isImplemented("NETWORK_SWITCH")).isTrue();
        assertThat(registry.isImplemented("FIREWALL")).isFalse();
        assertThat(registry.isImplemented(null)).isFalse();
    }
}
