package net.switchscope.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * What a route gives up when it reads its body as a string instead of letting Spring bind it, and
 * what has to be put back by hand.
 * <p>
 * Two things came with the typed {@code @Valid @RequestBody} and left with it. The discriminator:
 * Jackson demands {@code componentClass} when the declared parameter is a polymorphic subtype, so
 * {@code POST /api/housing/racks} required a body that repeated what the URL already said - and
 * answered 500 without it - while the PUT beside it had stopped requiring it. And bean validation:
 * every PUT in the project has been running without {@code @Valid} since the update endpoints
 * started reading raw bodies, so a constraint declared on the DTO and not on the entity was checked
 * on create and not on update.
 * <p>
 * Runs against the real mappers rather than the mocked ones in {@code AbstractContextTest}: a create
 * that goes through the service reaches {@code mapper.toEntity}, and a mock returns null there.
 * Each case runs in a rolled-back transaction, so the rows it writes do not outlive it.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class RawBodyValidationTest {

    private static final String RACKS = "/api/housing/racks";
    private static final String INSTALLATIONS = "/api/installations";
    private static final String LOCATIONS = "/api/locations";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    @DisplayName("a create needs no discriminator: the URL already says which type it is")
    void createsWithoutTheDiscriminator() throws Exception {
        mockMvc.perform(post(RACKS)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rackBody(null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.componentClass").value("RACK"));
    }

    @Test
    @Transactional
    @DisplayName("a discriminator that contradicts the URL does not win")
    void theUrlOutranksTheBodysDiscriminator() throws Exception {
        mockMvc.perform(post(RACKS)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rackBody("ROUTER"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.componentClass").value("RACK"));
    }

    @Test
    @Transactional
    @DisplayName("a create still validates the whole DTO")
    void createStillValidatesTheDto() throws Exception {
        Map<String, Object> body = rackBody(null);
        body.put("name", "x");

        mockMvc.perform(post(RACKS)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.invalid_params.name").exists());
    }

    @Test
    @Transactional
    @DisplayName("an update validates a constraint the entity does not carry")
    void updateValidatesADtoOnlyConstraint() throws Exception {
        UUID id = anyInstallationId();

        // @NoHtml is declared on InstallationTo.installedBy and nowhere on the Installation entity,
        // so until the raw body was validated this stored the markup and answered 200.
        updateInstallation(id, "{\"installedBy\": \"<script>alert(1)</script>\"}")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.invalid_params.installedBy").exists());
    }

    @Test
    @Transactional
    @DisplayName("an update validates a constraint the DTO inherits, not only its own")
    void updateValidatesAnInheritedConstraint() throws Exception {
        // @Size(min = 2) sits on NamedTo, not on LocationTo. A per-property check that only saw the
        // concrete class would pass this and leave the whole of NamedTo - name and description, the
        // two most widely inherited constrained fields in the project, @NoHtml included - unchecked
        // on every one of the twenty PUT routes. The invalid_params key is what says the refusal
        // came from here and not from the entity's own constraints at flush.
        mockMvc.perform(put(LOCATIONS + "/" + anyLocationId())
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"x\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.invalid_params.name").exists());
    }

    @Test
    @Transactional
    @DisplayName("an update still accepts a value that violates nothing")
    void updateAcceptsAValidValue() throws Exception {
        UUID id = anyInstallationId();

        updateInstallation(id, "{\"installedBy\": \"a technician\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.installedBy").value("a technician"));
    }

    private ResultActions updateInstallation(UUID id, String body) throws Exception {
        return mockMvc.perform(put(INSTALLATIONS + "/" + id)
                .with(httpBasic("admin@gmail.com", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private Map<String, Object> rackBody(String componentClass) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "raw-body-test-" + UUID.randomUUID());
        body.put("manufacturer", "test");
        // Not optional in practice: Rack's computed getters dereference it, so a rack created
        // without it answers 500 from the mapping back - the same before this change as after.
        body.put("rackUnitsTotal", 42);
        body.put("componentTypeId", componentTypeId("RACK"));
        body.put("componentStatusId", firstId("/api/catalogs/component-statuses"));
        if (componentClass != null) {
            body.put("componentClass", componentClass);
        }
        return body;
    }

    private UUID anyLocationId() throws Exception {
        JsonNode all = objectMapper.readTree(getBody(LOCATIONS));
        assertThat(all).as("the seeded locations are the fixture this test stands on").isNotEmpty();
        return UUID.fromString(all.get(0).get("id").asText());
    }

    private UUID anyInstallationId() throws Exception {
        JsonNode all = objectMapper.readTree(getBody(INSTALLATIONS));
        assertThat(all).as("the seeded installations are the fixture this test stands on").isNotEmpty();
        return UUID.fromString(all.get(0).get("id").asText());
    }

    private String firstId(String catalogUrl) throws Exception {
        JsonNode all = objectMapper.readTree(getBody(catalogUrl));
        assertThat(all).as("the seeded catalog is the fixture this test stands on").isNotEmpty();
        return all.get(0).get("id").asText();
    }

    private String componentTypeId(String code) throws Exception {
        for (JsonNode type : objectMapper.readTree(getBody("/api/catalogs/component-types"))) {
            if (code.equals(type.get("code").asText())) {
                return type.get("id").asText();
            }
        }
        throw new AssertionError("no seeded component type with code " + code);
    }

    private String getBody(String url) throws Exception {
        return mockMvc.perform(get(url)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }
}
