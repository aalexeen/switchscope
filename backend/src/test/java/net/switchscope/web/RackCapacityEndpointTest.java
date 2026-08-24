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

import java.util.LinkedHashMap;
import java.util.Map;
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
 * Where a rack's capacity comes from when the request does not carry one, and what happens to a
 * request that asks to take it away.
 * <p>
 * {@code rack_units_total} is nullable in the database because one table holds every component
 * class and a router has no rack units. For a rack it is not optional at all: {@code getAvailableSpace},
 * {@code getUtilizationPercentage} and {@code hasAvailableSpace} unbox it, and the mapper maps all
 * three into every response. So {@code POST /api/housing/racks} without the field answered 500 - the
 * generated create mapping assigns unconditionally and wrote {@code null} over the entity's own
 * default of 42, and nothing put it back before the response was mapped.
 * <p>
 * Runs against the real mappers rather than the mocked ones in {@code AbstractContextTest}: a create
 * that goes through the service reaches {@code mapper.toEntity}, and a mock returns null there.
 * <p>
 * Nothing here is wrapped in a rolled-back transaction, on purpose. The defect lives in the window
 * between {@code persist} and {@code flush}, so a test that never flushes cannot tell a value that
 * reached the row from one that only ever existed in memory - and the rack fixture in
 * {@code RawBodyValidationTest} is exactly that. Each case commits and reads the row back, and
 * removes it in a {@code finally}.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class RackCapacityEndpointTest {

    private static final String RACKS = "/api/housing/racks";
    private static final String MODELS = "/api/catalogs/component-models";

    /** What the entity falls back to when the rack type has nothing to say about capacity. */
    private static final int FLAT_DEFAULT = 42;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("a create that omits the capacity takes it from the rack type")
    void createWithoutCapacityTakesItFromTheRackType() throws Exception {
        JsonNode rackType = aRackTypeWithACapacityOtherThanTheFlatDefault();
        int expected = rackType.get("typicalCapacityU").asInt();

        Map<String, Object> body = rackBody(rackType.get("id").asText());
        body.remove("rackUnitsTotal");

        UUID id = create(body);
        try {
            assertThat(read(id).get("rackUnitsTotal").asInt())
                    .as("the value has to come from the type rather than from the flat %d, or the"
                            + " test would pass on a default that ignores the type entirely", FLAT_DEFAULT)
                    .isEqualTo(expected);
        } finally {
            remove(id);
        }
    }

    @Test
    @DisplayName("a create that carries a capacity keeps it")
    void createKeepsTheCapacityItWasGiven() throws Exception {
        Map<String, Object> body = rackBody(aRackTypeWithACapacityOtherThanTheFlatDefault().get("id").asText());
        body.put("rackUnitsTotal", 8);

        UUID id = create(body);
        try {
            assertThat(read(id).get("rackUnitsTotal").asInt())
                    .as("supplying what is missing must not overrule what was asked for")
                    .isEqualTo(8);
        } finally {
            remove(id);
        }
    }

    @Test
    @DisplayName("clearing the capacity is refused as impossible, not as forbidden")
    void clearingTheCapacityIsRefused() throws Exception {
        JsonNode rackType = aRackTypeWithACapacityOtherThanTheFlatDefault();
        UUID id = create(rackBody(rackType.get("id").asText()));
        try {
            putAs(id, "{\"rackUnitsTotal\": null}")
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("rackUnitsTotal")));

            assertThat(read(id).get("rackUnitsTotal").asInt())
                    .as("a refused clear must not have applied half of itself - and a rack whose"
                            + " capacity was cleared could not be read back at all")
                    .isEqualTo(FLAT_DEFAULT);
        } finally {
            remove(id);
        }
    }

    /**
     * A seeded rack type whose capacity differs from the flat default, so that a response carrying
     * that number can only have got it from the type.
     */
    private JsonNode aRackTypeWithACapacityOtherThanTheFlatDefault() throws Exception {
        for (JsonNode model : objectMapper.readTree(getBody(MODELS))) {
            JsonNode capacity = model.get("typicalCapacityU");
            if (capacity != null && !capacity.isNull() && capacity.asInt() != FLAT_DEFAULT) {
                return model;
            }
        }
        throw new AssertionError("no seeded rack type with a capacity other than " + FLAT_DEFAULT
                + "; the type branch of the default cannot be told apart from the flat one");
    }

    private Map<String, Object> rackBody(String rackTypeId) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "rack-capacity-test-" + UUID.randomUUID());
        body.put("manufacturer", "test");
        // NOT NULL in the schema, with no default: without it the insert fails at commit, which the
        // rolled-back rack fixtures elsewhere never reach.
        body.put("serialNumber", "SN-" + UUID.randomUUID());
        body.put("rackUnitsTotal", FLAT_DEFAULT);
        body.put("rackTypeId", rackTypeId);
        body.put("componentTypeId", componentTypeId("RACK"));
        body.put("componentStatusId", firstId("/api/catalogs/component-statuses"));
        return body;
    }

    private UUID create(Map<String, Object> body) throws Exception {
        String created = mockMvc.perform(post(RACKS)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(created).get("id").asText());
    }

    private ResultActions putAs(UUID id, String body) throws Exception {
        return mockMvc.perform(put(RACKS + "/" + id)
                .with(httpBasic("admin@gmail.com", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    /** Reads the row back through the API, so the assertion is about what was stored. */
    private JsonNode read(UUID id) throws Exception {
        return objectMapper.readTree(getBody(RACKS + "/" + id));
    }

    private void remove(UUID id) throws Exception {
        mockMvc.perform(delete(RACKS + "/" + id).with(httpBasic("admin@gmail.com", "admin")));
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
