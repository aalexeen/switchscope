package net.switchscope;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.switchscope.mapper.BaseMapper;
import net.switchscope.service.CrudService;
import net.switchscope.web.AbstractCatalogController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
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

@WebMvcTest(controllers = TestEntityController.class)
@Import({AbstractCatalogControllerTest.TestSecurityConfig.class, AbstractCatalogControllerTest.TestMapperConfig.class})
@org.springframework.test.context.ActiveProfiles("mvc-test")
@org.springframework.test.context.TestPropertySource(properties = "spring.web.resources.add-mappings=false")
class AbstractCatalogControllerTest {

    private static final String BASE_URL = "/api/test-entities";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrudService<TestEntity> service;

    @TestConfiguration
    static class TestMapperConfig {
        @Bean
        @Primary
        BaseMapper<TestEntity, TestEntityTo> testEntityMapper() {
            return new BaseMapper<>() {
                @Override
                public TestEntity toEntity(TestEntityTo to) {
                    if (to == null) return null;
                    return new TestEntity(to.getId(), to.getName());
                }

                @Override
                public java.util.List<TestEntity> toEntityList(java.util.Collection<TestEntityTo> tos) {
                    return tos == null ? java.util.List.of() : tos.stream().map(this::toEntity).toList();
                }

                @Override
                public TestEntity updateFromTo(TestEntity entity, TestEntityTo to) {
                    if (entity == null || to == null) return entity;
                    entity.setName(to.getName());
                    return entity;
                }

                @Override
                public TestEntityTo toTo(TestEntity entity) {
                    if (entity == null) return null;
                    return new TestEntityTo(entity.getId(), entity.getName());
                    
                }

                @Override
                public java.util.List<TestEntityTo> toToList(java.util.Collection<TestEntity> entities) {
                    return entities == null ? java.util.List.of() : entities.stream().map(this::toTo).toList();
                }
            };
        }
    }

    

    

    @Test
    @DisplayName("Should require authentication for GET all (401 when unauthenticated)")
    void shouldRequireAuthForGetAllWhenUnauthenticated() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return all entities for authenticated user")
    void getAllReturnsList() throws Exception {
        List<TestEntity> data = List.of(
                new TestEntity(UUID.fromString("00000000-0000-0000-0000-000000000001"), "A"),
                new TestEntity(UUID.fromString("00000000-0000-0000-0000-000000000002"), "B")
        );
        given(service.getAll()).willReturn(data);

        mockMvc.perform(get(BASE_URL).with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[1].name").value("B"));
    }

    @Test
    @DisplayName("Should return entity by id for authenticated user")
    void getByIdReturnsEntity() throws Exception {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000010");
        TestEntity entity = new TestEntity(id, "X");
        given(service.getById(id)).willReturn(entity);

        mockMvc.perform(get(BASE_URL + "/" + id).with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("X"));
    }

    @Test
    @DisplayName("Should create entity with ADMIN role and return 201")
    void createAsAdmin() throws Exception {
        TestEntity input = new TestEntity(null, "New");
        TestEntity created = new TestEntity(UUID.fromString("00000000-0000-0000-0000-0000000000AA"), "New");
        given(service.create(any(TestEntity.class))).willReturn(created);

        mockMvc.perform(post(BASE_URL)
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("00000000-0000-0000-0000-0000000000aa"))
                .andExpect(jsonPath("$.name").value("New"));

        ArgumentCaptor<TestEntity> captor = ArgumentCaptor.forClass(TestEntity.class);
        verify(service).create(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("New");
    }

    @Test
    @DisplayName("Should reject create for non-ADMIN with 403")
    void createForbiddenForNonAdmin() throws Exception {
        TestEntity input = new TestEntity(null, "New");

        mockMvc.perform(post(BASE_URL)
                        .with(httpBasic("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should update entity with ADMIN role and pass id to service")
    void updateAsAdmin() throws Exception {
        UUID id = UUID.fromString("00000000-0000-0000-0000-0000000000BB");
        TestEntity input = new TestEntity(null, "Upd");
        TestEntity updated = new TestEntity(id, "Upd");
        given(service.update(eq(id), any(TestEntity.class))).willReturn(updated);

        mockMvc.perform(put(BASE_URL + "/" + id)
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Upd"));

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(service).update(idCaptor.capture(), any(TestEntity.class));
        assertThat(idCaptor.getValue()).isEqualTo(id);
    }

    @Test
    @DisplayName("Should delete entity with ADMIN role and return 204")
    void deleteAsAdmin() throws Exception {
        UUID id = UUID.fromString("00000000-0000-0000-0000-0000000000CC");

        mockMvc.perform(delete(BASE_URL + "/" + id).with(httpBasic("admin", "password")))
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
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                    .anyRequest().authenticated())
                .httpBasic(basic -> {})
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
