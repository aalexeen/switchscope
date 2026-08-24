package net.switchscope.web.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The answers a catalog route gives, written down before the seven near-identical catalog
 * controllers are collapsed onto {@code AbstractCrudController}.
 * <p>
 * The collapse changes how a create reads its body: these controllers bind a typed
 * {@code @Valid @RequestBody}, the base class reads the body as raw JSON and validates the bound
 * DTO itself. The two are meant to answer identically - a violated constraint as 422 with
 * {@code invalid_params}, a body that is not JSON as 400 - and "meant to" is exactly the kind of
 * claim that a refactor quietly breaks. So the claim is pinned here first, against the controllers
 * as they are, and the same test has to still pass afterwards.
 * <p>
 * Component categories stand for all seven: it is the plainest of them, with no foreign key to
 * resolve and no extra endpoint. Each case creates its own row and removes it, so nothing depends
 * on the seeded catalog beyond one row to copy the shape from.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class CatalogWriteContractTest {

    private static final String CATEGORIES = "/api/catalogs/component-categories";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("a create answers 201 with the stored object, and a delete answers 204")
    void createsAndReadsBackAWholeObject() throws Exception {
        ObjectNode body = newCategory("catalog-contract-created");

        String created = post(body).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(body.get("name").asText()))
                .andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(objectMapper.readTree(created).get("id").asText());
        try {
            assertThat(read(id).get("name").asText())
                    .as("the row has to be readable back, not only echoed")
                    .isEqualTo(body.get("name").asText());
        } finally {
            mockMvc.perform(delete(CATEGORIES + "/" + id).with(httpBasic("admin@gmail.com", "admin")))
                    .andExpect(status().isNoContent());
        }
    }

    @Test
    @DisplayName("a body that is not JSON is the caller's mistake, not the server's")
    void refusesABodyItCannotRead() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(CATEGORIES)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("a create validates the DTO's constraints and names the field it refused")
    void refusesAValueTheDtoForbids() throws Exception {
        ObjectNode body = newCategory("catalog-contract-invalid");
        body.put("name", "x"); // @Size(min = 2) on NamedTo

        post(body)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.invalid_params.name").exists());
    }

    @Test
    @DisplayName("clearing a field is refused for a USER and allowed for an admin")
    void clearingObeysThePolicy() throws Exception {
        ObjectNode body = newCategory("catalog-contract-clearing");
        body.put("description", "set by the test");

        String created = post(body).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(objectMapper.readTree(created).get("id").asText());
        try {
            putAs(id, "{\"description\": null}", "user@gmail.com", "password")
                    .andExpect(status().isForbidden());
            assertThat(read(id).get("description").asText())
                    .as("description is ADMIN_NULLABLE, and a refused request must not have applied"
                            + " half of itself")
                    .isEqualTo("set by the test");

            putAs(id, "{\"description\": null}", "admin@gmail.com", "admin")
                    .andExpect(status().isOk());
            assertThat(read(id).get("description").isNull())
                    .as("the same request from an admin goes through, so the refusal above is about"
                            + " the policy and not about the field")
                    .isTrue();
        } finally {
            mockMvc.perform(delete(CATEGORIES + "/" + id).with(httpBasic("admin@gmail.com", "admin")));
        }
    }

    /**
     * A body shaped like a stored category, with the identity fields made unique. Copying a seeded
     * row rather than writing a literal keeps the test honest about what the route requires: a
     * field that becomes mandatory later is carried here without anyone remembering to add it.
     */
    private ObjectNode newCategory(String prefix) throws Exception {
        JsonNode all = objectMapper.readTree(getBody(CATEGORIES));
        assertThat(all).as("the seeded catalog is the fixture this test stands on").isNotEmpty();

        ObjectNode body = ((ObjectNode) all.get(0)).deepCopy();
        String unique = prefix + "-" + UUID.randomUUID();
        body.remove("id");
        body.remove("createdAt");
        body.remove("updatedAt");
        body.put("name", unique);
        if (body.has("code")) {
            body.put("code", unique.toUpperCase().replace('-', '_'));
        }
        return body;
    }

    private ResultActions post(ObjectNode body) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(CATEGORIES)
                .with(httpBasic("admin@gmail.com", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    private ResultActions putAs(UUID id, String body, String user, String password) throws Exception {
        return mockMvc.perform(put(CATEGORIES + "/" + id)
                .with(httpBasic(user, password))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private JsonNode read(UUID id) throws Exception {
        return objectMapper.readTree(getBody(CATEGORIES + "/" + id));
    }

    private String getBody(String url) throws Exception {
        return mockMvc.perform(get(url)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }
}
