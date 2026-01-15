package net.switchscope;

import net.switchscope.model.component.housing.Rack;
import net.switchscope.service.component.ComponentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Smoke-tests for real CRUD controllers: verify that endpoints are mapped
 * and are protected by authentication.
 */
@WebMvcTest(controllers = net.switchscope.web.component.ComponentController.class)
@Import(CrudSmokeControllerTest.TestSecurityConfig.class)
class CrudSmokeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComponentService componentService;

    // Mock mappers required by ComponentController constructor
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.device.NetworkSwitchMapper networkSwitchMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.device.RouterMapper routerMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.device.AccessPointMapper accessPointMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.connectivity.CableRunMapper cableRunMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.connectivity.ConnectorMapper connectorMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.connectivity.PatchPanelMapper patchPanelMapper;
    @org.springframework.boot.test.mock.mockito.MockBean
    private net.switchscope.mapper.component.housing.RackMapper rackMapper;

    @Test
    @DisplayName("GET /api/components requires authentication (401 when unauthenticated)")
    void componentsGetAllRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/components"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/components/{id} is mapped (not 404) when authenticated")
    void componentsGetByIdIsMapped() throws Exception {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        // Component is abstract, use a concrete subtype
        given(componentService.getById(id)).willReturn(new Rack());

        mockMvc.perform(get("/api/components/" + id)
                        .with(httpBasic("user", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .httpBasic(basic -> {
                    })
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }

        @Bean
        UserDetailsService userDetailsService() {
            return new InMemoryUserDetailsManager(
                    User.withUsername("user").password("{noop}password").roles("USER").build(),
                    User.withUsername("admin").password("{noop}password").roles("ADMIN").build()
            );
        }
    }
}
