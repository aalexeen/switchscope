package net.switchscope;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.switchscope.service.CrudService;
import net.switchscope.web.AbstractCrudController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = net.switchscope.web.component.ComponentController.class)
@Import(AbstractCrudControllerTest.TestSecurityConfig.class)
class AbstractCrudControllerTest {

    private static final String BASE_URL = "/api/components";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private net.switchscope.service.component.ComponentService service;

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
    @DisplayName("Should require authentication for GET all (401 when unauthenticated)")
    void shouldRequireAuthForGetAllWhenUnauthenticated() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET all is mapped for authenticated user (not 401)")
    void getAllIsMapped() throws Exception {
        // We only assert that mapping exists and security lets the request through.
        given(service.getAll()).willReturn(List.of());

        mockMvc.perform(get(BASE_URL).with(httpBasic("user", "password")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return entity by id for authenticated user")
    void getByIdReturnsEntity() throws Exception {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000010");
        given(service.getById(id)).willReturn(new net.switchscope.model.component.housing.Rack());

        mockMvc.perform(get(BASE_URL + "/" + id).with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST create rejects body without concrete type (500 due to abstract Component)")
    void createRejectsAbstractComponentBody() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .with(httpBasic("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("PUT update rejects body without concrete type (500 due to abstract Component)")
    void updateRejectsAbstractComponentBody() throws Exception {
        UUID id = UUID.fromString("00000000-0000-0000-0000-0000000000BB");

        mockMvc.perform(put(BASE_URL + "/" + id)
                        .with(httpBasic("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Should delete entity for authenticated user and return 204")
    void deleteAsAuthenticated() throws Exception {
        UUID id = UUID.fromString("00000000-0000-0000-0000-0000000000CC");

        mockMvc.perform(delete(BASE_URL + "/" + id).with(httpBasic("user", "password")))
                .andExpect(status().isNoContent());

        verify(service).delete(id);
    }

    
    @TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().authenticated())
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
