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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A row created from what the schema says is required, keeping the defaults for everything else.
 * <p>
 * The trap is one trap, and it is not about ports: the generated create mapping assigns every
 * property unconditionally - {@code NullValuePropertyMappingStrategy.IGNORE} governs update methods
 * only - so a payload that omits a field with a NOT NULL column writes null over the entity's
 * default and the insert is refused. Only <em>boxed</em> properties are exposed to it: a primitive
 * cannot hold null, so MapStruct guards those assignments itself, which is why a class full of
 * {@code boolean} flags has never needed anything. Two of these are pinned here; the rack's
 * capacity, the same shape found first, is pinned in {@code RackCapacityEndpointTest}.
 * <p>
 * Six of its fields are NOT NULL with a default in the entity and in the column, and the schema
 * marks none of them required: {@code status}, {@code adminStatus}, {@code operationalStatus},
 * {@code autoNegotiation}, {@code monitoringEnabled}, {@code poeEnabled}. The generated create
 * mapping assigns every property unconditionally - {@code NullValuePropertyMappingStrategy.IGNORE}
 * governs update methods only - so a payload that leaves them out writes null over each default and
 * the insert is refused: <b>409</b>, with the constraint's name in the detail. A client reading the
 * API description could not create a port at all, and the one test that did create one worked
 * around it by sending values it should not have needed.
 * <p>
 * The same shape as the rack's capacity (3060f03) and closed the same way, which is the point: the
 * default belongs to the entity, where a reader looks for it, and {@code @PrePersist} is the one
 * place every create path goes through.
 */
@SpringBootTest(properties = {
        "spring.testcontainers.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class CreateDefaultsEndpointTest {

    private static final String PORTS = "/api/ports";
    private static final String LOCATION_TYPES = "/api/catalogs/location-types";
    private static final String SWITCHES = "/api/devices/switches";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("a port is created from the fields the schema calls required, and keeps its defaults")
    void createsAPortWithoutTheValuesItsDefaultsCover() throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("portType", "ETHERNET");
        body.put("deviceId", aSwitchId().toString());
        body.put("portNumber", 9998);
        body.put("name", "port-defaults-test-" + UUID.randomUUID());

        String created = mockMvc.perform(post(PORTS)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(objectMapper.readTree(created).get("id").asText());

        try {
            JsonNode stored = read(PORTS + "/" + id);

            assertThat(stored.get("status").asText())
                    .as("a port nobody has heard from is down, and that is the column's default too")
                    .isEqualTo("DOWN");
            assertThat(stored.get("adminStatus").asText()).isEqualTo("UP");
            assertThat(stored.get("operationalStatus").asText()).isEqualTo("DOWN");
            assertThat(stored.get("autoNegotiation").asBoolean()).isTrue();
            assertThat(stored.get("monitoringEnabled").asBoolean()).isTrue();
            assertThat(stored.get("poeEnabled").asBoolean()).isFalse();
        } finally {
            mockMvc.perform(delete(PORTS + "/" + id).with(httpBasic("admin@gmail.com", "admin")))
                    .andExpect(status().isNoContent());
        }
    }

    @Test
    @DisplayName("what the payload does state is kept, defaults or not")
    void keepsTheValuesThePayloadStates() throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("portType", "ETHERNET");
        body.put("deviceId", aSwitchId().toString());
        body.put("portNumber", 9997);
        body.put("name", "port-defaults-test-" + UUID.randomUUID());
        body.put("status", "UP");
        body.put("adminStatus", "DOWN");
        body.put("monitoringEnabled", false);

        String created = mockMvc.perform(post(PORTS)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(objectMapper.readTree(created).get("id").asText());

        try {
            JsonNode stored = read(PORTS + "/" + id);

            assertThat(stored.get("status").asText())
                    .as("supplying a default is not the same as leaving it out")
                    .isEqualTo("UP");
            assertThat(stored.get("adminStatus").asText()).isEqualTo("DOWN");
            assertThat(stored.get("monitoringEnabled").asBoolean()).isFalse();
            assertThat(stored.get("operationalStatus").asText())
                    .as("the one it did not state still gets its default")
                    .isEqualTo("DOWN");
        } finally {
            mockMvc.perform(delete(PORTS + "/" + id).with(httpBasic("admin@gmail.com", "admin")))
                    .andExpect(status().isNoContent());
        }
    }

    @Test
    @DisplayName("a location type is created without the hierarchy level its schema calls optional")
    void createsALocationTypeWithoutItsHierarchyLevel() throws Exception {
        com.fasterxml.jackson.databind.node.ObjectNode seeded =
                ((com.fasterxml.jackson.databind.node.ObjectNode) read(LOCATION_TYPES).get(0)).deepCopy();
        String unique = "create-defaults-" + UUID.randomUUID();
        seeded.remove("id");
        seeded.remove("createdAt");
        seeded.remove("updatedAt");
        seeded.remove("hierarchyLevel");
        seeded.put("name", unique);
        seeded.put("code", unique.toUpperCase().replace('-', '_'));
        seeded.put("displayName", unique);

        String created = mockMvc.perform(post(LOCATION_TYPES)
                        .with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seeded)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(objectMapper.readTree(created).get("id").asText());

        try {
            assertThat(read(LOCATION_TYPES + "/" + id).get("hierarchyLevel").asInt())
                    .as("neither a campus nor a rack until someone says so - the middle of the range")
                    .isEqualTo(50);
        } finally {
            mockMvc.perform(delete(LOCATION_TYPES + "/" + id).with(httpBasic("admin@gmail.com", "admin")))
                    .andExpect(status().isNoContent());
        }
    }

    private UUID aSwitchId() throws Exception {
        JsonNode switches = objectMapper.readTree(body(SWITCHES));
        assertThat(switches).as("a port needs a device to belong to").isNotEmpty();
        return UUID.fromString(switches.get(0).get("id").asText());
    }

    private JsonNode read(String url) throws Exception {
        return objectMapper.readTree(body(url));
    }

    private String body(String url) throws Exception {
        return mockMvc.perform(get(url).with(httpBasic("admin@gmail.com", "admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }
}
